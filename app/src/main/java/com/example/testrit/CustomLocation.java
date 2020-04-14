package com.example.testrit;

import android.location.Location;

public class CustomLocation extends Location {

    public CustomLocation(Location location) {
        super(location);
    }


    @Override
    public float getSpeed() {
        return super.getSpeed() * 3.6f;
    }

    @Override
    public long getTime() {
        return super.getTime();
    }
}
