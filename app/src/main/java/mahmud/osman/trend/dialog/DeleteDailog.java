package mahmud.osman.trend.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mahmud.osman.trend.R;

public class DeleteDailog {
      private Context context;
      private DatabaseReference databaseReference;
      private String UID;
      private String TYPE;

      public DeleteDailog(Context context , String UID , String TYPE) {
            this.context = context;
            this.UID = UID;
            this.TYPE = TYPE;
      }


      public void showDeleteDialog(final String kay) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
            final Dialog delete_dialog = new Dialog(context);
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
                        databaseReference.child(String.valueOf(R.string.Admin_news)).child(UID).child(TYPE).child(kay).removeValue();
                        databaseReference.child(String.valueOf(R.string.User_news)).child(TYPE).child(kay).removeValue();
                        delete_dialog.dismiss();
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
