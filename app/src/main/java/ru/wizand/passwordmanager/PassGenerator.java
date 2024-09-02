package ru.wizand.passwordmanager;

import static ru.wizand.passwordmanager.PasswordHelper.generatePassword;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;

public class PassGenerator extends AppCompatActivity {

    TextView textViewTitlePassGenerator, textViewGeneratedPass;
    EditText editViewLenght;
    CheckBox toggleButtonUpper, toggleButtonLower, toggleButtonDigits, toggleButtonSymbols;
    Button button;
    ImageView copyButton;
//    CheckBox checkBox;

    boolean useUpper, useLower, useNumbers, useSpecial;
    String toggleUpper, toggleLower, toggleDigits, toggleSymbols, passLength;
    Byte lengthPass;
    String outputPass;

//    boolean isChecked;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_generator);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.my_passwords);

        textViewTitlePassGenerator = findViewById(R.id.textViewGeneratedPass);
//        editViewLenght = findViewById(R.id.EditViewLenght);
        editViewLenght = (EditText)findViewById(R.id.EditViewLenght);

//        checkBox = findViewById(R.id.checkBox);


        toggleButtonUpper = findViewById(R.id.toggleButtonUpper);
        toggleButtonLower = findViewById(R.id.toggleButtonLower);
        toggleButtonDigits = findViewById(R.id.toggleDigits);
        toggleButtonSymbols = findViewById(R.id.toggleButtonSymbols);

        copyButton = findViewById(R.id.imageCopuButton);
        button = findViewById(R.id.button);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                toggleUpper = toggleButtonUpper.getText().toString();
//                toggleLower = toggleButtonLower.getText().toString();
//                toggleDigits = toggleButtonDigits.getText().toString();
//                toggleSymbols = toggleButtonSymbols.getText().toString();

                passLength = editViewLenght.getText().toString();

//                isChecked = checkBox.isChecked();
//                Log.i("testing", "" + isChecked);
                useUpper = toggleButtonUpper.isChecked();
                useLower = toggleButtonLower.isChecked();
                useNumbers = toggleButtonDigits.isChecked();
                useSpecial = toggleButtonSymbols.isChecked();

                Integer i;
                try {
                    lengthPass = Byte.parseByte(passLength);
                    // do something with i
                } catch (NumberFormatException e) {
                    // log and do something else like notify the user or set i to a default value
                    Toast.makeText(getApplicationContext(), R.string.enter_the_password_s_length,
                            Toast.LENGTH_SHORT).show();
                    lengthPass = 6;
                }
                if (lengthPass > 20) {
                    lengthPass = 20;
                    Toast.makeText(getApplicationContext(), R.string.enter_the_correct_length_for_password, Toast.LENGTH_SHORT).show();
                }
                else if(lengthPass < 1) {
                    lengthPass = 6;
                    Toast.makeText(getApplicationContext(), R.string.enter_the_length_for_password, Toast.LENGTH_SHORT).show();
                }



                if(!useUpper && !useLower && !useNumbers && !useSpecial) {
                    Toast.makeText(getApplicationContext(), R.string.choose_symbols_for_password, Toast.LENGTH_SHORT).show();
                }


                else if(lengthPass == 0) {
                    Toast.makeText(getApplicationContext(), R.string.enter_the_length_for_password, Toast.LENGTH_SHORT).show();
                }
                else{
                    outputPass = generatePassword(lengthPass,
                            useUpper,
                            useLower,
                            useNumbers,
                            useSpecial);
                    textViewTitlePassGenerator.setText(outputPass);
                }

            }
        });

        //set the ontouch listener
        copyButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        //overlay is black with transparency of 0x77 (119)
                        view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        //clear the overlay
                        view.getDrawable().clearColorFilter();
                        view.invalidate();

                        break;
                    }
                }

                return false;
            }
        });




        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipData clip = ClipData.newPlainText("arbitrary label",outputPass);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), R.string.copied, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, R.string.under_constraction, Toast.LENGTH_SHORT).show();
            return true;
        }

        else if (id ==R.id.main) {
            Toast.makeText(this, R.string.you_selected_main_section, Toast.LENGTH_SHORT).show();
            Class ourClass  = null;
            try {
                ourClass = Class.forName("ru.wizand.passwordmanager.MainScreenActivity");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Intent j = new Intent(getApplicationContext(), ourClass);
            startActivity(j);
        }

        else if (id ==R.id.generator) {
            Toast.makeText(this, R.string.you_press_password_generator, Toast.LENGTH_SHORT).show();
            Class ourClass  = null;
            try {
                ourClass = Class.forName("ru.wizand.passwordmanager.PassGenerator");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Intent j = new Intent(getApplicationContext(), ourClass);
            startActivity(j);
        }

        else if (id ==R.id.about) {
            Toast.makeText(this, R.string.you_selected_about_section, Toast.LENGTH_SHORT).show();
            Class ourClass  = null;
            try {
                ourClass = Class.forName("ru.wizand.passwordmanager.About");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Intent j = new Intent(getApplicationContext(), ourClass);
            startActivity(j);
        }


        return super.onOptionsItemSelected(item);

    }
}