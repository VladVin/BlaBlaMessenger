package data_storage;

import android.util.Log;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import data_structures.ContactId;
import data_structures.Contacts;
import data_structures.ResultData;
import data_structures.ResultTypes;

/**
 * Created by VladVin on 11.05.2015.
 */
public class DataStorage {
    private ContactId contactId;
    private Contacts contacts;

    private ArrayList<ResultTypes> whatUpdated;

    public DataStorage() {
        contactId = null;
        contacts = null;
        // FIXME: Remember that whatUpdated won't remove notification for unused data field
        whatUpdated = new ArrayList<ResultTypes>();
    }

    public void pushData(ResultData resData) {
        // TODO Throw the exception when unknown result type
        switch (resData.Type)
        {
            case ContactId:
                contactId = (ContactId)resData.Data;
                Log.d("DataStorage", "ContactId received");
                break;
            case UpdatedContacts:
                contacts = (Contacts)resData.Data;
                addWhatUpdate(ResultTypes.UpdatedContacts);
                Log.d("DataStorage", "Contacts received");
                break;
        }
    }

    public Contacts getContacts() {
        whatUpdated.remove(ResultTypes.UpdatedContacts);
        return contacts;
    }

    public ResultTypes whatUpdated() {
        if (whatUpdated.size() != 0)
            return whatUpdated.get(0);
        else return ResultTypes.None;
    }

    private void addWhatUpdate(ResultTypes whatUp) {
        if (!whatUpdated.contains(whatUp))
            whatUpdated.add(whatUp);
    }
}
