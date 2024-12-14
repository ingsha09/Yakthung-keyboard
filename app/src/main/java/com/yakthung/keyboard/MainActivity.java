package com.yakthung.keyboard;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager; // Import statement added
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.yakthung.keyboard.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set initial text for the TextView
        binding.textView.setText("Welcome to Yakthung Keyboard!");

        // Button to enable the Yakthung Keyboard
        binding.buttonEnableKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInputMethodSettings();
            }
        });

        // Button to set the Yakthung Keyboard as the default
        binding.buttonSetDefaultKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputMethodPicker();
            }
        });
    }

    private void openInputMethodSettings() {
        Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
        startActivity(intent);
        Toast.makeText(this, "Enable Yakthung Keyboard in settings.", Toast.LENGTH_LONG).show();
    }

    private void showInputMethodPicker() {
        // Show the system input method picker to change the keyboard
        InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
        if (imeManager != null) {
            imeManager.showInputMethodPicker();
        }
    }
}