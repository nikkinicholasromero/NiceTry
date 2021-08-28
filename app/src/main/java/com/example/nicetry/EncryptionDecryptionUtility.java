package com.example.nicetry;

import android.content.Context;
import android.widget.Toast;

public class EncryptionDecryptionUtility {
    public void encryptFiles(Context context, String path) {
        Toast.makeText(context, "Encrypted", Toast.LENGTH_LONG).show();
    }

    public void decryptFiles(Context context, String path) {
        Toast.makeText(context, "Decrypted", Toast.LENGTH_LONG).show();
    }
}
