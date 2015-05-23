package data_structures;

import java.io.Serializable;

public enum Commands implements Serializable {
    RegisterContact,
    Disconnect,
    RefreshContacts,
    CreateConference,
    AddToConference,
    RemoveFromConference,
    DeleteConference,
    SendMessageToContact,
    SendMessageToConference,
    RefreshStorage,
    UploadFile,
    DownloadFile,
    RemoveFile
}
