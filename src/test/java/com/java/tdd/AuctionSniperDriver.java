package com.java.tdd;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JLabelDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

import static org.hamcrest.Matchers.equalTo;

// Window Licker : api to control swing GUI components and synchronize with swing threads and queues
// wl has Component driver that can manipulate a feature in Swing user interface
public class AuctionSniperDriver extends JFrameDriver {

    /**
     * @param timeoutMillis
     *            timeout period for finding frames and components
     */
    public AuctionSniperDriver(int timeoutMillis) {
        super(new GesturePerformer(), JFrameDriver.topLevelFrame(
            named(Application.MAIN_WINDOW_NAME),
            showingOnScreen()),
            new AWTEventQueueProber(timeoutMillis, 100));
    }

    public void showSniperStatus(String statusText) {
        new JLabelDriver(this, named(Application.MainWindow.SNIPER_STATUS_NAME)).hasText(equalTo(statusText));
    }

    public void showsSniperStatus(String statusLost) {

    }
}
