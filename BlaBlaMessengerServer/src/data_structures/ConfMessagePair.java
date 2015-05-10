package data_structures;

public class ConfMessagePair extends DataObject {
    public ConferenceId Destination;
    public MessageData Message;
    
    public ConfMessagePair( final ConferenceId destination, 
            final MessageData message )
    {
        Destination = destination;
        Message = message;
    }
}
