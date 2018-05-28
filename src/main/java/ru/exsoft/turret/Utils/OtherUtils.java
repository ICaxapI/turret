package ru.exsoft.turret.Utils;

import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class OtherUtils {

    public static void sleepWinoutEx(long ms) {
        try {
            sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String checkString(String string) {
        Pattern pat=Pattern.compile("[-]?[0-9]+(.[0-9]+)?");
        Matcher matcher=pat.matcher(string);
        while (matcher.find())
        {
            return matcher.group();
        }
        return null;
    }

    public java.awt.Image bufferedImagetoImage(BufferedImage bi) {
        return Toolkit.getDefaultToolkit().createImage(bi.getSource());
    }

    public static byte[] encodeToStr(int x, int y, BufferedImage img, String type){
        byte[] imageBytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            BufferedImage scaled = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = scaled.createGraphics();
            g.drawImage(img, 0, 0, x, y, null);
            g.dispose();
            ImageIO.write(scaled, type, bos);
            imageBytes = bos.toByteArray();
            bos.close();
        } catch (IOException ex){
            //todo error
            ex.printStackTrace();
        }
        return imageBytes;
    }

    public static BufferedImage convertMatToBufferedImage(Mat mat) {
        byte[] data = new byte[mat.width() * mat.height() * (int)mat.elemSize()];
        int type;
        mat.get(0, 0, data);
        switch (mat.channels()) {
            case 1:
                type = BufferedImage.TYPE_BYTE_GRAY;
                break;
            case 3:
                type = BufferedImage.TYPE_3BYTE_BGR;
                // bgr to rgb
                byte b;
                for (int i = 0; i < data.length; i = i + 3) {
                    b = data[i];
                    data[i] = data[i + 2];
                    data[i + 2] = b;
                }
                break;
            default:
                throw new IllegalStateException("Unsupported number of channels");
        }
        BufferedImage out = new BufferedImage(mat.width(), mat.height(), type);
        out.getRaster().setDataElements(0, 0, mat.width(), mat.height(), data);
        mat = null;
        return out;
    }
}
