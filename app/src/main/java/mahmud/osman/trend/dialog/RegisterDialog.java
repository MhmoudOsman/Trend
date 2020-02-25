package mahmud.osman.trend.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Or;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import mahmud.osman.trend.R;
import mahmud.osman.trend.presenters.adapter.TextInputLayoutAdapter;

public class RegisterDialog extends Dialog implements Validator.ValidationListener {
      //Dialog
      public Button cancel_btn, create_btn;
      public CircleImageView profile_pic;

      @NotEmpty(messageResId = R.string.empty_name)
      @Pattern(regex = "[\\u0600-\\u065F\\u066A-\\u06EF\\u06FA-\\u06FFa-zA-Z ]+[\\u0600-\\u065F\\u066A-\\u06EF\\u06FA-\\u06FFa-zA-Z-_ ]", messageResId = R.string.invalid_name)
      private TextInputLayout user_name;

      @NotEmpty(messageResId = R.string.empty_email)
      @Pattern(regex ="[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+" ,messageResId = R.string.invalid_email)
      private TextInputLayout email_reg;

      @NotEmpty(messageResId = R.string.empty_pass)
      @Password(min = 8, scheme = Password.Scheme.ANY, messageResId = R.string.invalid_pass)
      private TextInputLayout pass_reg;

      @NotEmpty(messageResId = R.string.empty_conf_pass)
      @ConfirmPassword(messageResId = R.string.conf_pass_not_matched)
      private TextInputLayout confirm_pass;

      private Validator validator;
      private boolean validated = false;

      public RegisterDialog(@NonNull Context context) {
            super(context);

            validator = new Validator(this);
            validator.registerAdapter(TextInputLayout.class , new TextInputLayoutAdapter());
            validator.setViewValidatedAction(new Validator.ViewValidatedAction() {
                  @Override
                  public void onAllRulesPassed(View view) {
                        if (view instanceof TextInputLayout) {
                              ((TextInputLayout) view).setError("");
                              ((TextInputLayout) view).setErrorEnabled(false);
                        }
                  }
            });
            validator.setValidationListener(this);


            requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            setContentView(R.layout.register_dialog);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().getAttributes();
            setCancelable(true);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            profile_pic = findViewById(R.id.pro_pic);

            cancel_btn = findViewById(R.id.cancel_btn);
            create_btn = findViewById(R.id.creat_btn);

            user_name = findViewById(R.id.name_filed);
            email_reg = findViewById(R.id.email_filed_r);
            pass_reg = findViewById(R.id.pass_filed_r);
            confirm_pass = findViewById(R.id.confrim_pass_filed_r);
            getWindow().setAttributes(lp);

      }

      public TextInputLayout getUserName() {
            return user_name;
      }

      public TextInputLayout getEmail_reg() {
            return email_reg;
      }

      public TextInputLayout getPass_reg() {
            return pass_reg;
      }

      public Validator getValidator() {
            return validator;
      }

      public boolean isValidated() {
            return validated;
      }

      @Override
      public void onValidationSucceeded() {
            validated =true;
      }

      @Override
      public void onValidationFailed(List<ValidationError> errors) {

            for (ValidationError error : errors) {
                  View view = error.getView();
                  String message = error.getCollatedErrorMessage(getContext());

                  if (view instanceof TextInputLayout) {
                        ((TextInputLayout) view).setError(message);
                  } else {
                        Toast.makeText(getContext() , message , Toast.LENGTH_LONG).show();
                  }
            }
      }
}



