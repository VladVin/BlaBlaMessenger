package coreutilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Conference implements Serializable
{
    public String            name;
    public ArrayList< UUID > members;

    public
    Conference()
    {
        name    = "";
        members = new ArrayList<>();
    }
}
