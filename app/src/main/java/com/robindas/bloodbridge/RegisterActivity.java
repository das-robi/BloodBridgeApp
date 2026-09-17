package com.robindas.bloodbridge;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.robindas.bloodbridge.API.APIServices;
import com.robindas.bloodbridge.API.RetrofitClient;
import com.robindas.bloodbridge.Model.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    //Widgets
    private EditText etUsername;
    private EditText etUserEmail;
    private EditText etPassword;
    private Button regiBtn;

    //API
    private APIServices apiServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.userName);
        etUserEmail = findViewById(R.id.useremail);
        etPassword = findViewById(R.id.passWord);
        regiBtn = findViewById(R.id.lgnBtn);

        regiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Registration();
            }
        });


        apiServices = RetrofitClient.getRetrofitInstance(this)
                .create(APIServices.class);
    }

    private void Registration() {

        String username = etUsername.getText().toString().trim();
        String useremail = etUserEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (useremail.isEmpty()){
            etUsername.setError("Enter name");
            return;
        }
        if (useremail.isEmpty()){
            etUserEmail.setError("Enter email");
            return;
        }
        if (password.isEmpty()){
            etPassword.setError("Enter password");
            return;
        }


        regiBtn.setEnabled(false);

        RegisterRequest request = new RegisterRequest(username, useremail, password);

        apiServices.register(request).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                regiBtn.setEnabled(true);

                if (response.isSuccessful()){
                    Log.e("Register", "Register Success: " + response.body());
                    Toast.makeText(RegisterActivity.this, "Register Successful", Toast.LENGTH_SHORT).show();
                }
                else {

                    Log.e("Register ", "Error message: " + response.body());
                    Toast.makeText(RegisterActivity.this, "Register failed try again", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {

                regiBtn.setEnabled(true);

                Log.e("Register Error ", "Register Failed ", throwable);

                Toast.makeText(RegisterActivity.this, "Network error " + throwable.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}