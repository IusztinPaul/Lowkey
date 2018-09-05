package fusionkey.lowkey.listAdapters.PagerAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import fusionkey.lowkey.R;
import fusionkey.lowkey.main.Main2Activity;

public class HelpOthers extends Fragment {
    private static final String KEY_POSITION="position";

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
        return(result);
    }
}
