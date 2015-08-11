package com.example.larkinmcmahon.geogoals;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by djflash on 8/11/15.
 */
public class DonationWebViewFragment extends Fragment{
    private final int LOADER_ID = 2;
    private static String url = "";
    WebView webview;

    public DonationWebViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater  inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_donation_web_view, container, false);
        Intent intent = getActivity().getIntent();
        if (intent.hasExtra("url")) {
            url = intent.getStringExtra("url");
            webview = (WebView) rootView.findViewById(R.id.donation_webview);
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webview.loadUrl(url);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
