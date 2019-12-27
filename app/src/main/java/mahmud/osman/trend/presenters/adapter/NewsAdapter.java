package mahmud.osman.trend.presenters.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import mahmud.osman.trend.Models.NewsModel;
import mahmud.osman.trend.R;
import mahmud.osman.trend.admin.app.NewsItemActivity;
import mahmud.osman.trend.presenters.holders.NewsHolders;

public class NewsAdapter extends RecyclerView.Adapter<NewsHolders> {
    private List<NewsModel> newsModels;
    private Context context;

    public NewsAdapter(List<NewsModel> newsModels, Context context){
        this.context = context;
        this.newsModels = newsModels;
    }

    public Context getContext() {
        return context;
    }

    @NonNull
    @Override
    public NewsHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new NewsHolders(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NewsHolders newsholder, final int position) {
        NewsModel newsModel = newsModels.get(position);

        Picasso.get().load(newsModel.getImage_uri()).into(newsholder.item_image);
        newsholder.title.setText(newsModel.getTitl());
        newsholder.subject.setText(newsModel.getSubject());
        newsholder.writer.setText(newsModel.getWriter());
        newsholder.date.setText(newsModel.getDate());
        newsholder.card_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), NewsItemActivity.class);
                context.startActivity(i);
            }
        });
        newsholder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(),newsholder.more);

                popupMenu.getMenuInflater().inflate(R.menu.more_item,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.edit:
                                return true;
                            case R.id.delete:
                                return true;

                        }
                        return true;
                    }
                });
                popupMenu.show();

            }
        });


    }

    @Override
    public int getItemCount() {
        return newsModels.size();
    }
}
