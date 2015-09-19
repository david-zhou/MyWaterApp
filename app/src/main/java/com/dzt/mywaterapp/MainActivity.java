package com.dzt.mywaterapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

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
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private List<Marker> markerList;
    private List<Integer> zonaList;
    private List<String> zonaName;
    private String currentMarker = "";

    //private String json = "[{'zonaID':1,'nombre':'riviera','disponible':'si','horario':{'inicio':'00:00:01 12/09/2015','final':'19:00:00 12/09/2015'},'poligono':[{'lat':31.858571,'lng':-116.620207},{'lat':31.857183,'lng':-116.618897},{'lat':31.859367,'lng':-116.617071},{'lat':31.859967,'lng':-116.618974}]},{'zonaID':2,'nombre':'casa del chava','disponible':'no','horario':{'inicio':'16:00:00 12/09/2015','final':'19:00:00 12/09/2015'},'poligono':[{'lat':31.864324,'lng':-116.610125},{'lat':31.863895,'lng':-116.608902},{'lat':31.864852,'lng':-116.608478},{'lat':31.865280,'lng':-116.609634}]}]";
    //private JSONArray jsonArray = new JSONArray(json);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeLists();
        setMap();
    }

    private void initializeLists() {
        markerList = new ArrayList<Marker>();
        zonaList = new ArrayList<Integer>();
        zonaName = new ArrayList<String>();
    }

    private void setMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.Calculate:
                SharedPreferences myPreferences = getSharedPreferences("infoUsuario", Context.MODE_PRIVATE);
                String sUsuario = myPreferences.getString("nombreUsuario","");

                if(sUsuario == ""){
                    startActivity(new Intent(this,AltaUsuario.class));
                    this.finish();
                }
                else {
                    startActivity(new Intent(this, Estadistica.class));
                    this.finish();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        //map.addMarker(new MarkerOptions().position(latLng).title("you clciked here"));
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            LatLng latLng = new LatLng(latitude, longitude);

            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            //map.addMarker(new MarkerOptions().position(latLng).title("you are here"));

            map.animateCamera(CameraUpdateFactory.zoomTo(16));

            callAPI();

            //drawPolygons(json);
        }
    }

    private void callAPI() {
        URLpetition petition = new URLpetition("get zones times");
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.ip));
        sb.append("zonas");
        petition.execute(sb.toString());
    }



    private void drawPolygons(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);

            int l = jsonArray.length();
            Log.i("JSONArray.length()",""+l);

            for (int i = 0; i < l; i++) {
                int zonaId = jsonArray.getJSONObject(i).getInt("ZonaId");
                String name = jsonArray.getJSONObject(i).getString("Nombre");
                String inicioAgua = jsonArray.getJSONObject(i).getJSONObject("ZonaHorarioRelation").getString("FechaInicial");
                String finalAgua = jsonArray.getJSONObject(i).getJSONObject("ZonaHorarioRelation").getString("FechaFinal");

                String [] separatedTimezone = inicioAgua.split("T");
                String [] separatedTime = separatedTimezone[1].split("Z");
                String inicioAguaFormatted = separatedTime[0] + " de " + separatedTimezone[0];

                separatedTimezone = finalAgua.split("T");
                separatedTime = separatedTimezone[1].split("Z");
                String finalAguaFormatted = separatedTime[0] + " de " + separatedTimezone[0];

                String disp = jsonArray.getJSONObject(i).getString("Disponible");
                JSONArray poligono = jsonArray.getJSONObject(i).getJSONArray("ZonaPoligonoRelation");
                LatLng [] latLngPoints = convertJSONPointsToLatLng(poligono);
                if (disp.equals("si")) {
                    Polygon polygon = map.addPolygon(new PolygonOptions().add(latLngPoints).strokeColor(Color.GREEN).fillColor(0x3F00FF00).strokeWidth(2));
                    LatLng center = getPolygonCenterPoint(latLngPoints);

                    Marker marker = map.addMarker(new MarkerOptions().position(center).title("El agua se acaba " +finalAguaFormatted));
                    markerList.add(marker);
                    zonaList.add(zonaId);
                    zonaName.add(name);
                } else {
                    Polygon polygon = map.addPolygon(new PolygonOptions().add(latLngPoints).strokeColor(Color.RED).fillColor(0x3FFF0000).strokeWidth(2));
                    LatLng center = getPolygonCenterPoint(latLngPoints);
                    Marker marker = map.addMarker(new MarkerOptions().position(center).title("El agua llega " +inicioAguaFormatted));
                    markerList.add(marker);
                    zonaList.add(zonaId);
                    zonaName.add(name);
                }
            }

        } catch(JSONException e) {
            e.printStackTrace();
        }

        /*
        Polygon polygon = map.addPolygon(new PolygonOptions()
                .add(new LatLng(31.858571, -116.620207), new LatLng(31.857183, -116.618897), new LatLng(31.859367, -116.617071), new LatLng(31.859967, -116.618974))
                .strokeColor(Color.GREEN)
                .fillColor(0x3F00FF00)
                .strokeWidth(2));
                */
    }

    private LatLng [] convertJSONPointsToLatLng(JSONArray jsonPoints) throws JSONException {
        int l = jsonPoints.length();
        LatLng [] latLngPoints = new LatLng[l];
        for (int i = 0; i< l; i++) {
            double lat = jsonPoints.getJSONObject(i).getDouble("Latitud");
            double lng = jsonPoints.getJSONObject(i).getDouble("Longitud");
            latLngPoints[i] = new LatLng(lat, lng);
        }
        return latLngPoints;
    }

    private LatLng getPolygonCenterPoint(LatLng [] polygonPointsList){
        LatLng centerLatLng = null;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0 ; i < polygonPointsList.length ; i++)
        {
            builder.include(polygonPointsList[i]);
        }
        LatLngBounds bounds = builder.build();
        centerLatLng =  bounds.getCenter();

        return centerLatLng;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (int i = 0; i< markerList.size(); i++) {
            if(markerList.get(i).getId().equals(marker.getId())) {
                if (currentMarker.equals("")) {
                    currentMarker = marker.getId();
                } else {
                    if (currentMarker.equals(marker.getId())) {
                        //Toast.makeText(this, "Zona id = " + zonaList.get(i), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, listViewHorarios.class);
                        Bundle myBundle = new Bundle();
                        myBundle.putInt("zonaId",zonaList.get(i));
                        myBundle.putString("zonaName",zonaName.get(i));
                        intent.putExtras(myBundle);
                        startActivity(intent);
                        this.finish();
                        return true;
                    } else {
                        currentMarker = marker.getId();
                    }
                }
            }
        }
        return false;
    }

    private class URLpetition extends AsyncTask<String, Void, String>
    {
        String action;
        public URLpetition(String action)
        {
            this.action = action;
        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            Log.d("url = ", params[0]);
            HttpGet get = new HttpGet(params[0]);
            String retorno="";
            StringBuilder stringBuilder = new StringBuilder();
            try {
                HttpResponse response = client.execute(get);
                HttpEntity entity = response.getEntity();
                //InputStream stream = new InputStream(entity.getContent(),"UTF-8");
                InputStream stream = entity.getContent();
                BufferedReader r = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                String line;
                while ((line= r.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();

            }
            catch(IOException e) {
                Log.d("Error: ", e.getMessage());
            }
            Log.d("Return text = ", retorno);
            return retorno;
        }

        @Override
        protected void onPostExecute(String result) {
            switch (action)
            {
                default:
                    break;
                case "get zones times":
                    drawPolygons(result);
                    break;
            }
        }

        @Override
        protected void onPreExecute() {}
    }
}
