package fusionkey.lowkey.chat.Runnables;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DisconnectedRunnable implements Runnable {

    private TextView state;
    private LinearLayout chatbox;
    private static final int delay=2000;


    public DisconnectedRunnable(TextView state, LinearLayout chatbox){
        this.state=state;
        this.chatbox=chatbox;
    }

    public void run(){
        try{
            Thread.sleep(delay);
        }catch (InterruptedException e){

        }
        if(state.getText().equals("disconnected")){
         chatbox.setVisibility(View.GONE);
        }
    }
}
