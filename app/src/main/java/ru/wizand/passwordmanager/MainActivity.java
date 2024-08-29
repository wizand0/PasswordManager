package ru.wizand.passwordmanager;

import static ru.wizand.passwordmanager.AesCbcWithIntegrity.generateSalt;
import static ru.wizand.passwordmanager.AesCbcWithIntegrity.saltString;
import static ru.wizand.passwordmanager.EncryptionLibrary.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.security.GeneralSecurityException;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    TextView title;
    String userPass;
    String userLogin;
    String salt;
    Button btn;
    Context context;
    Boolean isLoggedIn;
    EditText inputMasterLogin;
    EditText inputMasterPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SharedPreferences


        prefs = getSharedPreferences("ru.wizand.passwordmanager", MODE_PRIVATE);
//        editor = prefs.edit();

        title = findViewById(R.id.new_password_title);

        btn = findViewById(R.id.btn);





        // Checking first launch
        //First launch

        if (prefs.getBoolean("firstrun", true)) {
            // При первом запуске (или если юзер удалял все данные приложения)
            // мы попадаем сюда. Делаем что-то
            //и после действия записывам false в переменную firstrun
            //Итого при следующих запусках этот код не вызывается.


//            salt = "4ooMz5/R/xl8df9Iife5GWmBuYaqBa54ESgTnZUOpkgNWKQ82i8OEMqoK/UwfGx8+DaRgCjidmqHYcCeL2OG3SqWjDAqukJRWCAAiBZGUGH6FdB4VqzTrg2Gp9Tbu/rbgt4tUflbPv9qQZ4C4aYs0hZBIKIguhuHqXybl0+ZvzQ=";


            title.setText("Restration");

            btn.setText("Register");

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inputMasterLogin = findViewById(R.id.login);
                    userLogin = inputMasterLogin.getText().toString();

                    inputMasterPass = findViewById(R.id.password);
                    userPass = inputMasterPass.getText().toString();




                    try {
                        byte[] saltByte = AesCbcWithIntegrity.generateSalt();
                        Log.i("testing_saltByte", Arrays.toString(saltByte));
                        salt = (String)saltString(saltByte);
                        Log.i("testing_saltString", salt);
                    } catch (GeneralSecurityException e) {
                        throw new RuntimeException(e);
                    }

                    prefs.edit().putString("salt", salt).apply();


                    prefs.edit().putBoolean("firstrun", false).apply();

                    String pass = encryptingKey(userPass, salt);
                    register(userPass, pass);
                    isLoggedIn = true;
                }
            });

        }
        //Other launches
        else{
            Log.i("testing_second", "testing_second");
            String getSalt = prefs.getString("salt", "");
            title.setText("Login");

            btn.setText("Login");

            inputMasterLogin = findViewById(R.id.login);

            inputMasterLogin.setVisibility(View.INVISIBLE);
            inputMasterPass = findViewById(R.id.password);
            inputMasterPass.setHint("Enter password");

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


//                    Log.i("testing_userLogin", userLogin);

                    userPass = inputMasterPass.getText().toString();
                    Log.i("testing_userPass", userPass);
                    salt = prefs.getString("salt", "");
                    Log.i("testing_salt", salt);




                    login(userPass);

                }
            });

        }
    }
    private void register(String username, String token){
        // Storing session data
        prefs.edit().putString("username", username).apply();
        prefs.edit().putString("token", token).apply();;
        prefs.edit().putBoolean("isLoggedIn", true).apply();;

        // Proceed to main activity or dashboard
        startActivity(new Intent(MainActivity.this, MainScreenActivity.class));
        finish();  // Optional: Close login activity
    }

    private void login(String pass){
        // Storing session data
        String EncrpPass = encryptingKey(pass, salt);

        String token = prefs.getString("token", "");

        isLoggedIn = EncrpPass.equals(token);

        if (isLoggedIn) {
            // Proceed to main activity or dashboard
            startActivity(new Intent(MainActivity.this, MainScreenActivity.class));
            finish();  // Optional: Close login activity
        }
        else{
            recreate();

        }
    }

    // Example logout method
//    private void logout () {
//        // Clearing session data
//        editor.clear();
//        editor.apply();
//
//        // Navigate back to login activity
//        startActivity(new Intent(this, MainActivity.class));
//        finish();  // Optional: Close main activity
//    }
}
