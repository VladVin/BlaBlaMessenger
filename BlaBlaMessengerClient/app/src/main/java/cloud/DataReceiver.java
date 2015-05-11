package cloud;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import data_structures.ResultData;

/**
 * Created by VladVin on 11.05.2015.
 */
public class DataReceiver extends Thread {
    private final Socket socket;
    private ObjectInputStream objInStream = null;
    private ReceiverStatus status = ReceiverStatus.NotBusy;
    private final ConcurrentLinkedQueue<ResultData> resDataQueue;

    public DataReceiver(ConcurrentLinkedQueue<ResultData> queue) throws DataReceiverException {
        try
        {
            socket = new Socket(ConnectionTrash.IpAddress, ConnectionTrash.Port);
        }
        catch (IOException e)
        {
            throw new DataReceiverException("Cannot connect to server");
        }
        resDataQueue = queue;
    }

    public void receiveData()
    {
        start();
    }

    public void run() {
        status = ReceiverStatus.DataWaiting;

        ResultData result = null;
        if (objInStream == null)
        {
            try
            {
                InputStream inStream = socket.getInputStream();
                objInStream = new ObjectInputStream(inStream);
            }
            catch(IOException e)
            {
                status = ReceiverStatus.Error;
            }
        }

        while (true)
        {
            try
            {
                result = (ResultData)objInStream.readObject();
            }
            catch(Exception e)
            {
                status = ReceiverStatus.Error;
            }
            resDataQueue.add(result);
            resDataQueue.notify();

        }

        // TODO: Add interrupting the thread
    }
}
