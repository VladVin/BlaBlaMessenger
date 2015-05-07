package data_structures;

import data_structures.FileIdNamePair.FileIdNamePair;
import java.util.ArrayList;

public class FileIdNamePairs extends DataObject {
    public ArrayList< FileIdNamePair > Pairs;
    
    public FileIdNamePairs( ArrayList< FileIdNamePair > pairs )
    { Pairs = pairs; }
}
