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
        // 1. Tell the auction to send a price to the Sniper
        auction.startSellingItem();

        // 2. Check the Sniper has received and responded to the price.
        application.startBiddingIn(auction);
        // Wait for stub auction to receive join request
        auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPPER_XMPP_ID);
        // stub auction sends message back to sniper with news that once the price is 1000 the next increment would be 98 and winning bidder is
        // other bidder
        auction.reportPrice(1000, 98, "other bidder");
        // check that the Sniper shows that it’s now bidding after it’s received the price update message from the auction
        application.hasShownSniperIsBidding();

        // 3. Check the auction has received an incremented bid from Sniper.
        auction.hasReceivedBid(1098, ApplicationRunner.SNIPPER_XMPP_ID);

        auction.announceClosed();
        application.showSniperHasLostAuction();
    }

    @After
    public void stopAuction() throws Exception {
        auction.stop();
    }

    @After
    public void stopApplication() throws Exception {
        application.stop();
    }
}
