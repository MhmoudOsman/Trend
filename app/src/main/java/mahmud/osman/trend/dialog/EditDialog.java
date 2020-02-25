package mahmud.osman.trend.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.regex.Pattern;

import mahmud.osman.trend.R;

public class EditDialog extends Dialog {

      private final static Pattern NAME_PATTERN = Pattern.compile("[\\u0600-\\u065F\\u066A-\\u06EF\\u06FA-\\u06FFa-zA-Z ]+[\\u0600-\\u065F\\u066A-\\u06EF\\u06FA-\\u06FFa-zA-Z-_ ]");
      private final static Pattern MOBILE_PATTERN = Pattern.compile("(010|011|012|015)[0-9]{8}");

      public Button d_cancel, d_ok;
      public EditText d_edit;
      private TextView d_title;

      public EditDialog(@NonNull Context context , String title) {
            super(context);

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.edite_dialog);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().getAttributes();
            setCanceledOnTouchOutside(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            d_edit = findViewById(R.id.et_dialog);
            d_title = findViewById(R.id.tv_dialog);
            d_cancel = findViewById(R.id.cancel_dialog_btn);
            d_ok = findViewById(R.id.ok_dialog_btn);

            d_title.setText(title);
            d_edit.setSelected(true);
            d_edit.requestFocus();
            getWindow().setAttributes(lp);

      }

      public boolean isNameValied() {
            String text = d_edit.getText().toString();
            if (text.isEmpty()) {
                  d_edit.setError(getContext().getString(R.string.empty_name));
                  return false;
            } else if (!NAME_PATTERN.matcher(text).matches()) {
                  d_edit.setError(getContext().getString(R.string.invalid_name));
                  return false;
            }else {
                  return true;
            }

      }
      public boolean isMobileValied() {
            String text = d_edit.getText().toString();
            if (text.isEmpty()) {
                  d_edit.setError(getContext().getString(R.string.empty_mobile));
                  return false;
            } else if (!MOBILE_PATTERN.matcher(text).matches()) {
                  d_edit.setError(getContext().getString(R.string.invalid_mobile));
                  return false;
            }else {
                  return true;
            }

      }
}
