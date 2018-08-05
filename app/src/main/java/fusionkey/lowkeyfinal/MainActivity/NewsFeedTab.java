package fusionkey.lowkeyfinal.MainActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;

import fusionkey.lowkeyfinal.Adapters.ChatTabAdapter;
import fusionkey.lowkeyfinal.Chat;
import fusionkey.lowkeyfinal.EntryActivity.EntryActivity;
import fusionkey.lowkeyfinal.Login.LoginActivity;
import fusionkey.lowkeyfinal.Models.NewsFeedMessage;
import fusionkey.lowkeyfinal.R;

public class NewsFeedTab extends Fragment{
    ChatTabAdapter adapter;
    ArrayList<NewsFeedMessage> messages;

    private RecyclerView msgRecyclerView;


    private Paint p = new Paint();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_newsfeed, container, false);
        msgRecyclerView = (RecyclerView) rootView.findViewById(R.id.chat_listview);

        // Set RecyclerView layout manager.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        msgRecyclerView.setHasFixedSize(true);
        msgRecyclerView.setLayoutManager(linearLayoutManager);

        // Create the initial data list.
        messages = new ArrayList<NewsFeedMessage>();

        NewsFeedMessage m1 = new NewsFeedMessage();m1.setUser("Paul");m1.setDate("12:22");m1.setContent("auctor pulvinar quis et lacus. Aliquam cursus erat pharetra magna accumsan imperdiet. Donec vestibulum efficitur enim vel commodo. In in posuere libero, et vestibulum metus. Suspendisse quis pulvinar urna.");
        NewsFeedMessage m2 = new NewsFeedMessage();m2.setUser("Sebi");m2.setDate("12:54");m2.setContent("a aliquet nulla porta non. Nulla sit amet mi efficitur, egestas justo ac, semper mi. Nunc nec rhoncus neque. Nullam nec tristique dolor. Nam eget rhoncus ni");
        NewsFeedMessage m3 = new NewsFeedMessage();m3.setUser("Ion");m3.setDate("13:55");m3.setContent("Morbi neque elit, dapibus ac diam scelerisque, tincidunt semper arcu. Vivamus gravida nibh eu tempor fringilla. Sed finibus, leo in vehicula ");
        NewsFeedMessage m4 = new NewsFeedMessage();m4.setUser("Radu");m4.setDate("15:11");m4.setContent("ibulum semper, est quis posuere venenatis, eros ante vulputate nibh, in vulputate risus eros sit amet ex. Curabitur enim iet. Donec vestibulum efficitur enim vel commodo. In in posuere libero, et vestibulum metus. Suspendisse quis pulvinar urna.");
        NewsFeedMessage m5 = new NewsFeedMessage();m5.setUser("Calin");m5.setDate("19:42");m5.setContent("tibulum metus. Suspendisse quis pulvinar urna.");
        NewsFeedMessage m6 = new NewsFeedMessage();m6.setUser("Vladimir Putin");m6.setDate("20:22");m6.setContent("auctops. Aliquam cursus erat picitur enim vo. In in posuere libero, et vestibulum metus. Suspendisse quis pulvinar urna.");
        NewsFeedMessage m7 = new NewsFeedMessage();m7.setUser("Osama Binladen");m7.setDate("21:22");m7.setContent("auctor pulvinan imperdieficitur enn in posuere libero, et vestibulum metus. Suspendisse quis pulvinar urna.");
        NewsFeedMessage m8 = new NewsFeedMessage();m8.setUser("ANON");m8.setDate("23:22");m8.setContent("auctor pulvinar quis et lacus. Aliharetra magnaefficitur enim vel commodo.e libero, et vestibulum metus. Suspendisse quis pulvinar urna.");
        m1.setAnswers("ANSWERS : 123");m2.setAnswers("ANSWERS : 12");m3.setAnswers("ANSWERS : 43");
        m4.setAnswers("ANSWERS : 1");m6.setAnswers("ANSWERS : 3");m8.setAnswers("ANSWERS : 10");
        m5.setAnswers("ANSWERS : 4");m7.setAnswers("ANSWERS : 500");
        messages.add(m1);messages.add(m2);messages.add(m3);
        messages.add(m8);messages.add(m7);messages.add(m4);
        messages.add(m6);messages.add(m5);

        messages.add(m1);
        adapter = new ChatTabAdapter(messages);
        msgRecyclerView.setAdapter(adapter);
        initSwipe();
        return rootView;
    }

    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView MsgrecyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT){
                    adapter.removeItem(position);
                } else {
                    //removeView();
                    Intent intent = new Intent(getContext(), Chat.class);
                    startActivity(intent);
                    adapter.removeItem(position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){

                        p.setColor(Color.parseColor("#27AE60"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        Drawable drawable = ContextCompat.getDrawable(getContext(),R.drawable.selecttab1);
                        icon = drawableToBitmap(drawable);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#FF4081"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        Drawable drawable = ContextCompat.getDrawable(getContext(),R.drawable.selecttab2);
                        icon = drawableToBitmap(drawable);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(msgRecyclerView);
    }
    private void removeView(){
        if(getParentFragment().getView().getParent()!=null) {
            ((ViewGroup) getParentFragment().getView().getParent()).removeView(getParentFragment().getView());
        }
    }
    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}