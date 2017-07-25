package com.example.yps.assignment_5;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DatabaseReference.CompletionListener{
    JSONArray countryListJSON, stateListJSON;
    Spinner countryListSpinner, stateListSpinner;
    String selectedCountry, selectedState, nickname, password, city, id;
    int year, statePosition, countryPosition;
    double lat = 0, lng = 0;
    TextView confirmSetLatLong;

    SharedPreferences settings;
    public static final String PREFS_NAME = "MyPrefsFile";

    EditText nicknameET, passwordET, cityET, yearET;
    ArrayList<String> countryList = new ArrayList<String>();
    ArrayList<String> stateList = new ArrayList<String>();

    //firebase declaration
    FirebaseDatabase database;
    DatabaseReference dbRef;
    ValueEventListener valueEventListener;

    String TAG = "mTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getSharedPreferences(PREFS_NAME, 0);

        //Connectivity Status
        isConnectionOK();
        getRequest();
        getNextId(); //get next available id on server


/*
        firebase START
*/
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("users");

        valueEventListener = dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UserDetailsModel_firebase userDetailsModel_firebase = postSnapshot.getValue(UserDetailsModel_firebase.class);
                   // Log.i(TAG, userDetailsModel_firebase.toString());
                    Log.i(TAG,"Read | Firebase| Nick: "+userDetailsModel_firebase.getNickname());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        /*dbRef = database.getReference("message");

        dbRef.setValue("Hello, World!");*/
/*
        firebase END
*/


        //getting data entered by user
        nickname = settings.getString("nick", nickname);
        password = settings.getString("password", password);
        selectedCountry = settings.getString("selectedCountry", selectedCountry);
        selectedState = settings.getString("selectedState", selectedState);
        city = settings.getString("city", city);
        year = settings.getInt("year", year);
        /*lat = Double.parseDouble(settings.getString("lat", Double.toString(lat)));
        lng = Double.parseDouble(settings.getString("lng", Double.toString(lng)));*/
        countryPosition = settings.getInt("countryPosition", countryPosition);
        statePosition = settings.getInt("statePosition", statePosition);
        id = settings.getString("id", id);

        Log.e(TAG, "Setting brought positions: " + countryPosition + statePosition);

        nicknameET = (EditText) findViewById(R.id.nickET);
        nicknameET.setText(nickname);

        passwordET = (EditText) findViewById(R.id.passwordET);
        passwordET.setText(password);

        yearET = (EditText) findViewById(R.id.yearET);
        if(year!=0) {
            yearET.setText(Integer.toString(year));
        }

        cityET = (EditText) findViewById(R.id.cityET);
        cityET.setText(city);

        countryListSpinner = (Spinner) findViewById(R.id.countryListSpinner);
        countryListSpinner.setSelection(countryPosition);

        stateListSpinner = (Spinner) findViewById(R.id.stateListSpinner);
        stateListSpinner.setSelection(statePosition);


        //Request getter


        //checking Extras
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        Log.e(TAG, "Didnt get returned" + bundle.toString());
        lat = bundle.getDouble("lat");
        lng = bundle.getDouble("lng");
        confirmSetLatLong = (TextView) findViewById(R.id.confirmSetLatLong);
        confirmSetLatLong.setVisibility(View.VISIBLE);

        //setData();

        Log.e(TAG, "MainActivity got this lat lng: " + lat + "/" + lng);
        readFromFirebase();
    }//onCreate End

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause Called!");
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        nicknameET = (EditText) findViewById(R.id.nickET);
        nickname = nicknameET.getText().toString();

        passwordET = (EditText) findViewById(R.id.passwordET);
        password = passwordET.getText().toString();

        yearET = (EditText) findViewById(R.id.yearET);
        year = Integer.parseInt(yearET.getText().toString());

        cityET = (EditText) findViewById(R.id.cityET);
        city = cityET.getText().toString();


        editor.putString("nick", nickname);
        editor.putString("password", password);
        editor.putString("selectedCountry", selectedCountry);
        editor.putString("selectedState", selectedState);
        editor.putString("city", city);
        editor.putInt("year", year);
        /*editor.putString("lat", Double.toString(lat));
        editor.putString("lng", Double.toString(lng));*/
        editor.putInt("countryPosition", countryPosition);
        editor.putInt("statePosition", statePosition);
        editor.putString("id", id);


        Log.e(TAG, "Setting sving" + nickname + password + "pos:" + countryPosition);

        editor.commit();
    }

    public void onRegisterClick(View view) throws JSONException, IOException {



        //get data
        nicknameET = (EditText) findViewById(R.id.nickET);
        nickname = nicknameET.getText().toString();

        passwordET = (EditText) findViewById(R.id.passwordET);
        password = passwordET.getText().toString();

        yearET = (EditText) findViewById(R.id.yearET);
        year = Integer.parseInt(yearET.getText().toString());

        cityET = (EditText) findViewById(R.id.cityET);
        city = cityET.getText().toString();

        selectedCountry = countryListSpinner.getSelectedItem().toString();
        selectedState = stateListSpinner.getSelectedItem().toString();

        insertUserFirebase();
        //validation
        if( nicknameET.getText().toString().length() == 0 || nicknameET.getText().toString().trim().equals("") ||
                passwordET.getText().toString().length() < 3 || cityET.getText().toString().equals("") ||
                cityET.getText().toString().length() == 0 || cityET.getText().toString().trim().equals("") ||
                yearET.getText().toString().length() == 0 || yearET.getText().toString().trim().equals("") ||
                Integer.parseInt(yearET.getText().toString()) < 1900 || Integer.parseInt(yearET.getText().toString()) > 2018
                ){
            Toast.makeText(getApplicationContext(),"Enter Valid Data!",Toast.LENGTH_SHORT).show();
            return;
        }



        final JSONObject jsonObject = new JSONObject();
        Log.d(TAG,"Putting nickname as: "+nickname);
        jsonObject.put("nickname", nickname);
        jsonObject.put("city", city);
        jsonObject.put("longitude", lng);
        jsonObject.put("state", selectedState);
        jsonObject.put("year", year);
        //jsonObject.put("id",id);
        jsonObject.put("latitude", lat);
        jsonObject.put("country", selectedCountry);
        jsonObject.put("password", password);

        postData(jsonObject);

    Intent intent = new Intent(this,DisplayUserListActivity.class);
    startActivity(intent);
    }//endOf OnRegisterClick

    private void postData(JSONObject jsonObject) {
        final String url = "http://bismarck.sdsu.edu/hometown/adduser";

        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
            @Override
            public int onResponse (JSONObject response){
                Log.i(TAG, response.toString());
                return 0;
            }

        };

        Response.ErrorListener failure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "post fail " + new String(error.networkResponse.data));
            }
        };
        JsonObjectRequest postRequest = new JsonObjectRequest(url, jsonObject, success, failure);
        VolleyQueue.instance(this).add(postRequest);
        Log.i(TAG,"PostData Completed!");
    }

    private void getNextId() {

        Response.Listener success = new Response.Listener() {
            public int onResponse(Object response) {
                try {
                    id = response.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Response ID: " + response.toString());
                return 0;
            }
        };//Endof onResponse

        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        };

        String url = "http://bismarck.sdsu.edu/hometown/nextid";
        StringRequest getRequest = new StringRequest(url, success, failure);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(getRequest);

        Log.e(TAG,"Firebase | Insertion : "+selectedCountry + selectedState+nickname+password+city+id+year+lat+lng);

        //dbRef.child("Pariksha").setValue(new UserDetailsModel_firebase(selectedCountry,selectedState,nickname,password,city,id,year,lat,lng));
    }

    /*
    firebase
    */

    private void readFromFirebase() {
        dbRef.addListenerForSingleValueEvent(valueEventListener);
    }


    private void insertUserFirebase() {
        UserDetailsModel_firebase userDetailsModel_firebase = null;
        dbRef.child(nickname).setValue(new UserDetailsModel_firebase(selectedCountry,selectedState,nickname,password,city,id,year,lat,lng));
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError == null) {
            Log.i(TAG,"Firebase Insertion SUCCESSFUL!!");
        }
        else {
            Log.e(TAG, databaseError.getMessage());
        }
    }

    /*
    firebase
    */

    //Endof onResponse
    //Original

    public void getRequest() {
        //JsonArrayRequest
        Log.i(TAG, "getReq");
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {

            public int onResponse(JSONArray response) {
                countryListJSON = response;
                Log.e(TAG, "Response is: " + response.toString());
                showCountryList();

                return 0;
            }
        };//Endof onResponse

        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        };

        String url = "http://bismarck.sdsu.edu/hometown/countries";
        JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(getRequest);
    }

    private void showCountryList() {
        for (int i = 0; i < countryListJSON.length(); i++) {
            try {
                countryList.add(i, countryListJSON.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, ">><<>>" + countryList.get(1));
        countryListSpinner = (Spinner) findViewById(R.id.countryListSpinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countryList);
        countryListSpinner.setAdapter(arrayAdapter);
        countryListSpinner.setSelection(countryPosition);

        //onItemSelect listener
        countryListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryPosition = position;
                selectedCountry = countryListSpinner.getSelectedItem().toString();
                Log.e(TAG, "OnItemSelected:::" + selectedCountry + "pos: >>" + countryPosition);
                loadState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void loadState() {

        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {

            public int onResponse(JSONArray response) {
                stateListJSON = response;
                Log.e(TAG, "Response is: " + response.toString());
                showStateList();
                return 0;
            }
        };//Endof onResponse

        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        };

        String url = "http://bismarck.sdsu.edu/hometown/states?country=" + selectedCountry;
        JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(getRequest);
    }

    private void showStateList() {
        for (int i = 0; i < stateListJSON.length(); i++) {
            try {
                stateList.add(i, stateListJSON.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, ">><<>>" + stateList.get(1));
        stateListSpinner = (Spinner) findViewById(R.id.stateListSpinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stateList);
        stateListSpinner.setAdapter(arrayAdapter);
        stateListSpinner.setSelection(statePosition);

        //onItemSelect listener
        stateListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statePosition = position;
                selectedState = stateListSpinner.getSelectedItem().toString();
                Log.e(TAG, "OnItemSelected:::" + selectedState);
                //loadState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void onSetLatLongClick(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("nick", nickname);
        startActivity(intent);
    }

    private void isConnectionOK() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.e(TAG, "Success in fetching data");
        } else {
            Log.e(TAG, "Error in fetching data");
        }
    }
}