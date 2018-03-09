package com.java.tdd;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;
import javax.swing.border.LineBorder;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class Application {

    public static final String  MAIN_WINDOW_NAME    = "Welcome";
    public static final String  BID_COMMAND_FORMAT  = "SOLVersion: 1.1; Command: BID; Price: %d;";
    public static final String  JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
    public static final String  AUCTION_RESOURCE    = "Auction";
    public static final String  ITEM_ID_AS_LOGIN    = "auction-%s";
    private static final String AUCTION_ID_FORMAT   = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
    private MainWindow          ui;

    @SuppressWarnings("unused")
    private Chat                notToBeGCD;

    public Application() throws Exception {
        startUserInterface();
    }

    private void startUserInterface() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow());
    }

    public static void main(String xmppHostname, String sniperId, String sniperPassword, String itemId) throws Exception {
        Application application = new Application();
        application.joinAuction(connectTo(xmppHostname, "user", "password"), itemId);
    }

    private void joinAuction(XMPPConnection connection, String itemId) throws XMPPException {
        final Chat chat = connection.getChatManager().createChat(
            auctionId(itemId, connection),
            new MessageListener() {

                @Override
                public void processMessage(Chat chat, Message message) {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {

                            @Override
                            public void run() {
                                ui.showStatus(MainWindow.STATUS_LOST);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            });
        this.notToBeGCD = chat;
        chat.sendMessage(new Message());
    }

    private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }

    private static XMPPConnection connectTo(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }

    // Swing's JFrame
    public class MainWindow extends JFrame {

        public static final String  SNIPER_STATUS_NAME = "Sniper Status";
        private static final String STATUS_JOINING     = "Joining Auction";
        private static final String STATUS_LOST        = "Lost Auction";
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

        public void showStatus(String status) {
            sniperStatus.setText(status);
        }

    }
}
