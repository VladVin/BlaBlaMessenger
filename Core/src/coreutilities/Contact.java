package coreutilities;

import java.io.Serializable;

public class Contact implements Serializable
{
    public String name;
    
    public
    Contact(
        String name
    )
    {
        this.name = name;
    }
}
