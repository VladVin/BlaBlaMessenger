package data_structures;

import java.io.Serializable;
import java.util.UUID;

public class CommandData implements Serializable {
    public Commands Command;
    public CommandId Id;
    public DataObject Data;
    
    public CommandData( final Commands command, 
            final CommandId id, 
            final DataObject data )
    {
        Command = command;
        Id = id;
        Data = data;
    }
    
    public class CommandId extends DataObject {
        public UUID Id;
    
        public CommandId( final UUID id ) { Id = id; }
    }
}
