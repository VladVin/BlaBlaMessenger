package java_laba.blablamessengerclient;


import java.util.UUID;

/**
 * Created by VladVin on 23.05.2015.
 */
public class ConversationContactsPair {
    public UUID me;
    public UUID friend;

    public ConversationContactsPair(UUID meId, UUID friendId) {
        me = meId;
        friend = friendId;
    }
}
