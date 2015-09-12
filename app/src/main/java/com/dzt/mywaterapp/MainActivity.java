package com.dzt.mywaterapp;

import android.graphics.Color;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setMap();
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
            case R.id.action_settings:
                Toast.makeText(this, "new activity with ifffffff", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        map.addMarker(new MarkerOptions().position(latLng).title("you clciked here"));
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            LatLng latLng = new LatLng(latitude, longitude);

            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.addMarker(new MarkerOptions().position(latLng).title("you are here"));

            map.animateCamera(CameraUpdateFactory.zoomTo(16));

            drawPolygonTest();
        }
    }

    private void drawPolygonTest() {

        Polygon polygon = map.addPolygon(new PolygonOptions()
                .add(new LatLng(31.858571, -116.620207), new LatLng(31.857183, -116.618897), new LatLng(31.859367, -116.617071), new LatLng(31.859967, -116.618974))
                .strokeColor(Color.GREEN)
                .fillColor(0x3F00FF00)
                .strokeWidth(2));
    }

    private LatLng [] convertJSONPointsToLatLng(JSONArray jsonPoints) throws JSONException {
        int l = jsonPoints.length();
        LatLng [] latLngPoints = new LatLng[l];
        for (int i = 0; i< l; i++) {
            double lat = jsonPoints.getJSONObject(i).getDouble("lat");
            double lng = jsonPoints.getJSONObject(i).getDouble("lng");
            latLngPoints[i] = new LatLng(lat, lng);
        }
        return latLngPoints;
    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
