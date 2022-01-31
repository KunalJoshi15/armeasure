package com.example.armeasure;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private String user = "root";
    private String pass = "root";
    private EditText username;
    private EditText password;
    private Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = (EditText) findViewById(R.id.editTextTextPersonName);
                password = (EditText) findViewById(R.id.editTextTextPassword);
                String user_text = username.getText().toString();
                String pass_text = password.getText().toString();

                if(user_text.equals(user) && pass_text.equals(pass)){
                    Intent intent = new Intent(MainActivity.this,MainMenuActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
