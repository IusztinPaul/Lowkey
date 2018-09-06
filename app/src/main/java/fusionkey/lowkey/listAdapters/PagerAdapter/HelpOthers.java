package fusionkey.lowkey.listAdapters.PagerAdapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import fusionkey.lowkey.R;
import fusionkey.lowkey.main.Main2Activity;

public class HelpOthers extends Fragment {
    private static final String KEY_POSITION="position";
    SharedPreferences sharedPreferences;
    static HelpOthers newInstance(int position) {
        HelpOthers frag=new HelpOthers();
        Bundle args=new Bundle();

        args.putInt(KEY_POSITION, position);
        frag.setArguments(args);

        return(frag);
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View result=inflater.inflate(R.layout.help_others, container, false);

        int position=getArguments().getInt(KEY_POSITION, -1);
        Button imag1 = (Button) result.findViewById(R.id.ChooseGreen);
        imag1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()){
                if(loadState()==0) {
                    Intent intent = new Intent(getContext(), Main2Activity.class);
                    saveState("step", 2);
                    getActivity().overridePendingTransition(0, 0);
                    startActivity(intent);
                    getActivity().overridePendingTransition(0, 0);
                }
            }else Toast.makeText(getActivity(), "Check if you're connected to the Internet", Toast.LENGTH_SHORT).show();
            }
        });
        return(result);
    }

    private void saveState(String key,int step){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, step);
        editor.apply();
    }
    private int loadState(){
         sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        return (sharedPreferences.getInt("step", 0));
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
