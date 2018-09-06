package fusionkey.lowkey.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.auth.ConfirmCodeActivity;
import fusionkey.lowkey.entryActivity.EntryActivity;
import fusionkey.lowkey.R;
import fusionkey.lowkey.main.settings.SettingsActivity;


public class ProfileTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_chat, container, false);
        final Button logOut = (Button) rootView.findViewById(R.id.button);

        ImageView settings = (ImageView) rootView.findViewById(R.id.settingsImg);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().startActivity(new Intent(getContext().getApplicationContext(), SettingsActivity.class));
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable())
                logOut();
                else Toast.makeText(getActivity(), "Check if you're connected to the Internet", Toast.LENGTH_SHORT).show();

            }
        });

        return rootView;
    }

    private void logOut(){
        LowKeyApplication.loginManager.logout();
        getActivity().startActivity(new Intent(getContext().getApplicationContext(), EntryActivity.class));
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}