package coreutilities;

import java.io.Serializable;

public class ResultData implements Serializable
{
    public ResultTypes type;
    public Object      data;
    
    public
    ResultData(
        ResultTypes type,
        Object      data
    )
    {
        this.type = type;
        this.data = data;
    }
}
