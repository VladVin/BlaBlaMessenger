package data_structures;

import java.util.ArrayList;

public class UuidFileNamePairsList extends DataObject {
    ArrayList< UuidFileNamePair > list;
    
    public UuidFileNamePairsList( ArrayList< UuidFileNamePair > pairs )
    { list = pairs; }
}
