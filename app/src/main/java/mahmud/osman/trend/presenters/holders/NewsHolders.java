package mahmud.osman.trend.presenters.holders;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;

import java.security.Key;

import mahmud.osman.trend.R;
import mahmud.osman.trend.admin.app.AdminNewsActivity;
import mahmud.osman.trend.admin.app.CreateNews;

public class NewsHolders extends RecyclerView.ViewHolder  {

    public ImageView item_image;
    public TextView title, subject, writer,date;
    public MaterialRippleLayout card_item;

    public NewsHolders(@NonNull View itemView) {
        super(itemView);
        item_image = itemView.findViewById(R.id.item_image);
        title = itemView.findViewById(R.id.item_title);
        title.setSelected(true);
        title.requestFocus();
        subject = itemView.findViewById(R.id.item_subject);
        writer = itemView.findViewById(R.id.item_writer);
        date = itemView.findViewById(R.id.item_date);
        card_item = itemView.findViewById(R.id.item_click);
    }
}
