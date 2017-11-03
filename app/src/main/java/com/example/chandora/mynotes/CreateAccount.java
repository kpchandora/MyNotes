package com.example.chandora.mynotes;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateAccount extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPassEditText;
    private TextInputLayout emailTextInput, passwordTextInput, confirmPassInput;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailEditText = (EditText) findViewById(R.id.cEmailEditText);
        passwordEditText = (EditText) findViewById(R.id.cPasswordEditText);
        confirmPassEditText = (EditText) findViewById(R.id.cConfirmPasswordEditText);
        emailTextInput = (TextInputLayout) findViewById(R.id.cEmailTextInput);
        passwordTextInput = (TextInputLayout) findViewById(R.id.cPasswordTextInput);
        confirmPassInput = (TextInputLayout) findViewById(R.id.cConfirmTextInput);

        mAuth = FirebaseAuth.getInstance();

    }



    public void createAccount(View view) {

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPassEditText.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)
                || password.length() < 6 || !password.equals(confirmPassword)) {

            if (email.equals("")) {
                emailTextInput.setError("Enter valid email");
            } else {
                emailTextInput.setError(null);
            }

            if (password.equals("") || password.length() < 6) {
                passwordTextInput.setError("Password must be 6 characters long");
            } else {
                passwordTextInput.setError(null);
            }

            if (confirmPassword.equals("") || !password.equals(confirmPassword)) {
                confirmPassInput.setError("Enter correct password");
            } else {
                confirmPassInput.setError(null);
            }

        } else {
            createNewAccount(email, password);
        }


    }

    private void createNewAccount(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                currentUser.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(CreateAccount.this, "Verification email sent to \n" + currentUser.getEmail(), Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    Toast.makeText(CreateAccount.this, "Failed to send verification email", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(CreateAccount.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    public void editTextClick(View view) {

        confirmPassInput.setErrorEnabled(false);
        passwordTextInput.setErrorEnabled(false);
        emailTextInput.setErrorEnabled(false);
    }
}
