package com.example.sc2infoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class SingupActivity extends AppCompatActivity {

    private static final String TAG = "Singup activity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSingup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
    }
}