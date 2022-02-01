package com.example.armeasure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        TextView widthView = (TextView) findViewById(R.id.widthView);
        TextView heightView = (TextView) findViewById(R.id.heightView);
        TextView objectView = (TextView) findViewById(R.id.objectView);
        SharedPreferences sharedPref = getSharedPreferences("myKey",MODE_PRIVATE);
        String width = sharedPref.getString("width","");
        String height = sharedPref.getString("height","");
        String measurementName = width.split(":")[0];
        objectView.setText(measurementName);
        widthView.setText("Width: "+width.split(":")[1]);
        heightView.setText("Height: "+height.split(":")[1]);
    }
}