package com.example.ani.instagramclone;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {
    EditText usernameEditText;
    EditText passwordEditText;
    TextView switchView;
    Button button;

    int mode;

    public void successfulEntry() {
        MainActivity.this.getSharedPreferences("com.example.ani.instagramclone", Context.MODE_PRIVATE).edit().putBoolean("hasUsed", true).apply();

        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
    }

    public void signUp() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(username.equals("") || password.equals("")) {
            Toast.makeText(getApplicationContext(), "A username and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        ParseUser user = new ParseUser();
        user.setUsername(usernameEditText.getText().toString());
        user.setPassword(passwordEditText.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null)
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                else {
                    successfulEntry();
                }
            }
        });
    }

    public void logIn() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(username.equals("") || password.equals("")) {
            Toast.makeText(getApplicationContext(), "A username and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(user == null)
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                else
                    successfulEntry();
            }
        });
    }

    public void buttonFunction(View view) {
        if(mode == 0)
            signUp();
        else
            logIn();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Instagram");

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main);
        ImageView imageView = (ImageView) findViewById(R.id.logoView);

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        switchView = (TextView) findViewById(R.id.switchView);
        button = (Button) findViewById(R.id.button);

        mode = 0;

        switchView.setOnClickListener(this);
        layout.setOnClickListener(this);
        imageView.setOnClickListener(this);

        passwordEditText.setOnKeyListener(this);

        if(ParseUser.getCurrentUser() != null)
            successfulEntry();

        if(this.getSharedPreferences("com.example.ani.instagramclone", Context.MODE_PRIVATE).getBoolean("hasUsed", false)) {
            button.setText("Log In");
            switchView.setText("Or Sign Up");
            mode = 1;
        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)
            buttonFunction(v);

        return false;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.switchView) {
            mode = (mode + 1) % 2;

            if(mode == 0) {
                button.setText("Sign Up");
                switchView.setText("Or Log In");
            }
            else {
                button.setText("Log In");
                switchView.setText("Or Sign Up");
            }
        }
        else if(v.getId() == R.id.activity_main || v.getId() == R.id.logoView) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
