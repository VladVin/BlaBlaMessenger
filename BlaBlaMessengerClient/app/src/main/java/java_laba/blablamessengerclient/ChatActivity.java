package java_laba.blablamessengerclient;

import android.content.Intent;
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
import java.util.ArrayList;

import cloud.Cloud;
import cloud.CloudException;
import cloud.DataSender;
import cloud.DataSenderException;
import data_storage.DataStorage;
import data_structures.CommandData;
import data_structures.Commands;
import data_structures.Contact;
import data_structures.ContactName;
import list_adapters_and_updaters.ContactListAdapter;

import static android.widget.Toast.*;

public class ChatActivity extends ActionBarActivity {

    private Cloud cloud = null;
    private Button sendMessageButton;
    private ContactListAdapter contactsAdapter = null;
    private String userName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("ChatActivity", "onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Receive the user name
        Intent loginIntent = getIntent();
        userName = loginIntent.getStringExtra(LoginActivity.EXTRA_MESSAGE);

        // Take the cloud
        cloud = GeneralData.cloud;

        // Add List View
        ListView contactList = (ListView)findViewById(R.id.contactsList);
        contactsAdapter = new ContactListAdapter(this, null);
        contactList.setAdapter(contactsAdapter);

        new DataUpdaterTask(cloud).execute();

        sendMessageButton = (Button)findViewById(R.id.sendMessage);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread()
                {
                    public void run() {
                        CommandData comData = new CommandData(Commands.RegisterContact, new ContactName(userName));
                        CommandData queryContacts = new CommandData(Commands.RefreshContacts, null);
                        try {
                            cloud.requestData(comData);
                            cloud.requestData(queryContacts);
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

    private class DataUpdaterTask extends AsyncTask<Void, Void, Void> {
        private Cloud cloud;

        public DataUpdaterTask(Cloud cl) {
            cloud = cl;
        }

        @Override
        protected Void doInBackground(Void... params){
            while (true)
            {
                if (cloud != null) {
                    DataStorage storage = cloud.getStorage();
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
        }

        private void updateData(final DataStorage storage)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (storage != null) {
                        switch (storage.whatUpdated()) {
                            case UpdatedContacts:
                                if (storage.getContacts() != null) {
                                    contactsAdapter.updateContacts(storage.getContacts().Contacts);
                                }
                                break;
                        }
                    }
                }
            });
        }

    }
}
