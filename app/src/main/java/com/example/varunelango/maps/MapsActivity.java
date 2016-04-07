package com.example.varunelango.maps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    String TextData;
    String[] SearchName=new String[10];
    Double[] SearchLatitude=new Double[10];
    Double[] SearchLongitude=new Double[10];
    LatLng latlng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        final EditText ST;
        ST = (EditText)findViewById(R.id.SearchText);
        ImageButton SB = (ImageButton) findViewById(R.id.SearchButton);

        SB.setOnClickListener(
                new ImageButton.OnClickListener(){
                    public void onClick (View v)
                    {
                        String SearchText = ST.getText().toString();
                        int i=0,flag=0;

                        for(;SearchName[i]!=null;i++)
                        {
                            if(SearchText.equalsIgnoreCase(SearchName[i])) {
                                Toast.makeText(MapsActivity.this, "Destination Found: " + SearchText, Toast.LENGTH_LONG).show();
                                latlng= new LatLng(SearchLatitude[i],SearchLongitude[i]);
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,20));
                                flag=1;
                            }
                        }
                        if(flag==0)
                        {
                            Toast.makeText(MapsActivity.this, "Destination not found", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

        Bundle EditData = getIntent().getBundleExtra("Bundle");
        if(EditData==null){
            return;
        }
        TextData = EditData.getString("Data");

        setUpMapIfNeeded();



    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        GetCoord g = new GetCoord(MapsActivity.this, getApplicationContext());
        g.execute();
        mMap.setMyLocationEnabled(true);



    }


    class GetCoord extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog dialog;
        JSONArray jsonArray = null;
        Context context;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Fetching Coordinates Data...");
            dialog.setCancelable(false);
            dialog.show();
        }

        public GetCoord(Activity activity, Context c) {
            this.context = c;
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String jsonstring = null;
            HttpClient client = new DefaultHttpClient();
            try {
                HttpGet request = new HttpGet(MainActivity.URL+TextData);
                HttpResponse response = client.execute(request);
                HttpEntity httpEntity = response.getEntity();
                if (httpEntity != null) {
                    InputStream inputStream = httpEntity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    inputStream.close();
                    jsonstring = sb.toString();
                    Log.i("json", jsonstring);
                    jsonArray = new JSONArray(jsonstring);
                }
            } catch (IOException e) {
                Log.e("Buffer Error", "Error parsing result " + e.toString());
            } catch (JSONException e) {
                Log.e("JSON Error", "Error parsing result " + e.toString());
            }
            return (jsonstring != null && jsonArray != null);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(dialog.isShowing())
                dialog.dismiss();
            if (aBoolean) {
                try {

                    int i=0;

                    while(jsonArray.getJSONObject(i)!=null) {
                        JSONObject jObj = jsonArray.getJSONObject(i);
                        String names = jObj.getString("name");
                        Double longitudes = jObj.getDouble("longitude");
                        Double latitudes = jObj.getDouble("latitude");

                        SearchName[i]=names;
                        SearchLatitude[i]=latitudes;
                        SearchLongitude[i]=longitudes;

                        mMap.addMarker(new MarkerOptions().position(new LatLng(latitudes, longitudes)).title(names).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        i++;
                    }




                } catch (JSONException e) {
                    Log.i("JSON", " Json exception in on post Execute");
                }
            }
            else {
                Log.i("Check","Poor");
                Toast.makeText(getApplicationContext(), "Poor connectivity. Failed to update!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
