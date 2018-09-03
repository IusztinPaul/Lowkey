package fusionkey.lowkey.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import fusionkey.lowkey.R;

public class StartTab extends Fragment {
    TextView getStarted;
    ProgressBar progressBar;
    TextView messages;
    ConstraintLayout green;
    ConstraintLayout redLayout;
    View rootView;
    Button imag1;
    Button imag2;
    CardView searchCard;

    public static final String currentUser = "sefullabani";

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
            rootView=inflater.inflate(R.layout.activity_profile_tab, container, false);

        messages = (TextView) rootView.findViewById(R.id.messages);
        getStarted = (TextView) rootView.findViewById(R.id.getstarted);
        imag1 = (Button) rootView.findViewById(R.id.ChooseGreen);
        imag2 = (Button) rootView.findViewById(R.id.ChooseRed);
        progressBar = (ProgressBar) rootView.findViewById(R.id.loadingBar);
        TextView goGreen = (TextView) rootView.findViewById(R.id.textView7);
        searchCard = (CardView) rootView.findViewById(R.id.searchCard);
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
    if(!Main2Activity.SEARCH_STATE) {
        imag1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Main2Activity.class);
                intent.putExtra("Listener", false);
                intent.putExtra("Mapping", "ON");
                getActivity().overridePendingTransition(0,0);
                startActivity(intent);
                getActivity().overridePendingTransition(0,0);
                //  final LoadingAsyncTask loadingAsyncTask = new LoadingAsyncTask(currentUser,getActivity(),progressBar,false);
                //  loadingAsyncTask.execute();
                // searchCard.setVisibility(rootView.VISIBLE);
            }
        });
        imag2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Main2Activity.class);
                intent.putExtra("Listener", true);
                intent.putExtra("Mapping", "ON");
                getActivity().overridePendingTransition(0,0);
                startActivity(intent);
                getActivity().overridePendingTransition(0,0);
                // final LoadingAsyncTask loadingAsyncTask = new LoadingAsyncTask(currentUser,getActivity(),progressBar,true);
                //   loadingAsyncTask.execute();
                // searchCard.setVisibility(View.VISIBLE);
            }
        });
    }
        return rootView;
    }

}