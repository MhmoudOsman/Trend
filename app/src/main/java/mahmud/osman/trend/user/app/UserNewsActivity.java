package mahmud.osman.trend.user.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leinardi.android.speeddial.SpeedDialView;
import com.squareup.picasso.Picasso;

import mahmud.osman.trend.Models.NewsModel;
import mahmud.osman.trend.R;
import mahmud.osman.trend.dialog.DeleteDialog;


public class UserNewsActivity extends AppCompatActivity {

    String KAY, TYPE;
    private ImageView news_image;
    private TextView subject, ex_title, cl_title;
    private SpeedDialView fab;
    private DatabaseReference databaseReference;
    private Menu menu;
    private CardView card_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_item_activity);

        Bundle extra = getIntent().getExtras();
        Toolbar toolbar = findViewById(R.id.toolbar_collapsing);
        news_image = findViewById(R.id.expand_image);
        subject = findViewById(R.id.subject_scroll);
        ex_title = findViewById(R.id.tv_title);
        cl_title = findViewById(R.id.tb_title);
        fab = findViewById(R.id.fab);
        card_title = findViewById(R.id.cv_title);

        AppBarLayout appBarLayout = findViewById(R.id.c_app_bar);


        KAY = extra.getString("open");
        TYPE = extra.getString("type");


        returnData(KAY,TYPE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_arrow_left);

        fab.setVisibility(View.GONE);
        cl_title.setVisibility(View.GONE);


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

                cl_title.setText(newsModel.getTitl());
                ex_title.setText(newsModel.getTitl());
                subject.setText(newsModel.getSubject());
                Picasso.get()
                        .load(newsModel.getImage_uri())
                        .placeholder(R.drawable.newspaper)
                        .error(R.drawable.newspaper)
                        .into(news_image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        UserNewsActivity.super.onBackPressed();
    }

}
