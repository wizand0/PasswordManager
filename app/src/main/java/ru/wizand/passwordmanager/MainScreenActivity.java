package ru.wizand.passwordmanager;

import static ru.wizand.passwordmanager.AesCbcWithIntegrity.generateKeyFromPassword;
import static ru.wizand.passwordmanager.AesCbcWithIntegrity.generateSalt;
import static ru.wizand.passwordmanager.AesCbcWithIntegrity.saltString;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.text.TextUtils;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.wizand.passwordmanager.adapter.PasswordsAdapter;
import ru.wizand.passwordmanager.db.PasswordsAppDatabase;
import ru.wizand.passwordmanager.db.entity.Password;


public class MainScreenActivity extends AppCompatActivity {

    // Variables
    private PasswordsAdapter passwordsAdapter;
    private ArrayList<Password> passwordArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PasswordsAppDatabase passwordsAppDatabase;
    FloatingActionButton fab;
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        prefs = getSharedPreferences("ru.wizand.passwordmanager", MODE_PRIVATE);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My passwords");

        // RecyclerView
        recyclerView = findViewById(R.id.recycler_view_passwords);


        // Database
        passwordsAppDatabase = Room.databaseBuilder(
                        getApplicationContext(),
                        PasswordsAppDatabase.class,
                        "PasswordDB")
                .addCallback(myCallBack)
                // Delete this to run CreatePasswordInBackGround or allow this to run CreatePassword
//                .allowMainThreadQueries()
                .build();

        // Displaying All Contacts List
        DisplayAllPasswordsInBackGround();


        passwordsAdapter = new PasswordsAdapter(this, passwordArrayList, MainScreenActivity.this);
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

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public void addAndEditPasswords(final boolean isUpdated,final Password password,final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.layout_add_password,null);

        AlertDialog.Builder alerDialogBuilder = new AlertDialog.Builder(MainScreenActivity.this);
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
//                                    DeletePassword(password, position);
                                    DeletePasswordInBackGround(password, position);
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
                    Toast.makeText(MainScreenActivity.this, "Please Enter a Name", Toast.LENGTH_SHORT).show();

                    return;
                }else{
                    alertDialog.dismiss();
                }

                if (isUpdated && password != null){
                    // This is for running all in main thread
//                    UpdatePassword(newPassword.getText().toString(), passwordUrl.getText().toString(), passwordLogin.getText().toString(),
//                            passwordPassword.getText().toString(), passwordAdditional.getText().toString(), position);
                    // This is for running in background
                    UpdatePasswordInBackGround(newPassword.getText().toString(), passwordUrl.getText().toString(), passwordLogin.getText().toString(),
                            passwordPassword.getText().toString(), passwordAdditional.getText().toString(), position);


                }else{
                    // This is for running all in main thread
//                    CreatePassword(newPassword.getText().toString(), passwordUrl.getText().toString(), passwordLogin.getText().toString(),
//                            passwordPassword.getText().toString(), passwordAdditional.getText().toString());
                    // This is for running in background
                    CreatePasswordInBackGround(newPassword.getText().toString(), passwordUrl.getText().toString(), passwordLogin.getText().toString(),
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




    private void CreatePasswordInBackGround(String name, String url, String login, String password, String additional){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Background Work

                long id = passwordsAppDatabase.getPasswordDAO()
                        .addPassword(new Password(0, name, url, login, password, additional));
                Password password1 = passwordsAppDatabase.getPasswordDAO().getPassword(id);

                if (password1 != null){
                    passwordArrayList.add(0, password1);
                }

                // Executed after the background work had finished
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        passwordsAdapter.notifyDataSetChanged();
                    }
                });


            }
        });

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


    private void UpdatePasswordInBackGround(String name, String url, String login, String password, String additional, int position){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Background Work

                Password password1 = passwordArrayList.get(position);

                password1.setName(name);
                password1.setUrl(url);
                password1.setLogin(login);
                password1.setPassword(password);
                password1.setAdditional(additional);

                passwordsAppDatabase.getPasswordDAO().updatePassword(password1);
                passwordArrayList.set(position, password1);


                // Executed after the background work had finished
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        passwordsAdapter.notifyDataSetChanged();
                    }
                });


            }
        });

    }


    private void DeletePassword(Password password, int position) {
        passwordArrayList.remove(position);
        passwordsAppDatabase.getPasswordDAO().deletePassword(password);
        passwordsAdapter.notifyDataSetChanged();


    }


    private void DeletePasswordInBackGround(Password password, int position){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Background Work

                passwordArrayList.remove(position);
                passwordsAppDatabase.getPasswordDAO().deletePassword(password);

                // Executed after the background work had finished
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        passwordsAdapter.notifyDataSetChanged();
                    }
                });


            }
        });

    }





    private void DisplayAllPasswordsInBackGround(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Background Work
                passwordArrayList.addAll(passwordsAppDatabase.getPasswordDAO().getPasswords());

                // Executed after the background work had finished
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        passwordsAdapter.notifyDataSetChanged();
                    }
                });


            }
        });

    }



    // Menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // CallBacks
    RoomDatabase.Callback myCallBack = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);


            // These are 4 contacts already created in the app when installed (Built-In Contacts)
//            CreateContact("Bill Gates", "billgates@microsoft.com");
//            CreateContact("Nicolas Tesla", "nicolatesla@tesla.com");
//            CreateContact("Mark Zuker", "mark_zuker@facebook.com");
//            CreateContact("Satushi Namk","satushi@bitcoin.com");
//            DisplayAllContactInBackGround();
            Log.i("TAG","Database has been Created");
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            Log.i("TAG","Database has been Opened");
        }
    };



}