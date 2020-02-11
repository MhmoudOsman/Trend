package mahmud.osman.trend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.google.android.material.snackbar.Snackbar;

public class ConnectionReceiver extends BroadcastReceiver {
      public boolean noConnection;
      @Override
      public void onReceive(Context context , Intent intent) {

            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
                   noConnection = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,false);

            }


      }
}
