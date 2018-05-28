package ru.exsoft.turret.Utils;

import org.opencv.core.Mat;
import ru.exsoft.turret.Modules.Turret;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MusicUtils {

    public static void playSound(String name) {
        new Thread(() -> {
            List<File> files = getFiles(name, "sound");
            if (files.size() > 1) {
                playMusicSDL(files.get(MathUtils.random(1, files.size())).getAbsolutePath());
            } else {
                playMusicSDL(files.get(0).getAbsolutePath());
            }
        }).start();
    }

    public static void playFire() {
        new Thread(() -> {
            while (Turret.isFire()) {
                List<File> files = getFiles("fire", "sound");
                if (files.size() > 1) {
                    playMusicSDL(files.get(MathUtils.random(1, files.size())).getAbsolutePath());
                } else {
                    playMusicSDL(files.get(0).getAbsolutePath());
                }
            }
        }).start();
    }

    public static void loopSound(String name, int count) {
        new Thread(() -> {
            try {
                List<File> files = getFiles(name, "sound");
                String loopName = "";
                if (files.size() > 1) {
                    loopName = files.get(MathUtils.random(1, files.size())).getAbsolutePath();
                } else {
                    loopName = files.get(0).getAbsolutePath();
                }
                System.out.println("Looping " + loopName);
                File f = new File(loopName);
                AudioInputStream ais = AudioSystem.getAudioInputStream(f);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.loop(count);
                clip.start();
            } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void playVoice(String name) {
        new Thread(() -> {
            List<File> files = getFiles(name, "voice");
            if (files.size() > 1) {
                playMusicSDL(files.get(MathUtils.random(1, files.size())).getAbsolutePath());
            } else {
                playMusicSDL(files.get(0).getAbsolutePath());
            }
        }).start();
    }

    private static ArrayList<File> getFiles(String name, String folder) {
        File[] files = new File(folder).listFiles(((dir1, name1) -> name.startsWith(name)));
        ArrayList<File> ret = new ArrayList<>();
        for (File file : Arrays.asList(files)) {
            if (file.getName().contains(name)) ret.add(file);
        }
        return ret;
    }

    private static void playMusicSDL(String file) {
        //System.out.println("Playing " + file);
        SourceDataLine clipSDL = null;
        AudioInputStream ais = null;
        byte[] b = new byte[2048];
        try {
            File f = new File(file);
            ais = AudioSystem.getAudioInputStream(f);
            AudioFormat af = ais.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
            if (AudioSystem.isLineSupported(info)) {
                clipSDL = (SourceDataLine) AudioSystem.getLine(info);
                clipSDL.open(af);
                clipSDL.start();
                int num = 0;
                while ((num = ais.read(b)) != -1)
                    clipSDL.write(b, 0, num);
                clipSDL.drain();
                ais.close();
                clipSDL.stop();
                clipSDL.close();
            } else {
                System.exit(0);
            }
        } catch (Exception e) {
            //todo error
            System.out.println(e);
        }
    }
}
