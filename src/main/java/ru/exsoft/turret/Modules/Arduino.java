package ru.exsoft.turret.Modules;

import javafx.application.Platform;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import ru.exsoft.turret.Utils.Data;
import ru.exsoft.turret.Utils.MusicUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Arduino {
    private static Arduino instance;
    private SerialPort serialPort;

    private Arduino() {
//        if (System.getProperty("os.name").equals("Linux")) {
//            serialPort = new SerialPort("/dev/ttyUSB" + Data.getInstance().getComPort());
//        } else {
//            serialPort = new SerialPort("COM" + Data.getInstance().getComPort());
//        }
//        System.out.println("Port " + Data.getInstance().getComPort() + " opened: " + openPort());
//        try {
//            System.out.println("Params setted: " + serialPort.setParams(115200, 8, 1, 0));
//            serialPort.addEventListener((SerialPortEvent serialPortEvent) -> {
//                try {
//                    System.out.println(serialPort.readString());
//                } catch (SerialPortException e) {
//                    e.printStackTrace();
//                }
//            });
//        } catch (SerialPortException ex) {
//            System.out.println(ex);
//        }
//        //MusicUtils.playVoice("deploy");
//        writeMsg("x50&y50&z100");
    }

    public boolean writeMsg(String msg) {
//        try {
//            //System.out.println("Writing: " + msg);
//            msg += '&';
//            return serialPort.writeBytes(msg.getBytes());
//        } catch (SerialPortException e) {
//            e.printStackTrace();
//        }
        return false;
    }

    public boolean openPort() {
        try {
            return serialPort.openPort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean closePort() {
        try {
            return serialPort.closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Arduino getInstance() {
        if (instance == null) {
            instance = new Arduino();
            return instance;
        } else {
            return instance;
        }
    }
}
