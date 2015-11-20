package data_structures;

import java.io.Serializable;

public class CommandData implements Serializable
{
    public Commands   command;
    public DataObject data;
    
    public
    CommandData(
        Commands command,
        DataObject data
    )
    {
        this.command = command;
        this.data    = data;
    }
}
