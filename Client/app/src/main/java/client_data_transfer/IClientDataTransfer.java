package client_data_transfer;

import client_controller.IClientController;
import coreutilities.CommandData;

/**
 * Created by VladVin on 18.12.2015.
 */
public interface IClientDataTransfer {
    void sendCommand(CommandData commandData);
}
