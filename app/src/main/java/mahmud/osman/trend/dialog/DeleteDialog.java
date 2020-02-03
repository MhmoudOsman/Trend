package mahmud.osman.trend.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;

import mahmud.osman.trend.R;

public class DeleteDialog extends Dialog {
      public Button d_cancel, d_ok;


      public DeleteDialog(@NonNull Context context) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.delete_dialog);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().getAttributes();
            setCanceledOnTouchOutside(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            d_cancel = findViewById(R.id.d_cancel_btn);
            d_ok = findViewById(R.id.d_yes_btn);
            getWindow().setAttributes(lp);

      }


}



