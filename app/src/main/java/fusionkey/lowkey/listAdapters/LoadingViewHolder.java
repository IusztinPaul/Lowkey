package fusionkey.lowkey.listAdapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import fusionkey.lowkey.R;

public class LoadingViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;

    public LoadingViewHolder(View view) {
        super(view);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
    }
}
