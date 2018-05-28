package ru.exsoft.turret.Utils;

import java.util.Random;

public class MathUtils {
    private static int lastRandom = -1;
    private static Random randomRand = new Random(System.currentTimeMillis());

    public static float middleOf3(float a, float b, float c) {
        float middle;
        if ((a <= b) && (a <= c)) {
            middle = (b <= c) ? b : c;
        } else {
            if ((b <= a) && (b <= c)) {
                middle = (a <= c) ? a : c;
            } else {
                middle = (a <= b) ? a : b;
            }
        }
        return middle;
    }

    public static int random(int floor, int roof) {
        Random random = new Random(randomRand.nextInt((1000000000) - (0)) + (0));
        int returnInt = random.nextInt((roof) - (floor - 1)) + (floor - 1);
        if (returnInt == lastRandom){
            returnInt = random(floor, roof);
        } else {
            lastRandom = returnInt;
        }
        return returnInt;
    }

    public static float map(float x, float in_min, float in_max, float out_min, float out_max){
        return ((x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min);
    }
}
