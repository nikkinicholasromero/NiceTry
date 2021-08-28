package com.example.nicetry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class NetworkChangeListener extends BroadcastReceiver {
    private NetworkState networkState = NetworkState.NOT_DETECTED;
    private final EncryptionDecryptionUtility encryptionDecryptionUtility = new EncryptionDecryptionUtility();

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onReceive(Context context, Intent intent) {
        if (isConnectedToInternet(context)) {
            encryptionDecryptionUtility.encryptFiles(context);
        } else {
            encryptionDecryptionUtility.decryptFiles(context);
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
                for (NetworkInfo networkInfo : info) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        networkState = NetworkState.CONNECTED;
                        return true;
                    }
                }
            }
        }

        networkState = NetworkState.DISCONNECTED;
        return false;
    }
}
