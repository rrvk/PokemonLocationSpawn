package nl.rrvk.pokemonspawnlocation.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import nl.rrvk.pokemonspawnlocation.MainActivity;
import nl.rrvk.pokemonspawnlocation.R;
import nl.rrvk.pokemonspawnlocation.utils.ActivityUtils;

public class WebFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        //ActivityUtils.setActionbarTitles((MainActivity) getActivity(), R.string.nav_pokevision, null);

        WebView myWebView = (WebView) v.findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl("https://pokevision.com/");
        return v;
    }
}
