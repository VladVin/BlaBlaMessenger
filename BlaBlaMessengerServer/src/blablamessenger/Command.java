package blablamessenger;

import data_structures.CommandData;
import data_structures.Commands;
import data_structures.DataObject;
import java.util.UUID;

public class Command { 
    public enum Sources {
        Server,
        Client
    }
    
    public Sources Source;
    public Commands Command;
    public UUID Uuid;
    public DataObject Data;
    
    public Command( Sources src, 
            Commands command, UUID id, DataObject data) 
    {
        Source = src;
        Command = command;
        Uuid = id;
        Data = data;
    }    
    public Command( Sources source, CommandData commandData ) {
        Source = source;
        Command = commandData.Command;
        Uuid = commandData.Uuid;
        Data = commandData.Data;
    }
}
