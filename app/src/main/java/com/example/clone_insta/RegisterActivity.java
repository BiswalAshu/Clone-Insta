package com.example.clone_insta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Properties;

public class RegisterActivity extends AppCompatActivity {
    private EditText username,name,email,password,password2;
    private DatabaseReference mRootref;
    private Button register;
    private TextView loginUser;
    private FirebaseAuth mAuth;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username=findViewById(R.id.username);
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        password2=findViewById(R.id.password2);
        mRootref= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        register=findViewById(R.id.register1);
        loginUser=findViewById(R.id.loginUser);
        pd=new ProgressDialog(this);
        loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textUsername=username.getText().toString();
                String textName=name.getText().toString();
                String textPassword = password.getText().toString();
                String passChk=password2.getText().toString();
                String textEmail=email.getText().toString();
                if(TextUtils.isEmpty(textEmail)||TextUtils.isEmpty(textName)||
                        TextUtils.isEmpty(textUsername)||TextUtils.isEmpty(textPassword)||
                        TextUtils.isEmpty(passChk))
                    Toast.makeText(RegisterActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
                else if(!passChk.equalsIgnoreCase(textPassword))
                    Toast.makeText(RegisterActivity.this, "Passwords don't match !!", Toast.LENGTH_SHORT).show();
                else if (textPassword.length()<6)
                    Toast.makeText(RegisterActivity.this, "Password too short", Toast.LENGTH_SHORT).show();
                else
                    registerUser(textUsername,textEmail,textPassword,textName);
            }
        });

    }
    private void registerUser(final String username1,final String email1,final String password1, final String name1) {
        pd.setMessage("Please wait...");
        pd.show();
        mAuth.createUserWithEmailAndPassword(email1,password1).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String, Object> map=new HashMap<>();
                map.put("name",name1);
                map.put("email",email1);
                map.put("uesrname",username1);
                map.put("bio","");
                map.put("imageurl","default");
                map.put("id",mAuth.getCurrentUser().getUid());
                mRootref.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "Welcome !!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
