package com.example.testrit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ICustomGPSListener {
    public static final int GPS_REQuEST = 125;

    @BindView(R.id.SpeedTextView) TextView speedMeterTV;
    @BindView(R.id.avgTextView) TextView avgSpeedMeterTV;
    @BindView(R.id.pathTextView) TextView pathMeterTV;
    @BindView(R.id.speedLinearLayout) LinearLayout speedLinearLayout;
    @BindView(R.id.avgLinearLayout)LinearLayout avgSpeedLinearLayout;
    @BindView(R.id.pathLinearLayout)LinearLayout pathLinearLayout;
    @BindView(R.id.speedometrView)SpeedometerDrawlerView speedometerDrawlerView;
    @BindView(R.id.hudSwitchImageButton) ImageButton hudSwitchImageButton;
    @BindView(R.id.speedometrSwitchImageButton) ImageButton speedometrSwitchImageButton;
    LocationManager locationManager;
    CustomLocation previousLocation;
    LocationListener locationListener;
    boolean rotate;
    boolean analogSpeedometr;
    List<Float> speedList;
    float allPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        speedList = new ArrayList<>();
        allPath = 0;
        locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {

                CustomLocation CLocation = new CustomLocation(location,true);
                updateSpeed(CLocation);
                Log.v("tag2",String.format("%s", location.getSpeed()));
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }

        };
        rotate = false;
        analogSpeedometr = false;
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},GPS_REQuEST);

        updateSpeed(null);

        hudSwitchImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rotate){
                    hudOff();
                    rotate = false;
                }else {
                    hudOn();
                    rotate = true;
                }
            }
        });

        speedometrSwitchImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(analogSpeedometr){
                    setNumeralSpeedometr();
                    analogSpeedometr = false;
                }else {
                    setAnalogSpeedometr();
                    analogSpeedometr = true;
                }
            }
        });

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
        speedLinearLayout.setRotationY(180);
        speedometerDrawlerView.setRotationY(180);
        pathLinearLayout.setRotationY(180);
        avgSpeedLinearLayout.setRotationY(180);
        hudSwitchImageButton.setImageResource(R.drawable.hud_off);

    }
    private void hudOff(){
        speedLinearLayout.setRotationY(0);
        speedometerDrawlerView.setRotationY(0);
        pathLinearLayout.setRotationY(0);
        avgSpeedLinearLayout.setRotationY(0);
        hudSwitchImageButton.setImageResource(R.drawable.hud_on);
    }
    private void updateSpeed(CustomLocation location) {

        float nCurrentSpeed = 0;
        float path = 0;
        if(location != null)
        {
            location.setUseMetricUnits(useMetricUnits());
            nCurrentSpeed = location.getSpeed();
            if (previousLocation!=null){
                path = location.distanceTo(previousLocation);
                Log.v("tagPath",path+"");
                allPath+=path;
            }
            previousLocation = location;
        }


        String strCurrentSpeed = String.valueOf((int)nCurrentSpeed);
        //strCurrentSpeed = strCurrentSpeed.replace(' ', '0');
        speedList.add(nCurrentSpeed);
        if (analogSpeedometr){
            speedometerDrawlerView.setSpeed(nCurrentSpeed);
        }else{
            speedMeterTV.setText(strCurrentSpeed);
        }
        avgSpeedMeterTV.setText(String.valueOf((int)avgSpeed()));
        pathMeterTV.setText(String.valueOf((int)allPath));

    }

    @SuppressLint("MissingPermission")
    private void gpsTrecing() {
        if(locationManager != null){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
    }

    private float avgSpeed(){
        float sumSpeed=0;
        for (float f:speedList){
            sumSpeed+=f;
        }
        return speedList.size()<900?sumSpeed/speedList.size():sumSpeed/900;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    updateSpeed(null);
        switch(requestCode) {
            case GPS_REQuEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   gpsTrecing();
                }
                break;
        }
    }


    private boolean useMetricUnits() {

        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        if(location != null)
        {
            CustomLocation myLocation = new CustomLocation(location, useMetricUnits());
            this.updateSpeed(myLocation);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGpsStatusChanged(int event) {
        // TODO Auto-generated method stub

    }
}
