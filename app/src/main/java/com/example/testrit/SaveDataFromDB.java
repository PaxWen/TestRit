package com.example.testrit;

public class SaveDataFromDB {
    private double speed;
    private int countSpeed;
    private double path;
    private boolean rotate;
    private boolean spdType;

    public double getSpeed() {
        return speed;
    }

    public int getCountSpeed() {
        return countSpeed;
    }

    public double getPath() {
        return path;
    }

    public boolean isRotate() {
        return rotate;
    }

    public boolean isSpdType() {
        return spdType;
    }

    public SaveDataFromDB(double speed, int countSpeed, double path, boolean rotate, boolean spdType) {
        this.speed = speed;
        this.countSpeed = countSpeed;
        this.path = path;
        this.rotate = rotate;
        this.spdType = spdType;
    }
}
