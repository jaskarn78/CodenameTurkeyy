package com.example.android.hackathon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/** Main Activity for the application */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Redirect user to signin page
        Intent intent = new Intent(this, UserType.class);
        startActivity(intent);
    }
}
