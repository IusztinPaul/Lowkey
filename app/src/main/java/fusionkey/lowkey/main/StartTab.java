package fusionkey.lowkey.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import fusionkey.lowkey.R;
import fusionkey.lowkey.entryActivity.EntryActivity;
import fusionkey.lowkey.login.LoginActivity;

public class StartTab extends Fragment {
    TextView getStarted;
    TextView messages;
    ConstraintLayout green;
    ConstraintLayout redLayout;
    View rootView;
    ImageView imag1;
    ImageView imag2;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
            rootView=inflater.inflate(R.layout.activity_profile_tab, container, false);

        messages = (TextView) rootView.findViewById(R.id.messages);
        getStarted = (TextView) rootView.findViewById(R.id.getstarted);
        green= (ConstraintLayout) rootView.findViewById(R.id.green);
        redLayout = (ConstraintLayout) rootView.findViewById(R.id.red);
        imag1 = (ImageView) rootView.findViewById(R.id.imageView6);
        imag2 = (ImageView) rootView.findViewById(R.id.imageView7);
        TextView goGreen = (TextView) rootView.findViewById(R.id.textView7);
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // rootView = inflater.inflate(R.layout.activity_profile_tabmess,container,false);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("layout","messages");
                editor.apply();

            }
        });

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("layout","getStarted");
                editor.apply();

            }
        });

        imag1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), GetStarted.class);
                intent.putExtra("Listener",true);
                startActivity(intent);

            }
        });
        imag2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), GetStarted.class);
                intent.putExtra("Listener",false);
                startActivity(intent);

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