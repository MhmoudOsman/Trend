package mahmud.osman.trend.admin.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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


public class NewsItemActivity extends AppCompatActivity implements View.OnClickListener {

    String KAY;
    ImageView news_image;
    TextView supject;
    Toolbar toolbar;
    FloatingActionButton fab;
    Menu menu;
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_item_activity);
        Bundle extra = getIntent().getExtras();
        toolbar = findViewById(R.id.toolbar_collapsing);
        news_image = findViewById(R.id.expand_image);
        supject = findViewById(R.id.subject_scroll);
        fab = findViewById(R.id.fab);
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        appBarLayout = findViewById(R.id.c_app_bar);


        KAY = extra.getString("open");


        reternData(KAY);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_arrow_left);

        fab.setOnClickListener(this);

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
                    showOption(R.id.satting);

                } else {
                    isShow = false;
                    hideOption(R.id.satting);
                }


            }
        });


    }

    private void showOption(int menu) {

    }

    private void hideOption(int menu) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.more_bar , menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.edit:
                Intent intent = new Intent(NewsItemActivity.this , CreateNews.class);
                intent.putExtra("edit" , KAY);
                startActivity(intent);

                return true;
            case  R.id.delete:
                return  true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void reternData(String key) {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        mDatabase.child("Admin_news")
                .child(getUID())
                .child(getString(R.string.interview))
                .child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                NewsModel newsModel = dataSnapshot.getValue(NewsModel.class);

                getSupportActionBar().setTitle(newsModel.getTitl());
                collapsingToolbarLayout.setTitle(newsModel.getTitl());
                toolbar.setTitle(newsModel.getTitl());
                supject.setText(newsModel.getSubject());
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        NewsItemActivity.super.onBackPressed();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fab:
                PopupMenu popupMenu = new PopupMenu(NewsItemActivity.this , fab);
                popupMenu.getMenuInflater().inflate(R.menu.more_bar , popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
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
                });

                popupMenu.show();

        }
    }
}
