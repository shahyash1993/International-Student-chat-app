package com.example.yps.assignment_5;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DisplayUserListActivity extends AppCompatActivity {

    String TAG = "mtag";
    String nickname = "New User";

    JSONArray userListJSON;
    ListView userListView, userListView2;
    public static int pageCounter = 0;

    TableLayout tableLayout;
    private DatabaseHelper namesHelper;
    String TABLE_NAME = "userdata";
    int DB_RECORD_COUNT = 0;
    SQLiteDatabase db;
    ArrayList<ArrayList<String>> userList = new ArrayList<ArrayList<String>>();
    Boolean firstTime = true;

    public int nextIdServer = 0;
    public int maxIdServer = 0;
    public int minIdServer = 0;
    public int minIdDB = 0;
    public int maxIdDB= 0;

    String filteredString;

    SharedPreferences settings;
    public static final String PREFS_NAME = "MyPrefsFile";

    protected Handler handler;

    private List<UserListModel> userModelList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserListAdapter userListAdapter;
    TextView welcomeTV;

    final String urlDefault = "http://bismarck.sdsu.edu/hometown/users?reverse=true&page=0";

    //    RecyclerView.LayoutManager mLayoutManager;
    private LinearLayoutManager mLayoutManager;

    EditText yearFilterET1, stateFilterET1, countryFilterET1;
    String filterCountry = null, filterState = null, filterYear = null;

    @Override
    protected void onStop() {
        super.onStop();
        db.close();
    }

    private void prepareUserListRecycler() {

        //userModelList.add(new UserListModel("amm", "desh", "rajya", "2015"));

        for (int i = 0; i < userList.size(); i++) {
            userModelList.add(new UserListModel(userList.get(i).get(0),
                    userList.get(i).get(4),
                    userList.get(i).get(1),
                    userList.get(i).get(3)));
        }

        userListAdapter.notifyDataSetChanged();
        userListAdapter.setLoaded();    //new change
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table);

        fetchNextId();    //fetches nextId from server

        settings = this.getSharedPreferences(PREFS_NAME, 0);
        //settings = PreferenceManager.getDefaultSharedPreferences(this);
//        settings = getSharedPreferences(PREFS_NAME, 0);

        //get nickname and set welcome message
        welcomeTV = (TextView) this.findViewById(R.id.welcomeTV);
        nickname = settings.getString("nickNameLogin", nickname);    //Enter Nickname
        Log.i(TAG,"DisplayUsers | gettin nickname: "+nickname);
        welcomeTV.setText("Hello "+nickname);

        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        namesHelper = new DatabaseHelper(this);

        handler = new Handler();

        //recycler View stuff
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
       recyclerViewSetUp();
        //END recycler View stuff
/*
        new DB Stuff
*/
        db = namesHelper.getWritableDatabase();
        boolean isDbEmpty = isDbEmpty();

        Log.e(TAG, "isDbEmpty: " + isDbEmpty);

        if (isDbEmpty) {
            fetchFromServer(urlDefault);
            //Don't write anything here...
        } else {
            final Runnable r = new Runnable() {
                public void run() {
                    Log.e(TAG,"Waiting for nextId");
                    fetchUnfilteredDbData();
                    prepareUserListRecycler();

                    if(firstTime){
                        Log.e(TAG,"First TIme::");

                        firstTime = false;
                        userListAdapter.clearData();

                        fetchFromServer(urlDefault);
                    }
                }
            };
            handler.postDelayed(r, 2000);
        }
/*
        END new DB stuff
*/
        pageCounter = 0;

/*
        Button & Listners
*/

        //onLoadMoreListener

        userListAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Toast.makeText(DisplayUserListActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
                //add null , so the adapter will check view_type and show progress bar at bottom
                userModelList.add(null);
                userListAdapter.notifyItemInserted(userModelList.size() - 1);


                //handler was here
                userModelList.remove(userModelList.size() - 1);
                userListAdapter.notifyItemRemoved(userModelList.size());


                //userList = new ArrayList<ArrayList<String>>();
                pageCounter++;
                String url;
                if(filteredString!=null && !filteredString.equals("")){
                    url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&page=" + pageCounter+filteredString;
                }
                else{
                    url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&page=" + pageCounter;
                }

                fetchFromServer(url);
            }//End of loadListener
        });

        //display Button
        Button displayButton = (Button) findViewById(R.id.displayButton);
        displayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                db = namesHelper.getWritableDatabase();

                String query1 = "SELECT * FROM " + TABLE_NAME + ";";
                Cursor result1 = db.rawQuery(query1, null);

                int total = result1.getCount();
                Log.i(TAG, "Total: " + total);

                if (result1.moveToFirst()) {
                    do {
                        Log.e(TAG, "#0: " + result1.getInt(0)
                                + "#1: " + result1.getString(1)
                                + "#2: " + result1.getString(2)
                                + "#3: " + result1.getString(3)
                                + "#4: " + result1.getString(4)
                                + "#5: " + result1.getString(5)
                        );
                    }
                    while (result1.moveToNext());
                }

                Log.i(TAG, "Everything Displayed");
            }
        });

        //testing purpose
        //delete button
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                db = namesHelper.getWritableDatabase();

                String query = "DELETE FROM " + TABLE_NAME + ";";
                Cursor result = db.rawQuery(query, null);
                result.moveToFirst();
                Log.i(TAG, "Everything Deleted");
            }
        });

        //Filtering stuff
        Button filterButton1 = (Button) findViewById(R.id.filterButton1);
        filterButton1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                pageCounter = 0;
                //progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Loading..", Toast.LENGTH_LONG).show();

                countryFilterET1 = (EditText) findViewById(R.id.countryFilterET1);
                stateFilterET1 = (EditText) findViewById(R.id.stateFilterET1);
                yearFilterET1 = (EditText) findViewById(R.id.yearFilterET1);

                filterCountry = countryFilterET1.getText().toString();
                filterState = stateFilterET1.getText().toString();
                filterYear = yearFilterET1.getText().toString();

                fetchFilteredFromServer();
                //getUsers();
                Log.i(TAG, "Received Filter Info: " + filterCountry + filterState + filterYear);

                //getLatLong(filterCountry,filterState,filterYear);
                //getUserListJson();
            }
        });

        //old Buttons
        Button usersOnMapButton = (Button) findViewById(R.id.usersOnMapButton);
        Button nextPageButton = (Button) findViewById(R.id.nextPage);
        Button previousPageButton = (Button) findViewById(R.id.previousPage);

        //button listeners
        usersOnMapButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), UsersOnMapsActivity.class);
                startActivity(intent);
            }
        });


        //previous Button listener
        previousPageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i(TAG, "nextPageClicked!");

                //next page functionality
                pageCounter--;
                userList = new ArrayList<ArrayList<String>>();
                String url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&page=" + pageCounter;
                //fetchFromServer(url);
            }//onClick END
        });

        //next Button listener
        nextPageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i(TAG, "nextPageClicked!");

                //next page functionality
                pageCounter++;
                userList = new ArrayList<ArrayList<String>>();
                String url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&page=" + pageCounter;
                fetchFromServer(url);
            }//onClick END
        });
        //db.close();
    }//end of ONCREATE()

