package data_storage;

import android.util.Log;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import data_structures.ContactId;
import data_structures.ContactMessagePair;
import data_structures.Contacts;
import data_structures.FileIdNamePair;
import data_structures.FileIdNamePairs;
import data_structures.ResultData;
import data_structures.ResultTypes;

/**
 * Created by VladVin on 11.05.2015.
 */
public class DataStorage {
    private ContactId contactId;
    private Contacts contacts;
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
        switch (resData.Type)
        {
            case ContactId:
                contactId = (ContactId)resData.Data;
                addWhatUpdate(ResultTypes.ContactId);
                Log.d("DataStorage", "ContactId received");
                break;
            case UpdatedContacts:
                contacts = (Contacts)resData.Data;
                addWhatUpdate(ResultTypes.UpdatedContacts);
                Log.d("DataStorage", "Contacts received");
                break;
            case Message:
                ContactMessagePair mes = (ContactMessagePair)resData.Data;
                messages.add(new ContactMessagePair(mes.Contact, mes.Message));
                addWhatUpdate(ResultTypes.Message);
                Log.d("DataStorage", "Message received");
                break;
            case UpdatedFiles:
                files = ((FileIdNamePairs)resData.Data).Pairs;
                addWhatUpdate(ResultTypes.UpdatedFiles);
                Log.d("DataStorage", "Files list received");
                break;
        }
    }

    public Contacts getContacts() {
        whatUpdated.remove(ResultTypes.UpdatedContacts);
        return contacts;
    }

    public ContactId getContactId() {
        whatUpdated.remove(ResultTypes.ContactId);
        return contactId;
    }

    public ArrayList<ContactMessagePair> getMessages() {
        whatUpdated.remove(ResultTypes.Message);
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
