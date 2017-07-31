package com.app.master.puntosderecargatrancaribe.Vista;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app.master.puntosderecargatrancaribe.MapsActivity;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();
    }
}
