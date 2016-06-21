package client_controller;

import java.util.List;
import java.util.UUID;

import coreutilities.ResultData;
import user_model.IUserModel;

/**
 * Created by VladVin on 18.12.2015.
 */
public class BaseClientController implements IClientController {
    private final IUserModel userModel;

    public BaseClientController(IUserModel userModel) {
        this.userModel = userModel;
    }

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
    public void pushResult(ResultData resultData) {

    }
}
