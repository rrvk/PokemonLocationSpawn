package nl.rrvk.pokemonspawnlocation.manager;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import nl.rrvk.pokemonspawnlocation.R;
import nl.rrvk.pokemonspawnlocation.model.PokemonModel;

public class PokeDataManager {

    // this is the context that is needed
    private Context con;
    // hashmap with the pokemon markers
    private HashMap<String, PokemonModel> pokemonAndMarkers = new HashMap<>();

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    // the map where the markers should put on
    private GoogleMap mMap;

    public PokeDataManager(Context con) {
        this.con = con;
    }

    /**
     * this methode return the hashmap with the pokemonModels
     * @return
     */
    public HashMap<String, PokemonModel> getPokemonAndMarkers(){
        return pokemonAndMarkers;
    }

    /**
     * With this methode you can request pokemon location data from the pokevision servers
     *
     * @param location
     */
    public void getDataServer(LatLng location) {
        // this makes a queue for the volley request
        RequestQueue queue = Volley.newRequestQueue(con);
        // url voor data
        final String url = con.getString(R.string.map_data_root_url) + location.latitude + "/" + location.longitude;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volley Request", "received data from" + url);
                        handelData(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley Request", "Error on url " + url + "error:" + error.getMessage());
                Toast.makeText(con, con.getString(R.string.nl_error_server), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }

    /**
     * With this methode you can request pokemon location data from the pokevision servers
     *
     * @param location
     */
    public void getDataServer(Location location) {
        if (location!=null) {
            getDataServer(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    /**
     * Whit this methode the response data from the pokevision server will be processed
     *
     * @param data
     */
    private void handelData(String data) {
        if (mMap==null){
            Toast.makeText(con, con.getString(R.string.nl_error_mMap_empty), Toast.LENGTH_LONG).show();
            return;
        }
        try {
            JSONObject jsonResponse = new JSONObject(data);
            if (jsonResponse.has(con.getString(R.string.api_status)) && jsonResponse.getString(con.getString(R.string.api_status)).equals(con.getString(R.string.api_status_success))) {
                if (jsonResponse.has(con.getString(R.string.api_pokemon))) {
                    JSONArray jsonArrPokemons = jsonResponse.getJSONArray(con.getString(R.string.api_pokemon));
                    if (jsonArrPokemons.length() > 0) {
                        // if there are pokemons then loop through them to see if there are new ones
                        for (int i = 0; i < jsonArrPokemons.length(); i++) {
                            JSONObject jsonObjPokemon = jsonArrPokemons.getJSONObject(i);
                            // get the different opties in the api
                            String markerId = jsonObjPokemon.getString(con.getString(R.string.api_pokemon_marker_id));
                            // if the pokemon is alive and there is no marker for the pokemon
                            if (!pokemonAndMarkers.containsKey(markerId)) {
                                String pokemonId = jsonObjPokemon.getString(con.getString(R.string.api_pokemon_pokemon_id));
                                Double lat = jsonObjPokemon.getDouble(con.getString(R.string.api_pokemon_latitude));
                                Double lng = jsonObjPokemon.getDouble(con.getString(R.string.api_pokemon_longitude));
                                Long expTime = jsonObjPokemon.getLong(con.getString(R.string.api_pokemon_expiration_time));

                                // make the pokemon model
                                PokemonModel pokemon = new PokemonModel(markerId, expTime, pokemonId, new LatLng(lat, lng));
                                // make marker and add to map
                                Marker pokeMarker = mMap.addMarker(new MarkerOptions()
                                        .position(pokemon.getLocation())
                                        .title(getStringResourceByName("pokemon_id_" + pokemon.getPokeId()))
                                        .icon(BitmapDescriptorFactory.fromResource(con.getResources().getIdentifier("pokemonicon" + pokemon.getPokeId(), "drawable", con.getPackageName())))
                                        .snippet("time alive: " + pokemon.getTimeLeftFormat())
                                        .flat(true));

                                pokemon.setMarker(pokeMarker);
                                // add the pokemon and marker to the hashmap
                                pokemonAndMarkers.put(markerId, pokemon);
                            }
                        }
                    } else {
                        // todo misschien controleren of de server inderdaad niet werkt, door een plaatst te checken met altijd pokemons in het
                        Toast.makeText(con, con.getString(R.string.nl_error_no_pokemons_found), Toast.LENGTH_LONG).show();
                    }
                }
            }
        } catch (Throwable t) {
            if (data.contains("Maintenance")) {
                Toast.makeText(con, con.getString(R.string.nl_error_maintenance), Toast.LENGTH_LONG).show();
                Log.d("Handel data", "Maintenance");
            } else {
                Toast.makeText(con, con.getString(R.string.nl_error_onbekende_fout), Toast.LENGTH_LONG).show();
                Log.d("Handel data", "Error whit the JSON: \"" + data + "\"");
            }
        }
    }

    /**
     * White this methode you can get a string by name
     *
     * @param aString
     * @return
     */
    private String getStringResourceByName(String aString) {
        String packageName = con.getPackageName();
        int resId = con.getResources().getIdentifier(aString, "string", packageName);
        return con.getString(resId);
    }

    /**
     * Get the pokeModel from the marker
     * returns the pokemodel when found else return null
     * @param marker
     * @return
     */
    public PokemonModel getPokeModelFromMarker(Marker marker) {
        for (Map.Entry<String, PokemonModel> entry : pokemonAndMarkers.entrySet()) {
            PokemonModel value = entry.getValue();
            if  (value.getMarker().equals(marker)){
                return value;
            }
        }
        return null;
    }
}
