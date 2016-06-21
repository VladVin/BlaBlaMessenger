package data_storage;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;

import coreutilities.Contact;
import coreutilities.ContactMessagePair;
import coreutilities.FileIdNamePair;
import coreutilities.ResultData;
import coreutilities.ResultTypes;


/**
 * Created by VladVin on 11.05.2015.
 */
public class DataStorage {
    private UUID contactId;
    private HashMap<UUID, Contact> contacts;
    private ArrayList<ContactMessagePair> messages;
    private ArrayList<FileIdNamePair> files;

    private ArrayList<ResultTypes> whatUpdated;

    public DataStorage() {
        contactId = null;
        contacts = null;
        messages = new ArrayList<ContactMessagePair>();
        // FIXME: Remember that whatUpdated won't remove notification for unused data field
        whatUpdated = new ArrayList<ResultTypes>();
    }

    public void pushData(ResultData resData) {
        // TODO: Throw the exception when unknown result type
        switch (resData.type)
        {
            case ContactID:
                contactId = (UUID)resData.data;
                addWhatUpdate(ResultTypes.ContactID);
                Log.d("DataStorage", "UUID received");
                break;
            case UpdatedContacts:
                contacts = (HashMap<UUID, Contact>)resData.data;
                addWhatUpdate(ResultTypes.UpdatedContacts);
                Log.d("DataStorage", "Contacts received");
                break;
            case MessageToContact:
                ContactMessagePair mes = (ContactMessagePair)resData.data;
                messages.add(new ContactMessagePair(mes.contact, mes.text));
                addWhatUpdate(ResultTypes.MessageToContact);
                Log.d("DataStorage", "Message received");
                break;
//            case UpdatedFiles:
//                files = ((FileIdNamePairs)resData.data).Pairs;
//                addWhatUpdate(ResultTypes.UpdatedFiles);
//                Log.d("DataStorage", "Files list received");
//                break;
        }
    }

    public HashMap<UUID, Contact> getContacts() {
        whatUpdated.remove(ResultTypes.UpdatedContacts);
        return contacts;
    }

    public UUID getUUID() {
        whatUpdated.remove(ResultTypes.ContactID);
        return contactId;
    }

    public ArrayList<ContactMessagePair> getMessages() {
        whatUpdated.remove(ResultTypes.MessageToContact);
        return messages;
    }

    public ArrayList<FileIdNamePair> getFiles() {
        whatUpdated.remove(ResultTypes.UpdatedFiles);
        return files;
    }

    public ResultTypes whatUpdated() {
        if (whatUpdated.size() != 0)
            return whatUpdated.get(whatUpdated.size() - 1);
        else return ResultTypes.None;
    }

    private void addWhatUpdate(ResultTypes whatUp) {
        if (!whatUpdated.contains(whatUp))
            whatUpdated.add(whatUp);
    }
}
