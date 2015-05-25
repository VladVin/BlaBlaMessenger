package cloud;

import android.util.Log;

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
    private final ConcurrentLinkedQueue<ResultData> resDataQueue;

    public DataReceiver(Socket socket, ConcurrentLinkedQueue<ResultData> queue) throws DataReceiverException {
        this.socket = socket;
        resDataQueue = queue;

        try
        {
            InputStream inStream = socket.getInputStream();
            objInStream = new ObjectInputStream(inStream);
        }
        catch(IOException e)
        {
            throw new DataReceiverException("Cannot get input stream");
        }
    }

    public void run() {
        while (true)
        {
            ResultData result = null;
            try
            {
                Log.d("DataReceiver", "Waiting data...");
                result = (ResultData)objInStream.readObject();
                Log.d("DataReceiver", "Data received");
            }
            catch(Exception e)
            {
                Log.d("DataReceiver", "Read object exception: " + e.getMessage());
                // TODO: Handle the exception
            }
            if (result != null) {
                // This queue is thread-safely
                resDataQueue.add(result);
                synchronized (resDataQueue)
                {
                    resDataQueue.notifyAll();
                }
            }
        }

        // TODO: Add interrupting the thread
    }
}
