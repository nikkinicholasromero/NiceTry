package com.example.nicetry;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

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
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Hi User")
                .setMessage("Your personal photos and videos will be encrypted")
                .setPositiveButton("Agree", null)
                .show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view -> {
            dialog.dismiss();
            // show loading
            for (String targetDirectory : targetDirectoryList) {
                fileEncryption.encryptEntireDirectory(targetDirectory);
            }
            Toast.makeText(context, "Files encrypted", Toast.LENGTH_LONG).show();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void decryptFiles(Context context) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Hi User")
                .setMessage("Your personal photos and videos will be decrypted")
                .setPositiveButton("Agree", null)
                .show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view -> {
            dialog.dismiss();
            // show loading
            for (String targetDirectory : targetDirectoryList) {
                fileEncryption.decryptEntireDirectory(targetDirectory);
            }
            Toast.makeText(context, "Files decrypted", Toast.LENGTH_LONG).show();
        });
    }
}
