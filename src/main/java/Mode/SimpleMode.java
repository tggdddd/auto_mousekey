package Mode;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

/**
 * @ClassName DefaultRecord
 * @Description
 * @Author 15014
 * @Time 2023/1/7 0:33
 * @Version 1.0
 */
@Deprecated
/* 键盘输入keyCode部分不相同 */
public class SimpleMode extends Mode {
    private static final int NATIVE_KEY_RELEASED = 1;
    private static final int NATIVE_KEY_PRESSED = 0;
    private static final int NATIVE_MOUSE_PRESSED = 2;
    private static final int NATIVE_MOUSE_RELEASED = 3;
    private static final int NATIVE_MOUSE_MOVED = 4;
    private static final int NATIVE_MOUSE_WHEEL = 5;

    @Override
    public void play() {
        String string = null;
        try {
            startTime = 0;
            long endTime = 0;
            Robot robot = new Robot();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while ((string = bufferedReader.readLine()) != null) {
                String[] strings = string.split(",");
                if (strings.length < 3) {
                    continue;
                }
                endTime = Integer.parseInt(strings[1]);
                robot.setAutoDelay((int) (endTime - startTime));
                startTime = endTime;
                switch (Integer.parseInt(strings[0])) {
                    case NATIVE_KEY_RELEASED:
                        robot.keyRelease(KeyEvent.VK_ACCEPT);
                        robot.keyRelease(Integer.parseInt(strings[2]));
                        break;
                    case NATIVE_KEY_PRESSED:
                        robot.keyPress(Integer.parseInt(strings[2]));
                        break;
                    case NATIVE_MOUSE_PRESSED:
                        robot.mouseMove(Integer.parseInt(strings[3]), Integer.parseInt(strings[4]));
                        robot.mousePress(InputEvent.getMaskForButton(Integer.parseInt(strings[2])));
                        break;
                    case NATIVE_MOUSE_RELEASED:
                        robot.mouseMove(Integer.parseInt(strings[3]), Integer.parseInt(strings[4]));
                        robot.mouseRelease(InputEvent.getMaskForButton(Integer.parseInt(strings[2])));
                        break;
                    case NATIVE_MOUSE_MOVED:
                        robot.mouseMove(Integer.parseInt(strings[2]), Integer.parseInt(strings[3]));
                        break;
                    case NATIVE_MOUSE_WHEEL:
                        robot.mouseWheel(Integer.parseInt(strings[2]));
                        break;
                }

            }
        } catch (IOException | AWTException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            System.out.println(string);
            e.printStackTrace();
        }

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        long delay = new Date().getTime() - startTime;
        int modifiers = NATIVE_KEY_PRESSED;
        String keyCode = String.valueOf(nativeEvent.getRawCode());
        recordString(modifiers + "," + delay + "," + keyCode);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
        long delay = new Date().getTime() - startTime;
        int modifiers = NATIVE_KEY_RELEASED;
        String keyCode = String.valueOf(nativeEvent.getRawCode());
        recordString(modifiers + "," + delay + "," + keyCode);
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeEvent) {
        long delay = new Date().getTime() - startTime;
        int modifiers = NATIVE_MOUSE_PRESSED;
        String button = String.valueOf(nativeEvent.getButton());
        String positionX = String.valueOf(nativeEvent.getX());
        String positionY = String.valueOf(nativeEvent.getY());
        recordString(modifiers + "," + delay + "," + button + "," + positionX + "," + positionY);
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
        long delay = new Date().getTime() - startTime;
        int modifiers = NATIVE_MOUSE_RELEASED;
        String button = String.valueOf(nativeEvent.getButton());
        String positionX = String.valueOf(nativeEvent.getX());
        String positionY = String.valueOf(nativeEvent.getY());
        recordString(modifiers + "," + delay + "," + button + "," + positionX + "," + positionY);
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeEvent) {
        long delay = new Date().getTime() - startTime;
        int modifiers = NATIVE_MOUSE_MOVED;
        String positionX = String.valueOf(nativeEvent.getX());
        String positionY = String.valueOf(nativeEvent.getY());
        recordString(modifiers + "," + delay + "," + positionX + "," + positionY);
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeEvent) {
        nativeMouseMoved(nativeEvent);
    }

    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeEvent) {
        long delay = new Date().getTime() - startTime;
        int modifiers = NATIVE_MOUSE_WHEEL;
        String direct = String.valueOf(nativeEvent.getWheelRotation());
        String positionX = String.valueOf(nativeEvent.getX());
        String positionY = String.valueOf(nativeEvent.getY());
        recordString(modifiers + "," + delay + "," + direct + "," + positionX + "," + positionY);
    }
}
