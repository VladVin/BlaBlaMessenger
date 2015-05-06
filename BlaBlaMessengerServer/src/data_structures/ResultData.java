package data_structures;

import java.io.Serializable;

public class ResultData implements Serializable {
    public ResultTypes ResultType;
    public DataObject Data;

    public ResultData( ResultTypes resultType, Contacts contacts ) {
        ResultType = resultType;
        Data = contacts;
    }
}
