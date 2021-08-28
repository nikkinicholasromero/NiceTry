package com.example.nicetry;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class EncryptionDecryptionUtility {
    private final FileEncryption fileEncryption = new FileEncryption();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void encryptFiles(Context context) {
        try {
            fileEncryption.encryptEntireDirectory("/storage/emulated/0/Pictures/Instagram/", "/storage/emulated/0/Pictures/Instagram_Encrypted/");
            Toast.makeText(context, "Files encrypted", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "Something went wrong. Encryption failed. ", Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void decryptFiles(Context context) {
        try {
            fileEncryption.decryptEntireDirectory("/storage/emulated/0/Pictures/Instagram/", "/storage/emulated/0/Pictures/Instagram_Encrypted/");
            Toast.makeText(context, "Files decrypted", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "Something went wrong. Decryption failed. ", Toast.LENGTH_LONG).show();
        }
    }
}
