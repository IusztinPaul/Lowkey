package fusionkey.lowkey.chat;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.ROOMdatabase.AppDatabase;
import fusionkey.lowkey.ROOMdatabase.UserDao;
import fusionkey.lowkey.auth.models.UserDB;
import fusionkey.lowkey.auth.utils.UserManager;
import fusionkey.lowkey.chat.Runnables.DisconnectedRunnable;
import fusionkey.lowkey.chat.Runnables.InChatRunnable;
import fusionkey.lowkey.chat.models.MessageTOFactory;
import fusionkey.lowkey.listAdapters.ChatServiceAdapters.ChatAppMsgAdapter;
import fusionkey.lowkey.chat.models.MessageTO;
import fusionkey.lowkey.auth.utils.*;
import fusionkey.lowkey.main.utils.Callback;
import fusionkey.lowkey.main.utils.EmailBuilder;
import fusionkey.lowkey.main.utils.PhotoUploader;
import fusionkey.lowkey.main.utils.PhotoUtils;
import fusionkey.lowkey.main.utils.ProfilePhotoUploader;
import fusionkey.lowkey.models.UserD;
import fusionkey.lowkey.pointsAlgorithm.PointsCalculator;

/**
 * @author Sandru Sebastian
 * @version 1.0
 * @since 24-Aug-18
 *
 * <h1>MAIN CHAT ACTIVITY</h1>
 *
 */
public class ChatActivity extends AppCompatActivity {
    private final int GALLERY_REQUEST = 1;
    private final int PHOTO_SCORE_POINTS = 3;
    private final int POSITIVE_BUTTON_REVIEW_POINTS = 5;

    final long periodForT = 1000, periodForT1 =10000, delay=0;
    long last_text_edit=0;

    String role;
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
    private CircleImageView image;
    String userRequest;
    ArrayList<MessageTO> msgDtoList;

    private int stringCounter,
            clock;
    ArrayList<Integer> stringL;

    private static final String disconnectedDialog = "just disconnected from the chat !";
    private static final String listenerIntent ="Listener";
    private static final String userIntent ="User";
    private static final String roleIntent = "role";

    private ChatAppMsgAdapter chatAppMsgAdapter;
    private RecyclerView msgRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        //INIT
        Toolbar toolbar = findViewById(R.id.toolbar);
        state = findViewById(R.id.isWritting);
        connectDot = findViewById(R.id.textView8);
        msgRecyclerView = findViewById(R.id.reyclerview_message_list);
        final String listener = getIntent().getStringExtra(listenerIntent);
        final String user = getIntent().getStringExtra(userIntent);
        role = getIntent().getStringExtra(roleIntent);
        chatbox = findViewById(R.id.layout_chatbox);
        image = findViewById(R.id.circleImageView2);
        msgInputText = findViewById(R.id.chat_input_msg);
        listenerRequest = listener.replace("[", "").replace("]", "").replace("\"","");
        userRequest = user.replace("[", "").replace("]", "").replace("\"","");
        chatRoom = new ChatRoom(userRequest,listenerRequest);
        msgDtoList = new ArrayList<>();
        stringL = new ArrayList<>();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(linearLayoutManager);
        chatAppMsgAdapter = new ChatAppMsgAdapter(msgDtoList);
        msgRecyclerView.setAdapter(chatAppMsgAdapter);
        Log.e("INFO","LISTENER : " + listenerRequest + " & USER : " + userRequest);


        chatAsyncTask = new ChatAsyncTask(chatRoom,msgRecyclerView,chatAppMsgAdapter,msgDtoList);
        chatAsyncTask.execute();

        //Object that makes request and updates the UI if the user is/isn't connected/writting
        inChatRunnable = new InChatRunnable(state,chatRoom);

        String email = EmailBuilder.buildEmail(userRequest);

