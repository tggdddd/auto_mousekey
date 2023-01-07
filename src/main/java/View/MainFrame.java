package View;

import Constant.Constant;
import Mode.Mode;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;


/**
 * @ClassName MainFrame
 * @Description 主视图
 * @Author 15014
 * @Time 2023/1/6 20:52
 * @Version 1.0
 */

public class MainFrame extends JFrame {
    final Component parent = this;
    // 录制使用的模式
    private final Mode mode;
    public Dialog settingDialog = null;
    public JButton stop;
    JButton record;
    JButton play;
    JButton pause;
    JButton setting;
    JButton load;
    JSlider speed;
    // play stop pause模式
    private String status;

    public MainFrame(Class<? extends Mode> modeClass) {
        settingDialog = new SettingDialog(parent);
        try {
            this.mode = modeClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        this.mode.mainFrame = this;
        // 初始设置
        setTitle("自动鼠标键盘");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // 布局
        JPanel top = new JPanel();
        JPanel bottom = new JPanel();
        add(top, BorderLayout.NORTH);
        add(bottom, BorderLayout.SOUTH);
        // top面板
        record = new JButton("录制");
        play = new JButton("播放");
        pause = new JButton("暂停");
        stop = new JButton("结束");
        load = new JButton("加载");
        setting = new JButton("热键");
        top.add(record);
        top.add(load);
        top.add(play);
        top.add(pause);
        top.add(stop);
        top.add(setting);
        // bottom面板
        JPanel speedPanel = new JPanel();
        JLabel speedLabel = new JLabel("速度:");
        speed = new JSlider(0, 15, 5);
        speed.setPaintTicks(true);
        speed.setPaintLabels(true);
        speed.setMajorTickSpacing(5);
        speed.setMinorTickSpacing(1);
        speed.setLabelTable(new Hashtable<Integer, Component>() {
            {
                put(0, new JLabel("0.1"));
                put(5, new JLabel("1"));
                put(10, new JLabel("2"));
                put(15, new JLabel("3"));
            }
        });
        speedPanel.add(speedLabel);
        speedPanel.add(speed);
        JPanel intervalPanel = new JPanel();
        JLabel intervalLabel = new JLabel("重复:");
        // 限制数字 与 参数同步
        JTextField interval = new JTextField(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                try {
                    Long.valueOf(str);
                } catch (NumberFormatException e) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
                super.insertString(offs, str, a);
            }
        }, "1", 2);
        intervalPanel.add(intervalLabel);
        intervalPanel.add(interval);
        bottom.add(speedPanel, BorderLayout.WEST);
        bottom.add(intervalPanel, BorderLayout.EAST);

        // 事件监听
        record.addActionListener(e -> setStatus(Constant.RECORD));
        load.addActionListener(e -> setStatus(Constant.LOAD));
        play.addActionListener(e -> setStatus(Constant.PLAY));
        pause.addActionListener(e -> setStatus(Constant.PAUSE));
        stop.addActionListener(e -> setStatus(Constant.STOP));
        setting.addActionListener(e -> setStatus(Constant.SETTING));
        // 参数监听
        // 速度
        speed.addChangeListener(e -> {
            double value = speed.getValue() / 5.0;
            if (value == 0) {
                value = 0.1;
            }
            mode.setSpeed(value);
        });
        interval.addActionListener(e -> {
            mode.setInterval(Integer.parseInt(interval.getText()));
        });
        // 热键窗口
        setting.addActionListener(e -> {
            settingDialog.setVisible(true);
            settingDialog.setLocationRelativeTo(parent);
        });
        // 热键窗口监控
        settingDialog.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                // GlobalScreen.removeNativeKeyListener((NativeKeyListener) settingDialog);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                // GlobalScreen.addNativeKeyListener((NativeKeyListener) settingDialog);
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        // 窗口设置
        pack();
        setResizable(false);
        setStatus(Constant.INIT);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        try {
            getClass().getMethod(status).invoke(this);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        this.status = status;
    }

