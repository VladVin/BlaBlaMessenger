package client_controller;

import user_model.IUserModel;

/**
 * Created by VladVin on 18.12.2015.
 */
public class ClientControllerFactory {
    public enum ClientControllerType { BASE_CLIENT_CONTROLLER }

    IClientController createClientController(ClientControllerType type, IUserModel userModel) {
        switch (type) {
            case BASE_CLIENT_CONTROLLER:
                return new BaseClientController(userModel);
            default:
                return null;
        }
    }
}