        final ProfilePhotoUploader photoUploader = new ProfilePhotoUploader();
        photoUploader.download(UserManager.parseEmailToPhotoFileName(email),
                new Callback() {
                    @Override
                    public void handle() {
                        Log.e("PHOTO", "photo downloaded");
                        Picasso.with(getApplicationContext()).load((photoUploader.getFileTO())).into(image);
                    }
                }, null);


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

                    }
                    else {

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


        Button msgSendButton = (Button)findViewById(R.id.sendComment);
        msgSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msgContent = msgInputText.getText().toString();
                chatRoom.stopIsWritting();
                if(!TextUtils.isEmpty(msgContent))
                {
                    processMessage(msgContent, false);
                    msgInputText.setText("");
                }
            }
        });

        Button msgSendPhoto = findViewById(R.id.sendPhoto);
        msgSendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForImage();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    @Override
    public void onBackPressed(){
        chatAsyncTask.cancel(true);
        t.cancel();
        t1.cancel();


        if(msgDtoList!=null && msgDtoList.size() > 0) {
            String lastMessage;
            if((msgDtoList.get(msgDtoList.size()-1).getContentType())==1)
                lastMessage = "user sent a photo";
            else
                lastMessage = msgDtoList.get(msgDtoList.size()-1).getRawContent();

            UserD userD = new UserD(userRequest, lastMessage, msgDtoList,role);

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


         updatePoints();
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST:
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                        // Resize image and serialize it before saving it.
                        bitmap = PhotoUtils.resizeBitmap(bitmap, PhotoUtils.LARGE);
                        String msgContent = new PhotoUploader.BitMapOperator(bitmap).serializeToString();

                        processMessage(msgContent, true);
                    } catch (IOException e) {
                        Log.e("GalleryRequest", e.getMessage());
                    }
                    break;
            }
    }

    private void showDialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Was this helpful ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        //rebuild the email
                        String userEmail = EmailBuilder.buildEmail(userRequest);
                        Log.e("userEmail ", userEmail);

                        UserAttributeManager userAttributeManager = new UserAttributeManager(userEmail);
                        UserDB user = userAttributeManager.getUserDB();
                        user.setScore(user.getScore() + POSITIVE_BUTTON_REVIEW_POINTS);
                        userAttributeManager.updateUserAttributes(null);

                        onBackPressed();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        onBackPressed();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void updatePoints(){
        UserDB user = LowKeyApplication.userManager.getUserDetails();
        long newScore = user.getScore() + (long) PointsCalculator.calculateStringsValue(stringCounter,stringL,clock);
        updateUserWithNewScore(user, newScore);
        Log.e("points ::::: ", "score"+newScore);
    }

    private void updateUserWithNewScore(UserDB user, long newScore) {
        user.setScore(newScore);
        LowKeyApplication.userManager.updateCurrentUser(user);
    }

    private void startRunnable(){
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                clock++;
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

    private void askForImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    private void processMessage(String msgContent, boolean isPhoto) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        addMsgToAdapter(msgContent, isPhoto, timestamp);
        sendMessageRequest(msgContent, isPhoto, timestamp);
        calculateScore(msgContent, isPhoto);
    }

    private void addMsgToAdapter(String msgContent, boolean isPhoto, Timestamp timestamp) {
        MessageTO msgDto = new MessageTOFactory("me",userRequest,timestamp.getTime(),
                msgContent,isPhoto, MessageTO.MSG_TYPE_SENT).createMessage();
        msgDtoList.add(msgDto);
        int newMsgPosition = msgDtoList.size() - 1;

        chatAppMsgAdapter.notifyItemInserted(newMsgPosition);
        msgRecyclerView.scrollToPosition(newMsgPosition);
    }

    private void sendMessageRequest(String msgContent, boolean isPhoto, Timestamp timestamp) {
        Message msgToSend = new Message(listenerRequest,userRequest,listenerRequest,
                msgContent, timestamp,isPhoto + "");
        msgToSend.sendMsg();
    }

    private void calculateScore(String msgContent, boolean isPhoto) {
        int score = -1;
        if(isPhoto)
            score = PHOTO_SCORE_POINTS;
        else if(msgContent != null)
            score = msgContent.length();

        if(score != -1) {
            stringL.add(score);
            stringCounter++;
        }
    }

}
