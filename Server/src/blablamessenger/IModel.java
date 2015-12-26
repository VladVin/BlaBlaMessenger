package blablamessenger;

import coreutilities.*;

import java.util.ArrayList;
import java.util.UUID;

public interface IModel
{
    ResultData
    registerContact(
        Task     task,
        IController controller
    );

    ResultData
    disconnect(
        UUID myID,
        ArrayList< UUID > myConferences
    );

    ResultData
    refreshContacts();

    ResultData
    createConference(
        Task              task,
        UUID              myID,
        ArrayList< UUID > myConferences
    );

    ResultData
    addToConference(
        Task              task,
        UUID              myID,
        ArrayList< UUID > myConferences
    );

    ResultData
    removeFromConference(
        Task              task,
        UUID              myID,
        ArrayList< UUID > myConferences
    );

    ResultData
    deleteConference(
        Task              task,
        UUID              myID,
        ArrayList< UUID > myConferences
    );

    ResultData
    sendMessageToConference(
        Task              task,
        UUID              myID,
        ArrayList< UUID > myConferences
    );

    ResultData
    sendMessageToContact(
        Task task,
        UUID myID
    );

    ResultData
    refreshStorage();

    ResultData
    uploadFile(
        Task task
    );

    ResultData
    downloadFile(
        Task task
    );

    ResultData
    removeFile(
        Task task
    );
}
