package com.example.gluko_smart;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        alreadyHaveAccount = findViewById(R.id.tv_accCreation);
        loginStatus = findViewById(R.id.tv_loginStatus);

        inputEmail = findViewById(R.id.et_email);
        inputPassword = findViewById(R.id.password);
        inputConfirmPassword = findViewById(R.id.passwordConfirm);
        btnRegister = findViewById(R.id.bt_login);
        btnRegister.setEnabled(false);

        progressBar = findViewById(R.id.pb_loading);
        progressBar.setVisibility(View.VISIBLE);

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick (View v){
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
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
        progressBar.setProgress(5);
        if (inputEmail.getText().toString().matches(emailPattern)) {
            email_boolean = true;
        } else
            email_boolean = false;
        if (inputPassword.getText().length()>6) {
            pw_boolean = true;
        } else
            pw_boolean = false;
        if (inputConfirmPassword.getText().length()>6) {
            pwConf_boolean = true;
        } else
            pwConf_boolean = false;
        if (email_boolean) {
            progressBar.setProgress(30);
        }
        if (pw_boolean) {
            progressBar.setProgress(60);
        }
        if (pwConf_boolean) {
            progressBar.setProgress(100);
        }
    }

    private void performAuth() {
        String email = inputEmail.getText().toString();
        String [] split_output = email.split("@");
        String userNameShort = split_output[0];
        String password = inputPassword.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();

        if (!email.matches(emailPattern))
        {
            inputEmail.setError(getString(R.string.invalid_username));
        } else if(password.isEmpty() || password.length()<6 )
        {
            inputPassword.setError(getString(R.string.invalid_passwortReg));
        } else if(!password.equals(confirmPassword))
        {
            inputConfirmPassword.setError(getString(R.string.notmaching_passwords));
        }else
        {
            openDialog();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        openDialog().dismiss();
                        sendUserToNextActivity(userNameShort);
                        Toast.makeText(RegisterActivity.this, R.string.registration_successful, Toast.LENGTH_SHORT).show();
                        
                    } else {
                        openDialog().dismiss();
                        Toast.makeText(RegisterActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToNextActivity(String userName) {
        Intent intent=new Intent(RegisterActivity.this,Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("UserName",userName);
        startActivity(intent);
    }

    private RegisterDialog openDialog() {
        RegisterDialog regDial1 = new RegisterDialog();
        regDial1.show(getSupportFragmentManager(),"regDialog1");
        return regDial1;
    }

}


