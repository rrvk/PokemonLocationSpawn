package nl.rrvk.pokemonspawnlocation.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class PokemonModel {
    private String markerId;
    private Long expTime;
    private String pokeId;
    private LatLng location;

    public PokemonModel(String markerId, Long expTime, String pokeId, LatLng location) {
        this.markerId = markerId;
        this.expTime = expTime;
        this.pokeId = pokeId;
        this.location = location;
    }
    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    private Marker marker;

    public String getMarkerId() {
        return markerId;
    }

    public Long getExpTime() {
        return expTime;
    }

    public String getPokeId() {
        return pokeId;
    }

    public LatLng getLocation() {
        return location;
    }

    private long getTimeLeft() {
        long currentTime = System.currentTimeMillis() / 1000;
        return (expTime - currentTime);
    }

    public int getRemainingMin() {
        return (int) getTimeLeft() / 60;
    }

    public int getRemainingSec() {
        return (int) getTimeLeft() - (getRemainingMin() * 60);
    }

    public String getTimeLeftFormat() {
        return String.format("%d min, %d sec", getRemainingMin(), getRemainingSec());
    }

    public boolean timesUp() {
        if (getRemainingMin()>0)
            return false;
        if (getRemainingSec()>0)
            return false;
        return true;
    }
}
