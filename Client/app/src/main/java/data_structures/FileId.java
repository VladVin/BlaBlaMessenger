package data_structures;

import java.util.UUID;

public class FileId extends DataObject {
    public UUID Id;

    public FileId() { Id = UUID.randomUUID(); }
}
