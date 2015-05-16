package java_laba.blablamessengerclient;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import cloud.Cloud;
import cloud.CloudException;
import cloud.DataSender;
import cloud.DataSenderException;
import data_storage.DataStorage;
import data_structures.CommandData;
import data_structures.Commands;
import data_structures.ContactName;

import static android.widget.Toast.*;

public class ChatActivity extends ActionBarActivity {

    private Socket client;
    private static final String ipAddress = "192.168.137.138";
    private String ip;
    private static final int port = 4444;
    private DataSender dataSender;
    private DataStorage storage = null;
    private Cloud cloud = null;
    private Button sendMessageButton;
    private ArrayAdapter<String> contactsAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("MyLog", "onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Add List View
        ListView contactList = (ListView)findViewById(R.id.contactsList);
        String[] values = {"First", "Second"};
        contactsAdapter = new ArrayAdapter<String>(this, R.layout.contact_list_item, values);
        contactList.setAdapter(contactsAdapter);

        sendMessageButton = (Button)findViewById(R.id.sendMessage);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread()
                {
                    public void run() {
                        CommandData comData = new CommandData(Commands.RegisterContact, new ContactName("VladVin"));
                        CommandData queryContacts = new CommandData(Commands.RefreshContacts, null);
                        try {
                            cloud.requestData(comData);
                            cloud.requestData(queryContacts);
                            Log.d("MyLog", "Commands sent");
                        }
                        catch (CloudException e) {
                            showMessage("Cloud: " + e.getMessage());
                        }
                        catch (NullPointerException e) {
                            showMessage("Cloud has not been created yet");
                        }
                    }
                }.start();
            }
        });

        new DataUpdaterTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DataUpdaterTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params){
            storage = new DataStorage();
            try
            {
                cloud = new Cloud(storage);
            }
            catch(CloudException e)
            {
                showMessage("Cloud: " + e.getMessage());
            }
            if (cloud != null) {
                synchronized (cloud) {
                    cloud.start();
                }
            }

            while (true)
            {
                synchronized (storage) {
                    updateData(storage);
                }
                try
                {
                    Thread.sleep(50);
                }
                catch (InterruptedException e)
                {
                    return null;
                }
            }
        }
        private void updateData(final DataStorage storage)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView resultField = (TextView)findViewById(R.id.resultField);
                    if (storage.contactId != null && storage.contactId.Id != null)
                    {
                        resultField.setText(storage.contactId.Id.toString());
                    }

                    if (storage.contacts != null)
                    {
                        for (int i = 0; i < storage.contacts.Contacts.size(); ++i) {
                            contactsAdapter.add(storage.contacts.Contacts.get(i).Name.Name);
                        }
                        contactsAdapter.notifyDataSetChanged();
                    }
//                    ListView contactsList = (ListView)findViewById(R.id.contactsList);
//                    if (storage.contacts != null) {
//                        contactsList.clearChoices();
//                        for (int i = 0; i < storage.contacts.Contacts.size(); ++i) {
//                            TextView contactName = new TextView(getBaseContext());
//                            contactName.setText(storage.contacts.Contacts.get(i).Name.Name);
//                            contactsList.addView(contactName);
//                        }
//                    }
                }
            });
        }

    }
    private void showError(final String text){
        runOnUiThread(new Runnable() {
            public void run() {
                makeText(ChatActivity.this, text, LENGTH_LONG).show();
            }
        });
        finish();
    }

    private void showMessage(final String text){
        runOnUiThread(new Runnable() {
            public void run()
            {
                makeText(ChatActivity.this, text, LENGTH_LONG).show();
            }
        });
    }
}
