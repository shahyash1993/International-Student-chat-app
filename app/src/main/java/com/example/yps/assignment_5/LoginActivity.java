package com.example.yps.assignment_5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity{

    EditText nickNameET_login;
    EditText passwordET_login;

    Button registerButton_login;
    Button loginButton_login;

    String TAG = "mTag";
    SharedPreferences settings;
    public static final String PREFS_NAME = "MyPrefsFile";

    //firebase declaration
    FirebaseDatabase database;
    DatabaseReference dbRef;
    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        settings = getSharedPreferences(PREFS_NAME, 0);
        settings = PreferenceManager.getDefaultSharedPreferences(this);

        //getting user data
        nickNameET_login = (EditText) this.findViewById(R.id.nicknameET_login);
        passwordET_login = (EditText) this.findViewById(R.id.passwordET_login);

        //buttons
        registerButton_login = (Button) this.findViewById(R.id.registerButton_login);
        loginButton_login = (Button) this.findViewById(R.id.loginButton_login);
    }

    //button click handled
    public void onLoginClick_login(View view){

        setNickNamePref();

        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("users");

        valueEventListener = dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UserDetailsModel_firebase userDetailsModel_firebase = postSnapshot.getValue(UserDetailsModel_firebase.class);
                    // Log.i(TAG, userDetailsModel_firebase.toString());
                    Log.i(TAG,"Read | Firebase| Nick: "+userDetailsModel_firebase.getNickname().toString() + "And Entered Nickname: "+nickNameET_login.getText());
                        if(userDetailsModel_firebase.getNickname().toString().equalsIgnoreCase(String.valueOf(nickNameET_login.getText()))){
                            if(userDetailsModel_firebase.getPassword().toString().equalsIgnoreCase(String.valueOf(passwordET_login.getText()))){
                                Intent intent = new Intent(LoginActivity.this, DisplayUserListActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(LoginActivity.this,"Wrong Username/Password!",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void setNickNamePref() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("nickNameLogin", String.valueOf(nickNameET_login.getText()));
        Log.e(TAG,"Login | Setting nickname: "+String.valueOf(nickNameET_login.getText()));
        editor.commit();
    }

    public void onRegistrationClick_login(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //gettin data from server

}
