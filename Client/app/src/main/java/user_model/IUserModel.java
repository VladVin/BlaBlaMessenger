package user_model;

import java.util.List;
import java.util.UUID;

import coreutilities.Conference;
import coreutilities.ConferenceData;
import coreutilities.Contact;
import coreutilities.File;
import coreutilities.FileData;

/**
 * Created by VladVin on 18.12.2015.
 */
public interface IUserModel {
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
    void updateContactList(List<Contact> contacts);
    void pushMessageFromContact(UUID contactId, String message);
    void pushMessageToConference(UUID senderId, UUID conferenceId, String message);
    void addUserToConference(UUID conferenceId, ConferenceData conferenceData);
    void deleteUserFromConference(UUID conferenceId);
    void deleteUserConference(UUID conferenceId);
    void refreshStorage(List<File> fileList);
    void saveFile(UUID fileId, FileData fileData);
    void removeUserFile(UUID id);
}
