package data_structures;

import java.util.ArrayList;

public class Contacts extends DataObject {
    ArrayList< Contact > list;
    
    public Contacts( ArrayList< Contact > contacts )
    { list = contacts; }
}
