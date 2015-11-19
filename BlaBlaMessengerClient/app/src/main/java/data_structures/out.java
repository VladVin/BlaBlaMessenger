public class CommandData implements Serializable {
    public Commands Command;
    public DataObject Data;
    
    public CommandData( final Commands command, final DataObject data )
    {
        Command = command;
        Data = data;
    }
}

public enum Commands implements Serializable {
    RegisterContact,
    Disconnect,
    RefreshContacts,
    CreateConference,
    AddToConference,
    RemoveFromConference,
    DeleteConference,
    SendMessageToContact,
    SendMessageToConference,
    RefreshStorage,
    UploadFile,
    DownloadFile,
    RemoveFile
}

public class Conference extends DataObject {
    public ConferenceName Name;
    public ConferenceId Id;
    public ArrayList< ContactId > Members;
    
    public Conference( final ConferenceName name, 
            final ArrayList< ContactId > members )
    {
        Name = name;
        Id = null;
        Members = members;
    }
    
    public Conference( final Conference conference )
    {
        Name = conference.Name;
        Id = new ConferenceId();
        Members = conference.Members;
    }
}

public class ConferenceId extends DataObject {
    public UUID Id;
    public ConferenceId() { Id = UUID.randomUUID(); }
}

public class ConferenceName extends DataObject {
    public String Name;
    public ConferenceName( String name ) { Name = name; }
}

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

public class Contact extends DataObject {
    public ContactName Name;
    public ContactId Id;
    
    public Contact( final ContactName name, final ContactId id )
    {
        Name = name;
        Id = id;
    }
}

public class ContactConfMessagePair extends DataObject {
    public ContactId Source;
    public ConfMessagePair Message;
    
    public ContactConfMessagePair( final ContactId source,
        final ConfMessagePair message )
    {
        Source = source;
        Message = message;
    }
}

public class ContactConfPair extends DataObject {
    public ContactId Contact;
    public ConferenceId Conference;
    
    public ContactConfPair( final ContactId contact, 
            final ConferenceId conference )
    {
        Contact = contact;
        Conference = conference;
    }
}

public class ContactId extends DataObject {
    public UUID Id;
    public ContactId() { Id = UUID.randomUUID(); }
}

public class ContactMessagePair extends DataObject {
    public ContactId Contact;
    public MessageData Message;
    
    public ContactMessagePair( final ContactId contact, 
            final MessageData message )
    {
        Contact = contact;
        Message = message;
    }
}

public class ContactName extends DataObject {
    public String Name;
    public ContactName( final String name ) { Name = name; }
}

public class Contacts extends DataObject {
    public ArrayList< Contact > Contacts;
    
    public Contacts( ArrayList< Contact > contacts ) 
    { Contacts = contacts; }
}

public class DataObject implements Serializable {}

public class File extends DataObject {
    public FileName Name;
    public FileData Data;
    
    public File( final FileName name, final FileData data )
    {
        Name = name;
        Data = data;
    }
}

public class FileData extends DataObject {
    public byte[] Data;
    public FileData( final byte[] data ) { Data = data; }
}

public class FileId extends DataObject {
    public UUID Id;
    public FileId() { Id = UUID.randomUUID(); }
}

public class FileIdNamePair extends DataObject {
    public FileId Id;
    public FileName Name;
    
    public FileIdNamePair( final FileId id, final FileName name )
    {
        Id = id;
        Name = name;
    }
}

public class FileIdNamePairs extends DataObject {
    public ArrayList< FileIdNamePair > Pairs;
    
    public FileIdNamePairs( ArrayList< FileIdNamePair > pairs ) 
    { Pairs = pairs; }
}

public class FileName extends DataObject {
    public String Name;
    public FileName( String name ) { Name = name; }
}

public class MessageData extends DataObject {
    public String Data;
    
    public MessageData( final String data ) { Data = data; }
}

public class ResultData implements Serializable {
    public ResultTypes Type;
    public DataObject Data;
    
    public ResultData( final ResultTypes type, final DataObject data )
    {
        Type = type;
        Data = data;
    }
}

public enum ResultTypes implements Serializable {
    ContactId,
    UpdatedContacts,
    CreatedConference,
    AddedToNewConference,
    AddedToConference,
    AddedConference,
    RemovedFromConference,
    DeletedConference,
    Message,
    UpdatedFiles,
    UploadedFile,
    DownloadedFile,
    RemovedFile,
    None
}
