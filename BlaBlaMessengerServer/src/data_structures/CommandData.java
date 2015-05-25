package data_structures;

import java.io.Serializable;

public class CommandData implements Serializable {
    public Commands Command;
    public DataObject Data;
    
    public CommandData( final Commands command, final DataObject data )
    {
        Command = command;
        Data = data;
    }
}
