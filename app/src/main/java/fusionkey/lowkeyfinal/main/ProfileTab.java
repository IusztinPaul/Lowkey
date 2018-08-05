package fusionkey.lowkeyfinal.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fusionkey.lowkeyfinal.R;

public class ProfileTab extends Fragment {
    TextView getStarted;
    TextView messages;
    View rootView;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        if(checkLayout()==1)
        rootView = inflater.inflate(R.layout.activity_profile_tabmess, container, false);
        else
            rootView=inflater.inflate(R.layout.activity_profile_tab, container, false);

        messages = (TextView) rootView.findViewById(R.id.messages);
        getStarted = (TextView) rootView.findViewById(R.id.getstarted);

        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // rootView = inflater.inflate(R.layout.activity_profile_tabmess,container,false);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("layout","messages");
                editor.apply();
                startActivity(new Intent(getContext().getApplicationContext(), ProfileTab.class));
            }
        });

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("layout","getStarted");
                editor.apply();
                startActivity(new Intent(getContext().getApplicationContext(), ProfileTab.class));
            }
        });

        return rootView;
    }
    private int checkLayout(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String layout = preferences.getString("layout", "");
        if(layout.equals("messages")){
            return 1;
        }
    return 0;
    }
}