package ru.exsoft.turret.Gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import ru.exsoft.turret.Utils.Data;
import ru.exsoft.turret.Main;
import ru.exsoft.turret.Modules.OpenCVFaceDetected;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneralController {

    public static GeneralController instance;

    @FXML private ImageView imageviev;
    @FXML private Label last;
    @FXML private Label next;
    @FXML private TextField intervallabel;


    public void initialize() {
        instance = this;
        Main.tracker = new OpenCVFaceDetected();
        Main.tracker.start();
    }

    public void setIntervallabel(){
        OpenCVFaceDetected.interval = Integer.parseInt(intervallabel.getText());
    }

    public void stop(){
        OpenCVFaceDetected.notstop = false;
        Main.tracker.interrupt();
    }

    public void setLastTime(Date date){
        String temp = "Последний снимок был сделан: ";
        SimpleDateFormat sdf = new SimpleDateFormat("dd HH:mm:ss");
        temp += sdf.format(date);
        String finalTemp = temp;
        Platform.runLater(new Runnable() {
            @Override public void run() {
                last.setText(finalTemp);
            }
        });
    }

    public void setNextTime(Date date){
        Date newdate = (Date) date.clone();
        String temp = "Следующая серия снимков начнётся: ";
        SimpleDateFormat sdf = new SimpleDateFormat("dd HH:mm:ss");
        SimpleDateFormat min = new SimpleDateFormat("mm");
        SimpleDateFormat sec = new SimpleDateFormat("ss");
        newdate.setTime(newdate.getTime() + 3600000 - (Integer.parseInt(min.format(newdate)) * 60000) - (Integer.parseInt(sec.format(newdate)) * 1000));
        temp += sdf.format(newdate);
        String finalTemp = temp;
        Platform.runLater(new Runnable() {
            @Override public void run() {
                next.setText(finalTemp);
            }
        });
    }

    public void openFolder(){
        try {
           Desktop desktop = null;
           if (Desktop.isDesktopSupported()) {
               desktop = Desktop.getDesktop();
           }
           File file = new File("images/");
           desktop.open(file);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void UpdateImage(){
        imageviev.setImage(Data.getInstance().getCurrentImg());
    }

    public ImageView getImageView() {
        return imageviev;
    }
}
