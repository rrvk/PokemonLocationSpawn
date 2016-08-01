package nl.rrvk.pokemonspawnlocation.fragments;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.rrvk.pokemonspawnlocation.MainActivity;
import nl.rrvk.pokemonspawnlocation.R;
import nl.rrvk.pokemonspawnlocation.adapter.NotificationSettingDrawer;
import nl.rrvk.pokemonspawnlocation.listeners.SettingsNotificationListener;
import nl.rrvk.pokemonspawnlocation.listeners.SettingsOnMapListener;
import nl.rrvk.pokemonspawnlocation.utils.ActivityUtils;

public class PokemonSettingFragment extends Fragment {
    private RecyclerView mDrawerListView;
    private NotificationSettingDrawer mAdapter;
    private String[] mPokemonItems;
    private TypedArray mPokemonIcons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spawn_notification_settings, container, false);
        ActivityUtils.setActionbarTitles((MainActivity) getActivity(), R.string.nav_notification_settings, null);

        initList(view);
        return view;
    }


    private void initList(View view) {
        mPokemonIcons = getResources().obtainTypedArray(R.array.pokemon_options_icon);
        mPokemonItems = getResources().getStringArray(R.array.pokemon_options_tekst);
        mDrawerListView = (RecyclerView) view.findViewById(R.id.recycler_notification);
        mAdapter = new NotificationSettingDrawer(mPokemonItems, mPokemonIcons, new SettingsNotificationListener(getActivity()), new SettingsOnMapListener(getActivity()),getActivity());
        mDrawerListView.setHasFixedSize(true);
        mDrawerListView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mDrawerListView.setLayoutManager(layoutManager);
    }
}
