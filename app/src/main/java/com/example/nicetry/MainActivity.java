package com.example.nicetry;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private final int STORAGE_PERMISSION_CODE = 1;
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private final EncryptionDecryptionUtility encryptionDecryptionUtility = new EncryptionDecryptionUtility();

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        managePermissions();
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void managePermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        } else {
            initiateEncryptionOrDecryption();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initiateEncryptionOrDecryption() {
        if (networkChangeListener.getNetworkState() == NetworkState.CONNECTED) {
            encryptionDecryptionUtility.encryptFiles(getApplicationContext(), "file/path/");
        } else {
            encryptionDecryptionUtility.decryptFiles(getApplicationContext(), "file/path/");
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_MEDIA_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This app needs access to your storage to encrypt your photos")
                    .setPositiveButton("ok", (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_MEDIA_LOCATION}, STORAGE_PERMISSION_CODE))
                    .setNegativeButton("cancel", (dialog, which) -> {
                        System.exit(0);
                    })
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_MEDIA_LOCATION}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (!(requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            System.exit(0);
        } else {
            initiateEncryptionOrDecryption();
        }
    }
}
