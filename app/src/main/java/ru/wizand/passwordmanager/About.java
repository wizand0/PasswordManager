package ru.wizand.passwordmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class About extends AppCompatActivity {

    TextView textView7, textView8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My passwords");

        textView7 = findViewById(R.id.textView7);
        textView8 = findViewById(R.id.textView8);

        textView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri webpage = Uri.parse("https://github.com/wizand0/PasswordManager");
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                startActivity(intent);
            }
        });

        textView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri webpage = Uri.parse("https://github.com/wizand0/PasswordManager");
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                startActivity(intent);
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
            Toast.makeText(this, "R.string.you_selected_about_section", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "R.string.you_selected_about_section", Toast.LENGTH_SHORT).show();
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