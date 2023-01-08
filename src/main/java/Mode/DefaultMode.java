package Mode;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;
import util.ObjectUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

/**
 * @ClassName DefaultMode
 * @Description
 * @Author 15014
 * @Time 2023/1/7 13:04
 * @Version 1.0
 */
public class DefaultMode extends Mode {
    @Override
    public void play() {
        String string = null;
        int times = getInterval() == 0 ? Integer.MAX_VALUE : getInterval();
        while (times-- != 0) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                while ((string = bufferedReader.readLine()) != null) {
                    String[] s = string.split("#");
                    long delay = Long.parseLong(s[0]);
                    Object o = ObjectUtil.StringToObject(s, true);
                    if (isStop()) {
                        lock.wait();
                    }
                    if (isEnd()) {
                        return;
                    }
                    sleep((long) (delay / getSpeed()));
                    if (isStop()) {
                        lock.wait();
                    }
                    if (isEnd()) {
                        return;
                    }
                    GlobalScreen.postNativeEvent((NativeInputEvent) o);
                }
            } catch (IOException | InterruptedException | IllegalArgumentException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        long delay = new Date().getTime() - startTime;
        startTime = new Date().getTime();
        recordString(String.valueOf(delay), ObjectUtil.ObjectToString(nativeEvent));
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
        long delay = new Date().getTime() - startTime;
        startTime = new Date().getTime();
        recordString(String.valueOf(delay), ObjectUtil.ObjectToString(nativeEvent));
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeEvent) {
        long delay = new Date().getTime() - startTime;
        startTime = new Date().getTime();
        recordString(String.valueOf(delay), ObjectUtil.ObjectToString(nativeEvent));
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
        long delay = new Date().getTime() - startTime;
        startTime = new Date().getTime();
        recordString(String.valueOf(delay), ObjectUtil.ObjectToString(nativeEvent));
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeEvent) {
        long delay = new Date().getTime() - startTime;
        startTime = new Date().getTime();
        recordString(String.valueOf(delay), ObjectUtil.ObjectToString(nativeEvent));
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeEvent) {
        long delay = new Date().getTime() - startTime;
        startTime = new Date().getTime();
        recordString(String.valueOf(delay), ObjectUtil.ObjectToString(nativeEvent));
    }

    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeEvent) {
        long delay = new Date().getTime() - startTime;
        startTime = new Date().getTime();
        recordString(String.valueOf(delay), ObjectUtil.ObjectToString(nativeEvent));
    }

    public void recordString(String delay, String s) {
        if (!isRecord()) {
            return;
        }
        try {
            bufferedWriter.write(delay + "#");
            bufferedWriter.write(s + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
