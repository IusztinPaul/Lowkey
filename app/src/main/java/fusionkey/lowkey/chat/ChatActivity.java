package fusionkey.lowkey.chat;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import fusionkey.lowkey.R;
import fusionkey.lowkey.ROOMdatabase.AppDatabase;
import fusionkey.lowkey.ROOMdatabase.UserDao;
import fusionkey.lowkey.chat.Runnables.DisconnectedRunnable;
import fusionkey.lowkey.chat.Runnables.InChatRunnable;
import fusionkey.lowkey.listAdapters.ChatServiceAdapters.ChatAppMsgAdapter;
import fusionkey.lowkey.models.MessageTO;
import fusionkey.lowkey.models.UserD;

/**
 * @author Sandru Sebastian
 * @version 1.0
 * @since 24-Aug-18
 *
 * <h1>MAIN CHAT ACTIVITY</h1>
 *
 */
public class ChatActivity extends AppCompatActivity {


    final long periodForT = 1000, periodForT1 =4000, delay=0;
    long last_text_edit=0;

    InChatRunnable inChatRunnable;
    ChatRoom chatRoom;
    DisconnectedRunnable disconnectedRunnable;
    ChatAsyncTask chatAsyncTask;
    Timer t,t1;
    Handler h1;
    Bundle bb = new Bundle();
    Thread thread;
    TextView state;
    TextView connectDot;
    EditText msgInputText;
    LinearLayout chatbox;
    String listenerRequest;
    String userRequest;
    ArrayList<MessageTO> msgDtoList;

    private static final String disconnectedDialog = "just disconnected from the chat !";
    private static final String listenerIntent ="Listener";
    private static final String userIntent ="User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        //INIT
        Toolbar toolbar = findViewById(R.id.toolbar);
        state = findViewById(R.id.isWritting);
        connectDot = findViewById(R.id.textView8);
        final RecyclerView msgRecyclerView = findViewById(R.id.reyclerview_message_list);
        final String listener = getIntent().getStringExtra(listenerIntent);
        final String user = getIntent().getStringExtra(userIntent);

        chatbox = findViewById(R.id.layout_chatbox);
        msgInputText = findViewById(R.id.chat_input_msg);
        listenerRequest = listener.replace("[", "").replace("]", "").replace("\"","");
        userRequest = user.replace("[", "").replace("]", "").replace("\"","");
        chatRoom = new ChatRoom(userRequest,listenerRequest);
        msgDtoList = new ArrayList<>();



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(linearLayoutManager);
        final ChatAppMsgAdapter chatAppMsgAdapter = new ChatAppMsgAdapter(msgDtoList);
        msgRecyclerView.setAdapter(chatAppMsgAdapter);
        Log.e("INFO","LISTENER : " + listenerRequest + " & USER : " + userRequest);


        chatAsyncTask = new ChatAsyncTask(chatRoom,msgRecyclerView,chatAppMsgAdapter,msgDtoList);
        chatAsyncTask.execute();
        //Object that makes request and updates the UI if the user is/isn't connected/writting
        inChatRunnable = new InChatRunnable(state,chatRoom);

        t = new Timer();
        startRunnable();
        startWritingListener();
        t1 = new Timer();

        h1 = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(android.os.Message msg) {
                bb = msg.getData();
                String str = bb.getString("TheState");
                try {
                    if (str.equals("disconnected")) {
                        Log.e("Checking DISCONNECT", "checking");
                        chatbox.setVisibility(View.INVISIBLE);
                        Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.red_dot);
                        connectDot.setBackground(drawable);
                    }
                    else {
                        Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.green_dot);
                        connectDot.setBackground(drawable);
                    }
                } catch(NullPointerException e){

                }
            }
        };


        t1.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                thread = new Thread(new DisconnectedRunnable(h1,state));
                thread.start();
            }

            @Override
            public boolean cancel() {
                return super.cancel();
            }
        },delay, periodForT1);




        Button msgSendButton = (Button)findViewById(R.id.button_chatbox_send);
        msgSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msgContent = msgInputText.getText().toString();
                chatRoom.stopIsWritting();
                if(!TextUtils.isEmpty(msgContent))
                {   //MessageTO is the class that is embedded to the RecyclerView because i was lazy to change the main Message Class
                    SimpleDateFormat df = new SimpleDateFormat("hh:mm");
                    String formattedDate = df.format(Calendar.getInstance().getTime());
                    MessageTO msgDto = new MessageTO("me",userRequest,msgContent,formattedDate,MessageTO.MSG_TYPE_SENT);
                    msgDtoList.add(msgDto);
                    int newMsgPosition = msgDtoList.size() - 1;
                    chatAppMsgAdapter.notifyItemInserted(newMsgPosition);
                    msgRecyclerView.scrollToPosition(newMsgPosition);
                    //Message to send
                    Timestamp time = new Timestamp(4242);
                    Message msgToSend = new Message(listenerRequest,userRequest,listenerRequest,msgContent, time,"no");
                    msgToSend.sendMsg();

                    msgInputText.setText("");
                }
            }
        });


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    @Override
    public void onBackPressed(){
        chatAsyncTask.cancel(true);
        t.cancel();
        t1.cancel();

        if(msgDtoList!=null && msgDtoList.size() > 1) {
            UserD userD = new UserD(userRequest, msgDtoList.get(msgDtoList.size() - 1).getContent(), msgDtoList);
            AppDatabase database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "user-database")
                    .allowMainThreadQueries()   //Allows room to do operation on main thread
                    .build();
            UserDao userDAO = database.userDao();

            UserD userF = userDAO.findByName(userRequest);
            if (userF != null) {
                userF.addMessages(msgDtoList);
                userDAO.update(userF);
            } else {
                userDAO.insertAll(userD);
            }
            database.close();
        }
        super.onBackPressed();
    }

    private void startRunnable(){
     t.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
            inChatRunnable.run();
        }

        @Override
        public boolean cancel() {
            return super.cancel();
        }
        },delay, periodForT);


    }

    private void startWritingListener(){
        //Following lines set your writting-flag true/false if you're writting
        final Handler handler = new Handler();
        final Runnable input_finish_checker = new Runnable() {
            @Override
            public void run() {
                chatRoom.stopIsWritting();
                if(System.currentTimeMillis() > (last_text_edit + periodForT - 500)){

                }
            }
        };

        msgInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(input_finish_checker);

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if(s.length() > 0){
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, periodForT);
                    chatRoom.userIsWritting();
                }
            }
        });
    }

}