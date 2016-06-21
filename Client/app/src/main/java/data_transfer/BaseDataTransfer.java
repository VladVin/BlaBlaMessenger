package data_transfer;

import client_controller.BaseClientController;
import client_controller.IClientController;
import coreutilities.CommandData;

/**
 * Created by VladVin on 18.12.2015.
 */
public class BaseDataTransfer implements IDataTransfer {
    private final IClientController clientController;

    public BaseDataTransfer(IClientController clientController) {
        this.clientController = clientController;
    }

    @Override
    public void sendCommand(CommandData commandData) {

    }
}
