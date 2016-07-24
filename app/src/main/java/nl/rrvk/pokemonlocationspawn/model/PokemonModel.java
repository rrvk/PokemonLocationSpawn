package nl.rrvk.pokemonlocationspawn.model;

import com.google.android.gms.maps.model.LatLng;

public class PokemonModel {
    private String id;
    private Long expTime;
    private String pokeId;
    private LatLng location;

    public PokemonModel(String id, Long expTime, String pokeId, LatLng location) {
        this.id = id;
        this.expTime = expTime;
        this.pokeId = pokeId;
        this.location = location;
    }

    public String getId() {
        return id;
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

    ;

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
