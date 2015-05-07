package data_structures.CommandData;

import data_structures.Commands;
import data_structures.DataObject;
import java.io.Serializable;

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
}
