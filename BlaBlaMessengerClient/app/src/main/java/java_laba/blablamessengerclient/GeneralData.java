package java_laba.blablamessengerclient;

import cloud.Cloud;
import data_structures.ContactId;

/**
 * Created by VladVin on 17.05.2015.
 */
public class GeneralData {
    public static Cloud cloud;
    public static ConversationContactsPair conversationContactsPair;

    public static class ConversationContactsPair {
        public ContactId me;
        public ContactId friend;

        public ConversationContactsPair(ContactId meId, ContactId friendId) {
            me = meId;
            friend = friendId;
        }
    }
}
