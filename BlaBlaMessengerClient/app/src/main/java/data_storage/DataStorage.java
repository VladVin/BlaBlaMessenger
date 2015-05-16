package data_storage;

import android.util.Log;

import data_structures.ContactId;
import data_structures.Contacts;
import data_structures.ResultData;
import data_structures.ResultTypes;

/**
 * Created by VladVin on 11.05.2015.
 */
public class DataStorage {
    public ContactId contactId;
    public Contacts contacts;

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
                Log.d("DataStorage", "Contacts received");
                break;

        }
    }
}
