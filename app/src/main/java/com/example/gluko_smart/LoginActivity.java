package com.example.gluko_smart;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    TextView neuenAccErstellen;
    EditText inputEmail, inputPassword;
    Button btnLogin;
    ProgressBar progressBar;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        neuenAccErstellen = findViewById(R.id.tv_accCreation);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        inputEmail = findViewById(R.id.et_email);
        inputPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.bt_login);
        progressBar = findViewById(R.id.pb_loading);
        progressBar.setVisibility(View.VISIBLE);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        neuenAccErstellen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();

            }
        });

    }

    private void performLogin() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String [] split_output = email.split("@");
        String userNameShort = split_output[0];

        if (!email.matches(emailPattern)) {
            inputEmail.setError(getString(R.string.invalid_username));
        } else if (password.isEmpty() || password.length() < 6) {
            inputPassword.setError(getString(R.string.invalid_passwortReg));
        } else {
            openDialog();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        openDialog().dismiss();
                        sendUserToNextActivity(userNameShort);
                        Toast.makeText(LoginActivity.this, R.string.login_successful, Toast.LENGTH_SHORT).show();
                    } else {
                        openDialog().dismiss();
                        Toast.makeText(LoginActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToNextActivity(String userNameShort) {
        Intent intent=new Intent(LoginActivity.this,Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("UserName",userNameShort);
        startActivity(intent);
    }

    private LoginDialog openDialog() {
        LoginDialog LogDial1 = new LoginDialog();
        LogDial1.show(getSupportFragmentManager(),"regDialog1");
        return LogDial1;
    }
}
