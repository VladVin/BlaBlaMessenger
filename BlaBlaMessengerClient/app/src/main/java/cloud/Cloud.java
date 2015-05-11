package cloud;

import java.util.concurrent.ConcurrentLinkedQueue;

import data_storage.DataStorage;
import data_structures.ResultData;

/**
 * Created by VladVin on 11.05.2015.
 */
public class Cloud extends Thread {
    private final ConcurrentLinkedQueue<ResultData> resDataQueue;
    private final DataReceiver dataReceiver;
    private final DataStorage storage;

    public Cloud(DataStorage dStorage) throws CloudException {
        resDataQueue = new ConcurrentLinkedQueue<ResultData>();

        try
        {
            dataReceiver = new DataReceiver(resDataQueue);
        }
        catch (DataReceiverException e)
        {
            throw new CloudException("Cannot create data receiver");
        }

        storage = dStorage;
    }

    public void runDataListener() {
        start();
    }

    public void run() {
        dataReceiver.receiveData();

        while(true)
        {
            try
            {
                resDataQueue.wait();
            }
            catch (InterruptedException e)
            {
                // TODO: Handle the exception
                break;
            }
            // TODO: Fill the storage
        }
    }
}
