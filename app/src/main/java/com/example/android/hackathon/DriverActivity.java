package com.example.android.hackathon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class DriverActivity extends AppCompatActivity {
    private Spinner food_Spinner;
    private String[] food_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        food_Spinner = (Spinner)findViewById(R.id.food_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.food_spinner, android.R.layout.simple_spinner_item);

        food_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) {
                }else {
                    // Your code to process the selection
                }
            }
        });

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        food_Spinner.setAdapter(adapter);
    }
}
