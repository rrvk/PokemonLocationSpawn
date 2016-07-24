package nl.rrvk.pokemonlocationspawn.listeners;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import nl.rrvk.pokemonlocationspawn.R;
import nl.rrvk.pokemonlocationspawn.utils.FragmentUtils;
import nl.rrvk.pokemonlocationspawn.factories.FragmentFactory;

public class DrawerListener implements View.OnClickListener {

    private Context mCtx;
    private DrawerLayout mDrawer;

    public DrawerListener(Context mCtx, DrawerLayout drawer) {
        this.mCtx = mCtx;
        this.mDrawer = drawer;
    }

    @Override
    public void onClick(View view) {
        TextView drawerItem = (TextView) view.findViewById(R.id.drawer_item_text);
        if (drawerItem != null) {
            selectItem(drawerItem);
        }
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(TextView textView) {
        String text = (String) textView.getText();

        /*if (mCtx.getString(R.string.nav_log_out).equals(text)) {
            //todo logout
        } else {*/
        FragmentFactory factory = FragmentFactory.getInstance(mCtx);
        Fragment fragment = factory.createFragment(text);

        if (fragment != null) {
            FragmentUtils.replace(fragment);
        } else {
            Toast.makeText(mCtx, "Nog geen fragment voor: " + text, Toast.LENGTH_SHORT).show();
            Log.e(getClass().getSimpleName(), "No fragment for: " + text);
        }
        //}
        mDrawer.closeDrawers();
    }
}
