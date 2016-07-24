package nl.rrvk.pokemonlocationspawn.factories;

import android.content.Context;
import android.support.v4.app.Fragment;

import nl.rrvk.pokemonlocationspawn.R;
import nl.rrvk.pokemonlocationspawn.fragments.HomeFragment;
import nl.rrvk.pokemonlocationspawn.fragments.MapFragment;

public class FragmentFactory {

    private static FragmentFactory factory;
    private Context context;

    private FragmentFactory(Context context) {
        this.context = context;
    }

    public static FragmentFactory getInstance(Context context) {
        if (factory == null) {
            factory = new FragmentFactory(context);
        }
        return factory;
    }

    public Fragment createFragment(String item) {
        Fragment toReturn = null;
        // TODO get existing fragment with fragmentManager;
        if (equals(R.string.nav_pokevision, item)) {
            toReturn = new HomeFragment();
        } else if (equals(R.string.nav_map, item)) {
            toReturn = new MapFragment();
        } /*else if (equals(R.string.nav_observations, item)) {
            toReturn = new ObservationFragment();
        } else if (equals(R.string.nav_chat, item)) {
            toReturn = new ChatFragment();
        } else if (equals(R.string.nav_earthquake, item)) {
            toReturn = new EarthquakeFragment();
        }/*else if (equals(R.string.nav_archive, item)) {
            toReturn = new ArchiveFragment();
            //todo enable
        }*/
        return toReturn;
    }

    private boolean equals(int resourceId, String s) {
        return context.getString(resourceId).equals(s);
    }
}
