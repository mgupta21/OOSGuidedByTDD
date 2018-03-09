package com.java.tdd;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import static com.java.tdd.Application.BID_COMMAND_FORMAT;
import static com.java.tdd.Application.JOIN_COMMAND_FORMAT;
import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

// A substitute server that allows the test to check how the Auction Sniper interacts with an auction using XMPP messages (smack)
// There are two levels of events: events about a chat, such as people joining, and events within a chat, such as messages being received.
// 1. Connects to XMPP broker and accepts requests to join the chat from Sniper app
// 2. Receive chat messages from sniper
// 3. Sends messages back to sniper
// Smack : an event driven XMPP client library, since its event driven it has to register listener objects for events about new chat or
// events within existing chat
public class FakeAuctionServer {

    private static final String         ITEM_ID_AS_LOGIN = "acution-%s";
    private static final String         AUCTION_RESOURCE = "Auction";
    private static final String         XMPP_HOSTNAME    = "localhost";
    private static final String         AUCTION_PASSWORD = "auction";

    private String                      itemId;
    // XMPP Message Broker. The Sniper and fake auction in our end-to-end tests, even though they’re running in the same process, will communicate through this server.
    private final XMPPConnection        connection;
    private Chat                        currentChat;
    // Smack calls this listener with a chat object that represents the session when Sniper connects in
    // The Auction holds on to chat so it can exchange
    private final SingleMessageListener messageListener;

    public FakeAuctionServer(String itemId) {
        this.itemId = itemId;
        this.connection = new XMPPConnection(XMPP_HOSTNAME);
        currentChat = null;
        messageListener = new SingleMessageListener();
    }

    public String getItemId() {
        return itemId;
    }

    public void startSellingItem() throws XMPPException {
        connection.connect();
        connection.login(String.format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE);
        connection.getChatManager().addChatListener((chat, createdLocally) -> {
            currentChat = chat;
            // Add message listener to chat to accept message from the Sniper
            // we need to coordinate between the thread that runs the test and the Smack thread that feeds messages to the listener
            chat.addMessageListener(messageListener);
        });
    }

    public void announceClosed() throws XMPPException {
        currentChat.sendMessage(new Message());
    }

    public void stop() {
        connection.disconnect();
    }

    // Tells the stub auction to send a message back to the Sniper with the news of increment for the next bid and the winning bidder is “other
    // bidder.
    public void reportPrice(int price, int increment, String bidder) throws XMPPException {
        currentChat.sendMessage(format("SOLVersion: 1.1; Event: PRICE; "
            + "CurrentPrice: %d; Increment: %d; Bidder: %s;",
            price, increment, bidder));

    }

    // Test needs to know when a join message has arrived
    // We have to wait for the stub auction to receive the Join request before continuing with the test.
    // We use this assertion to synchronize the Sniper with the auction.
    public void hasReceivedJoinRequestFromSniper(String sniperId) throws InterruptedException {
        receivesMessageMatching(sniperId, equalTo(format(JOIN_COMMAND_FORMAT)));

    }

    public void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
        receivesMessageMatching(sniperId, equalTo(format(BID_COMMAND_FORMAT, bid)));
    }

    private void receivesMessageMatching(String sniperId, Matcher<String> messageMatcher) throws InterruptedException {
        messageListener.receivesAMessage(messageMatcher);
        // we check the Sniper’s identifier after we check the contents of the message.
        // This forces the server to wait until the message has arrived, which means that it must have accepted a connection and set up currentChat
        assertThat(currentChat.getParticipant(), equalTo(sniperId));
    }

    public class SingleMessageListener implements MessageListener {

        // the test has to wait for messages to arrive and time out if they don’t
        private final ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<>(1);

        @Override
        public void processMessage(Chat chat, Message message) {
            messages.add(message);
        }

        public void receivesAMessage(Matcher<? super String> messageMatcher) throws InterruptedException {
            final Message message = messages.poll(5, TimeUnit.SECONDS);
            assertThat("Message", message, is(notNullValue()));
            assertThat(message.getBody(), messageMatcher);

        }
    }
}
