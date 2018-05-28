package ru.exsoft.turret.Utils;

import javafx.scene.image.Image;
import ru.exsoft.turret.Modules.Arduino;

public class Data {
    private static Data instance;
    private Image currentImg; //For gui
    private String comPort; //NUMBER OF COM PORT
    private Arduino arduino;

    public static Data getInstance(){
        if (instance == null){
            instance = new Data();
        }
        return instance;
    }

    public Arduino getArduino() {
        return Arduino.getInstance();
    }

    public Image getCurrentImg() {
        return currentImg;
    }

    public void setCurrentImg(Image currentImg) {
        this.currentImg = currentImg;
    }

    public String getComPort() {
        return comPort;
    }

    public void setComPort(String comPort) {
        this.comPort = comPort;
    }
}
