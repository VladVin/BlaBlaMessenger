package coreutilities;

import java.io.Serializable;

public class CommandData implements Serializable
{
    public Commands command;
    public Object   data;
    
    public
    CommandData(
        Commands command,
        Object   data
    )
    {
        this.command = command;
        this.data    = data;
    }
}
