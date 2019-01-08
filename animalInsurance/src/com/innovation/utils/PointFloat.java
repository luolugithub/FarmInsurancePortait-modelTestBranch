package com.innovation.utils;

/**
 * Created by Luolu on 2018/10/23.
 * InnovationAI
 * luolu@innovationai.cn
 */
public class PointFloat {
    private float x;
    private float y;


    public PointFloat() {}

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public PointFloat(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public PointFloat(PointFloat src) {
        this.x = src.x;
        this.y = src.y;
    }

    /**
     * Set the point's x and y coordinates
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns true if the point's coordinates equal (x,y)
     */
    public final boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PointFloat pointFloat = (PointFloat) o;

        if (x != pointFloat.x) return false;
        if (y != pointFloat.y) return false;

        return true;
    }


    @Override
    public String toString() {
        return "PointFloat(" + x + ", " + y + ")";
    }


}
