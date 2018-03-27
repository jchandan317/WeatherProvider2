package com.assignment.weatherprovider.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.assignment.weatherprovider.R;
import com.assignment.weatherprovider.fragment.TenDayForecastFragment;

public class MainActivity extends AppCompatActivity {

    protected static final int REQUEST_CHECK_SETTINGS = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new TenDayForecastFragment())
                    .commit();
        }
    }
}
