package data_structures;

import java.io.Serializable;

public class ResultData implements Serializable
{
    public ResultTypes type;
    public DataObject  data;
    
    public
    ResultData(
        ResultTypes type,
        DataObject  data
    )
    {
        this.type = type;
        this.data = data;
    }
}
