package com.example.gluko_smart;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    TextView alreadyHaveAccount, loginStatus;
    EditText inputEmail, inputPassword, inputConfirmPassword;
    Button btnRegister;
    ProgressBar progressBar;
    boolean pw_boolean;
    boolean pwConf_boolean;
    boolean email_boolean;
    int pb_counter = 0;
    private Handler handler = new Handler();

    String emailPattern ="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        alreadyHaveAccount = findViewById(R.id.tv_accAlreadyExists);
        loginStatus = findViewById(R.id.tv_loginStatus);

        inputEmail = findViewById(R.id.et_email);
        inputPassword = findViewById(R.id.password);
        inputConfirmPassword = findViewById(R.id.passwordConfirm);
        btnRegister = findViewById(R.id.bt_login);
        btnRegister.setEnabled(false);

        progressBar = findViewById(R.id.pb_loading);
        progressBar.setVisibility(View.VISIBLE);

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick (View v){
                startActivity(new Intent(RegisterActivity.this, Home.class));
            }
        });

        inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                setProg();
            }
        });

        inputConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
               setProg();
            }
        });

        inputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                btnRegister.setClickable(false);
                btnRegister.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    btnRegister.setClickable(true);
                    btnRegister.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                setProg();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAuth();
            }
        });
    }



    private void setProg() {
        progressBar.setProgress(15);
        if (inputPassword.getText().length()>6) {
            pw_boolean = true;
        } else
            pw_boolean = false;
        if (inputConfirmPassword.getText().length()>6) {
            pwConf_boolean = true;
        } else
            pwConf_boolean = false;
        if (inputEmail.getText().toString().matches(emailPattern)) {
            email_boolean = true;
        } else
            email_boolean = false;
        if (pw_boolean) {
            progressBar.setProgress(30);
        }
        if (pwConf_boolean) {
            progressBar.setProgress(60);
        }
        if (email_boolean) {
            progressBar.setProgress(100);
        }
    }

    private void performAuth() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();

        if (!email.matches(emailPattern))
        {
            inputEmail.setError("Enter an existing E-mail");
        } else if(password.isEmpty() || password.length()<6 )
        {
            inputPassword.setError("Enter a proper password");
        } else if(!password.equals(confirmPassword))
        {
            inputConfirmPassword.setError("Passwords do not match");
        }else
        {
            openDialog();
        }
    }

    private void openDialog() {
        RegisterDialog regDial1 = new RegisterDialog();
        regDial1.show(getSupportFragmentManager(),"regDialog1");
    }
}