/*
    Logout
*/
    public void onLogoutClick_DisplayUserList(View view){
        //clearing shared pref
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.apply();

        //redirecting to firstActivity
        Intent intent = new Intent(DisplayUserListActivity.this,PrimaryMainActivity.class);
        startActivity(intent);
        finish();
    }
    //Logout
    private void recyclerViewSetUp() {
        recyclerView.invalidate();
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        userListAdapter = new UserListAdapter(userModelList, recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(userListAdapter);
    }


    //fetching from DATA BASE
    private void fetchUnfilteredDbData() {
        db = namesHelper.getWritableDatabase();

        //getMinMaxIdDb();    //new

        if (minIdDB==0){
            minIdDB = nextIdServer;
        }   //new

        String query1 = "SELECT * FROM " + TABLE_NAME + " where _id <= "+maxIdDB+" order by _id desc limit 25;";
        Cursor result1 = db.rawQuery(query1, null);

        int total = result1.getCount();
        Log.i(TAG, "Total Data received: " + total + "| With query: "+query1);

        if (result1.moveToFirst()) {
            do {
                ArrayList<String> userDetailsList = new ArrayList<String>();

                userDetailsList.add(result1.getString(result1.getColumnIndex("nickname")));
                userDetailsList.add(result1.getString(result1.getColumnIndex("state")));
                userDetailsList.add(result1.getString(result1.getColumnIndex("city")));
                userDetailsList.add(result1.getString(result1.getColumnIndex("year")));
                userDetailsList.add(result1.getString(result1.getColumnIndex("country")));
                userDetailsList.add(result1.getString(result1.getColumnIndex("longitude")));
                userDetailsList.add(result1.getString(result1.getColumnIndex("latitude")));
                userDetailsList.add(result1.getString(result1.getColumnIndex("_id")));
                userDetailsList.add(result1.getString(result1.getColumnIndex("timestamp")));

                userList.add(userDetailsList);

                Log.e(TAG, "#0: " + result1.getInt(0)  //id
                        + "#1: " + result1.getString(1)    //nickname
                        + "#2: " + result1.getString(2)    //long
                        + "#3: " + result1.getString(3)    //lat
                        + "#4: " + result1.getString(4)    //country
                        + "#5: " + result1.getString(5)    //state
                        + "#6: " + result1.getString(6)    //city
                        + "#7: " + result1.getString(7)    //year
                );
            }
            while (result1.moveToNext());
        }

        Log.i(TAG, "Everything Displayed on LOG");
    }

    //fetching from DATA BASE
    private void fetchFilteredDbData() {
        db = namesHelper.getWritableDatabase();

        if (minIdDB==0){
            minIdDB = nextIdServer;
        }   //new

        StringBuilder dbWhereBuilder = new StringBuilder();
        String dbWhere = null;

        if(filterCountry!=null && !filterCountry.equals("")){
            dbWhereBuilder.append(" and country = \""+filterCountry+"\" ");
        }
        if(filterState!=null && !filterState.equals("")){
            dbWhereBuilder.append(" and state = \""+filterState+"\" ");
        }
        if(filterCountry!=null && !filterYear.equals("")){
            dbWhereBuilder.append(" and year = "+filterYear);
        }

        if(dbWhereBuilder == null){
            dbWhere = "";
        }
        else{
            dbWhere = dbWhereBuilder.toString();
        }

        //maxIdDb   select min(_id) from (select _id from userdata where _id<23901 order by _id desc limit 25)
        String query1 = "SELECT * FROM " + TABLE_NAME + " where _id <= "+maxIdDB+dbWhere+" order by _id desc limit 25;";
        Cursor result1 = db.rawQuery(query1, null);
        Log.i(TAG,"Query Fired | FilterDB: "+query1);

        int total = result1.getCount();
        Log.i(TAG, "Total Data received: " + total + "| With query: "+query1);

        if (result1.moveToFirst()) {
            do {
                ArrayList<String> userDetailsList = new ArrayList<String>();

                userDetailsList.add(result1.getString(result1.getColumnIndex("nickname")));
                userDetailsList.add(result1.getString(result1.getColumnIndex("state")));
                userDetailsList.add(result1.getString(result1.getColumnIndex("city")));
                userDetailsList.add(result1.getString(result1.getColumnIndex("year")));
                userDetailsList.add(result1.getString(result1.getColumnIndex("country")));
                userDetailsList.add(result1.getString(result1.getColumnIndex("longitude")));
                userDetailsList.add(result1.getString(result1.getColumnIndex("latitude")));
                userDetailsList.add(result1.getString(result1.getColumnIndex("_id")));
                userDetailsList.add(result1.getString(result1.getColumnIndex("timestamp")));

                userList.add(userDetailsList);

                Log.e(TAG, "#0: " + result1.getInt(0)  //id
                        + "#1: " + result1.getString(1)    //nickname
                        + "#2: " + result1.getString(2)    //long
                        + "#3: " + result1.getString(3)    //lat
                        + "#4: " + result1.getString(4)    //country
                        + "#5: " + result1.getString(5)    //state
                        + "#6: " + result1.getString(6)    //city
                        + "#7: " + result1.getString(7)    //year
                );
            }
            while (result1.moveToNext());
        }

        Log.i(TAG, "Everything Displayed on LOG");
    }

    private void prepareUserList() throws JSONException {
        userList.clear();
        double longi,lati;
        for (int i = 0; i < userListJSON.length(); i++) {

            ArrayList<String> userDetailsList = new ArrayList<String>();
            JSONObject object;
            object = (JSONObject) userListJSON.get(i);

            userDetailsList.add(object.getString("nickname"));
            userDetailsList.add(object.getString("state"));
            userDetailsList.add(object.getString("city"));
            userDetailsList.add(object.getString("year"));
            userDetailsList.add(object.getString("country"));

            longi = Double.parseDouble(object.getString("longitude"));
            lati = Double.parseDouble(object.getString("latitude"));

            if (longi == 0.0 && lati == 0.0) {
                //Geocode starts
                geocode(longi,lati,nickname);

            }//end of geocodeIT
            else {
                //checking whether city is on water or not
                Log.i(TAG, "Calling is it true for" + object.getString("nickname"));

                //ISitTRUE
                isItTrue(longi,lati);
            }


            userDetailsList.add(object.getString("longitude"));
            userDetailsList.add(object.getString("latitude"));
            userDetailsList.add(object.getString("id"));
            userDetailsList.add(object.getString("time-stamp"));

            //Log.i(TAG,"userDetailList: "+userDetailsList);
            userList.add(userDetailsList);
            Log.i(TAG,"userList: NickName: 1st: "+userList.get(0).get(0)+" && last: "+userList.get(i).get(0));
        }
    }

    private void insertInDb() {
        ContentValues newName;

        for (int i = 0; i < userList.size(); i++) {
            newName = new ContentValues();
            newName.put("nickname", userList.get(i).get(0));
            newName.put("state", userList.get(i).get(1));
            newName.put("city", userList.get(i).get(2));
            newName.put("year", userList.get(i).get(3));
            newName.put("country", userList.get(i).get(4));
            newName.put("longitude", userList.get(i).get(5));
            newName.put("latitude", userList.get(i).get(6));
            newName.put("_id", userList.get(i).get(7));
            newName.put("timestamp", userList.get(i).get(8));

            db.insert(TABLE_NAME, null, newName);
        }
        Log.e(TAG, "DB insertion Successful!");
    }

    private void fetchFromServer(String url) {
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public int onResponse(JSONArray response) throws JSONException {
                userListJSON = response;
                Log.i(TAG, "Success Response is: #1" + response.get(0).toString());

                getMinMaxIdServer();
                getMinMaxIdDb();
                compareServerDb();
                /*prepareUserList();        //moved to if fetching occurs from server and not db
                insertInDb();*/
                showUserList(); //will be deleted soon
                prepareUserListRecycler();
                Log.e(TAG,"fetchEnd:: MinIdDB & MaxIdDb: "+minIdDB+" "+maxIdDB);
                Log.e(TAG,"fetchEnd:: MinId Server & MaxId Server: "+minIdServer+" "+maxIdServer);

                return 0;
            }
        };//Endof onResponse
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        };

        Log.i(TAG, "For Url fetchFromUrl: " + url);
        JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(getRequest);
    }

    private void compareServerDb() throws JSONException {
        if(minIdDB == minIdServer && maxIdDB == maxIdServer){
            fetchUnfilteredDbData();
        }
    /*    else if(maxIdDB>=minIdServer){
            maxIdDB = minIdDB;
            //getMinIdDbConflict();
            prepareUserList();
            insertInDb();
        }*/
        else{
            prepareUserList();
            insertInDb();
        }
    }

    private void compareFilteredServerDb() throws JSONException {
        if(minIdDB == minIdServer && maxIdDB == maxIdServer){
            fetchFilteredDbData();
        }
   /*     else if(maxIdDB>=minIdServer){
            maxIdDB = minIdDB;
            getMinIdDbConflict();
            prepareUserList();
            insertInDb();
        }*/
        else{
            prepareUserList();
            insertInDb();
        }
    }

