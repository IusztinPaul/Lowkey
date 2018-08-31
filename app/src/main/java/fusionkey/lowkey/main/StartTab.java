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
import android.widget.Button;
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
    Button imag1;
    Button imag2;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
            rootView=inflater.inflate(R.layout.activity_profile_tab, container, false);

        messages = (TextView) rootView.findViewById(R.id.messages);
        getStarted = (TextView) rootView.findViewById(R.id.getstarted);
        imag1 = (Button) rootView.findViewById(R.id.ChooseGreen);
        imag2 = (Button) rootView.findViewById(R.id.ChooseRed);
        TextView goGreen = (TextView) rootView.findViewById(R.id.textView7);
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        imag1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), GetStarted.class);
                intent.putExtra("Listener",false);
                startActivity(intent);

            }
        });
        imag2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), GetStarted.class);
                intent.putExtra("Listener",true);
                startActivity(intent);

            }
        });
        return rootView;
    }

}