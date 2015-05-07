package data_structures;

import java.util.UUID;

public class Command { 
    public enum Sources {
        Server,
        Client
    }
    
    Sources Source;
    Commands Command;
    UUID Uuid;
    DataObject Data;

    public Command( Sources source, CommandData commandData ) {
        Source = source;
        Command = commandData.Command;
        Uuid = commandData.Uuid;
        Data = commandData.Data;
    }
}
