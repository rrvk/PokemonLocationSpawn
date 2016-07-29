package nl.rrvk.pokemonspawnlocation.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.apache.commons.lang3.StringUtils;

import nl.rrvk.pokemonspawnlocation.MainActivity;

/**
 * Created by Paulien on 28-10-2015.
 */
public class ActivityUtils {

    public static void hideSoftKeyboard(Activity activity) {
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(currentFocus.getApplicationWindowToken(), 0);
        }
    }

    public static void setActionbarTitles(MainActivity activity, Integer titleId, @Nullable Integer subtitleId) {
        if (activity == null) return;
        if (titleId != null) {
            String title = activity.getString(titleId);
            String subtitle = null;

            if (subtitleId != null) {
                subtitle = activity.getString(subtitleId);
            }

            setActionbarTitles(activity, title, subtitle);
        }
    }

    public static void setActionbarTitles(MainActivity activity, String title, String subtitle) {
        ActionBar ab = activity.getSupportActionBar();
        if (ab != null) {
            if (!StringUtils.isEmpty(title)) {
                activity.setDrawerHeader(title);
                ab.setTitle(title);
            } else {
                activity.setDrawerHeader("");
                ab.setTitle(null);
            }

            if (!StringUtils.isEmpty(title)) ab.setSubtitle(subtitle);
            else ab.setSubtitle(null);
        }
    }
}
