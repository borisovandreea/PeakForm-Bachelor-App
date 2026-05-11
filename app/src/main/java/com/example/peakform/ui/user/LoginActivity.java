package com.example.peakform.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.peakform.MainActivity;
import com.example.peakform.R;
import com.example.peakform.logic.settings.SettingsManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etName, etPin;
    private TextView tvUsernameLabel;
    private Button btnLogin;
    private SettingsManager settingsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingsManager = new SettingsManager(this);


        boolean isRegistered = settingsManager.hasPinSet();

        setContentView(R.layout.activity_login);

        etName = findViewById(R.id.etUserName);
        etPin = findViewById(R.id.etUserPin);
        tvUsernameLabel = findViewById(R.id.tvUsernameLabel);
        btnLogin = findViewById(R.id.btnLogin);

        if (isRegistered) {
            etName.setVisibility(View.GONE);
            tvUsernameLabel.setVisibility(View.GONE);
            btnLogin.setText("Unlock PeakForm");
        } else {
            btnLogin.setText("Create Local Profile");
        }


        btnLogin.setOnClickListener(v -> {
            String pin = etPin.getText().toString().trim();

            if (pin.length() != 4) {
                etPin.setError("Enter 4-digit PIN");
                return;
            }

            if (isRegistered) {
                handleLogin(pin);
            } else {
                handleRegistration(pin);
            }
        });
    }

    private void handleRegistration(String pin) {
        String name = etName.getText().toString().trim();

        if (name.isEmpty() || name.length() < 2) {
            etName.setError("Please enter a valid name");
            return;
        }

        settingsManager.saveUsername(name);
        settingsManager.savePin(pin);

        Toast.makeText(this, "Profile created securely!", Toast.LENGTH_SHORT).show();
        navigateToMain(true);
    }

    private void handleLogin(String pin) {
        if (settingsManager.checkPin(pin)) {
            navigateToMain(false);
        } else {
            Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
            etPin.setText("");
        }
    }

    private void navigateToMain(boolean justRegistered) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("OPEN_LOG_SCREEN", justRegistered);
        startActivity(intent);
        finish();
    }
}
