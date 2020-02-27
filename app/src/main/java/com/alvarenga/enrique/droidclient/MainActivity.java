package com.alvarenga.enrique.droidclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    static final String USER_MESSAGE_STATE = "userMessageState";

    private EditText userMessage;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(USER_MESSAGE_STATE, String.valueOf(userMessage.getText()));
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Initialize components */
        userMessage = findViewById(R.id.userMessageInput);

        /* Recover the instance state */
        if (savedInstanceState != null) {
            /* Reload the app */
            userMessage.setText(savedInstanceState.getString(USER_MESSAGE_STATE));
        } else {
            setContentView(R.layout.activity_main);
        }



    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
