package ru.wizand.passwordmanager;

import static ru.wizand.passwordmanager.AesCbcWithIntegrity.generateKeyFromPassword;
import static ru.wizand.passwordmanager.AesCbcWithIntegrity.generateSalt;
import static ru.wizand.passwordmanager.AesCbcWithIntegrity.saltString;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.text.TextUtils;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.security.GeneralSecurityException;
import java.util.ArrayList;

import ru.wizand.passwordmanager.adapter.PasswordsAdapter;
import ru.wizand.passwordmanager.db.PasswordsAppDatabase;
import ru.wizand.passwordmanager.db.entity.Password;

public class MainActivity extends AppCompatActivity {

    // Variables
    private PasswordsAdapter passwordsAdapter;
    private ArrayList<Password> passwordArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PasswordsAppDatabase passwordsAppDatabase;

    private String masterPassText;
    private String masterEncryptKey;
    boolean isMasterPass;
    boolean isAuthorization;
    SharedPreferences prefs;

    private String userPassText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("ru.wizand.passwordmanager", MODE_PRIVATE);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My passwords");

        // RecyclerView
        recyclerView = findViewById(R.id.recycler_view_passwords);

        // Databsse
        passwordsAppDatabase = Room.databaseBuilder(
                getApplicationContext(),
                PasswordsAppDatabase.class,
                "PasswordDB").allowMainThreadQueries().build();

        //Passwords List
        passwordArrayList.addAll(passwordsAppDatabase.getPasswordDAO().getPasswords());

