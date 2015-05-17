package cloud;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import data_structures.*;

/**
 * Created by VladVin on 05.05.2015.
 */
public class DataSender extends Thread {
    private final Socket socket;
    private ObjectOutputStream objOutStream = null;
    private CommandData comData = null;
    private final Object dataSendingNotifier;
    private boolean running = false;

    public DataSender(Socket socket) throws DataSenderException{
        this.socket = socket;
        this.dataSendingNotifier = new Object();

        try
        {
            OutputStream outStream = socket.getOutputStream();
            objOutStream = new ObjectOutputStream(outStream);
        }
        catch (IOException e)
        {
            throw new DataSenderException("Cannot get output stream");
        }
    }

    public void run() {
        running = true;

        while (running) {
            synchronized(dataSendingNotifier) {
                try {
                    dataSendingNotifier.wait();
                }
                catch (InterruptedException e) {
                    // TODO: Handle the exception
                }

                try
                {
                    objOutStream.writeObject(comData);
                    objOutStream.flush();
                    Log.d("DataSender", "Command sent");
                }
                catch(IOException io)
                {
                    // TODO: Handle the exception
                }
            }
        }
    }

    public void sendData(CommandData cData) throws DataSenderException {
        synchronized (dataSendingNotifier) {
            comData = cData;
            dataSendingNotifier.notifyAll();
        }
    }

    public void cancel(){
        // TODO: Disconnect
        running = false;
        try
        {
            socket.close();
        }
        catch (IOException ioEx){}
    }
}
