package com.example.nicetry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);

        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = findViewById(R.id.username);
                EditText password = findViewById(R.id.password);

                if (username.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter your username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }

                login(username.getText().toString(), password.getText().toString());
            }
        });

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void login(String username, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.16:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        Call<ResponseBody> call = retrofitAPI.authenticate(user);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login successful. ", Toast.LENGTH_LONG).show();

                    MyApplication.user = username;
                    SharedPreferences sharedPreferences = getSharedPreferences("NICE_TRY", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user", username);
                    editor.apply();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    ResponseBody responseBody = null;
                    try {
                        responseBody = new Gson().fromJson(response.errorBody().string(), ResponseBody.class);
                        Toast.makeText(LoginActivity.this, responseBody.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(LoginActivity.this, "Something went wrong. Please try again. ", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Something went wrong. Please try again. ", Toast.LENGTH_LONG).show();
            }
        });
    }
}