        passwordsAdapter = new PasswordsAdapter(this, passwordArrayList, MainActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(passwordsAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAndEditPasswords(false,null, -1);
            }
        });

        FloatingActionButton lock = (FloatingActionButton) findViewById(R.id.lock);
    }

    @Override
    protected void onResume() {
        super.onResume();
        masterEncryptKey = "";
        userPassText = "";
        // Fetching the stored data from the SharedPreference

        if (prefs.getBoolean("firstrun", true)) {
            // При первом запуске (или если юзер удалял все данные приложения)
            // мы попадаем сюда. Делаем что-то
            //и после действия записывам false в переменную firstrun
            //Итого при следующих запусках этот код не вызывается.

            Log.i("testing_FirstRun_onResume_key_1", masterEncryptKey);
            Log.i("testing_FirstRun_onResume_userPass_1", userPassText);

            setupMasterPass();

            prefs.edit().putBoolean("firstrun", false).apply();


            Log.i("testing_FirstRun_onResume_key_2", masterEncryptKey);
            Log.i("testing_FirstRun_onResume_userPass_2", userPassText);


        }
        else {
            do {
                checkMasterPass();
            }while (isAuthorization = false);
        }

        String passShared = prefs.getString("userPassText", "");
        String keyShared = prefs.getString("key", "");
        Log.i("testing_onResume_key_fromXML", keyShared);
        Log.i("testing_onResume_pass_fromXML", passShared);

        boolean equals = userPassText.equals(masterPassText);
        Log.i("testing_Equals", Boolean.toString(equals));

        boolean stringIsNotEmpty = passShared != null && !keyShared.isEmpty();

        Log.i("testing_stringIsNotEmpty", Boolean.toString(stringIsNotEmpty));

        Log.i("testing_isAuth", Boolean.toString(isAuthorization));


    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i("testing_onPause_key_masterKey", masterEncryptKey);
        Log.i("testing_onPause_key_userPass", userPassText);
        String passShared = prefs.getString("userPassText", "");
        String keyShared = prefs.getString("key", "");
        Log.i("testing_onPause_key_fromXML", keyShared);
        Log.i("testing_onPause_pass_fromXML", passShared);
        Log.i("testing_isAuth", Boolean.toString(isAuthorization));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAuthorization = false;

        Log.i("testing_onDestroy_key_masterKey", masterEncryptKey);
        Log.i("testing_onDestroy_key_userPass", userPassText);

        userPassText = "";

        prefs.edit().putString("userPassText", userPassText).apply();

        String passShared = prefs.getString("userPassText", "");
        String keyShared = prefs.getString("key", "");
        Log.i("testing_onDestroy_key_fromXML", keyShared);
        Log.i("testing_onDestroy_pass_fromXML", passShared);
        Log.i("testing_isAuth", Boolean.toString(isAuthorization));

    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i("testing_onStop_key_masterKey", masterEncryptKey);
        Log.i("testing_onStop_key_userPass", userPassText);
        String passShared = prefs.getString("userPassText", "");
        String keyShared = prefs.getString("key", "");
        Log.i("testing_onStop_key_fromXML", keyShared);
        Log.i("testing_onStop_pass_fromXML", passShared);

    }

    //For
    private void checkMasterPass() {
        // First running app and encryption the MASTERPASSWORD
        AlertDialog.Builder masterpassBuilder = new AlertDialog.Builder(this);
        masterpassBuilder.setTitle("Enter your Password");
        //Set up the input
        final EditText inputMaster = new EditText(this);
        inputMaster.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        masterpassBuilder.setView(inputMaster);

        masterEncryptKey = prefs.getString("key", "");



        //Set up the buttons
        masterpassBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Checking the input and master password
                userPassText = inputMaster.getText().toString();



                String pass = encryptString(userPassText);

                prefs.edit().putString("userPassText", pass).apply();

                String keyShared = prefs.getString("key", "");


                isAuthorization = pass.equals(keyShared);
                Log.i("testing_isAuthorization_method", Boolean.toString(isAuthorization));


//                userPassText = masterEncryptKey;
                popupMessage(masterEncryptKey, userPassText);

            }
        });

        masterpassBuilder.show();
    }


    private void setupMasterPass() {
        // First running app and encryption the MASTERPASSWORD
        AlertDialog.Builder masterpassBuilder = new AlertDialog.Builder(this);
        masterpassBuilder.setTitle("Enter Master Password");
        //Set up the input
        final EditText inputMaster = new EditText(this);
        inputMaster.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        masterpassBuilder.setView(inputMaster);
        //Set up the buttons
        masterpassBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                masterPassText = inputMaster.getText().toString();

                Log.i("testing_input_master", masterPassText);

                isMasterPass = true;
                isAuthorization = true;

                String key = encryptString(masterPassText);

                Log.i("testing_input_key", key);

                prefs.edit().putString("key", key).apply();
                prefs.edit().putString("userPassText", key).apply();


                popupMessage(masterPassText, key);
                masterEncryptKey = key;
                dialog.dismiss();
            }
        });

        masterpassBuilder.show();
    }


    //---------------------------------------------------------------------------------------------
    // This is popup for testing/ It shows password, salt and secret key. NOT FOR RELEASE
    private void popupMessage(String string1, String string2) {
        AlertDialog.Builder masterpassBuilder = new AlertDialog.Builder(this);
        masterpassBuilder.setTitle("Your passwords are:");
        //Set up the input
        final TextView passText = new TextView(this);
        passText.setTextSize(20);

        // Generating output
        passText.setText("Pass1: " + string1 + "\n Pass2: " + string2);
        masterpassBuilder.setView(passText);

        masterpassBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        masterpassBuilder.show();

    }

    //---------------------------------------------------------------------------------------------
    //Encrypt string

    private String encryptString(String inputString){

        //Generating salt
//         String salt = null;
        String salt = "4ooMz5/R/xl8df9Iife5GWmBuYaqBa54ESgTnZUOpkgNWKQ82i8OEMqoK/UwfGx8+DaRgCjidmqHYcCeL2OG3SqWjDAqukJRWCAAiBZGUGH6FdB4VqzTrg2Gp9Tbu/rbgt4tUflbPv9qQZ4C4aYs0hZBIKIguhuHqXybl0+ZvzQ=";
//        try {
//            salt = saltString(generateSalt());
//            Log.i("testing_input_key", salt);
//        } catch (GeneralSecurityException e) {
//            throw new RuntimeException(e);
//        }
        //Generating Master key
        String key = null;
        try {
            key = String.valueOf(generateKeyFromPassword(String.valueOf(inputString), salt));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        return key;
    }

    public void addAndEditPasswords(final boolean isUpdated,final Password password,final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.layout_add_password,null);

        AlertDialog.Builder alerDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alerDialogBuilder.setView(view);


        TextView passwordTitle = view.findViewById(R.id.new_password_title);
        final EditText newPassword = view.findViewById(R.id.name);
        final EditText passwordUrl = view.findViewById(R.id.url);
        final EditText passwordLogin = view.findViewById(R.id.login);
        final EditText passwordPassword = view.findViewById(R.id.password);
        final EditText passwordAdditional = view.findViewById(R.id.additional);

        passwordTitle.setText(!isUpdated ? "Add New Password" : "Edit Password");


        if (isUpdated && password != null){
            newPassword.setText(password.getName());
            passwordUrl.setText(password.getUrl());
            passwordLogin.setText(password.getLogin());
            passwordPassword.setText(password.getPassword());
            passwordAdditional.setText(password.getAdditional());
        }

        alerDialogBuilder.setCancelable(false)
                .setPositiveButton(isUpdated ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (isUpdated){
                                    DeletePassword(password, position);
                                }else{
                                    dialogInterface.cancel();
                                }
                            }
                        }
                );

        final AlertDialog alertDialog = alerDialogBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(newPassword.getText().toString())){
                    Toast.makeText(MainActivity.this, "Please Enter a Name", Toast.LENGTH_SHORT).show();

                    return;
                }else{
                    alertDialog.dismiss();
                }

                if (isUpdated && password != null){
                    UpdatePassword(newPassword.getText().toString(), passwordUrl.getText().toString(), passwordLogin.getText().toString(),
                            passwordPassword.getText().toString(), passwordAdditional.getText().toString(), position);

                }else{
                    CreatePassword(newPassword.getText().toString(), passwordUrl.getText().toString(), passwordLogin.getText().toString(),
                            passwordPassword.getText().toString(), passwordAdditional.getText().toString());

                }

            }
        });

    }

    private void CreatePassword(String name, String url, String login, String password, String additional) {
        long id = passwordsAppDatabase.getPasswordDAO()
                .addPassword(new Password(0, name, url, login, password, additional));
        Password password1 = passwordsAppDatabase.getPasswordDAO().getPassword(id);

        if (password1 != null){
            passwordArrayList.add(0, password1);
            passwordsAdapter.notifyDataSetChanged();
        }
    }

    private void UpdatePassword(String name, String url, String login, String password, String additional, int position) {
        Password password1 = passwordArrayList.get(position);

        password1.setName(name);
        password1.setUrl(url);
        password1.setLogin(login);
        password1.setPassword(password);
        password1.setAdditional(additional);

        passwordsAppDatabase.getPasswordDAO().updatePassword(password1);
        passwordArrayList.set(position, password1);
        passwordsAdapter.notifyDataSetChanged();
    }

    private void DeletePassword(Password password, int position) {
        passwordArrayList.remove(position);
        passwordsAppDatabase.getPasswordDAO().deletePassword(password);
        passwordsAdapter.notifyDataSetChanged();


    }
}