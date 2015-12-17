package blablamessenger;

import coreutilities.Contact;
import coreutilities.ContactData;
import coreutilities.File;
import coreutilities.FileData;

import java.util.ArrayList;
import java.util.UUID;

public interface IBase
{
    ContactData
    addContact(
        UUID id,
        ContactData contact
    );

    ContactData
    removeContact(
        UUID contactID
    );

    ArrayList< Contact >
    getContacts();

    ConcurrentConferenceData
    addConference(
        UUID                     id,
        ConcurrentConferenceData conference
    );

    ConcurrentConferenceData
    removeConference(
        UUID conferenceID
    );

    ConcurrentConferenceData
    getConference(
        UUID conferenceID
    );

    IController
    addController(
        UUID        id,
        IController controller
    );

    IController
    removeController(
        UUID controllerID
    );

    IController
    getController(
        UUID controllerID
    );

    void
    upload(
        UUID     id,
        FileData fileData
    );

    FileData
    download(
        UUID id
    );

    FileData
    removeFile(
        UUID id
    );

    ArrayList<File>
    getFiles();

    boolean
    isRunning();

    void
    close();
}
