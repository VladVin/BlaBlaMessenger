package data_structures.CommandData;

import data_structures.DataObject;
import java.util.UUID;

public class CommandId extends DataObject {
    public UUID Id;
    
    public CommandId( final UUID id ) { Id = id; }
}
