package mahmud.osman.trend.presenters.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import mahmud.osman.trend.Models.NewsModel;
import mahmud.osman.trend.R;
import mahmud.osman.trend.admin.app.AdminNewsActivity;
import mahmud.osman.trend.admin.app.CreateNews;
import mahmud.osman.trend.dialog.DeleteDailog;
import mahmud.osman.trend.presenters.holders.NewsHolders;
import mahmud.osman.trend.user.app.UserNewsActivity;

public class NewsAdaptor extends FirebaseRecyclerAdapter<NewsModel, NewsHolders> {
      /**
       * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
       * {@link FirebaseRecyclerOptions} for configuration options.
       *
       * @param options
       */
      private Context context;
      private String TYPE;
      private String UID;

      public NewsAdaptor(@NonNull FirebaseRecyclerOptions<NewsModel> options , Context context , String TYPE , String UID) {
            super(options);
            this.context = context;
            this.TYPE = TYPE;
            this.UID = UID;
      }


      public NewsAdaptor(@NonNull FirebaseRecyclerOptions<NewsModel> options , Context context , String TYPE) {
            super(options);
            this.context = context;
            this.TYPE = TYPE;
      }


      @Override
      protected void onBindViewHolder(@NonNull final NewsHolders newsHolders , final int position , @NonNull NewsModel newsModel) {

            if (TextUtils.isEmpty(UID)) {
                  newsHolders.more.setVisibility(View.GONE);
            }
            Picasso.get()
                    .load(newsModel.getImage_uri())
                    .placeholder(R.drawable.newspaper)
                    .error(R.drawable.newspaper)
                    .into(newsHolders.item_image);

            newsHolders.title.setText(newsModel.getTitl());
            newsHolders.subject.setText(newsModel.getSubject());
            newsHolders.writer.setText(newsModel.getWriter());
            newsHolders.date.setText(newsModel.getDate());

            newsHolders.card_item.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                        if (TextUtils.isEmpty(UID)) {
                              String kay = getRef(position).getKey();
                              Intent i = new Intent(context , UserNewsActivity.class);
                              i.putExtra("open" , kay);
                              i.putExtra("type" , TYPE);
                              context.startActivity(i);
                        } else {
                              String kay = getRef(position).getKey();
                              Intent i = new Intent(context , AdminNewsActivity.class);
                              i.putExtra("open" , kay);
                              i.putExtra("type" , TYPE);
                              context.startActivity(i);
                        }
                  }
            });
            newsHolders.more.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {


                        final String kay = getRef(position).getKey();
                        final PopupMenu popupMenu = new PopupMenu(context , newsHolders.more);

                        popupMenu.getMenuInflater().inflate(R.menu.more_item , popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                              @Override
                              public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                          case R.id.edit:

                                                Intent intent = new Intent(context , CreateNews.class);
                                                intent.putExtra("edit" , kay);
                                                intent.putExtra("type" , TYPE);
                                                context.startActivity(intent);

                                                return true;
                                          case R.id.delete:

                                                DeleteDailog deleteDailog = new DeleteDailog(context , UID , TYPE);
                                                deleteDailog.showDeleteDialog(kay);

                                                return true;

                                    }
                                    return true;
                              }
                        });
                        popupMenu.show();

                  }
            });


      }

      @NonNull
      @Override
      public NewsHolders onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item , null);
            return new NewsHolders(view);
      }
}



