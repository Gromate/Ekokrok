package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.RequiresApi;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    float pointNumber, pointsSpent;
    TextView stepCounter_textView;
    boolean running;
    private AppBarConfiguration mAppBarConfiguration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pointNumber = summoningVariables();

        stepCounter_textView = findViewById(R.id.aktywnosc_LiczbaKrokow);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        running = true;
        Sensor stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepCounter != null) {
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Sensor nie działa!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        stepCounter_textView = findViewById(R.id.aktywnosc_LiczbaKrokow);
        if (running) {
            stepCounter_textView.setText(String.valueOf(event.values[0]));
            savingSteps(event.values[0]);
            refreshingVariablesMainScreen();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public float summoningVariables(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        float stepCount = sharedPref.getFloat("stepCount", 0f);
        return stepCount;
    }

    public void savingSteps(float stepCount){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat("stepCount", stepCount);
        editor.commit();
    }


    public void refreshingVariablesMainScreen() {
        float liczbaCO2 = 0.00176772f;
        float stepLenght = 0.85f;

        TextView aktywnosc_co2 = findViewById(R.id.aktywnosc_co2);

        //Obliczenia
        float savedCO2 = summoningVariables() * liczbaCO2 * stepLenght;

        //Wyswietlanie CO2 na fragment_aktywnosc
        aktywnosc_co2.setText(String.valueOf(savedCO2));

    }

    public void refreshingVariablesFragmentProfil(View v){
        float liczbaCO2 = 0.00176772f;
        float stepLenght = 0.85f;

        TextView profil_OgolnaLiczbaCO2 = findViewById(R.id.profil_OgolnaLiczbaCO2);
        TextView profil_PrzebytyDystans = findViewById(R.id.profil_PrzebytyDystans);
        TextView profil_OgolnaLiczbaKrokow = findViewById(R.id.profil_OgolnaLiczbaKrokow);

        float savedCO2 = summoningVariables() * liczbaCO2 * stepLenght;
        float distance = summoningVariables() * stepLenght;

        profil_OgolnaLiczbaKrokow.setText(String.valueOf(summoningVariables()));
        profil_OgolnaLiczbaCO2.setText(String.valueOf(savedCO2));
        profil_PrzebytyDystans.setText(String.valueOf(distance));
    }

    public void makingForest(View v){
        TextView pointDisplay = findViewById(R.id.las_pointNumber);

        if(pointNumber > 100) {
            ImageButton buttonPressed = findViewById(v.getId());
            buttonPressed.setImageResource(R.drawable.drzewo_1);
            pointsSpent += 100;
            pointNumber = summoningVariables() - pointsSpent;

        }

        pointDisplay.setText(String.valueOf(pointNumber));
    }
}
