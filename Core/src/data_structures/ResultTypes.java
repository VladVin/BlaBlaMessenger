package data_structures;

import java.io.Serializable;

public enum ResultTypes implements Serializable
{
    ContactID,
    UpdatedContacts,
    CreatedConference,
    AddedToNewConference,
    AddedToConference,
    AddedConference,
    RemovedFromConference,
    DeletedConference,
    MessageToContact,
    MessageToConference,
    UpdatedFiles,
    UploadedFile,
    DownloadedFile,
    RemovedFile
}
