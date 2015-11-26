package data_structures;

import java.io.Serializable;
import java.util.UUID;

public class ConfMessagePair implements Serializable
{
    public UUID   conference;
    public String text;

    public
    ConfMessagePair(
        UUID   conference,
        String text
    )
    {
        this.conference = conference;
        this.text       = text;
    }
}
