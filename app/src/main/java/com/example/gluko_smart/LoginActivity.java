package com.example.gluko_smart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
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

    CheckBox rememberMeCB;

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
        rememberMeCB = findViewById(R.id.remember_me_checkbox);

        //Load Shared Preferences for auto Login
        SharedPreferences preferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        String savedEmail = preferences.getString("email", "");
        String savedPassword = preferences.getString("password", "");
        boolean rememberMe = !TextUtils.isEmpty(savedEmail) && !TextUtils.isEmpty(savedPassword);

        if (rememberMe) {
            // If both email and password are saved, attempt to sign in automatically
            inputEmail.setText(savedEmail);
            inputPassword.setText(savedPassword);
            rememberMeCB.setChecked(true);
            progressBar.setProgress(100);
        }

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

        inputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    setProg();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    setProg();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void performLogin() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String[] split_output = email.split("@");
        String userNameShort = split_output[0];

        if (!email.matches(emailPattern)) {
            inputEmail.setError(getString(R.string.invalid_username));
        } else if (password.isEmpty() || password.length() < 6) {
            inputPassword.setError(getString(R.string.invalid_passwortReg));
        } else {
            openDialog();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        openDialog().dismiss();
                        sendUserToNextActivity(userNameShort);
                        Toast.makeText(LoginActivity.this, R.string.login_successful, Toast.LENGTH_SHORT).show();
                    } else {
                        openDialog().dismiss();
                        Toast.makeText(LoginActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }

                    //Username, Login Preferences get stored
                    SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", userNameShort);

                    if (rememberMeCB.isChecked()) {
                        editor.putString("email", inputEmail.getText().toString());
                        editor.putString("password", inputPassword.getText().toString());
                    } else {
                        editor.remove("email");
                        editor.remove("password");
                    }
                    editor.apply();
                }
            });
        }
    }

    //wird nur beim ersten Mal auf dem Home gezeigt; müsste immer gezeigt werden!
    private void sendUserToNextActivity(String userNameShort) {
        Intent intent = new Intent(LoginActivity.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("UserName", userNameShort);
        startActivity(intent);

    }

    private LoginDialog openDialog() {
        LoginDialog LogDial1 = new LoginDialog();
        LogDial1.show(getSupportFragmentManager(), "regDialog1");
        return LogDial1;
    }

    private void setProg() {
        progressBar.setProgress(0);
        boolean isEmailHalfValid = inputEmail.getText().toString().contains("@");
        boolean isEmailValid = inputEmail.getText().toString().matches(emailPattern);
        boolean isPasswordHalfValid = inputPassword.getText().length() > 3;
        boolean isPasswordValid = inputPassword.getText().length() > 6;
        if (isEmailHalfValid) {
            progressBar.incrementProgressBy(25);
        }
        if (isPasswordValid) {
            progressBar.incrementProgressBy(25);
        }
        if (isEmailValid) {
            progressBar.incrementProgressBy(25);
        }
        if (isPasswordValid) {
            progressBar.incrementProgressBy(25);
        }
    }

}
