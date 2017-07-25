package com.example.yps.assignment_5;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity implements DatabaseReference.CompletionListener {

    //vars
    String TAG = "mTag";
    Boolean firstTime = true;
    SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddmmss", Locale.ENGLISH);

    SharedPreferences settings;
    public static final String PREFS_NAME = "MyPrefsFile";

    String nickname, chatPersonNickname, chatRoom1, chatRoom2, message;

    RelativeLayout relativeLayout;
    private RecyclerView chatRecyclerView;
    private ArrayList<Chat> chatArrayList = new ArrayList<>();;
    private ChatListAdapter chatListAdapter;
    LinearLayoutManager mLayoutManager;

    EditText sendTextEV;
    TextView chatPersonNicknameTV;
        //firebase declaration
        FirebaseDatabase database;
        DatabaseReference dbRef;
        ValueEventListener valueEventListener;

    //vars

    public ChatActivity() {
        // Required empty public constructor
    }

    /*
    Logout
*/
    public void onLogoutClick_Chat(View view){
        //clearing shared pref
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.apply();

        //redirecting to firstActivity
        Intent intent = new Intent(ChatActivity.this,PrimaryMainActivity.class);
        startActivity(intent);
        finish();
    }
    //Logout



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //init
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();
        settings = this.getSharedPreferences(PREFS_NAME, 0);
        nickname = settings.getString("nickNameLogin", nickname);
        chatPersonNickname= settings.getString("chatPersonNickname", chatPersonNickname);
        sendTextEV = (EditText) this.findViewById(R.id.sendTextEV);
        chatPersonNicknameTV = (TextView) this.findViewById(R.id.chatPersonNickname);

        chatPersonNicknameTV.setText(chatPersonNickname);

        chatRoom1 = nickname+"_" + chatPersonNickname;
        chatRoom2 = chatPersonNickname + "_" + nickname;

        Log.e(TAG,"Message received from View: "+message);

        chatRecyclerView = (RecyclerView) this.findViewById(R.id.recycler_view_chat);
        mLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mLayoutManager.setStackFromEnd(true);
        recyclerViewSetUp();
        getMessageFromFirebase(chatRoom1,chatRoom2);

        //sendButton listener
        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                message = sendTextEV.getText().toString();
                Chat chat = new Chat();
                chat.setMessage(message);
                chat.setSender(nickname);
                chat.setReceiver(chatPersonNickname);
                chat.setTimeStamp(new Date());

                if(chat.getMessage()!=null){
                    Log.d(TAG, "typed message: " + message);
                    Toast.makeText(ChatActivity.this, "Message is " + message, Toast.LENGTH_SHORT).show();
                    sendMessageToFirebase(ChatActivity.this,chat);
                    sendTextEV.setText("");
                } else {
                    Toast.makeText(ChatActivity.this, "Something Wrong!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Log.i(TAG,"Received Nickname & ChatPersonNickname : "+nickname+" | "+chatPersonNickname);
    }//onCreate End


    private void recyclerViewSetUp() {
        chatRecyclerView.invalidate();
        chatRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        chatRecyclerView.setLayoutManager(mLayoutManager);
        chatListAdapter= new ChatListAdapter(chatArrayList);
        chatRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chatRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        chatRecyclerView.setAdapter(chatListAdapter);
    }

    /*
    firebase
    */

    public void getMessageFromFirebase(final String chatRoom1, final String chatRoom2) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference();

        databaseReference.child("chats")
                .getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(chatRoom1)) {
                            Log.e(TAG, "getMessageFromFirebaseUser: " + chatRoom1 + " exists");
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("chats")
                                    .child(chatRoom1)
                                    .addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            // Chat message is retreived.
                                            //Chat chat = dataSnapshot.getValue(Chat.class);
                                            onGetChild(dataSnapshot);

                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Unable to get message.
                                        }
                                    });
                        } else if (dataSnapshot.hasChild(chatRoom2)) {
                            Log.e(TAG, "getMessageFromFirebaseUser: " + chatRoom2 + " exists");
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("chats")
                                    .child(chatRoom2)
                                    .addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            // Chat message is retreived.
                                            //Chat chat = dataSnapshot.getValue(Chat.class);
                                            onGetChild(dataSnapshot);
                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Unable to get message.
                                        }
                                    });
                        } else {
                            Log.i(TAG,"ELSE");
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("chats")
                                    .child(chatRoom1)
                                    .addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            // Chat message is retreived.
                                            //Chat chat = dataSnapshot.getValue(Chat.class);
                                            onGetChild(dataSnapshot);
                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Unable to get message.
                                        }
                                    });

                            Log.e(TAG, "getMessageFromFirebaseUser: no room available");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to get message
                    }
                });
    }

    private void onGetChild(DataSnapshot dataSnapshot){
        Chat chat = dataSnapshot.getValue(Chat.class);

        Log.i("Snapshot Read: ",chat.getMessage());
        chatArrayList.add(chat);
        if(chatRecyclerView!=null){
            chatRecyclerView.setVisibility(View.VISIBLE);
        }

        Log.d("User List SIZE", String.valueOf(chatArrayList.size()));

        chatListAdapter = new ChatListAdapter(chatArrayList);
        chatListAdapter.notifyDataSetChanged();

        chatRecyclerView.setLayoutManager(mLayoutManager);
        chatRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chatRecyclerView.setAdapter(chatListAdapter);
    }


    public void sendMessageToFirebase(final Context context, final Chat chat){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference();

        databaseReference.child("chats")
                .getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(chatRoom1)) {
                            Log.e(TAG, "sendMessageToFirebase: " + chatRoom1 + " exists");
                            databaseReference.child("chats")
                                    .child(chatRoom1)
                                    .child(String.valueOf(dataFormat.format(chat.getTimeStamp())))
                                    .setValue(chat);
                        } else if (dataSnapshot.hasChild(chatRoom2)) {
                            Log.e(TAG, "sendMessageToFirebase:: " + chatRoom2+ " exists");
                            databaseReference.child("chats")
                                    .child(chatRoom2)
                                    .child(String.valueOf(dataFormat.format(chat.getTimeStamp())))
                                    .setValue(chat);
                        } else {
                            Log.e(TAG, "sendMessageToFirebase:: New Entry");
                            databaseReference.child("chats")
                                    .child(chatRoom1)
                                    .child(String.valueOf(dataFormat.format(chat.getTimeStamp())))
                                    .setValue(chat);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                       Log.e(TAG,"Error Occured!");
                    }
                });

     /*   if(firstTime){
            Log.e(TAG,"firstTime");
            getMessageFromFirebase(chatRoom1,chatRoom2);
            firstTime = false;
        }*/
    }

    private void insertChatTitleFirebase() {
        dbRef.setValue(chatRoom1);
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError == null) {
            Log.e(TAG,"Firebase Insertion SUCCESSFUL!!");
        }
        else {
            Log.e(TAG, databaseError.getMessage());
        }
    }

    /*
    firebase
    */
}//Activity End
