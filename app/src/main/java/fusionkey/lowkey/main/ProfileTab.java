package fusionkey.lowkey.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

<<<<<<< HEAD:app/src/main/java/fusionkey/lowkeyfinal/main/ProfileTab.java
import fusionkey.lowkeyfinal.entryActivity.EntryActivity;
import fusionkey.lowkeyfinal.R;
import fusionkey.lowkeyfinal.listAdapters.MenuAdapter;
=======
import fusionkey.lowkey.entryActivity.EntryActivity;
import fusionkey.lowkey.R;
>>>>>>> 744897878c20e10adb81e5de530428651923e1b7:app/src/main/java/fusionkey/lowkey/main/ProfileTab.java

public class ProfileTab extends Fragment {

    MenuAdapter adapter;

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