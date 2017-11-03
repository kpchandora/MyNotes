package com.example.chandora.mynotes;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 0;
    private static  int TAG = 0;
    private FirebaseAuth mFirebaseAuth;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton signInButton;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private EditText emailText, passwordText;
    private TextView createAccountText;
    private Button loginInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        emailText = (EditText) findViewById(R.id.emailEditText);
        passwordText = (EditText) findViewById(R.id.passwordEditText);
        createAccountText = (TextView) findViewById(R.id.createAccountText);
        loginInButton = (Button) findViewById(R.id.loginButton);
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                final FirebaseUser mUser = firebaseAuth.getCurrentUser();


                if (mUser != null) {

                   boolean emailVerified = mUser.isEmailVerified();

                    if (emailVerified) {
                        startActivity(new Intent(SignInActivity.this, MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }

                }


            }
        };

        createAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignInActivity.this, CreateAccount.class);
                startActivity(i);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton.setOnClickListener(this);
        loginInButton.setOnClickListener(this);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuthStateListener != null) {
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.loginButton:
                signInWithEmail();
        }
    }

    private void signInWithEmail() {

        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();


        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

            Toast.makeText(this, "Fields are empty", Toast.LENGTH_LONG).show();

        } else {


            mFirebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignInActivity.this, "Failed...", Toast.LENGTH_LONG).show();
                            }else {
                                TAG = 1;
                            }
                        }
                    });
        }


    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {

            }

        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credentials = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credentials)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this, "Authentication Failed..", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


}