    public void stop() {
        if (!stop.isEnabled()) {
            return;
        }
        mode.close();
        if (getStatus().equals(Constant.RECORD)) {
            mode.save(this);
        } else {
            mode.setEnd(true);
        }
        record.setEnabled(true);
        load.setEnabled(true);
        play.setEnabled(true);
        pause.setEnabled(false);
        stop.setEnabled(false);
    }

    public void pause() {
        if (!pause.isEnabled()) {
            return;
        }
        play.setEnabled(true);
        pause.setEnabled(false);
        stop.setEnabled(true);
        record.setEnabled(false);
        load.setEnabled(false);
        mode.setStop(true);
    }

    public void play() {
        if (!play.isEnabled()) {
            return;
        }
        play.setEnabled(false);
        pause.setEnabled(true);
        stop.setEnabled(true);
        mode.setEnd(false);
        if (mode.getState() == Thread.State.NEW) {
            mode.start();
        } else {
            mode.setStop(false);
            synchronized (Mode.lock) {
                Mode.lock.notifyAll();
            }
        }
    }

    public void record() {
        if (!record.isEnabled()) {
            return;
        }
        record.setEnabled(false);
        load.setEnabled(false);
        play.setEnabled(false);
        pause.setEnabled(false);
        stop.setEnabled(true);
        mode.setRecord(true);
    }

    public void init() {
        play.setEnabled(false);
        pause.setEnabled(false);
        stop.setEnabled(false);
    }

    public void load() {
        if (!load.isEnabled()) {
            return;
        }
        if (mode.load(this)) {
            play.setEnabled(true);
            pause.setEnabled(false);
            stop.setEnabled(false);
        }
    }

    public void setting() {

    }
}

class SettingDialog extends JDialog implements NativeKeyListener {
    public static final int RECORD = 0;
    public static final int START = 1;
    public static final int PAUSE = 2;
    public static final int STOP = 3;
    public static final LinkedList<Integer>[] list = new LinkedList[4];
    private static final ArrayList<Integer> candidate = new ArrayList<Integer>(4);
    JTextField recordText;
    JTextField playText;
    JTextField stopText;
    JTextField pauseText;
    Component parent;
    JTextField[] settingTexts = new JTextField[4];
    private boolean isEdit;
    private int selectEdit;

    //  默认热键
    {
        list[0] = new LinkedList<Integer>() {{
            add(29);
            add(1);
        }};
        list[1] = new LinkedList<Integer>() {{
            add(29);
            add(2);
        }};
        list[2] = new LinkedList<Integer>() {{
            add(29);
            add(3);
        }};
        list[3] = new LinkedList<Integer>() {{
            add(29);
            add(4);
        }};
    }

    public SettingDialog(Component parent) {
        this.parent = parent;
        GlobalScreen.addNativeKeyListener(this);
        //  热键交互信息
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer o : list[RECORD]) {
            stringBuilder.append(NativeKeyEvent.getKeyText(o)).append("+");
        }
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        String record = stringBuilder.toString();
        stringBuilder = new StringBuilder();
        for (Integer o : list[START]) {
            stringBuilder.append(NativeKeyEvent.getKeyText(o)).append("+");
        }
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        String play = stringBuilder.toString();
        stringBuilder = new StringBuilder();
        for (Integer o : list[PAUSE]) {
            stringBuilder.append(NativeKeyEvent.getKeyText(o)).append("+");
        }
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        String pause = stringBuilder.toString();
        stringBuilder = new StringBuilder();
        for (Integer o : list[STOP]) {
            stringBuilder.append(NativeKeyEvent.getKeyText(o)).append("+");
        }
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        String stop = stringBuilder.toString();

