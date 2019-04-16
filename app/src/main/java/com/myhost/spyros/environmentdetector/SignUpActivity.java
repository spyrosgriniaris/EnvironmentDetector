package com.myhost.spyros.environmentdetector;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {


    //request codes
    private final int REQUEST_LOGIN_ACTIVITY = 1;
    private final int REQUEST_MAIN_ACTIVITY_AFTER_REGISTRATION = 2;

    //variable to check when to add new user, only when button sign up is clicked
    private boolean add_user = false;

    //declare Views
    TextView loginTextView;
    EditText email, password, fullName;
    Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //init views
        loginTextView = (TextView) findViewById(R.id.loginTextView);
        email = (EditText) findViewById(R.id.emailEditTextSignUp);
        password = (EditText) findViewById(R.id.passwordEditTextSignUp);
        fullName = (EditText) findViewById(R.id.fullNameEditTextSignUp);
        signUp = (Button) findViewById(R.id.btn_signup);

        //set up listeners
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(SignUpActivity.this,LoginActivity.class),REQUEST_LOGIN_ACTIVITY);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_user = true;
                if((check_inputs(email.getText().toString(),password.getText().toString().trim(),fullName.getText().toString().trim())) && add_user == true){
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    add_user = false;
                                    Toast.makeText(getApplicationContext(),"Registration Successful",Toast.LENGTH_SHORT).show();
                                    startActivityForResult(new Intent(SignUpActivity.this,MainActivity.class),REQUEST_MAIN_ACTIVITY_AFTER_REGISTRATION);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            add_user = false;
                            Toast.makeText(getApplicationContext(),"Something went wrong. Try again.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    private boolean check_inputs(String email, String password, String fullName){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches() || password.isEmpty() || password == null || fullName.isEmpty() || fullName == null)
            return false;
        else
            return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_MAIN_ACTIVITY_AFTER_REGISTRATION && resultCode == RESULT_OK){
            startActivity(data);
        }
    }
}
