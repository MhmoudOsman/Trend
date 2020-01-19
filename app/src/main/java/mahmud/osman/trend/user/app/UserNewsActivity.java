package mahmud.osman.trend.user.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import mahmud.osman.trend.Models.NewsModel;
import mahmud.osman.trend.R;
import mahmud.osman.trend.admin.app.CreateNews;


public class UserNewsActivity extends AppCompatActivity {

    String KAY;
    ImageView news_image;
    ImageButton fab_hide;
    TextView subject ;
    Toolbar toolbar;
    FloatingActionButton fab;
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_item_activity);

        Bundle extra = getIntent().getExtras();

        toolbar = findViewById(R.id.toolbar_collapsing);
        news_image = findViewById(R.id.expand_image);
        subject = findViewById(R.id.subject_scroll);
        fab = findViewById(R.id.fab);
        fab_hide = findViewById(R.id.fab_hide);
        appBarLayout = findViewById(R.id.c_app_bar);
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);


        KAY = extra.getString("open");


        returnData(KAY);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_arrow_left);

        fab.setVisibility(View.GONE);
        fab_hide.setVisibility(View.GONE);

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
                } else {
                    isShow = false;
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


    private void returnData(String key) {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        mDatabase.child(getString(R.string.User_news))
                .child(getString(R.string.interview))
                .child(key).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                NewsModel newsModel = dataSnapshot.getValue(NewsModel.class);

                toolbar.setTitle(newsModel.getTitl());
                collapsingToolbarLayout.setTitle(newsModel.getTitl());
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
