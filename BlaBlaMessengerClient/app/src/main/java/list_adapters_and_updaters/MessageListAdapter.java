package list_adapters_and_updaters;

/**
 * Created by VladVin on 18.05.2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import data_structures.Contact;
import data_structures.ContactMessagePair;
import data_structures.MessageData;
import java_laba.blablamessengerclient.R;


public class MessageListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<ContactMessagePair> mMessages;

    public MessageListAdapter(Context context, ArrayList<ContactMessagePair> messages) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMessages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContactMessagePair messagePair = (ContactMessagePair) getItem(position);
        // TODO: Show various messages with various color
        if (convertView == null)
            convertView = mLayoutInflater.inflate(R.layout.contact_list_item, parent, false);
        TextView contactInfo = (TextView)convertView.findViewById(R.id.contactLine);
        if (messagePair != null)
            contactInfo.setText(messagePair.Message.Data);
        else contactInfo.setText("There is no messages");

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        if (mMessages != null)
            return mMessages.get(position);
        else return null;
    }

    @Override
    public int getCount() {
        if (mMessages != null)
            return mMessages.size();
        else return 0;
    }

    @Override
    public long getItemId(int position) {
        if (mMessages != null)
            return position;
        else return -1;
    }

    public void updateContacts(ArrayList<ContactMessagePair> uMessages) {
        mMessages = uMessages;
        notifyDataSetChanged();
    }
}

