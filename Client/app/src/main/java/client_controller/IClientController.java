package client_controller;

import java.util.List;
import java.util.UUID;

import coreutilities.ResultData;

/**
 * Created by VladVin on 18.12.2015.
 */
public interface IClientController {
    // From client to server
    void register(String name);
    void disconnect();
    void refreshContacts();
    void createConference(String name, List<UUID> members);
    void addToConference(UUID contactId, UUID conferenceId);
    void removeFromConference(UUID contactId, UUID conferenceId);
    void deleteConference(UUID conferenceId);
    void sendMessageToContact(String message, UUID toUser);
    void sendMessageToConference(String message, UUID conferenceId);
    void refreshStorage();
    void uploadFile(String name, byte[] data);
    void downloadFile(UUID id);
    void removeFile(UUID id);

    // From server to client
    void pushResult(ResultData resultData);
}
