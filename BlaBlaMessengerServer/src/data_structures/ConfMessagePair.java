package data_structures;

public class ConferenceMessagePair extends DataObject {
    public ConferenceId Conference;
    public MessageData Message;
    
    public ConferenceMessagePair( final ConferenceId conference, 
            final MessageData message )
    {
        Conference = conference;
        Message = message;
    }
}
