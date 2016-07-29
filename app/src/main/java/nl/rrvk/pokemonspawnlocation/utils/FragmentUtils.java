package nl.rrvk.pokemonspawnlocation.utils;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import nl.rrvk.pokemonspawnlocation.R;

public class FragmentUtils {
    private static final int contentFrame = R.id.content_frame;
    private static FragmentManager fragmentManager;

    public static void init(FragmentManager fm) {
        fragmentManager = fm;
    }

    /**
     * Replaces all fragments with given fragment in R.id.content_frame
     *
     * @param fragment
     */
    public static void replace(Fragment fragment) {
        Fragment existing = fragmentManager.findFragmentByTag(fragment.getClass().getSimpleName());
        if (existing != null) {
            if (existing.isVisible()) {
                //Bundle previousArgs = fragment.getArguments();
                return;
            }
            fragment = existing;
        }

        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction t = fragmentManager.beginTransaction();
        t.replace(contentFrame, fragment, tag);
        t.addToBackStack(tag);
        t.commit();
    }
}
