package nl.rrvk.pokemonlocationspawn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import nl.rrvk.pokemonlocationspawn.adapter.DrawerAdapter;
import nl.rrvk.pokemonlocationspawn.fragments.MapFragment;
import nl.rrvk.pokemonlocationspawn.listeners.DrawerListener;
import nl.rrvk.pokemonlocationspawn.utils.FragmentUtils;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerListView;
    private DrawerAdapter mAdapter;
    private String[] mDrawerItems;
    private FragmentManager mFragmentManager;

    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDrawer();
        initToolbar();
        determineFragment();
        checkForUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        // ... your own onResume implementation
        checkForCrashes();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterManagers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }

    private void determineFragment() {
        mFragmentManager = getSupportFragmentManager();
        FragmentUtils.init(mFragmentManager);
        // todo misschien weer met home als die cloudflare weer tegen werkt?
        Fragment fragment = new MapFragment();
        FragmentUtils.replace(fragment);
    }

    private void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDrawer();
            }
        });
    }

    public void toggleDrawer() {
        if (mDrawerLayout.isDrawerOpen(mDrawerListView)) {
            mDrawerLayout.closeDrawer(mDrawerListView);
        } else {
            mDrawerLayout.openDrawer(mDrawerListView);
        }
    }

    private void initDrawer(){
        //mDrawerIcons = getResources().obtainTypedArray(R.array.nav_icons);
        mDrawerItems = getResources().getStringArray(R.array.drawer_options);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListView = (RecyclerView) findViewById(R.id.left_drawer);
        //mAdapter = new DrawerAdapter(mDrawerItems, mDrawerIcons, new DrawerListener(getApplicationContext(), mDrawerLayout));
        mAdapter = new DrawerAdapter(mDrawerItems, new DrawerListener(getApplicationContext(), mDrawerLayout));
        mDrawerListView.setHasFixedSize(true);
        mDrawerListView.setAdapter(mAdapter);
        mDrawerListView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void setDrawerHeader(String title){
        mAdapter.setHeader(title);
    }
}
