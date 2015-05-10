package blablamessenger;

import data_structures.CommandData;
import data_structures.Commands;
import data_structures.DataObject;

public class Command { 
    public enum Sources {
        Server,
        Client
    }
    
    public Sources Source;
    public Commands Command;
    public DataObject Data;
    
    public Command( final Sources src, 
        final Commands command, final DataObject data ) 
    {
        Source = src;
        Command = command;
        Data = data;
    }    
    public Command( final Sources source, final CommandData commandData ) {
        Source = source;
        Command = commandData.Command;
        Data = commandData.Data;
    }
    public Command( final Sources source, final Command command ) {
        Source = source;
        Command = command.Command;
        Data = command.Data;
    }
}
