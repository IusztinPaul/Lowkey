package fusionkey.lowkey.chat;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import fusionkey.lowkey.chat.interfaces.VolleyResponseListener;
import fusionkey.lowkey.listAdapters.ChatServiceAdapters.ChatAppMsgAdapter;
import fusionkey.lowkey.models.MessageTO;
import fusionkey.lowkey.queue.QueueMatcherUtils;
/**
 * @author Sandru Sebastian
 * @version 1.0
 * @since 24-Aug-18
 *
 * <h1>The Chat AsyncTask that gets the messages in background and updates the RecyclerView</h1>
 *
 */
public class ChatAsyncTask extends AsyncTask<Void,String,JSONObject> {

    private ChatRoom chatRoom;
    private RecyclerView recyclerView;
    private List<MessageTO> list;
    private ChatAppMsgAdapter chatAppMsgAdapter;

    public ChatAsyncTask(ChatRoom chatRoom, RecyclerView recyclerView, ChatAppMsgAdapter chatAppMsgAdapter, List<MessageTO> list){
        this.chatRoom=chatRoom;
        this.chatAppMsgAdapter=chatAppMsgAdapter;
        this.recyclerView=recyclerView;
        this.list=list;

    }

    @Override
    protected void onPreExecute() {
        chatRoom.joinRoom();
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        try{
            Thread.sleep(500);
        }catch (InterruptedException e) {
            e.printStackTrace();
            return AbstractChat.JSON_FAILED_REQUESTED_OBJECT;
        }
        while(chatRoom.isChatRoomAlive){

                chatRoom.getMsg(new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Log.e("Error", message);
                }
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.e("RESPONSE :",response.toString());
                        JSONObject msgJson = new JSONObject(response.getJSONObject("data").getString("message"));
                        Timestamp time =new Timestamp(msgJson.getLong("sent_timestamp")*1000L);
                        Date date = new Date(time.getTime());
                        SimpleDateFormat jdf = new SimpleDateFormat("HH:mm");
                        jdf.setTimeZone(TimeZone.getDefault());
                        String java_date = jdf.format(date);
                        if((list.get(list.size()-1).getMsgType()).equals(MessageTO.MSG_TYPE_RECEIVED) || (list.get(list.size()-1).getMsgType()).equals(MessageTO.MSG_TYPE_RECEIVED_LAST)) {
                            MessageTO msgDto = new MessageTO(msgJson.getString("from"), msgJson.getString("to"), msgJson.getString("message"), java_date, MessageTO.MSG_TYPE_RECEIVED_LAST);
                            list.add(msgDto);
                            publishProgress();
                        }
                        else {
                            MessageTO msgDto = new MessageTO(msgJson.getString("from"), msgJson.getString("to"), msgJson.getString("message"), java_date, MessageTO.MSG_TYPE_RECEIVED);
                            list.add(msgDto);
                            publishProgress();
                        }

                    }catch(JSONException e){
                        Log.e("getMsg" + "Error", "The response has no data");

                    }

                }
            });
            try {
                Thread.sleep(750);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return QueueMatcherUtils.JSON_FAILED_REQUESTED_OBJECT;
            }

        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        int newMsgPosition = list.size() - 1;

        chatAppMsgAdapter.notifyItemInserted(newMsgPosition);
        recyclerView.scrollToPosition(newMsgPosition);
    }

    @Override
    protected void onCancelled(JSONObject jsonObject) {

        chatRoom.deleteRoom();

    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {

    }
}