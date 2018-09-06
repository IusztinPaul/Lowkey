package fusionkey.lowkey.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.entryActivity.EntryActivity;
import fusionkey.lowkey.R;
import fusionkey.lowkey.main.profile.ChangePasswordActivity;
import fusionkey.lowkey.main.profile.EditUserActivity;


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

        final ImageView imageViewEdit = rootView.findViewById(R.id.imageViewEdit);
        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileTab.this.getContext(), EditUserActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void logOut(){
        LowKeyApplication.userManager.logout();
        getActivity().startActivity(new Intent(getContext().getApplicationContext(), EntryActivity.class));
    }


}