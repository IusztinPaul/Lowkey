package fusionkey.lowkey.listAdapters.PagerAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import fusionkey.lowkey.R;
import fusionkey.lowkey.main.Main2Activity;

public class GetHelp extends Fragment {
    private static final String KEY_POSITION="position";

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
        return(result);
    }
}