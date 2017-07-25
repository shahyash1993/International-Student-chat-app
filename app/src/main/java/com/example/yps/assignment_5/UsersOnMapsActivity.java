package com.example.yps.assignment_5;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UsersOnMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String TAG = "mtag";
    JSONArray userListJSON;
    ArrayList<Double> latList = new ArrayList<>();
    ArrayList<Double> longList = new ArrayList<>();
    ArrayList<String> nickList = new ArrayList<>();
    ArrayList<String> countryList = new ArrayList<>();
    ArrayList<String> stateList = new ArrayList<>();
    int totalData = 25;
    private ProgressBar progressBar;

    EditText yearFilterET, stateFilterET, countryFilterET;
    String filterCountry = null, filterState = null, filterYear = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_on_maps);
        Toast.makeText(UsersOnMapsActivity.this, "Loading...", Toast.LENGTH_LONG).show();


        //fetching number of records from server
        /////////Temp
        //getTotalData();

        Log.e(TAG, "TOTAL DATA received: " + totalData);

        //getUserJSON data
        getUserListJson();

        progressBar = (ProgressBar) findViewById(R.id.loadingPB);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(totalData);

        //background THREAD getting STARTED.
        //      MyTask myTask = new MyTask();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(UsersOnMapsActivity.this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

        //Filtering data
        Button filterButton = (Button) findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mMap.clear();

                progressBar.setVisibility(View.VISIBLE);

                Toast.makeText(getApplicationContext(), "Loading..", Toast.LENGTH_LONG).show();

                countryFilterET = (EditText) findViewById(R.id.countryFilterET);
                stateFilterET = (EditText) findViewById(R.id.stateFilterET);
                yearFilterET = (EditText) findViewById(R.id.yearFilterET);

                filterCountry = countryFilterET.getText().toString();
                filterState = stateFilterET.getText().toString();
                filterYear = yearFilterET.getText().toString();

                Log.i(TAG, "Received Filter Info: " + filterCountry + filterState + filterYear);
                getUserListJson();
            }
        });
        //Filtering ENDs.

    }//onCreate ENds

    //fetching UserListJSON
    private void getUserListJson() {

        //fetch data from server
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
                Log.i(TAG,"Success passed");
                new MyTask().execute("startAsync");
                return 0;
            }
        };//Endof onResponse
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        };

        String url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&page=0" + urlExt;

        Log.d(TAG, "URL BEING SENT:::>" + url);
        JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
        RequestQueue queue = Volley.newRequestQueue(UsersOnMapsActivity.this);
        queue.add(getRequest);
        Log.i(TAG,"url being pass end passed");
    }//end of getting userListJSON


    private void getTotalData() {
        Response.Listener success = new Response.Listener() {
            public int onResponse(Object response) {
                try {
                    totalData = Integer.parseInt(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Response ID: " + response.toString());
                return 0;
            }
        };//Endof onResponse

        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error in response : "+error.toString());
            }
        };

        String url = "http://bismarck.sdsu.edu/hometown/nextid";
        StringRequest getRequest = new StringRequest(url, success, failure);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(getRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Log.i(TAG,"before map: Size of nick: "+nickList.size()+" size of Lat/long: "+latList.size()+" / "+longList.size());
        for(int i=0; i<latList.size(); i++){
            Log.d(TAG,"while making map: nick:"+nickList.get(i)+"lat/long :"+latList.get(i)+" / "+longList.get(i));

            LatLng sydney = new LatLng(Double.parseDouble(latList.get(i).toString()),Double.parseDouble(longList.get(i).toString()) );
            mMap.addMarker(new MarkerOptions().position(sydney).title(nickList.get(i)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            Log.i(TAG,"Added Marker was: #"+(i+1));
        }
        Log.d(TAG,"After mapReady | size of nick"+nickList.size());
    }

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
            Geocoder locator = new Geocoder(UsersOnMapsActivity.this);
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

                Geocoder locator = new Geocoder(UsersOnMapsActivity.this);
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

            if(mMap != null)
                mMap.clear();

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);

            mapFragment.getMapAsync(UsersOnMapsActivity.this);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(UsersOnMapsActivity.this, "Everything Done", Toast.LENGTH_SHORT).show();
            Log.e(TAG,"POST EXECUTION");

            if(mMap != null)
                mMap.clear();

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);

            mapFragment.getMapAsync(UsersOnMapsActivity.this);

            progressBar.setVisibility(View.INVISIBLE);
            //setProgressBarVisibility(false);
        }

    }//end AsyncTask
}//End Activity
