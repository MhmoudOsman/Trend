package mahmud.osman.trend.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.victor.loading.rotate.RotateLoading;

import mahmud.osman.trend.R;

public class ReconnectDialog extends Dialog {
    private TextView error_massage;
    private Button retry;
    private RotateLoading reconnect_loading;

    public ReconnectDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.reconnect_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        setCanceledOnTouchOutside(false);

        error_massage = findViewById(R.id.tv_error_connection);
        retry = findViewById(R.id.retry_btn);
        reconnect_loading = findViewById(R.id.rl_connecting);
    }

    public TextView getError_massage() {
        return error_massage;
    }

    public Button getRetry() {
        return retry;
    }

    public RotateLoading getReconnect_loading() {
        return reconnect_loading;
    }
}
