package java_laba.blablamessengerclient;

import data_structures.ContactId;

/**
 * Created by VladVin on 23.05.2015.
 */
public class ConversationContactsPair {
    public ContactId me;
    public ContactId friend;

    public ConversationContactsPair(ContactId meId, ContactId friendId) {
        me = meId;
        friend = friendId;
    }
}
