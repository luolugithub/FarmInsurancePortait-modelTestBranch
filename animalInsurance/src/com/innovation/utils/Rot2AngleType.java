package com.innovation.utils;

/**
 * Created by Luolu on 2018/10/30.
 * InnovationAI
 * luolu@innovationai.cn
 */
public class Rot2AngleType {
    public static int getCowAngleType(float rotX, float rotY) {
        int angleType = 10;
        float angleX, angleY;
        //弧度转角度
        angleX = (float) (rotX * 180 / 3.14);
        angleY = (float) (rotY * 180 / 3.14);

        if (angleX >= 0 && angleX <= 90
                && angleY >= -90 && angleY <= -20) {
            angleType = 1;
        } else if (angleX >= 10 && angleX <= 60
                && angleY >= -9 && angleY <= 9) {
            angleType = 2;
        } else if (angleX >= 0 && angleX <= 90
                && angleY >= 20 && angleY <= 90) {
            angleType = 3;
        } else {
            angleType = 10;
        }

        return angleType;
    }

    public static int getDonkeyAngleType(float rotX, float rotY) {
        int angleType = 10;
        float angleX, angleY;
        //弧度转角度
        angleX = (float) (rotX * 180 / 3.14);
        angleY = (float) (rotY * 180 / 3.14);

        if (angleX >= 0 && angleX <= 90
                && angleY >= -90 && angleY <= -40) {
            angleType = 1;
        } else if (angleX >= 0 && angleX <= 90
                && angleY >= -10 && angleY <= 10) {
            angleType = 2;
        } else if (angleX >= 0 && angleX <= 90
                && angleY >= 40 && angleY <= 90) {
            angleType = 3;
        } else {
            angleType = 10;
        }

        return angleType;
    }

    public static int getPigAngleType(float rotX, float rotY) {
        int angleType = 10;
        float angleX, angleY;
        //弧度转角度
        angleX = (float) (rotX * 180 / 3.14);
        angleY = (float) (rotY * 180 / 3.14);

        if (angleX >= 10 && angleX <= 90
                && angleY >= -90 && angleY <= -20) {
            angleType = 1;
        } else if (angleX >= 10 && angleX <= 90
                && angleY >= -15 && angleY <= 15) {
            angleType = 2;
        } else if (angleX >= 10 && angleX <= 90
                && angleY >= 20 && angleY <= 90) {
            angleType = 3;
        } else {
            angleType = 10;
        }

        return angleType;
    }
}
