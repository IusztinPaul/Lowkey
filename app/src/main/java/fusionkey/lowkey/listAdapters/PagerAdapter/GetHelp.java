package fusionkey.lowkey.listAdapters.PagerAdapter;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.Toast;

import fusionkey.lowkey.R;
import fusionkey.lowkey.main.Main2Activity;

public class GetHelp extends Fragment {
    private static final String KEY_POSITION="position";
    SharedPreferences sharedPreferences;
    static GetHelp newInstance(int position) {
        GetHelp frag=new GetHelp();
        Bundle args=new Bundle();

        args.putInt(KEY_POSITION, position);
        frag.setArguments(args);

        return(frag);
    }



    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View result=inflater.inflate(R.layout.get_help, container, false);
        Button imag2 = (Button) result.findViewById(R.id.ChooseRed);
        int position=getArguments().getInt(KEY_POSITION, -1);

        imag2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()){
                if(loadState()==0) {
                    Intent intent = new Intent(getContext(), Main2Activity.class);
                    saveState("step", 1);
                    getActivity().overridePendingTransition(0, 0);
                    startActivity(intent);
                    getActivity().overridePendingTransition(0, 0);
                }

                // final LoadingAsyncTask loadingAsyncTask = new LoadingAsyncTask(currentUser,getActivity(),progressBar,true);
                //   loadingAsyncTask.execute();
                // searchCard.setVisibility(View.VISIBLE);
            } else Toast.makeText(getActivity(), "Check if you're connected to the Internet", Toast.LENGTH_SHORT).show();
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