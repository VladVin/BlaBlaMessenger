package blablamessenger;

import data_structures.CommandData;
import data_structures.Commands;

public class Task
{
    public enum Sources
    {
        Server,
        Client
    }
    
    public Sources     source;
    public CommandData operation;

    public
    Task(
        Sources     source,
        CommandData operation
    )
    {
        this.source    = source;
        this.operation = operation;
    }

    public
    Task(
        Sources  source,
        Commands operation,
        Object   data
    )
    {
        this.source    = source;
        this.operation = new CommandData( operation, data );
    }

    public Commands
    getCommand()
    {
        if ( operation == null ) {
            return null;
        }

        return operation.command;
    }

}
