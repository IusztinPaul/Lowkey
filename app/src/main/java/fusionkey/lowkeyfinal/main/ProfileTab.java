package fusionkey.lowkeyfinal.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import fusionkey.lowkeyfinal.entryActivity.EntryActivity;
import fusionkey.lowkeyfinal.R;

public class ProfileTab extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_chat, container, false);
        final Button logOut = (Button) rootView.findViewById(R.id.button);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             logOut();
            }
        });

        return rootView;
    }

    private void logOut(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("loggedIn","false");
        editor.apply();
        startActivity(new Intent(getContext().getApplicationContext(), EntryActivity.class));
    }
}