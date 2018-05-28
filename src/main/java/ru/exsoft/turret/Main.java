package ru.exsoft.turret;

import jssc.SerialPort;
import jssc.SerialPortException;
import ru.exsoft.turret.Gui.Gui;
import ru.exsoft.turret.Modules.Arduino;
import ru.exsoft.turret.Modules.OpenCVFaceDetected;
import ru.exsoft.turret.Modules.Turret;
import ru.exsoft.turret.Utils.Data;
import ru.exsoft.turret.Utils.MathUtils;
import ru.exsoft.turret.Utils.MusicUtils;
import ru.exsoft.turret.Utils.OtherUtils;

import java.text.DecimalFormat;
import java.util.Random;

import static java.lang.Thread.sleep;

public class Main {
    public static boolean isGui = true;
    public static Thread tracker;


    public static void main(String[] args) throws InterruptedException {
        if (args.length > 0) {
            for (String arg : args) {
                switch (arg) {
                    case "nogui":
                        isGui = false;
                        break;
                }
                if (arg.contains("COM")) {
                    Data.getInstance().setComPort(OtherUtils.checkString(arg));
                }
            }
        }
        System.out.println(MathUtils.random(1,2));
        //Turret.loop();
        //System.out.println(getX(0,100));
        //Arduino.getInstance();
        //OtherUtils.sleepWinoutEx(2000);
        //!Turret.search();
        if (isGui) {
            Gui.main(args);
        } else {
            tracker = new OpenCVFaceDetected();
            tracker.start();
        }
    }

    public static float getX(int diapMin, int diapMax) {
        return (455 - 390) * (((520 - 390) / 100f) * ((diapMax - diapMin) / 100f));
    }
}
