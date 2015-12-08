package coreutilities;

import java.io.Serializable;
import java.util.UUID;

public class Contact implements Serializable
{
    public UUID        id;
    public ContactData data;

    public
    Contact(
        UUID        id,
        ContactData data
    )
    {
        this.id   = id;
        this.data = data;
    }
}
