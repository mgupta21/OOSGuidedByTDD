package com.java.tdd;

// Wraps up all management and communication with the swing application
// It obtains and holds a reference to main window for querying the state of the GUI and shutting down the application at the end of the
// test
// Swing : an event driven framework that creates its own internal threads to dispatch events
public class ApplicationRunner {

    public static final String  SNIPER_ID       = "sniper";
    public static final String  SNIPER_PASSWORD = "sniper";
    private static final String STATUS_LOST     = "";
    private static final String STATUS_JOINING  = "";
    public static final String  SNIPPER_XMPP_ID = "";
    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer auction) {
        // 1. WindowLicker can control Swing components if they’re in the same JVM, so we start the Sniper in a new thread
        Thread thread = new Thread("Test Application") {

            public static final String XMPP_HOSTNAME = "";

            @Override
            public void run() {
                try {
                    // 2. Bid for one item and pass the item identifier to main()
                    Application.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        // Window Licker Interface
        driver = new AuctionSniperDriver(1000);
        // Wait for the status to change to Joining so we know that the application has attempted to connect
        driver.showSniperStatus(STATUS_JOINING);
    }

    public void showSniperHHasLostAution() {
        driver.showsSniperStatus(STATUS_LOST);
    }

    public void stop() {
        //  Dispose of the window to make sure it won’t be picked up in another test before being garbage-collected.
        if (driver != null) {
            driver.dispose();
        }
    }

    public void showSniperHasLostAuction() {

    }

    public void hasShownSniperIsBidding() {

    }
}