/*    private void getMinIdDbConflict() {
        //minIdDb   SELECT Min(_id) FROM (select _id from userdata where _id< 23965 order by _id desc limit 25)
        String query1 = "SELECT MIN(_id) FROM ( select _id from  " + TABLE_NAME + " where _id <= "+maxIdDB+"  order by _id desc limit 25);";
        Cursor result1 = db.rawQuery(query1, null);

        result1.moveToFirst();
        minIdDB = result1.getInt(0);

        Log.i(TAG, "MinIdDb and MaxIdDb Changed to: "+minIdDB+" & "+maxIdDB);
    }*/



    private void getFilteredMinMaxIdDb() {
        db = namesHelper.getWritableDatabase();
/*
        if(minIdDB == 0){
            minIdDB = nextIdServer;
            if(maxIdDB == 0){*/

                StringBuilder dbWhereBuilder = new StringBuilder();
                String dbWhere = null;

                if(filterCountry!=null && !filterCountry.equals("")){
                    dbWhereBuilder.append(" and country = \""+filterCountry+"\" ");
                }
                if(filterState!=null && !filterState.equals("")){
                    dbWhereBuilder.append(" and state = \""+filterState+"\" ");
                }
                if(filterCountry!=null && !filterYear.equals("")){
                    dbWhereBuilder.append(" and year = "+filterYear);
                }

                if(dbWhereBuilder == null){
                    dbWhere = "";
                }
                else{
                    dbWhere = dbWhereBuilder.toString();
                }

                //maxIdDb   select min(_id) from (select _id from userdata where _id<23901 order by _id desc limit 25)

                String query1 = "SELECT MAX(_id) FROM ( select _id from " + TABLE_NAME + " where _id <= "+maxIdServer+dbWhere+" order by _id desc limit 25);";
                Cursor result1 = db.rawQuery(query1, null);
                Log.i(TAG,"Query Fired | FilterDB: "+query1);

                result1.moveToFirst();
                maxIdDB = result1.getInt(0);

                //minIdDb
                query1 = "SELECT MIN(_id) FROM ( select _id from  " + TABLE_NAME + " where _id <= "+maxIdServer+dbWhere+"  order by _id desc limit 25);";
                result1 = db.rawQuery(query1, null);

                result1.moveToFirst();
                minIdDB = result1.getInt(0);
     /*       }//ifEnd
        }//ifEnd*/
        Log.i(TAG, "MinIdDb and MaxIdDb fetched are: "+minIdDB+" & "+maxIdDB);

    }

    private void getMinMaxIdDb() {
        db = namesHelper.getWritableDatabase();

/*        if(minIdDB == 0){
            minIdDB = nextIdServer;
            if(maxIdDB == 0){*/

                //maxIdDb   select min(_id) from (select _id from userdata where _id<23901 order by _id desc limit 25)
                String query1 = "SELECT MAX(_id) FROM ( select _id from " + TABLE_NAME + " where _id <= "+maxIdServer+" order by _id desc limit 25);";
                Cursor result1 = db.rawQuery(query1, null);

                result1.moveToFirst();
                maxIdDB = result1.getInt(0);

                //minIdDb
                query1 = "SELECT MIN(_id) FROM ( select _id from  " + TABLE_NAME + " where _id <= "+maxIdServer+"  order by _id desc limit 25);";
                result1 = db.rawQuery(query1, null);

                result1.moveToFirst();
                minIdDB = result1.getInt(0);
    /*        }//ifEnd
    }//ifEnd*/
        Log.i(TAG, "MinIdDb and MaxIdDb fetched are: "+minIdDB+" & "+maxIdDB);
    }

    private void getMinMaxIdServer() throws JSONException {

        JSONObject object;
        Log.i(TAG, "getMinMaxServer: userListJson SIZE: " + userListJSON.length());
        if (userListJSON.length() >= 25) {
            object = (JSONObject) userListJSON.get(userListJSON.length() - 25);
            maxIdServer = Integer.parseInt(object.getString("id"));

            object = (JSONObject) userListJSON.get(userListJSON.length() - 1);
            minIdServer = Integer.parseInt(object.getString("id"));

            Log.i(TAG, "Server: Min & Max ID fetched are: " + minIdServer + " & " + maxIdServer);
        }
    }

    private boolean isDbEmpty() {
        db = namesHelper.getWritableDatabase();

        String query = "SELECT count(*) FROM " + TABLE_NAME + ";";
        Cursor result = db.rawQuery(query, null);
        result.moveToFirst();
        DB_RECORD_COUNT = result.getInt(0);
        Log.i(TAG, "Total Data in DB: " + DB_RECORD_COUNT);

        return (DB_RECORD_COUNT == 0);
    }

    private void fetchNextId() {

        Response.Listener success = new Response.Listener() {
            public int onResponse(Object response) {
                nextIdServer = Integer.parseInt(response.toString());
                Log.i(TAG, "next ID: " + response.toString());
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
    }



    private void fetchFilteredFromServer() {
        Log.i(TAG, "fetchFilteredFromServer()");

        StringBuilder urlExtBuilder = new StringBuilder();
        String urlExt = null;

        if (filterCountry != null && !filterCountry.equals("")) {
            if (filterCountry.contains(" "))
                filterCountry.replace(" ", "%20");
            urlExtBuilder.append("&country=" + filterCountry);
        }
        if (filterState != (null) && !filterState.equals("")) {
            urlExtBuilder.append("&state=" + filterState);
        }
        if (filterYear != (null) && !filterYear.equals("")) {
            urlExtBuilder.append("&year=" + filterYear);
        }
        if (urlExtBuilder == null) {
            urlExt = "";
        } else {
            urlExt = urlExtBuilder.toString();
        }

        filteredString = urlExt;

        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public int onResponse(JSONArray response) throws JSONException {
                userListJSON = response;
                Log.i(TAG, "Success Response is: #1" + response.get(0).toString());

                userList.clear();
                userModelList.clear();
                userListAdapter.clearData();
                getMinMaxIdServer();
                getFilteredMinMaxIdDb();    //not ready
                compareFilteredServerDb();
                /*prepareUserList();        //moved to if fetching occurs from server and not db
                insertInDb();*/
                showUserList(); //will be deleted soon
                prepareUserListRecycler();
                Log.e(TAG,"fetchEnd:: MinIdDB & MaxIdDb: "+minIdDB+" "+maxIdDB);
                Log.e(TAG,"fetchEnd:: MinId Server & MaxId Server: "+minIdServer+" "+maxIdServer);

                return 0;
            }
        };//Endof onResponse
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        };
        String url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&page="+pageCounter+urlExt;
        Log.i(TAG, "For Url fetchFromUrl: " + url);
        JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(getRequest);
    }



    //Useless Old code
    private void getUsers() {
        Log.i(TAG, "getReq");

        StringBuilder urlExtBuilder = new StringBuilder();
        String urlExt = null;

        if (filterCountry != null && !filterCountry.equals("")) {
            if (filterCountry.contains(" "))
                filterCountry.replace(" ", "%20");
            urlExtBuilder.append("&country=" + filterCountry);
        }
        if (filterState != (null) && !filterState.equals("")) {
            urlExtBuilder.append("&state=" + filterState);
        }
        if (filterYear != (null) && !filterYear.equals("")) {
            urlExtBuilder.append("&year=" + filterYear);
        }
        if (urlExtBuilder == null) {
            urlExt = "";
        } else {
            urlExt = urlExtBuilder.toString();
        }

        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public int onResponse(JSONArray response) throws JSONException {
                userListJSON = response;
                Log.e(TAG, "Success Response is: " + response.get(0).toString());
                showUserList();

                return 0;
            }
        };//Endof onResponse
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        };
        String url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&page="+pageCounter+urlExt;
        Log.i(TAG,"Url: "+url);
        JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(getRequest);
    }

        private void showUserList() throws JSONException {
            //prepareUserList();

            //new
            tableLayout.removeAllViews();
            for (int i=0;i<userList.size();i++){

            View tableRow = LayoutInflater.from(this).inflate(R.layout.table_item,null,false);
            TextView tableNick  = (TextView) tableRow.findViewById(R.id.tableNick);
            TextView tableState  = (TextView) tableRow.findViewById(R.id.tableState);
            TextView tableCountry  = (TextView) tableRow.findViewById(R.id.tableCountry);
            TextView tableYear= (TextView) tableRow.findViewById(R.id.tableYear);

            tableNick.setText(userList.get(i).get(0));
            tableState.setText(userList.get(i).get(1));
            tableCountry.setText(userList.get(i).get(2));
            tableYear.setText(userList.get(i).get(3));
            tableLayout.addView(tableRow);
        }

    }//end show user list





        /*
    *
    *ASYNC task
    *
    * */

    public class MyTask extends AsyncTask<String, Integer, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                Log.e(TAG, "Success Response userListJSON size: " + userListJSON.length());

                JSONObject object;
                latList = new ArrayList<>();
                longList = new ArrayList<>();
                nickList = new ArrayList<>();
                countryList = new ArrayList<String>();
                stateList = new ArrayList<String>();

                String stateName, countryName, nickname;
                Double latitude, longitude;

                for (int j = 0; j < userListJSON.length(); j++) {
                    object = (JSONObject) userListJSON.get(j);

                    latitude = Double.parseDouble(object.get("latitude").toString());
                    longitude = Double.parseDouble(object.get("longitude").toString());
                    countryName = object.get("country").toString();
                    stateName = object.get("state").toString();
                    nickname = object.get("nickname").toString();

                    if (longitude == 0.0 && latitude == 0.0) {
                        //Geocode starts
                        geocode(longitude,latitude,nickname,stateName,countryName);

                    }//end of geocodeIT
                    else {
                        //checking whether city is on water or not
                        Log.i(TAG, "Calling is it true for" + nickname);

                        //ISitTRUE
                        isItTrue(longitude,latitude,nickname,stateName,countryName);
                    }

                    Log.i(TAG, "Publishing Progress Bar: " + (j + 1));
                    publishProgress(j + 1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "nick Size: " + nickList.size() + "| ");
            return null;
        }

        private void isItTrue(Double longitude, Double latitude, String nickname, String stateName, String countryName) {
            Geocoder locator = new Geocoder(DisplayUserListActivity.this);
            try {
                List<Address> state = locator.getFromLocation(latitude, longitude, 1);

                for (int i = 0; i < state.size(); i++) {
                    Log.i(TAG, "Geocode: Country: " + state.get(i).getCountryName() + " || state:" + state.get(i).getLocality() +
                            "|| subLocal:" + state.get(i).getSubLocality() + " || subAdmin: " + state.get(i).getSubAdminArea());

                    if (state.get(i).getCountryName() != null &&
                            state.get(i).getLocality() != null &&
                            state.get(i).getCountryName().equalsIgnoreCase(countryName) &&
                            state.get(i).getLocality().equalsIgnoreCase(stateName)) {
                        latList.add(latitude);
                        longList.add(longitude);
                        nickList.add(nickname);
                        countryList.add(countryName);
                        stateList.add(stateName);
                    } else {
                        Log.e(TAG, "Wrong Data found! Culprit: " + nickname);
                        geocode(longitude,latitude,nickname,stateName,countryName);
                    }
                }
            } catch (Exception error) {
                Log.e(TAG, "Address lookup Error", error);
            }
        }

        private void geocode(Double longitude, Double latitude, String nickname, String stateName, String countryName) {
            // /////////GeocodeIt

            Log.i(TAG, "Calling geocode for" + nickname);

            Geocoder locator = new Geocoder(DisplayUserListActivity.this);
            try {
                List<Address> state = locator.getFromLocationName(stateName + ", " + countryName, 1);

                for (Address stateLocation : state) {
                    if (stateLocation.hasLatitude())
                        latList.add(stateLocation.getLatitude());   //pos removed
                    if (stateLocation.hasLongitude())
                        longList.add(stateLocation.getLongitude()); //pos removed

                    Log.d(TAG, "geocodeIT for: " + nickname + ":: lat/longLIST: " + latList + " / " + longList);
                    nickList.add(nickname);
                    countryList.add(countryName);
                    stateList.add(stateName);
                }
            } catch (Exception error) {
                Log.e(TAG, "Address lookup Error", error);
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //show data
            super.onProgressUpdate(values);
            Log.i(TAG,"ProgressUpdate: "+values[0]+" out of "+totalData);
            progressBar.setProgress(values[0]+1);
        }

        @Override
        protected void onPreExecute() {
            Log.e(TAG,"PREe EXECUTION");

          /*  if(mMap != null)
                mMap.clear();

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);

            mapFragment.getMapAsync(UsersOnMapsActivity.this);*/
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(DisplayUserListActivity.this, "Everything Done", Toast.LENGTH_SHORT).show();
            Log.e(TAG,"POST EXECUTION");

          /*  if(mMap != null)
                mMap.clear();

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);

            mapFragment.getMapAsync(Disp.this);*/

            progressBar.setVisibility(View.INVISIBLE);
            //setProgressBarVisibility(false);
        }

    }//end AsyncTask
}//end of activity
