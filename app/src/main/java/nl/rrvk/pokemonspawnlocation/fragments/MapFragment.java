package nl.rrvk.pokemonspawnlocation.fragments;

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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nl.rrvk.pokemonspawnlocation.MainActivity;
import nl.rrvk.pokemonspawnlocation.PermissionUtils;
import nl.rrvk.pokemonspawnlocation.R;
import nl.rrvk.pokemonspawnlocation.manager.PokeDataManager;
import nl.rrvk.pokemonspawnlocation.model.PokemonModel;
import nl.rrvk.pokemonspawnlocation.utils.ActivityUtils;

import android.support.v7.app.AppCompatActivity;

public class MapFragment extends Fragment implements LocationListener, OnMapReadyCallback, OnMarkerClickListener, OnMapClickListener, OnRequestPermissionsResultCallback {
    private boolean markerClicked = false;
    private PokemonModel pokeeMarkerWhatIsChecked = null;

    private final int timeToReload = 30;
    private int tickReload = 0;

    private static View view;
    private GoogleMap mMap;
    private LocationManager locationManager;


    private String locationProvider;

    private PokeDataManager pokeDataManager;

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
        pokeDataManager = new PokeDataManager(this.getContext());
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
        // todo naar kijken
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // todo naar kijken
        mMap = googleMap;
        pokeDataManager.setmMap(mMap);
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
        // todo naar kijken
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            //todo dit allemaal doen bij die andere reqeust dingen
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            Location location = locationManager.getLastKnownLocation(locationProvider);
            //todo vragen als er geen locatie beschikbaar is
            if (location != null) {
                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                mMap.moveCamera(center);

                CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                mMap.animateCamera(zoom);
                pokeDataManager.getDataServer(location);
            }
        }
    }

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
                if (l != null) {
                    pokeDataManager.getDataServer(new LatLng(l.getLatitude(), l.getLongitude()));
                }
            } else {
                tickReload++;
            }

            for(Iterator<Map.Entry<String, PokemonModel>> it = pokeDataManager.getPokemonAndMarkers().entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, PokemonModel> entry = it.next();
                PokemonModel pokemon = entry.getValue();
                if (!pokemon.timesUp()) {
                    if (markerClicked && pokeeMarkerWhatIsChecked != null) {
                        if (pokeeMarkerWhatIsChecked.equals(entry.getValue())) {
                            pokemon.getMarker().setSnippet(R.string.marker_text+ pokemon.getTimeLeftFormat());
                            // this so the snippet is reloaded
                            pokemon.getMarker().hideInfoWindow();
                            pokemon.getMarker().showInfoWindow();
                        }
                    }
                } else {
                    pokemon.getMarker().remove();
                    it.remove();
                }
            }
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public boolean onMarkerClick(Marker marker) {
        PokemonModel poke = pokeDataManager.getPokeModelFromMarker(marker);
        if (poke!=null){
            markerClicked = true;
            pokeeMarkerWhatIsChecked = poke;
            poke.getMarker().setSnippet(R.string.marker_text + poke.getTimeLeftFormat());
        }
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (markerClicked == true) {
            pokeeMarkerWhatIsChecked = null;
            markerClicked = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // remove the timer callback
        timerHandler.removeCallbacks(timerRunnable);
        // check and ask premissions and remove location asking
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
            // todo kijken of dat andere ook hier moet
        } else {
            this.locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // resume the timer
        timerHandler.postDelayed(timerRunnable, 0);
        // check and ask premissions and get current location
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
            // todo kijken of dat andere ook hier moet
        } else {
            this.locationManager.requestLocationUpdates(this.locationProvider, 400, 1, this);
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog.newInstance(true).show(getActivity().getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
        pokeDataManager.getDataServer(location);
        tickReload = 0;
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
