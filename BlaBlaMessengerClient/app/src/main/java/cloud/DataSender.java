package cloud;

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

    public DataSender() throws DataSenderException{
        try
        {
            socket = new Socket(ConnectionTrash.IpAddress, ConnectionTrash.Port);
        }
        catch (IOException e)
        {
            throw new DataSenderException("Cannot connect to server");
        }

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
        try
        {
            objOutStream.writeObject(comData);
            objOutStream.flush();
        }
        catch(IOException io)
        {
        }
    }

    public void sendData(CommandData cData) throws DataSenderException {
        comData = cData;
        start();
    }

    public void cancel(){
        try
        {
            socket.close();
        }
        catch (IOException ioEx){}
    }
}
