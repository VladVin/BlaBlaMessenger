package coreutilities;

import java.util.UUID;

public class Contact
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
