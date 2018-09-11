package fusionkey.lowkey.listAdapters.PagerAdapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.main.Main2Activity;
import fusionkey.lowkey.main.MainCallback;
import fusionkey.lowkey.main.utils.NetworkManager;

public class HelpOthers extends Fragment {
    private static final String KEY_POSITION="position";
    SharedPreferences sharedPreferences;
    private MainCallback mainCallback;

    static HelpOthers newInstance(int position) {
        HelpOthers frag=new HelpOthers();
        Bundle args=new Bundle();

        args.putInt(KEY_POSITION, position);
        frag.setArguments(args);

        return(frag);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            mainCallback = (MainCallback) context;
        }catch(ClassCastException castException){
            castException.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View result=inflater.inflate(R.layout.help_others, container, false);



        Button imag1 = (Button) result.findViewById(R.id.ChooseGreen);
        imag1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkManager.isNetworkAvailable() && loadState()==0){
                    mainCallback.helpOthers();
                    saveState("step",1);


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
}
