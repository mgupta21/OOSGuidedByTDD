package com.java.tdd;

import org.junit.After;
import org.junit.Test;

/**
 * Created by mgupta on 2/7/18.
 */
public class AuctionSniperEndToEndTest {

    // Requirements
    // 1. XMPP message broker (smack) to let application talk to stub auction
    // 2. stub/fake auction that can communicate over XMPP
    // 3. GUI testing framework i.e. a framework for testing swing applications (window licker)
    // 4. Test harness

    // Application runner is an object that wraps up all management and communication with swing application
    // It has reference to its main window for querying the state of GUI and shutting down application after test
    // Window licker does the hard work to find and control swing GUI components

    private final FakeAuctionServer auction     = new FakeAuctionServer("item-12383");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void testSniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper();
        auction.announceClosed();
        application.showSniperHasLostAuction();
    }

    @After
    public void stopAuction() throws Exception {
        auction.close();
    }

    @After
    public void stopApplication() throws Exception {
        application.stop();
    }
}
