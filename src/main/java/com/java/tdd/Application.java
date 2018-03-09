package com.java.tdd;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;
import javax.swing.border.LineBorder;

public class Application {

    public static final String MAIN_WINDOW_NAME    = "Welcome";
    public static final String BID_COMMAND_FORMAT  = "SOLVersion: 1.1; Command: BID; Price: %d;";
    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
    private MainWindow         ui;

    public Application() throws Exception {
        startUserInterface();
    }

    private void startUserInterface() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow());
    }

    public static void main(String xmppHostname, String sniperId, String sniperPassword, String itemId) throws Exception {
        Application application = new Application();

    }

    // Swing's JFrame
    public class MainWindow extends JFrame {

        public static final String  SNIPER_STATUS_NAME = "Sniper Status";
        private static final String STATUS_JOINING     = "Joining";
        private final JLabel        sniperStatus       = createLabel(STATUS_JOINING);

        public MainWindow() {
            super("Auction Sniper");
            setName(MAIN_WINDOW_NAME);
            add(sniperStatus);
            pack();
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
        }

        private JLabel createLabel(String initialText) {
            JLabel result = new JLabel(initialText);
            result.setName(SNIPER_STATUS_NAME);
            result.setBorder(new LineBorder(Color.black));
            return result;
        }
    }
}
