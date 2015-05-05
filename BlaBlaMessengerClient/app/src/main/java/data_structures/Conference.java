import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by VladVin on 26.04.2015.
 */
public class Conference extends DataObject{
    public String Name;
    public UUID ConfID;
    public ArrayList<UUID> ContactsIDs;
}
