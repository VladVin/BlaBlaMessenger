package blablamessenger;

import data_structures.CommandData;
import data_structures.CommandId;
import data_structures.Commands;
import data_structures.DataObject;

public class Command { 
    public enum Sources {
        Server,
        Client
    }
    
    public Sources Source;
    public Commands Command;
    public CommandId Id;
    public DataObject Data;
    
    public Command( final Sources src, 
            final Commands command, final CommandId id, final DataObject data) 
    {
        Source = src;
        Command = command;
        Id = id;
        Data = data;
    }    
    public Command( final Sources source, final CommandData commandData ) {
        Source = source;
        Command = commandData.Command;
        Id = commandData.Id;
        Data = commandData.Data;
    }
    public Command( final Sources source, final Command command ) {
        Source = source;
        Command = command.Command;
        Id = command.Id;
        Data = command.Data;
    }
}
