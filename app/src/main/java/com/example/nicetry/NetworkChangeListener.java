package com.example.nicetry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkChangeListener extends BroadcastReceiver {
    private NetworkState networkState = NetworkState.NOT_DETECTED;
    private EncryptionDecryptionUtility encryptionDecryptionUtility = new EncryptionDecryptionUtility();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isConnectedToInternet(context)) {
            encryptionDecryptionUtility.encryptFiles(context, "file/path/");
        } else {
            encryptionDecryptionUtility.decryptFiles(context, "file/path/");
        }
    }

    public NetworkState getNetworkState() {
        return networkState;
    }

    private boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }

        return false;
    }
}
