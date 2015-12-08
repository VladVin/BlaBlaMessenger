package coreutilities;

import java.io.Serializable;

public class ContactData implements Serializable
{
    public String name;
    
    public ContactData(
        String name
    )
    {
        this.name = name;
    }
}
