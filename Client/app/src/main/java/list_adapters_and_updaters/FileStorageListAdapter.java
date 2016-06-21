package list_adapters_and_updaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


import coreutilities.FileIdNamePair;
import java_laba.blablamessengerclient.ConversationContactsPair;
import java_laba.blablamessengerclient.R;

/**
 * Created by VladVin on 23.05.2015.
 */
public class FileStorageListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<FileIdNamePair> mFiles;
    private ConversationContactsPair mDialog;

    public FileStorageListAdapter(Context context, ArrayList<FileIdNamePair> files) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFiles = files;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FileIdNamePair filePair = (FileIdNamePair) getItem(position);
        TextView fileInfo = null;
        // TODO: Show various messages with various color
        if (convertView == null)
            convertView = mLayoutInflater.inflate(R.layout.contact_list_item, parent, false);
        fileInfo = (TextView) convertView.findViewById(R.id.contactLine);
        if (filePair != null)
            fileInfo.setText(filePair.Name.Name);
        else fileInfo.setText("There is no files");

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        if (mFiles != null)
            return mFiles.get(position);
        else return null;
    }

    @Override
    public int getCount() {
        if (mFiles != null)
            return mFiles.size();
        else return 0;
    }

    @Override
    public long getItemId(int position) {
        if (mFiles != null)
            return position;
        else return -1;
    }

    public void updateFiles(ArrayList<FileIdNamePair> uFiles) {
        mFiles = uFiles;
        notifyDataSetChanged();
    }
}
