package data_structures;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by VladVin on 25.04.2015.
 */
public class CommandData implements Serializable {
    public Commands Command;
    public UUID Uuid;
    public DataObject Data;
}
