package cloud;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import data_storage.DataStorage;
import data_structures.CommandData;
import data_structures.Commands;
import data_structures.ResultData;

/**
 * Created by VladVin on 11.05.2015.
 */
public class Cloud extends Thread {
    private static final String ipAddress = "192.168.0.47";
    private static final int port = 4444;
    private Socket socket = null;
    private final ConcurrentLinkedQueue<ResultData> resDataQueue;
    private DataSender dataSender;
    private DataReceiver dataReceiver;
    private final DataStorage storage;
    private boolean running = false;

    public Cloud() throws CloudException {
        this.resDataQueue = new ConcurrentLinkedQueue<ResultData>();
        this.storage = new DataStorage();

        connect();
    }

    public void run() {
        running = true;

        while (running && (dataSender == null || dataReceiver == null));
        if (dataSender != null && dataReceiver != null) {
            dataSender.start();
            dataReceiver.start();
        }
        else
        {
            // TODO: Do something
        }

        while(running) {
            try {
                synchronized (resDataQueue) {
                    resDataQueue.wait();
                }
            }
            catch (InterruptedException e) {
                // TODO: Handle the exception
                break;
            }
            // TODO: Fill the storage
            synchronized (storage) {
                for (int i = 0; i < resDataQueue.size(); i++) {
                    storage.pushData(resDataQueue.poll());
                }
            }
        }
    }

    public void connect() throws CloudException {
        if (socket == null) {
            // Create the connection
            try {
                socket = new Socket(ipAddress, port);
            }
            catch (IOException e) {
                throw new CloudException("Cannot connect to server");
            }

            // Create Data Sender
            try {
                dataSender = new DataSender(socket);
            } catch (DataSenderException e) {
                throw new CloudException("Cannot create Data Sender");
            }

            // Create Data Receiver
            try {
                dataReceiver = new DataReceiver(socket, resDataQueue);
            }
            catch (DataReceiverException e) {
                throw new CloudException("Cannot create data receiver");
            }
        }
    }

    public void requestData(final CommandData comData){
        dataSender.sendData(comData);
    }

    public DataStorage getStorage() {
        return storage;
    }

    public void cancel() {
        // TODO: Implement this method correctly
        running = false;
    }
}
