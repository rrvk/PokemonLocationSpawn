package nl.rrvk.pokemonspawnlocation.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import nl.rrvk.pokemonspawnlocation.R;
import nl.rrvk.pokemonspawnlocation.listeners.SettingsNotificationListener;
import nl.rrvk.pokemonspawnlocation.listeners.SettingsOnMapListener;

public class NotificationSettingDrawer extends RecyclerView.Adapter<NotificationSettingDrawer.Holder> {

    private final String[] mPokemonItems;
    private final TypedArray mPokemonIcons;
    private SettingsNotificationListener mNotfiListener;
    private SettingsOnMapListener mOnMapListener;
    private Activity activity;
    private int number = 1;

    public NotificationSettingDrawer(String[] mPokemonItems, TypedArray mPokemonIcons, SettingsNotificationListener notfiListener, SettingsOnMapListener onMapListener, Activity activity) {
        this.mPokemonItems = mPokemonItems;
        this.mPokemonIcons = mPokemonIcons;
        this.activity=activity;
        mNotfiListener = notfiListener;
        mOnMapListener = onMapListener;

    }

    public static class Holder extends RecyclerView.ViewHolder {
        View notificationSettingItem;
        ImageView iconImageView;
        TextView itemTextView;
        CheckBox itemCheckBoxOnMap;
        CheckBox itemCheckBoxNotification;

        public Holder(View itemView, SettingsNotificationListener notfiListener, SettingsOnMapListener onMapListener) {
            super(itemView);

            notificationSettingItem = itemView.findViewById(R.id.notification_item);
            itemTextView = (TextView) itemView.findViewById(R.id.notification_item_text);
            iconImageView = (ImageView) itemView.findViewById(R.id.notification_item_icon);
            itemCheckBoxOnMap = (CheckBox) itemView.findViewById(R.id.notification_item_on_map);
            itemCheckBoxOnMap.setOnClickListener(onMapListener);
            itemCheckBoxNotification = (CheckBox) itemView.findViewById(R.id.notification_item_active);
            itemCheckBoxNotification.setOnClickListener(notfiListener);
        }
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int resourceId = R.layout.notification_setting_item;
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(resourceId, viewGroup, false);
        Log.d("count", String.valueOf(viewGroup.getChildCount()));
        Holder holder = new Holder(view, mNotfiListener, mOnMapListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int i) {
        holder.iconImageView.setImageResource(mPokemonIcons.getResourceId(i, 0));
        holder.itemTextView.setText(mPokemonItems[i]);
        holder.itemCheckBoxNotification.setId(i+1);
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.preference_notification), Context.MODE_PRIVATE);
        boolean defaultValue = true;
        boolean checked = sharedPref.getBoolean("pokemon"+(i+1), defaultValue);
        holder.itemCheckBoxNotification.setChecked(checked);
        holder.itemCheckBoxOnMap.setId(i+1);
        sharedPref = activity.getSharedPreferences(activity.getString(R.string.preference_on_map), Context.MODE_PRIVATE);
        defaultValue = true;
        checked = sharedPref.getBoolean("pokemon"+(i+1), defaultValue);
        holder.itemCheckBoxOnMap.setChecked(checked);
    }

    @Override
    public int getItemCount() {
        return mPokemonItems.length;
    }
}