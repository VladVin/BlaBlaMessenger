package data_structures;

import java.io.Serializable;

public class ResultData implements Serializable {
    public ResultTypes Type;
    public DataObject Data;
    
    public ResultData( final ResultTypes type, final DataObject data )
    {
        Type = type;
        Data = data;
    }
}
