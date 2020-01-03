package mahmud.osman.trend.presenters.holders;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;

import mahmud.osman.trend.R;

public class NewsHolders extends RecyclerView.ViewHolder {

    public ImageView item_image;
    public ImageButton more;
    public TextView title, subject, writer,date;
    public MaterialRippleLayout card_item;

    public NewsHolders(@NonNull View itemView) {
        super(itemView);
        item_image = itemView.findViewById(R.id.item_image);
        more = itemView.findViewById(R.id.more_btn);
        title = itemView.findViewById(R.id.item_title);
        title.setSelected(true);
        title.requestFocus();
        subject = itemView.findViewById(R.id.item_subject);
        writer = itemView.findViewById(R.id.item_writer);
        date = itemView.findViewById(R.id.item_date);
        card_item = itemView.findViewById(R.id.item_click);

    }

}
