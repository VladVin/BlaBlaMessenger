package data_structures;

import java.io.Serializable;

public enum ResultTypes implements Serializable {
    ContactId,
    UpdatedContacts,
    CreatedConference,
    AddedToNewConference,
    AddedToConference,
    AddedConference,
    RemovedFromConference,
    DeletedConference,
    Message,
    UpdatedFiles,
    UploadedFile,
    DownloadedFile,
    RemovedFile,
    None
}
