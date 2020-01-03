package mahmud.osman.trend.admin.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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


public class NewsItemActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    String KAY;
    ImageView news_image;
    ImageButton fab_hide;
    TextView subject;
    Toolbar toolbar;
    FloatingActionButton fab;
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;


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
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        appBarLayout = findViewById(R.id.c_app_bar);


        KAY = extra.getString("open");


        returnData(KAY);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_arrow_left);

        fab.setOnClickListener(this);
        fab_hide.setOnClickListener(this);

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
                    showOption(fab_hide);

                } else {
                    isShow = false;
                    hideOption(fab_hide);
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
        mDatabase.child("Admin_news")
                .child(getUID())
                .child(getString(R.string.interview))
                .child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                NewsModel newsModel = dataSnapshot.getValue(NewsModel.class);

//                getSupportActionBar().setTitle(newsModel.getTitl());
                collapsingToolbarLayout.setTitle(newsModel.getTitl());
                toolbar.setTitle(newsModel.getTitl());
                subject.setText(newsModel.getSubject());
                Picasso.get().load(newsModel.getImage_uri())
                        .placeholder(R.drawable.newspaper)
                        .error(R.drawable.newspaper)
                        .into(news_image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private String getUID() {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return id;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fab:
                PopupMenu fabMenu = new PopupMenu(NewsItemActivity.this , fab);
                fabMenu.getMenuInflater().inflate(R.menu.more_item , fabMenu.getMenu());
                fabMenu.setOnMenuItemClickListener(this);
                fabMenu.show();
                break;
            case R.id.fab_hide:
                PopupMenu fabHideMenu = new PopupMenu(NewsItemActivity.this,fab_hide);
                fabHideMenu.getMenuInflater().inflate(R.menu.more_item,fabHideMenu.getMenu());
                fabHideMenu.setOnMenuItemClickListener(this);
                fabHideMenu.show();
                break;


        }
    }

    private void showOption(ImageButton fab_hide) {
        fab_hide.setVisibility(View.VISIBLE);
    }

    private void hideOption(ImageButton fab_hide) {
       fab_hide.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        NewsItemActivity.super.onBackPressed();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit:
                Intent intent = new Intent(NewsItemActivity.this , CreateNews.class);
                intent.putExtra("edit" , KAY);
                startActivity(intent);
                return true;
            case R.id.delete:
                return true;

        }

        return false;
    }
}
