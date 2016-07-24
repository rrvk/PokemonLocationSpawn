package nl.rrvk.pokemonlocationspawn.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import nl.rrvk.pokemonlocationspawn.MainActivity;
import nl.rrvk.pokemonlocationspawn.PermissionUtils;
import nl.rrvk.pokemonlocationspawn.R;
import nl.rrvk.pokemonlocationspawn.model.PokemonModel;
import nl.rrvk.pokemonlocationspawn.utils.ActivityUtils;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MapFragment extends Fragment implements LocationListener, OnMapReadyCallback, OnMarkerClickListener, OnMapClickListener, OnRequestPermissionsResultCallback {
    private static View view;
    private GoogleMap mMap;
    private Marker locationMarker = null;
    private LocationManager locationManager;
    private HashMap<PokemonModel, Marker> pokeMarkers = new HashMap<>();

    private String locationProvider;

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //todo dit nog anders doen
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);
            ActivityUtils.setActionbarTitles((MainActivity) getActivity(), R.string.nav_map, null);
            timerHandler.postDelayed(timerRunnable, 0);

            initMap();
            initLocationManager();

        } catch (InflateException e) {
        }
        return view;
    }

    private void initMap() {
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    private void initLocationManager() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        else {
            //get the location manager
            this.locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            //define the location manager criteria
            Criteria criteria = new Criteria();
            this.locationProvider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(locationProvider);

            //initialize the location
            if (location != null) {
                onLocationChanged(location);
            }
        }
    }

    /*@Override
    public void onLocationChanged(Location location) {
        getPokemonOnLocation(location);
        updateCurrentLocationMarker(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        //set map type
        //todo hier naar kijken
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        enableMyLocation();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            //todo dit allemaal doen bij die andere reqeust dingen
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            Location location = locationManager.getLastKnownLocation(locationProvider);
            //todo vragen als er geen locatie beschikbaar is
            if (location!=null) {
                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                mMap.moveCamera(center);

                CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                mMap.animateCamera(zoom);
                getPokemonOnLocation(location);
            }
        }
    }

    /*private void initLocationAndMarker() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LatLng currentLoc = null;

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; //todo misschien nog een exeption gooien
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //altert gps aanzetten?
            //todo
        } else if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {/*todo*/ /*} else if (!locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {/*todo*/ //}
    // location updater aanzetten
       /* locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        Location l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (l != null) {
            currentLoc = new LatLng(l.getLatitude(), l.getLongitude());
        } else {
            //standard location home
            currentLoc = new LatLng(53.2137957, 6.5638837);
        }
        // uitprinten om te kijken welke die heeft
        //System.out.println(currentLoc);
        //nieuwe marker aanmaken met locatie en title
        MarkerOptions locationMarkerOptions = new MarkerOptions().position(currentLoc).title("Huidige locatie");
        // marker op map zetten als map bestaad
        if (mMap != null) {
            locationMarker = mMap.addMarker(locationMarkerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }
        // haal de pokemons op in de buurt.
        getPokemonOnLocation(currentLoc);
    }*/

    private void getPokemonOnLocation(Location location) {
        getPokemonOnLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void getPokemonOnLocation(LatLng location) {
        // make que voor pokemon location
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        // url voor data
        String url = "https://pokevision.com/map/data/" + location.latitude + "/" + location.longitude;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handelData(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
                Toast.makeText(getActivity().getApplicationContext(), "De server is nu tijdelijk niet beschikbaar probeer later opnieuw", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }

    private String getStringResourceByName(String aString) {
        String packageName = getActivity().getPackageName();
        int resId = getResources().getIdentifier(aString, "string", packageName);
        return getString(resId);
    }

    /*private void updateCurrentLocationMarker(Location location) {
        locationMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
    }*/

    private void handelData(String data) {
        try {
            JSONObject response = new JSONObject(data);
            if (response.has("status") && response.getString("status").equals("success")) {
                if (response.has("pokemon")) {
                    JSONArray respPokemons = (JSONArray) response.getJSONArray("pokemon");
                    for (int i = 0; i < respPokemons.length(); i++) {
                        JSONObject respPokemon = respPokemons.getJSONObject(i);

                        // get if alive
                        Boolean alive = respPokemon.getBoolean("is_alive");
                        // get the marker id
                        String markerId = respPokemon.getString("id");
                        // get location
                        Double lat = respPokemon.getDouble("latitude");
                        Double lng = respPokemon.getDouble("longitude");
                        // make pokemon model
                        PokemonModel poke = new PokemonModel(markerId, respPokemon.getLong("expiration_time"), respPokemon.getString("pokemonId"), new LatLng(lat, lng));

                        if (alive && !pokeMarkers.containsKey(poke)) {
                            // make marker and add to map
                            Marker pokeMarker = mMap.addMarker(new MarkerOptions()
                                    .position(poke.getLocation())
                                    .title(getStringResourceByName("pokemon_id_" + poke.getPokeId()))
                                    .icon(BitmapDescriptorFactory.fromResource(getResources().getIdentifier("pokemonicon" + poke.getPokeId(), "drawable", getActivity().getPackageName())))
                                    .snippet("time alive: " + poke.getTimeLeftFormat())
                                    .flat(true));

                            pokeMarkers.put(poke, pokeMarker);
                        }
                    }
                }

            }
        } catch (Throwable t) {
            if (data.contains("Maintenance")) {
                Toast.makeText(getActivity().getApplicationContext(), "Er is een maintenance bij pokevision", Toast.LENGTH_LONG).show();
            }
            Log.e("My App", "Could not parse malformed JSON: \"" + data + "\"");
        }
    }

    private boolean markerClicked = false;
    private Marker markerWhatIsChecked = null;

    private final int timeToReload = 30;
    private int tickReload = 0;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            if (tickReload >= timeToReload) {
				//todo deze ook op pauze zetten wanneer die wordt geminimaliseerd
                tickReload = 0;
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return; //todo misschien nog een exeption gooien
                }
                Location l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (l!=null) {
                    getPokemonOnLocation(new LatLng(l.getLatitude(), l.getLongitude()));
                }
            } else {
                tickReload++;
            }
            for (Map.Entry<PokemonModel, Marker> entry : pokeMarkers.entrySet()) {
                PokemonModel key = entry.getKey();
                Marker value = entry.getValue();
                if (!key.timesUp()) {
                    if (markerClicked && markerWhatIsChecked != null) {
                        if (markerWhatIsChecked.equals(value)) {
                            value.setSnippet("time alive: " + key.getTimeLeftFormat());
                            // this so the snippet is reloaded
                            value.hideInfoWindow();
                            value.showInfoWindow();
                        }
                    }
                } else {
                    value.remove();
                }
            }
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (Map.Entry<PokemonModel, Marker> entry : pokeMarkers.entrySet()) {
            PokemonModel key = entry.getKey();
            Marker value = entry.getValue();
            if (marker.equals(value)) {
                markerClicked = true;
                markerWhatIsChecked = marker;
                value.setSnippet("time alive:" + key.getTimeLeftFormat());
            }
        }
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (markerClicked == true) {
            markerWhatIsChecked = null;
            markerClicked = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.i("called", "Activity --> onPause");
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else {
            this.locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i("called", "Activity --> onResume");

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else {
            this.locationManager.requestLocationUpdates(this.locationProvider, 400, 1, this);
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getActivity().getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        getPokemonOnLocation(location);
        tickReload=0;
        // todo misschien nog kijken naar aantal meter en update en dit optioneel maken
        /* todo misschien dit ook nog doen?
        //when the location changes, update the map by zooming to the location
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
        this.googleMap.moveCamera(center);

        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
        this.googleMap.animateCamera(zoom);
        */
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i("called", "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i("called", "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i("called", "onProviderDisabled");
    }
}
