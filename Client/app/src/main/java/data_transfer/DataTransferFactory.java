package data_transfer;

import client_controller.IClientController;

/**
 * Created by VladVin on 18.12.2015.
 */
public class DataTransferFactory {
    public enum DataTransferType { BASE_DATA_TRANSFER }

    public IDataTransfer createDataTransfer(DataTransferType type, IClientController clientController) {
        switch (type) {
            case BASE_DATA_TRANSFER:
                return new BaseDataTransfer(clientController);
            default:
                return null;
        }
    }
}
