package Mode;

import View.MainFrame;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * @ClassName Mode
 * @Description
 * @Author 15014
 * @Time 2023/1/6 20:34
 * @Version 1.0
 */
public abstract class Mode extends Thread implements NativeMouseMotionListener, NativeMouseListener, NativeMouseWheelListener, NativeKeyListener {
    public static final Object lock = new Object();
    public MainFrame mainFrame;
    long startTime;
    File file;
    BufferedWriter bufferedWriter;
    BufferedReader bufferedReader;
    private double speed = 1;
    private int interval = 1;
    private boolean end;
    private boolean isRecord = false;
    private boolean stop = false;

    public Mode() {
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
            GlobalScreen.addNativeMouseListener(this);
            GlobalScreen.addNativeMouseWheelListener(this);
            GlobalScreen.addNativeMouseMotionListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public boolean isRecord() {
        return isRecord;
    }

    public void setRecord(boolean record) {
        if (record) {
            try {
                file = File.createTempFile(UUID.randomUUID().toString(), "akm");
                bufferedWriter = new BufferedWriter(new FileWriter(file));
                startTime = 0;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        isRecord = record;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean load(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        // 设置默认显示的文件夹为当前文件夹
        fileChooser.setCurrentDirectory(new File("."));
        // 设置文件选择的模式
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // 设置文件过滤器
        fileChooser.setFileFilter(new FileNameExtensionFilter("数据文件(*.akm)", "akm"));
        // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            // 如果点击了"确定", 则获取选择的文件路径
            file = fileChooser.getSelectedFile();
            return true;
        }
        return false;
    }

    public void close() {
        try {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setRecord(false);
    }

    public boolean save(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        // 设置默认显示的文件夹为当前文件夹
        fileChooser.setCurrentDirectory(new File("."));
        // 设置文件过滤器
        fileChooser.setFileFilter(new FileNameExtensionFilter("数据文件(*.akm)", "akm"));
        // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
        int result = fileChooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            // 如果点击了"确定", 则获取选择的文件路径
            File file = fileChooser.getSelectedFile();
            if (file.getName().lastIndexOf(".akm") == -1) {
                file = new File(file + ".akm");
            }
            if (file.exists()) {
                file.delete();
            }
            this.file.renameTo(file);
            this.file = file;
            return true;
        }
        return false;
    }

    public abstract void play();

    public void recordString(String s) {
        if (!isRecord) {
            return;
        }
        try {
            bufferedWriter.write(s + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        synchronized (lock) {
            while (true) {
                try {
                    lock.wait();
                    play();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    mainFrame.stop.doClick();
                }
            }
        }
    }
}
