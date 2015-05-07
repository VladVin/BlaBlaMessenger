package data_processor;

import android.bluetooth.BluetoothSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

import data_structures.*;

/**
 * Created by VladVin on 05.05.2015.
 */
public class DataSender extends Thread {
    private final Socket socket;
    private ObjectOutputStream objOutStream = null;
    private ObjectInputStream objInStream = null;

    public DataSender(Socket btSocket) throws DataSenderException{
        socket = btSocket;
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

    public void sendData(CommandData comData) throws DataSenderException {
        try
        {
            objOutStream.writeObject(comData);
        }
        catch(IOException io)
        {
            throw new DataSenderException("Cannot send data");
        }
    }

    public void sendMessage(String message) throws DataSenderException{
        try
        {
            objOutStream.writeObject(message);
            objOutStream.flush();
        }
        catch(IOException io)
        {
            throw new DataSenderException("Cannot send data");
        }
    }

    public UUID receiveMessage() throws DataSenderException{
        UUID result = null;

        if (objInStream == null)
        {
            try
            {
                InputStream inStream = socket.getInputStream();
                objInStream = new ObjectInputStream(inStream);
            }
            catch(IOException e)
            {
                throw new DataSenderException("Cannot get input stream");
            }
        }

        try
        {
            result = (UUID)objInStream.readObject();
        }
        catch(Exception e)
        {
            throw new DataSenderException("Cannot receive data");
        }

        return result;
    }

    public void cancel(){
        try
        {
            socket.close();
        }
        catch (IOException ioEx){}
    }

    public Object OutStreamStatus(){
        return objOutStream;
    }
}
