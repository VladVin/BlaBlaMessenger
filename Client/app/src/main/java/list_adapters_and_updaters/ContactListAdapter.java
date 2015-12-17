package list_adapters_and_updaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


import coreutilities.Contact;
import java_laba.blablamessengerclient.R;

/**
 * Created by VladVin on 17.05.2015.
 */
public class ContactListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Contact> mContacts;

    public ContactListAdapter(Context context, ArrayList<Contact> contacts) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContacts = contacts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contact contact = (Contact) getItem(position);
        if (convertView == null)
            convertView = mLayoutInflater.inflate(R.layout.contact_list_item, parent, false);
        TextView contactInfo = (TextView)convertView.findViewById(R.id.contactLine);
        if (contact != null)
            contactInfo.setText(contact.Name.Name);
        else contactInfo.setText("There is no contacts");

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        if (mContacts != null)
            return mContacts.get(position);
        else return null;
    }

    @Override
    public int getCount() {
        if (mContacts != null)
            return mContacts.size();
        else return 0;
    }

    @Override
    public long getItemId(int position) {
        if (mContacts != null)
            return position;
        else return -1;
    }

    public void updateContacts(ArrayList<Contact> uContacts) {
        mContacts = uContacts;
        notifyDataSetChanged();
    }
}
