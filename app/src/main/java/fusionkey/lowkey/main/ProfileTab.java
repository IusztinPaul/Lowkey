package fusionkey.lowkey.main;

import android.content.Intent;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;
import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.entryActivity.EntryActivity;
import fusionkey.lowkey.R;

import fusionkey.lowkey.main.settings.SettingsActivity;

import fusionkey.lowkey.main.profile.EditUserActivity;
import fusionkey.lowkey.main.utils.NetworkManager;


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
                if(NetworkManager.isNetworkAvailable())
                logOut();
                else Toast.makeText(getActivity(), "Check if you're connected to the Internet", Toast.LENGTH_SHORT).show();

            }
        });

        final ImageView imageViewEdit = rootView.findViewById(R.id.imageViewEdit);
        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileTab.this.getContext(), EditUserActivity.class);
                startActivity(intent);
            }
        });

        final CircleImageView circleImageView = rootView.findViewById(R.id.circleImageView);
        if(LowKeyApplication.profilePhoto != null)
            circleImageView.setImageBitmap(LowKeyApplication.profilePhoto);

        return rootView;
    }

    private void logOut(){
        LowKeyApplication.userManager.logout();
        getActivity().startActivity(new Intent(getContext().getApplicationContext(), EntryActivity.class));
    }
}