package mahmud.osman.trend.admin.app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import mahmud.osman.trend.admin.app.fragment.TrendAdminFragment;


public class AdminNewsActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

      String KAY,TYPE;
      private ImageView news_image;
      private ImageButton fab_hide;
      private TextView subject;
      private Toolbar toolbar;
      private FloatingActionButton fab;
      private CollapsingToolbarLayout collapsingToolbarLayout;
      private AppBarLayout appBarLayout;
      private DatabaseReference databaseReference;


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
            TYPE = extra.getString("type");


            returnData(KAY,TYPE);
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


      private void returnData(String key,String type) {

            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.keepSynced(true);
            databaseReference.child(getString(R.string.Admin_news))
                    .child(getUID())
                    .child(type)
                    .child(key).addListenerForSingleValueEvent(
                    new ValueEventListener() {
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

      private String getUID() {
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            return id;
      }


      @Override
      public void onClick(View v) {
            switch (v.getId()) {

                  case R.id.fab:
                        PopupMenu fabMenu = new PopupMenu(AdminNewsActivity.this , fab);
                        fabMenu.getMenuInflater().inflate(R.menu.more_item , fabMenu.getMenu());
                        fabMenu.setOnMenuItemClickListener(this);
                        fabMenu.show();
                        break;
                  case R.id.fab_hide:
                        PopupMenu fabHideMenu = new PopupMenu(AdminNewsActivity.this , fab_hide);
                        fabHideMenu.getMenuInflater().inflate(R.menu.more_item , fabHideMenu.getMenu());
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
            AdminNewsActivity.super.onBackPressed();
      }

      @Override
      public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {
                  case R.id.edit:
                        Intent intent = new Intent(AdminNewsActivity.this , CreateNews.class);
                        intent.putExtra("edit" , KAY);
                        intent.putExtra("type" , TYPE);

                        startActivity(intent);
                        return true;
                  case R.id.delete:
                        showDeleteDialog(KAY);
                        return true;

            }

            return false;
      }

      private void showDeleteDialog(final String kay) {

            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.keepSynced(true);

            final Dialog delete_dialog = new Dialog(this);
            delete_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            delete_dialog.setContentView(R.layout.delete_dialog);
            delete_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            delete_dialog.getWindow().getAttributes();
            delete_dialog.setCancelable(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(delete_dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            Button d_cancel = delete_dialog.findViewById(R.id.d_cancel_btn);
            Button d_ok = delete_dialog.findViewById(R.id.d_yes_btn);

            d_ok.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                        databaseReference.child(getString(R.string.Admin_news)).child(getUID()).child(getString(R.string.sport)).child(kay).removeValue();
                        databaseReference.child(getString(R.string.User_news)).child(getString(R.string.sport)).child(kay).removeValue();
                        delete_dialog.dismiss();
                        Intent intent = new Intent(AdminNewsActivity.this, TrendAdminFragment.class);
                        startActivity(intent);
                        finish();
                  }
            });
            d_cancel.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                        delete_dialog.dismiss();
                  }
            });
            delete_dialog.show();
            delete_dialog.getWindow().setAttributes(lp);

      }

}
