package fusionkey.lowkey.ROOMdatabase;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;
import fusionkey.lowkey.R;
import fusionkey.lowkey.auth.models.UserDB;
import fusionkey.lowkey.auth.utils.UserAttributeManager;
import fusionkey.lowkey.auth.utils.UserManager;
import fusionkey.lowkey.listAdapters.ChatServiceAdapters.ChatAppMsgAdapter;
import fusionkey.lowkey.main.utils.Callback;
import fusionkey.lowkey.main.utils.NetworkManager;
import fusionkey.lowkey.main.utils.ProfilePhotoUploader;
import fusionkey.lowkey.models.UserD;

public class MessagesActivity extends AppCompatActivity {
    public static final String OTHER_USER_EMAIL = "otherUserEmail";

    private LinearLayout chatLayout;
    private TextView upperText;
    private CircleImageView image;
    public static String USERNAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        chatLayout = findViewById(R.id.layout_chatbox);
        chatLayout.setVisibility(View.GONE);
        upperText = findViewById(R.id.isWritting);
        image = findViewById(R.id.circleImageView2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final RecyclerView msgRecyclerView = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(linearLayoutManager);

        Intent intent = getIntent();
        String otherUserEmail = intent.getStringExtra(OTHER_USER_EMAIL);
        String otherUserParsedEmail = UserManager.parseEmailToPhotoFileName(otherUserEmail);

        AppDatabase database = Room.databaseBuilder(this, AppDatabase.class, "user-database")
                .allowMainThreadQueries()   //Allows room to do operation on main thread
                .build();
        final UserDao userDAO = database.userDao();

        UserDB userDB = new UserAttributeManager(otherUserEmail).getUserDB();
        USERNAME = userDB.getUsername();

        try {
            upperText.setText(USERNAME);
        } catch (NullPointerException npe) {
            USERNAME = "not found";
            upperText.setText("Not found");
        }

        final ProfilePhotoUploader photoUploader = new ProfilePhotoUploader();
        photoUploader.download(otherUserParsedEmail,
                new Callback() {
                    @Override
                    public void handle() {
                        Log.e("PHOTO", "photo downloaded");
                        Picasso.with(getApplicationContext()).load((photoUploader.getFileTO())).into(image);
                    }
                }, new Callback() {
                    @Override
                    public void handle() {
                        Picasso.with(getApplicationContext()).load((R.drawable.avatar_placeholder)).into(image);
                    }
                });

        if (!NetworkManager.isNetworkAvailable())
            Toast.makeText(getApplicationContext(), "Check if you're connected to the Internet", Toast.LENGTH_SHORT).show();

        UserD user = userDAO.findByName(otherUserEmail);
        final ChatAppMsgAdapter chatAppMsgAdapter = new ChatAppMsgAdapter(user.getListMessage());
        msgRecyclerView.setAdapter(chatAppMsgAdapter);

        database.close();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}