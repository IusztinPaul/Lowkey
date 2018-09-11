package fusionkey.lowkey.chat;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fusionkey.lowkey.R;
import fusionkey.lowkey.ROOMdatabase.AppDatabase;
import fusionkey.lowkey.ROOMdatabase.UserDao;
import fusionkey.lowkey.chat.Runnables.InChatRunnable;
import fusionkey.lowkey.listAdapters.ChatServiceAdapters.ChatAppMsgAdapter;
import fusionkey.lowkey.main.Main2Activity;
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

    final long delay = 1000;
    long last_text_edit=0;
    ChatAsyncTask chatAsyncTask;
    Timer t;
    String listenerRequest;
    String userRequest;
    ArrayList<MessageTO> msgDtoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        //INIT
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final TextView isWritting = (TextView) findViewById(R.id.isWritting);
        final String listener = getIntent().getStringExtra("Listener");
        final String user = getIntent().getStringExtra("User");
        listenerRequest = listener.replace("[", "").replace("]", "").replace("\"","");
        userRequest = user.replace("[", "").replace("]", "").replace("\"","");
        final ChatRoom chatRoom = new ChatRoom(userRequest,listenerRequest); //C - D
        msgDtoList = new ArrayList<MessageTO>();
        final RecyclerView msgRecyclerView = (RecyclerView)findViewById(R.id.reyclerview_message_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(linearLayoutManager);
        final ChatAppMsgAdapter chatAppMsgAdapter = new ChatAppMsgAdapter(msgDtoList);
        msgRecyclerView.setAdapter(chatAppMsgAdapter);
        Log.e("INFO","LISTENER : " + listenerRequest + " & USER : " + userRequest);


        chatAsyncTask = new ChatAsyncTask(chatRoom,msgRecyclerView,chatAppMsgAdapter,msgDtoList);
        chatAsyncTask.execute();

        //Object that makes request and updates the UI if the user is/isn't connected/writting
        final InChatRunnable inChatRunnable = new InChatRunnable(isWritting,chatRoom);
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                inChatRunnable.run();

            }

            @Override
            public boolean cancel() {
                return super.cancel();
            }
        },0,500);



        //Following lines set your writting-flag true/false if you're writting
        final Handler handler = new Handler();
        final Runnable input_finish_checker = new Runnable() {
            @Override
            public void run() {
                chatRoom.stopIsWritting();
                if(System.currentTimeMillis() > (last_text_edit + delay - 500)){

                }
            }
        };
        final EditText msgInputText = (EditText)findViewById(R.id.chat_input_msg);
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
                    handler.postDelayed(input_finish_checker,delay);
                    chatRoom.userIsWritting();
                }
            }
        });


        Button msgSendButton = (Button)findViewById(R.id.button_chatbox_send);
        msgSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msgContent = msgInputText.getText().toString();
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
                chatAsyncTask.cancel(true);
                t.cancel();
                if(msgDtoList!=null && msgDtoList.size() > 0) {
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
                Intent intent = new Intent(ChatActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });


    }
    @Override
    public void onBackPressed(){
        chatAsyncTask.cancel(true);
        t.cancel();
        if(msgDtoList!=null) {
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



}