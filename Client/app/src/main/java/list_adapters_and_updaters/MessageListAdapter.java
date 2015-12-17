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
import java.util.UUID;


import coreutilities.ContactMessagePair;
import java_laba.blablamessengerclient.ConversationContactsPair;
import java_laba.blablamessengerclient.R;


public class MessageListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<ContactMessagePair> mMessages;
    private ConversationContactsPair mDialog;

    public MessageListAdapter(Context context, ArrayList<ContactMessagePair> messages, ConversationContactsPair dialog) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMessages = messages;
        mDialog = dialog;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContactMessagePair messagePair = (ContactMessagePair) getItem(position);
        TextView contactInfo = null;
        // TODO: Show various messages with various color
        if (convertView == null)
            if (messagePair.contact.id == mDialog.me) {
                convertView = mLayoutInflater.inflate(R.layout.message_list_item, parent, false);
                contactInfo = (TextView) convertView.findViewById(R.id.messageLine);
            }
            else {
                convertView = mLayoutInflater.inflate(R.layout.interlocutor_message_item, parent, false);
                contactInfo = (TextView) convertView.findViewById(R.id.messageLineInterlocutor);
            }
        if (contactInfo != null)
            if (messagePair != null)
                contactInfo.setText(messagePair.text);
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

