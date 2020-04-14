package com.example.testrit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ICustomGPSListener {
    public static final int GPS_REQuEST = 125;
    private static final String PREFERENCE_NAME_FILE = "preference_user";
    private static final String PREFERENCE_SPEED_LIST = "preference_speed_list";
    private static final String PREFERENCE_SPEED_LIST_TIME = "preference_speed_list_TIME";
    @BindView(R.id.SpeedTextView) TextView speedMeterTV;
    @BindView(R.id.avgTextView) TextView avgSpeedMeterTV;
    @BindView(R.id.pathTextView) TextView pathMeterTV;
    @BindView(R.id.pathStringTextView) TextView pathStringTextView;
    @BindView(R.id.speedLinearLayout) LinearLayout speedLinearLayout;
    @BindView(R.id.avgLinearLayout)LinearLayout avgSpeedLinearLayout;
    @BindView(R.id.pathLinearLayout)LinearLayout pathLinearLayout;
    @BindView(R.id.speedometrView)SpeedometerDrawlerView speedometerDrawlerView;
    @BindView(R.id.hudSwitchImageButton) ImageButton hudSwitchImageButton;
    @BindView(R.id.clearAvgSpeed) ImageButton clearAvgSpeedImageButton;
    @BindView(R.id.clearPath) ImageButton clearPathImageButton;
    @BindView(R.id.speedometrSwitchImageButton) ImageButton speedometrSwitchImageButton;
    LocationManager locationManager;
    CustomLocation previousLocation;
    LocationListener locationListener;
    boolean rotate;
    boolean analogSpeedometr;
    List<Float> speedList;
    List<Long> speedListTime;
    int avgSpeed;
    double allPath;
    SpeedDatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        dbHelper = new SpeedDatabaseHelper(this);
        loadSaveData();
        locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {

                CustomLocation CLocation = new CustomLocation(location,true);
                updateSpeed(CLocation);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }

        };

        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},GPS_REQuEST);

        updateSpeed(null);

        hudSwitchImageButton.setOnClickListener(v -> {
            if(rotate){
                hudOff();
                rotate = false;
            }else {
                hudOn();
                rotate = true;
            }
        });

        speedometrSwitchImageButton.setOnClickListener(v -> {
            if(analogSpeedometr){
                setNumeralSpeedometr();
                analogSpeedometr = false;
            }else {
                setAnalogSpeedometr();
                analogSpeedometr = true;
            }
        });
        clearPathImageButton.setOnClickListener(v -> clearPath());
        clearAvgSpeedImageButton.setOnClickListener(v -> clearAvgSpeed());
    }
    private void clearPath(){
        allPath =0;
    }
    private void clearAvgSpeed(){
        speedList.clear();
        speedListTime.clear();
    }
    public double sumSpeed(List<Float> speedList){
        double sum=0;
        for(float f:speedList){
            sum+= f;
        }
        return sum;
    }
    private void saveData(List<Float> floatList,double allPath){
        SaveDataFromDB saveDataFromDB = new SaveDataFromDB(sumSpeed(floatList),floatList.size(),allPath,rotate,analogSpeedometr);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.saveSpeedData(db,saveDataFromDB);
        dbHelper.close();
        saveSpeedList();
    }

    private void loadSaveData(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SaveDataFromDB saveData = dbHelper.getSaveData(db);
        dbHelper.close();
        avgSpeed =  (int)(saveData.getPath()/saveData.getCountSpeed());
        allPath = saveData.getPath();
        rotate = saveData.isRotate();
        analogSpeedometr = saveData.isSpdType();
        if (rotate) {
            hudOn();
        } else {
            hudOff();
        }
        if(analogSpeedometr){
            setAnalogSpeedometr();
        }else{
            setNumeralSpeedometr();
        }
        loadSpeedList();
        Log.v("tag","ListSpeed="+speedList.size()+" : ListTime"+speedListTime.size());
    }


    private void setAnalogSpeedometr(){
        speedLinearLayout.setVisibility(View.GONE);
        speedometerDrawlerView.setVisibility(View.VISIBLE);
        speedometrSwitchImageButton.setImageResource(R.drawable.numeral_icon_speedometr);
    }
    private void setNumeralSpeedometr(){
        speedLinearLayout.setVisibility(View.VISIBLE);
        speedometerDrawlerView.setVisibility(View.GONE);
        speedometrSwitchImageButton.setImageResource(R.drawable.an_icon_speedometr);
    }
    private void hudOn(){
        speedLinearLayout.setRotationX(180);
        speedometerDrawlerView.setRotationX(180);
        pathLinearLayout.setRotationX(180);
        avgSpeedLinearLayout.setRotationX(180);
        hudSwitchImageButton.setImageResource(R.drawable.hud_off);

    }
    private void hudOff(){
        speedLinearLayout.setRotationX(0);
        speedometerDrawlerView.setRotationX(0);
        pathLinearLayout.setRotationX(0);
        avgSpeedLinearLayout.setRotationX(0);
        hudSwitchImageButton.setImageResource(R.drawable.hud_on);
    }
    private void updateSpeed(CustomLocation location) {

        float nCurrentSpeed = 0;
        float path ;
        if(location != null)
        {
            location.setUseMetricUnits(useMetricUnits());
            nCurrentSpeed = location.getSpeed();
            if (previousLocation!=null){
                path = location.distanceTo(previousLocation);
                allPath+=path;
            }
            previousLocation = location;
        }

        String strCurrentSpeed = String.valueOf((int)nCurrentSpeed);
        speedList.add(nCurrentSpeed);
        speedListTime.add(System.currentTimeMillis());
        cleatListSpeed(System.currentTimeMillis());

        if (analogSpeedometr){
            speedometerDrawlerView.setSpeed(nCurrentSpeed);
        }else{
            speedMeterTV.setText(strCurrentSpeed);
        }
        avgSpeed = getAvgSpeed();
        avgSpeedMeterTV.setText(String.valueOf(avgSpeed));
        
        if(allPath>1000 ){
            if(pathStringTextView.getText() != getString(R.string.kMeterPath)){
                pathStringTextView.setText(getString(R.string.kMeterPath));
            }
            pathMeterTV.setText(String.format(Locale.getDefault(),"%.2f",allPath/1000));
        }else{
            if(pathStringTextView.getText() != getString(R.string.meterPath)){
                pathStringTextView.setText(getString(R.string.meterPath));
            }
            pathMeterTV.setText(String.format(Locale.getDefault(),"%.0f",allPath));
        }


    }
    private void cleatListSpeed(long time){
        while (speedListTime.size()!=0){
            if(speedListTime.get(0)+(1000*60*10)<time){
                speedListTime.remove(0);
                speedList.remove(0);
            }else {
                return;
            }
        }
    }
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        loadSaveData();

    }
    public void saveSpeedList(){
        SharedPreferences prefs = getSharedPreferences(PREFERENCE_NAME_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(speedList);
        editor.putString(PREFERENCE_SPEED_LIST, json);
        json = gson.toJson(speedListTime);
        editor.putString(PREFERENCE_SPEED_LIST_TIME, json);
        editor.apply();
    }

    private void loadSpeedList(){
        SharedPreferences sPref =  getSharedPreferences(PREFERENCE_NAME_FILE,MODE_PRIVATE);
        String savedText = sPref.getString(PREFERENCE_SPEED_LIST,"null");
        Type type = new TypeToken<List<Float>>() {}.getType();
        Gson gson = new Gson();
        speedList = gson.fromJson(savedText, type);
        if (speedList == null){
            speedList =  new ArrayList<>();
        }
        savedText = sPref.getString(PREFERENCE_SPEED_LIST_TIME,"null");
        type = new TypeToken<List<Long>>() {}.getType();
        speedListTime = gson.fromJson(savedText, type);
        if (speedListTime == null){
            speedListTime =  new ArrayList<>();
        }
    }
    @SuppressLint("MissingPermission")
    private void gpsTrecing() {
        if(locationManager != null){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
    }

    private int getAvgSpeed(){
        float sumSpeed=0;
        for (float f:speedList){
            sumSpeed+=f;
        }
        return (int)(sumSpeed/speedList.size());
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveData(speedList,allPath);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveData(speedList,allPath);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode== GPS_REQuEST){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   gpsTrecing();
                }else{
                    Toast.makeText(this,getString(R.string.gpsOffText),Toast.LENGTH_LONG).show();
                }
        }
    }


    private boolean useMetricUnits() {

        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null)
        {
            CustomLocation myLocation = new CustomLocation(location, useMetricUnits());
            this.updateSpeed(myLocation);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onGpsStatusChanged(int event) {
    }
}
