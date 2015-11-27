package coreutilities;

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
    MessageToConference,
    MessageToContact,
    UpdatedFiles,
    UploadedFile,
    DownloadedFile,
    RemovedFile
}
