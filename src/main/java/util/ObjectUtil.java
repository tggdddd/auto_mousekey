package util;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;

/**
 * @ClassName ObjectToString
 * @Description 对象存储  减少序列化导致的文件臃肿
 * @Author 15014
 * @Time 2023/1/7 13:12
 * @Version 1.0
 */
public class ObjectUtil {

    public static final int NATIVE_KEY_EVENT = 0;
    public static final int NATIVE_MOUSEWHEEL_EVENT = 1;
    public static final int NATIVE_MOUSE_EVENT = 2;

    public static String ObjectToString(NativeKeyEvent object) {
        String stringBuilder = NATIVE_KEY_EVENT +
                "#" + object.getID() +
                "#" + object.getModifiers() +
                "#" + object.getRawCode() +
                "#" + object.getKeyCode() +
                "#" + object.getKeyChar() +
                "#" + object.getKeyLocation();
        return stringBuilder;
    }

    public static String ObjectToString(NativeMouseEvent object) {
        String stringBuilder = NATIVE_MOUSE_EVENT +
                "#" + object.getID() +
                "#" + object.getModifiers() +
                "#" + object.getX() +
                "#" + object.getY() +
                "#" + object.getButton();
        return stringBuilder;
    }

    public static String ObjectToString(NativeMouseWheelEvent object) {
        String stringBuilder = NATIVE_MOUSEWHEEL_EVENT +
                "#" + object.getID() +
                "#" + object.getModifiers() +
                "#" + object.getX() +
                "#" + object.getY() +
                "#" + object.getScrollType() +
                "#" + object.getScrollAmount() +
                "#" + object.getWheelRotation();
        return stringBuilder;
    }

    public static Object StringToObject(String s) {
        String[] v = s.split("#");
        Object o = null;
        switch (Integer.parseInt(v[0])) {
            case NATIVE_KEY_EVENT:
                o = new NativeKeyEvent(Integer.parseInt(v[1]), Integer.parseInt(v[2]), Integer.parseInt(v[3]), Integer.parseInt(v[4]), v[5].toCharArray()[0], Integer.parseInt(v[6]));
                break;
            case NATIVE_MOUSE_EVENT:
                o = new NativeMouseEvent(Integer.parseInt(v[1]), Integer.parseInt(v[2]), Integer.parseInt(v[3]), Integer.parseInt(v[4]), 1, Integer.parseInt(v[5]));
                break;
            case NATIVE_MOUSEWHEEL_EVENT:
                o = new NativeMouseWheelEvent(Integer.parseInt(v[1]), Integer.parseInt(v[2]), Integer.parseInt(v[3]), Integer.parseInt(v[4]), 1, Integer.parseInt(v[5]), Integer.parseInt(v[6]), Integer.parseInt(v[7]));
                break;
        }
        return o;
    }

    public static Object StringToObject(String[] v) {
        Object o = null;
        switch (Integer.parseInt(v[0])) {
            case NATIVE_KEY_EVENT:
                o = new NativeKeyEvent(Integer.parseInt(v[1]), Integer.parseInt(v[2]), Integer.parseInt(v[3]), Integer.parseInt(v[4]), v[5].toCharArray()[0], Integer.parseInt(v[6]));
                break;
            case NATIVE_MOUSE_EVENT:
                o = new NativeMouseEvent(Integer.parseInt(v[1]), Integer.parseInt(v[2]), Integer.parseInt(v[3]), Integer.parseInt(v[4]), 1, Integer.parseInt(v[5]));
                break;
            case NATIVE_MOUSEWHEEL_EVENT:
                o = new NativeMouseWheelEvent(Integer.parseInt(v[1]), Integer.parseInt(v[2]), Integer.parseInt(v[3]), Integer.parseInt(v[4]), 1, Integer.parseInt(v[5]), Integer.parseInt(v[6]), Integer.parseInt(v[7]));
                break;
        }
        return o;
    }

    public static Object StringToObject(String[] v, boolean hasTime) {
        Object o = null;
        int i;
        switch (Integer.parseInt(v[1])) {
            case NATIVE_KEY_EVENT:
                i = 2;
                o = new NativeKeyEvent(Integer.parseInt(v[i++]), Integer.parseInt(v[i++]), Integer.parseInt(v[i++]), Integer.parseInt(v[i++]), v[i++].toCharArray()[0], Integer.parseInt(v[i++]));
                break;
            case NATIVE_MOUSE_EVENT:
                i = 2;
                o = new NativeMouseEvent(Integer.parseInt(v[i++]), Integer.parseInt(v[i++]), Integer.parseInt(v[i++]), Integer.parseInt(v[i++]), 1, Integer.parseInt(v[i++]));
                break;
            case NATIVE_MOUSEWHEEL_EVENT:
                i = 2;
                o = new NativeMouseWheelEvent(Integer.parseInt(v[i++]), Integer.parseInt(v[i++]), Integer.parseInt(v[i++]), Integer.parseInt(v[i++]), 1, Integer.parseInt(v[i++]), Integer.parseInt(v[i++]), Integer.parseInt(v[i++]));
                break;
        }
        return o;
    }
}
