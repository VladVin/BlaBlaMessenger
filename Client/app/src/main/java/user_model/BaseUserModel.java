package user_model;

import java.util.List;
import java.util.UUID;

import coreutilities.ConferenceData;
import coreutilities.Contact;
import coreutilities.File;
import coreutilities.FileData;

/**
 * Created by VladVin on 18.12.2015.
 */
public class BaseUserModel implements IUserModel {

    @Override
    public void register(String name) {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void refreshContacts() {

    }

    @Override
    public void createConference(String name, List<UUID> members) {

    }

    @Override
    public void addToConference(UUID contactId, UUID conferenceId) {

    }

    @Override
    public void removeFromConference(UUID contactId, UUID conferenceId) {

    }

    @Override
    public void deleteConference(UUID conferenceId) {

    }

    @Override
    public void sendMessageToContact(String message, UUID toUser) {

    }

    @Override
    public void sendMessageToConference(String message, UUID conferenceId) {

    }

    @Override
    public void refreshStorage() {

    }

    @Override
    public void uploadFile(String name, byte[] data) {

    }

    @Override
    public void downloadFile(UUID id) {

    }

    @Override
    public void removeFile(UUID id) {

    }

    @Override
    public void updateContactList(List<Contact> contacts) {

    }

    @Override
    public void pushMessageFromContact(UUID contactId, String message) {

    }

    @Override
    public void pushMessageToConference(UUID senderId, UUID conferenceId, String message) {

    }

    @Override
    public void addUserToConference(UUID conferenceId, ConferenceData conferenceData) {

    }

    @Override
    public void deleteUserFromConference(UUID conferenceId) {

    }

    @Override
    public void deleteUserConference(UUID conferenceId) {

    }

    @Override
    public void refreshStorage(List<File> fileList) {

    }

    @Override
    public void saveFile(UUID fileId, FileData fileData) {

    }

    @Override
    public void removeUserFile(UUID id) {

    }
}
