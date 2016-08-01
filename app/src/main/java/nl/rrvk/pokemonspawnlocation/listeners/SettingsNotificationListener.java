package nl.rrvk.pokemonspawnlocation.listeners;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import nl.rrvk.pokemonspawnlocation.R;

/**
 * Created by rvank on 30-7-2016.
 */
public class SettingsNotificationListener implements View.OnClickListener {
    Activity activity;
    public SettingsNotificationListener(FragmentActivity activity) {
        this.activity=activity;
    }

    @Override
    public void onClick(View v) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.preference_notification), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        CheckBox c = (CheckBox) v;
        editor.putBoolean("pokemon"+c.getId(),c.isChecked());
        editor.commit();
    }
}