        JPanel recordPanel = new JPanel();
        JLabel recordLabel = new JLabel("录制:");
        recordText = new JTextField(record, 6);
        recordText.setName(String.valueOf(RECORD));
        recordPanel.add(recordLabel);
        recordPanel.add(recordText);

        JPanel playPanel = new JPanel();
        JLabel playLabel = new JLabel("开始:");
        playText = new JTextField(play, 6);
        playText.setName(String.valueOf(START));
        playPanel.add(playLabel);
        playPanel.add(playText);

        JPanel pausePanel = new JPanel();
        JLabel pauseLabel = new JLabel("暂停:");
        pauseText = new JTextField(pause, 6);
        pauseText.setName(String.valueOf(PAUSE));
        pausePanel.add(pauseLabel);
        pausePanel.add(pauseText);

        JPanel stopPanel = new JPanel();
        JLabel stopLabel = new JLabel("结束:");
        stopText = new JTextField(stop, 6);
        stopText.setName(String.valueOf(STOP));
        stopPanel.add(stopLabel);
        stopPanel.add(stopText);
        JPanel bank = new JPanel();
        bank.setLayout(new GridLayout(4, 1));
        bank.add(recordPanel);
        bank.add(playPanel);
        bank.add(pausePanel);
        bank.add(stopPanel);
        Container contentPane = this.getContentPane();
        contentPane.add(bank);
        this.setTitle("热键设置");
        this.setVisible(false);
        this.setLocationRelativeTo(parent);
        this.pack();
        SettingTextFocusListener settingTextFocusListener = new SettingTextFocusListener();
        recordText.addFocusListener(settingTextFocusListener);
        stopText.addFocusListener(settingTextFocusListener);
        pauseText.addFocusListener(settingTextFocusListener);
        playText.addFocusListener(settingTextFocusListener);
        settingTexts[RECORD] = recordText;
        settingTexts[START] = playText;
        settingTexts[PAUSE] = pauseText;
        settingTexts[STOP] = stopText;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        candidate.add(nativeEvent.getKeyCode());
        if (isEdit) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Integer o : candidate) {
                stringBuilder.append(NativeKeyEvent.getKeyText(o)).append("+");
            }
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            String text = stringBuilder.toString();
            settingTexts[selectEdit].setText(text);
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
        if (isEdit) {
            if (candidate.size() == 0) {
                return;
            }
            list[selectEdit] = new LinkedList<>(candidate);
            for (int i = 0; i < list.length; i++) {
                LinkedList<Integer> list1 = list[i];
                System.out.print("位置" + i + ":");
                for (Integer integer : list1) {
                    System.out.print(NativeKeyEvent.getKeyText(integer) + " ");
                }
                System.out.println();
            }
        } else {
            // 匹配到热键
            for (int i = 0; i < list.length; i++) {
                if (candidate.equals(list[i])) {
                    System.out.println("匹配到了" + i);
                    switch (i) {
                        case RECORD:
                            ((MainFrame) parent).record.doClick();
                            break;
                        case START:
                            ((MainFrame) parent).play.doClick();
                            break;
                        case PAUSE:
                            ((MainFrame) parent).pause.doClick();
                            break;
                        case STOP:
                            ((MainFrame) parent).stop.doClick();
                            break;
                    }
                    break;
                }
            }
        }
        // 清空候选
        candidate.clear();
    }

    class SettingTextKeyListener implements KeyListener {
        ArrayList<Integer> candidate = new ArrayList<Integer>(4);

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

    class SettingTextFocusListener implements FocusListener {
        @Override
        public void focusGained(FocusEvent e) {
            JTextField textField = (JTextField) e.getComponent();
            textField.setEditable(false);
            isEdit = true;
            selectEdit = Integer.parseInt(e.getComponent().getName());
        }

        @Override
        public void focusLost(FocusEvent e) {
            JTextField textField = (JTextField) e.getComponent();
            textField.setEditable(true);
            isEdit = false;
        }
    }
}
