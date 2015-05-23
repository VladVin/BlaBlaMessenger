package java_laba.blablamessengerclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import cloud.Cloud;
import data_storage.DataStorage;
import data_structures.CommandData;
import data_structures.Commands;
import data_structures.Contact;
import data_structures.ContactId;
import data_structures.FileIdNamePair;
import list_adapters_and_updaters.ContactListAdapter;
import list_adapters_and_updaters.FileStorageListAdapter;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;


public class FileStorageActivity extends ActionBarActivity {

    private Cloud cloud = null;
    private FileStorageListAdapter filesAdapter = null;
    private DataUpdaterTask dataUpdaterTask = null;
    private CommandSenderTask commandSenderTask = null;
    private ContactId myContactId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_storage);

        // Take the cloud
        cloud = GeneralData.cloud;

        // Add List View
        final ListView filesList = (ListView)findViewById(R.id.filesList);
        filesAdapter = new FileStorageListAdapter(this, null);
        filesList.setAdapter(filesAdapter);

        // Start data updater
        dataUpdaterTask = new DataUpdaterTask(cloud);
        dataUpdaterTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        // Start command sender
        commandSenderTask = new CommandSenderTask();
        commandSenderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //Check the contact ID
        if (GeneralData.conversationContactsPair != null && GeneralData.conversationContactsPair.me != null) {
            myContactId = GeneralData.conversationContactsPair.me;
        }

        // Listeners
        filesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileIdNamePair filePair = (FileIdNamePair)filesList.getItemAtPosition(position);
                // TODO: Download the file
//                Intent intent = new Intent(getBaseContext(), ConversationActivity.class);
//                startActivity(intent);
//                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file_storage, menu);
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

    @Override
    protected void onDestroy() {
        dataUpdaterTask.cancel(false);
        commandSenderTask.cancel(false);
        super.onDestroy();
    }

    private void showError(final String text){
        runOnUiThread(new Runnable() {
            public void run() {
                makeText(FileStorageActivity.this, text, LENGTH_LONG).show();
            }
        });
        finish();
    }

    private void showMessage(final String text){
        runOnUiThread(new Runnable() {
            public void run()
            {
                makeText(FileStorageActivity.this, text, LENGTH_LONG).show();
            }
        });
    }

    private class CommandSenderTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            while (!isCancelled()) {
                CommandData queryFiles = new CommandData(Commands.RefreshStorage, null);
                try {
                    cloud.requestData(queryFiles);
                    Log.d("CommandSenderTask", "Sent update files query");
                }
                catch (NullPointerException e) {
                    showMessage("Cloud has not been created yet");
                }

                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    return null;
                }
            }
            return null;
        }
    }

    private class DataUpdaterTask extends AsyncTask<Void, Void, Void> {
        private Cloud cloud;

        public DataUpdaterTask(Cloud cl) {
            cloud = cl;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (!isCancelled()) {
                if (cloud != null) {
                    DataStorage storage = cloud.getStorage();
                    synchronized (storage) {
                        updateData(storage);
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        return null;
                    }
                }
            }
            return null;
        }

        private void updateData(final DataStorage storage) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (storage != null) {
                        switch (storage.whatUpdated()) {
                            case ContactId:
                                myContactId = storage.getContactId();
                                break;
                            case UpdatedContacts:
                                storage.getContacts();
                                break;
                            case UpdatedFiles:
                                ArrayList<FileIdNamePair> files = storage.getFiles();
                                filesAdapter.updateFiles(files);
                                break;
                        }
                    }
                }
            });
        }
    }
}
