package fusionkey.lowkey.chat.models;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

public class ContentViewCreatorUtils {
    void makeLayoutsVisibilityGONE(View... views) {
        setLayoutsVisibility(View.GONE, views);
    }

    void makeLayoutsVisibilityVisible(View... views) {
        setLayoutsVisibility(View.VISIBLE, views);
    }

    private void setLayoutsVisibility(int mode, View... views) {
        for (View view : views)
            view.setVisibility(mode);
    }

    void populateView(ConstraintLayout layout,
                      TextView msgTextView,
                      TextView dateTextView,
                      MessageTO msg) {
        if (layout != null)
            layout.setVisibility(View.VISIBLE);
        if (dateTextView != null)
            dateTextView.setText(msg.getDate());
        if (msgTextView != null)
            msgTextView.setText((String) msg.getContent());
    }

    void populateView(ConstraintLayout layout,
                      TextView dateTextView,
                      MessageTO msg) {
        populateView(layout, null, dateTextView, msg);
    }
}
