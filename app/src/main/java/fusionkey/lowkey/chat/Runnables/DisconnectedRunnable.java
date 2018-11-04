package fusionkey.lowkey.chat.Runnables;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class DisconnectedRunnable implements Runnable {

    private TextView state;
    private static final int delay=4000;
    private Handler h1;

    public DisconnectedRunnable(Handler h1, TextView state){
        this.state=state;
        this.h1=h1;
    }

    public void run(){
        try{
            Thread.sleep(delay);
        }catch (InterruptedException e){

        }

        if(state.getText().equals("disconnected")){
            Message msg = Message.obtain();
            Bundle b = new Bundle();
            b.putString("TheState","disconnected");
            msg.setData(b);
            h1.sendMessage(msg);
        }else{
            Message msg = Message.obtain();
            Bundle b = new Bundle();
            b.putString("TheState","connected");
            msg.setData(b);
            h1.sendMessage(msg);
        }
    }
}
