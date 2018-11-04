package fusionkey.lowkey.chat.Runnables;

import android.util.Log;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import fusionkey.lowkey.chat.ChatActivity;
import fusionkey.lowkey.chat.ChatRoom;
import fusionkey.lowkey.chat.interfaces.VolleyResponseListener;

/**
 * @author Sandru Sebastian
 * @version 1.0
 * @since 31-aug-2018
 * <h1> RUNNABLE that perfoms if user is/isn't connected/writting</h1>
 */
public class InChatRunnable implements Runnable {

    private TextView state;
    private TextView status;
    private ChatRoom c;

    public InChatRunnable(TextView status,TextView state, ChatRoom c) {
        this.state = state;
        this.status = status;
        this.c = c;
    }
    public void run(){
            c.getUserState(new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Log.e("USER STATE STATE",message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Log.e("USER STATE:",response.toString());
                    try {
                        if (!response.getBoolean("data")) {
                            status.setText("disconnected");
                        }else {
                            status.setText("connected");
                        }
                    }catch(JSONException e){
                        Log.e("JSON-STATE ERROR",e.toString());
                    }
                }
            });

            c.listenerIsWritting(new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Log.e("USER WRITE STATE",message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Log.e("USER WRITE STATE:",response.toString());
                    try {
                        if (response.getBoolean("data")) {
                            state.setText("user is writting");
                        }
                        else {
                            if(ChatActivity.USERNAME!=null)
                            state.setText(ChatActivity.USERNAME);
                            else
                                state.setText("");
                        }
                    }catch (JSONException e){
                        Log.e("JSON WRITE STATE",e.toString());
                    }
                }
            });


    }
}


