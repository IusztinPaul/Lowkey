package fusionkey.lowkey.ROOMdatabase;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fusionkey.lowkey.R;
import fusionkey.lowkey.chat.ChatActivity;
import fusionkey.lowkey.chat.Message;
import fusionkey.lowkey.listAdapters.ChatServiceAdapters.ChatAppMsgAdapter;
import fusionkey.lowkey.main.Main2Activity;
import fusionkey.lowkey.models.MessageTO;
import fusionkey.lowkey.models.UserD;

public class MessagesActivity extends AppCompatActivity {

    private LinearLayout chatLayout;
    private TextView upperText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        chatLayout = findViewById(R.id.layout_chatbox);
        chatLayout.setVisibility(View.GONE);
        upperText = findViewById(R.id.isWritting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final ArrayList<MessageTO> msgDtoList = new ArrayList<MessageTO>();
        final RecyclerView msgRecyclerView = (RecyclerView)findViewById(R.id.reyclerview_message_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(linearLayoutManager);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        AppDatabase database = Room.databaseBuilder(this, AppDatabase.class, "user-database")
                .allowMainThreadQueries()   //Allows room to do operation on main thread
                .build();
        final UserDao userDAO = database.userDao();
        upperText.setText("Chat you had with " + username);
        UserD user = userDAO.findByName(username);
        for(MessageTO s : user.getListMessage())
            msgDtoList.add(new MessageTO(s.getSender(),s.getReceiver(),s.getContent(),s.getDate(),s.getMsgType()));

        final ChatAppMsgAdapter chatAppMsgAdapter = new ChatAppMsgAdapter(msgDtoList);
        msgRecyclerView.setAdapter(chatAppMsgAdapter);

        database.close();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessagesActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}