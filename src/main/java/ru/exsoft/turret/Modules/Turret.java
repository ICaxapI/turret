package ru.exsoft.turret.Modules;

import org.opencv.core.Mat;
import ru.exsoft.turret.Utils.MathUtils;
import ru.exsoft.turret.Utils.MusicUtils;
import ru.exsoft.turret.Utils.OtherUtils;

import java.text.DecimalFormat;

public class Turret {
    private static final int minx = 390;
    private static final int maxx = 520;
    private static final int cx = (maxx - minx) / 2;
    private static final int miny = 145;
    private static final int maxy = 255;
    private static final int cy = (maxy - miny) / 2;
    private static final int minz = 100;
    private static final int maxz = 440;
    private static int x = cx + minx;
    private static int y = cy + miny;
    private static int z = maxz;
    private static long lastMotion;
    private static boolean moving = false;
    private static boolean fire = false;
    private static boolean deployed = false;
    private static boolean search = false;
    private static boolean busyDeploy = false;
    private static boolean busyMoving = false;
    private static boolean busySearch = false;

    public static void setX(double procents) { //in -100::100
        x = (int) procents;
    }

    public static void setY(double procents) {
        y = (int) procents;
    }

    public static void motion() {
        lastMotion = System.currentTimeMillis();
    }

    public static void loop() {
        new Thread(() -> {
            while (true) {
                OtherUtils.sleepWinoutEx(750);
                System.out.println(System.currentTimeMillis() - lastMotion);
                System.out.println(!deployed);
                if ((System.currentTimeMillis() - lastMotion < 200 && !deployed) || (System.currentTimeMillis() - lastMotion < 2000 && deployed)) {
                    if (!deployed && !busyDeploy) {
                        System.out.println("deploy: " + (System.currentTimeMillis() - lastMotion));
                        busyDeploy = true;
                        new Thread(() -> {
                            MusicUtils.playVoice("fire");
                            MusicUtils.loopSound("alert", 3);
                            deploy();
                            busyDeploy = false;
                        }).start();
                    } else if (deployed && !busyDeploy) {
                        if (!busyMoving) {
                            System.out.println("move: " + (System.currentTimeMillis() - lastMotion));
                            busyMoving = true;
                            fire = true;
                            MusicUtils.playFire();
                            Arduino.getInstance().writeMsg("ft");
                            OtherUtils.sleepWinoutEx(5);
                            while (busyMoving) {
                                updatePosition();
                                OtherUtils.sleepWinoutEx(30);
                            }
                        }
                    }
                } else if (System.currentTimeMillis() - lastMotion > 2000 && deployed) {
                    fire = false;
                    System.out.println("lost: " + (System.currentTimeMillis() - lastMotion));
                    Arduino.getInstance().writeMsg("ff");
                    busyMoving = false;
                    search();
                } else if (System.currentTimeMillis() - lastMotion > 700) {
                    System.out.println("blink: " + (System.currentTimeMillis() - lastMotion));
                    MusicUtils.playVoice("blink");
                } else {
                    System.out.println("error: " + (System.currentTimeMillis() - lastMotion));
                }
            }
        }).start();
    }

    public static void search() {
        if (!busySearch) {
            final int sleep = 6;
            final int numSearch = 3;
            busySearch = true;
            double maxX = 100;
            double c = 71.07;
            double xTemp = 0;
            double yTemp = 0;
            byte state = 0;
            deploy();
            MusicUtils.playVoice("losted");
            if (xTemp >= 0 && yTemp >= 0) {
                state = 0;
            } else if (xTemp >= 0 && yTemp < 0) {
                state = 1;
            } else if (xTemp < 0 && yTemp >= 0) {
                state = 2;
            } else if (xTemp < 0 && yTemp < 0) {
                state = 3;
            }
            System.out.println(state);
            DecimalFormat df = new DecimalFormat("#.####");
            int i = 0;
            for (; busySearch && i <= (numSearch - 1); i++) {
                for (; xTemp <= maxX && state == 0 && busySearch; xTemp += 0.5) {
                    yTemp = Math.sqrt(Math.sqrt(Math.pow(c, 4) + 4 * (Math.pow(xTemp, 2) * Math.pow(c, 2))) - Math.pow(xTemp, 2) - Math.pow(c, 2));
                    setX(xTemp);
                    setY(yTemp/2);
                    updatePosition();
                    OtherUtils.sleepWinoutEx(sleep);
                    if (xTemp >= maxX) {
                        state++;
                        MusicUtils.playSound("ping");
                    }
                }
                for (; xTemp >= 0 && state == 1 && busySearch; xTemp -= 0.5) {
                    yTemp = 0 - Math.sqrt(Math.sqrt(Math.pow(c, 4) + 4 * (Math.pow(xTemp, 2) * Math.pow(c, 2))) - Math.pow(xTemp, 2) - Math.pow(c, 2));
                    setX(xTemp);
                    setY(yTemp/2);
                    updatePosition();
                    OtherUtils.sleepWinoutEx(sleep);
                    if (xTemp <= 0) {
                        state++;
                        MusicUtils.playSound("ping");
                    }
                }
                for (; xTemp >= 0 - maxX && state == 2 && busySearch; xTemp -= 0.5) {
                    yTemp = Math.sqrt(Math.sqrt(Math.pow(c, 4) + 4 * (Math.pow(xTemp, 2) * Math.pow(c, 2))) - Math.pow(xTemp, 2) - Math.pow(c, 2));
                    setX(xTemp);
                    setY(yTemp/2);
                    updatePosition();
                    OtherUtils.sleepWinoutEx(sleep);
                    if (xTemp <= 0 - maxX) {
                        state++;
                        MusicUtils.playSound("ping");
                    }
                }
                for (; xTemp <= 0 && state == 3 && busySearch; xTemp += 0.5) {
                    yTemp = 0 - Math.sqrt(Math.sqrt(Math.pow(c, 4) + 4 * (Math.pow(xTemp, 2) * Math.pow(c, 2))) - Math.pow(xTemp, 2) - Math.pow(c, 2));
                    setX(xTemp);
                    setY(yTemp/2);
                    updatePosition();
                    OtherUtils.sleepWinoutEx(sleep);
                    if (xTemp >= 0) {
                        state = 0;
                        if (i != (numSearch - 1)) {
                            MusicUtils.playSound("ping");
                        }
                    }
                }
            }
            if (i == numSearch){
                MusicUtils.playVoice("lost_");
                undeploy();
            }
        }
    }

    public static void updatePosition(){
        Arduino.getInstance().writeMsg("x"+x+"&y"+y);
    }

    public static void deploy() {
        OtherUtils.sleepWinoutEx(5);
        z = -100;
        Arduino.getInstance().writeMsg("z-100");
        OtherUtils.sleepWinoutEx(1000);
        Arduino.getInstance().writeMsg("z-100");
        OtherUtils.sleepWinoutEx(1000);
        deployed = true;
    }

    public static void undeploy() {
        OtherUtils.sleepWinoutEx(5);
        z = 100;
        Arduino.getInstance().writeMsg("z100");
        OtherUtils.sleepWinoutEx(1000);
        deployed = false;
    }

    public static boolean isFire() {
        return fire;
    }


    public static float getX(int diapMin, int diapMax) {
        return MathUtils.map(x, maxx, minx, diapMin, diapMax);
    }

    public static float getY(int diapMin, int diapMax) {
        return MathUtils.map(y, maxy, miny, diapMin, diapMax);
    }


}
