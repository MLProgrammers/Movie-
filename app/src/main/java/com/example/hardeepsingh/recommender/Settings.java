package com.example.hardeepsingh.recommender;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    Spinner spinner1, spinner2;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        spinner1 = (Spinner) findViewById(R.id.spinner);
        spinner2 = (Spinner) findViewById(R.id.spinner2);


        //Preserve Selection
        spinner1.setSelection(prefs.getInt("spinner1_selection", 0));
        spinner2.setSelection(prefs.getInt("spinner2_selection", 0));


        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prefs.edit().putInt("spinner1_selection", position).apply();
                prefs.edit().putString("rating_minimum", spinner1.getSelectedItem().toString()).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prefs.edit().putInt("spinner2_selection", position).apply();
                prefs.edit().putString("rating_source", spinner2.getSelectedItem().toString()).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


}

