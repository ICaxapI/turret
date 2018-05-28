package ru.exsoft.turret.Modules;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.sun.xml.internal.ws.runtime.config.TubelineFeatureReader;
import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import ru.exsoft.turret.Utils.Data;
import ru.exsoft.turret.Gui.GeneralController;
import ru.exsoft.turret.Main;
import ru.exsoft.turret.Utils.MathUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class OpenCVFaceDetected extends Thread{
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd HH-mm-ss");
    private static SimpleDateFormat day = new SimpleDateFormat("dd");
    private static SimpleDateFormat hours = new SimpleDateFormat("HH");
    private static SimpleDateFormat minutes = new SimpleDateFormat("mm");
    private int[] smothx = new int[3];
    private int[] smothy = new int[3];
    private Date dateLast;
    private Date dateNext;
    private Date dateCurrent;
    public static int interval = 2;
    private long lastshoottime;
    public static boolean notstop = true;
    private int attempt = 0;
    private static final int WIDTH = 320;
    private static final int HEIGHT = 240;
    private byte indexS;



    public void run() {
        VideoCapture camera = null;
        try{
            //System.out.printf("java.library.path: %s%n", System.getProperty("java.library.path"));
            if (System.getProperty("os.name").equals("Linux")) {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME); //arm
            } else if (System.getProperty("os.arch").equals("amd64")){
                System.loadLibrary("./lib/Windows/x64/opencv_java341");
            } else if (true){  //TODO Узнать название архитектуры x86  вписать в if
                System.out.println(System.getProperty("os.arch"));
                System.loadLibrary("./lib/Windows/x86/opencv_java341");
            }
        } catch (UnsatisfiedLinkError ex){
            //todo error
            ex.printStackTrace();
        }
        camera = new VideoCapture(0);
        camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, WIDTH);
        camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, HEIGHT);
        if(!camera.isOpened()){
            System.out.println("Error, CAMERA NOT FOUND");
        } else {
            int sensivity = 20;
            double maxArea = 20;
            long lastTime = System.currentTimeMillis();
            int fps = 0;
            for (int j = 0; j < 3; j++) {
                smothx[j] = WIDTH/2;
                smothy[j] = HEIGHT/2;
            }
            boolean first = true;

            Mat frame = new Mat(WIDTH, HEIGHT, CvType.CV_8UC3);
            Mat frame_current = new Mat(WIDTH, HEIGHT, CvType.CV_8UC3);
            Mat frame_previous = new Mat(WIDTH, HEIGHT, CvType.CV_8UC3);
            Mat frame_result = new Mat(WIDTH, HEIGHT, CvType.CV_8UC3);
            Size size = new Size(3, 3);
            Mat v = new Mat();
            Scalar scalar1 = new Scalar(0, 0, 255);
            Scalar scalar2 = new Scalar(0, 255, 0);
            Scalar scalar3 = new Scalar(255, 0, 0);

            while(notstop){
                if (camera.read(frame)){
                    fps++;
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime > 1000){
                        //System.out.println("FPS: " + fps);
                        lastTime = currentTime;
                        fps = 0;
                    }
                    frame.copyTo(frame_current);
                    Imgproc.GaussianBlur(frame_current, frame_current, size, 0);
                    if (!first) {
                        Core.subtract(frame_previous, frame_current, frame_result);
                        Imgproc.cvtColor(frame_result, frame_result, Imgproc.COLOR_RGB2GRAY);
                        Imgproc.threshold(frame_result, frame_result, sensivity, 255, Imgproc.THRESH_BINARY);
                        List<MatOfPoint>contours = new ArrayList<MatOfPoint>();
                        Imgproc.findContours(frame_result, contours, v, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
                        v.release();
                        boolean found = false;
                        ArrayList<Rect> rects = new ArrayList<>();
                        for(int idx = 0; idx < contours.size(); idx++) {
                            Mat contour = contours.get(idx);
                            double contourarea = Imgproc.contourArea(contour);
                            if(contourarea > maxArea) {
                                found = true;
                                Rect r = Imgproc.boundingRect(contours.get(idx));
                                rects.add(r);
                                Imgproc.drawContours(frame, contours, idx, scalar3);
                                Imgproc.rectangle(frame, r.br(), r.tl(), scalar2, 1);
                            }
                            contour.release();
                        }
                        if (found) {
                            Turret.motion();
                            if (++indexS > 2) indexS = 0;
                            int tempx = 0;
                            int tempy = 0;
                            for (int j = 0; j < rects.size(); j++) {
                                tempx+= rects.get(j).x;
                                tempx+= rects.get(j).width/2;
                                tempy+= rects.get(j).height/2;
                                tempy+= rects.get(j).y;
                            }
                            smothx[indexS] = tempx/rects.size();
                            smothy[indexS] = tempy/rects.size();
                            Rect r = new Rect();
                            r.x = Math.round(MathUtils.middleOf3(smothx[0], smothx[1], smothx[2]));
                            r.y = Math.round(MathUtils.middleOf3(smothy[0], smothy[1], smothy[2]));
                            r.width = 3;
                            r.height = 3;
                            Imgproc.rectangle(frame, r.br(), r.tl(), scalar1, 3);
                            Turret.setX(MathUtils.map(r.x, 0, WIDTH, -100, 100));
                            Turret.setY(MathUtils.map(r.y, 0, HEIGHT, -100, 100));
                            //System.out.println("Moved X:" + r.x + " ; Y:" + r.y);
                        }
                    }
                    if (Main.isGui) {
                        MatOfByte byteMat = new MatOfByte();
                        Imgcodecs.imencode(".bmp", frame, byteMat);
                        Data.getInstance().setCurrentImg(new Image(new ByteArrayInputStream(byteMat.toArray())));
                        GeneralController.instance.UpdateImage();
                    }
                    first = false;
                    frame_current.copyTo(frame_previous);
                    frame.release();
                    frame_result.release();
                    frame_current.release();
                }
            }
        }camera.release();
    }
}  