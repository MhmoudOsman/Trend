package mahmud.osman.trend.user.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leinardi.android.speeddial.SpeedDialView;
import com.squareup.picasso.Picasso;

import mahmud.osman.trend.Models.NewsModel;
import mahmud.osman.trend.R;

import static mahmud.osman.trend.utils.Utils.YOUTUBE_API_KAY;
import static mahmud.osman.trend.utils.Utils.initialYouTubeVideo;
import static mahmud.osman.trend.utils.Utils.timestampToDateString;


public class UserNewsActivity extends AppCompatActivity {

    String KAY, TYPE;
    private ImageView news_image;
    private TextView subject, ex_title, cl_title, writer, date;
    private SpeedDialView fab;
    private CardView card_title;
    private YouTubePlayerFragment youtubeFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_item_activity);

        Bundle extra = getIntent().getExtras();
        Toolbar toolbar = findViewById(R.id.toolbar_collapsing);

        youtubeFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_player);
        news_image = findViewById(R.id.expand_image);
        subject = findViewById(R.id.subject_scroll);
        writer = findViewById(R.id.tv_writer);
        date = findViewById(R.id.tv_date);
        ex_title = findViewById(R.id.tv_title);
        cl_title = findViewById(R.id.tb_title);
        fab = findViewById(R.id.fab);
        card_title = findViewById(R.id.cv_title);

        int dpValue = 10; // margin in dips
        float d = getApplicationContext().getResources().getDisplayMetrics().density;
        int margin = (int)(dpValue * d);

        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMarginStart(margin);
        params.setMarginEnd(margin);
        params.setAnchorId(R.id.c_app_bar);
        params.anchorGravity = Gravity.BOTTOM;
        card_title.setLayoutParams(params);
        subject.setMovementMethod(LinkMovementMethod.getInstance());

        AppBarLayout appBarLayout = findViewById(R.id.c_app_bar);


        KAY = extra.getString("open");
        TYPE = extra.getString("type");


        returnData(KAY,TYPE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_arrow_left);

        fab.setVisibility(View.GONE);
        ex_title.setSelected(true);
        ex_title.requestFocus();
        cl_title.setSelected(true);
        cl_title.requestFocus();


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout , int i) {
                if (scrollRange == -1) {

                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + i == 0) {
                    isShow = true;
                    card_title.setVisibility(View.GONE);
                    cl_title.setVisibility(View.VISIBLE);
                } else {
                    isShow = false;
                    card_title.setVisibility(View.VISIBLE);
                    cl_title.setVisibility(View.GONE);
                }


            }
        });


    }



    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void returnData(String key , String TYPE) {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        mDatabase.child(getString(R.string.User_news))
                .child(TYPE)
                .child(key).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                NewsModel newsModel = dataSnapshot.getValue(NewsModel.class);
                String[] youtube_link;
                String video_link;
                if (newsModel != null) {
                    if (!TextUtils.isEmpty(newsModel.getSubject())) {
                        subject.setText(newsModel.getSubject());
                    } else {
                        subject.setVisibility(View.GONE);
                    }
                    if (!TextUtils.isEmpty(newsModel.getVideo_url())) {
                        youtube_link = newsModel.getVideo_url().split("/");
                        video_link = youtube_link[youtube_link.length - 1];
                        initialYouTubeVideo(youtubeFragment, video_link, YOUTUBE_API_KAY);
                        youtubeFragment.getView().setVisibility(View.VISIBLE);
                    } else {
                        youtubeFragment.getView().setVisibility(View.GONE);
                    }

                    cl_title.setText(newsModel.getTitle());
                    ex_title.setText(newsModel.getTitle());
                    writer.setText("كتب : " + newsModel.getWriter());
                    date.setText(timestampToDateString((long) newsModel.getDate()));

                    Picasso.get()
                            .load(newsModel.getImage_uri())
                            .placeholder(R.drawable.defult_pic)
                            .error(R.drawable.defult_pic)
                            .into(news_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        returnData(KAY, TYPE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        returnData(KAY, TYPE);
    }

    @Override
    public void onBackPressed() {
        UserNewsActivity.super.onBackPressed();
    }


}
