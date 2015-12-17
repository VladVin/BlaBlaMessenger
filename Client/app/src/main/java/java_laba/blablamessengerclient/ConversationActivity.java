package java_laba.blablamessengerclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import cloud.Cloud;
import data_storage.DataStorage;








import list_adapters_and_updaters.MessageListAdapter;


public class ConversationActivity extends ActionBarActivity {

    private UUID myUUID = null;
    private UUID friendUUID = null;
    private MessageListAdapter messageListAdapter = null;
    private Cloud cloud = null;
    private DataUpdaterTask dataUpdaterTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        myUUID = GeneralData.conversationContactsPair.me;
        friendUUID = GeneralData.conversationContactsPair.friend;
        cloud = GeneralData.cloud;

        ArrayList<ContactMessagePair> tMessages = chooseMessages(cloud.getStorage().getMessages());
        messageListAdapter = new MessageListAdapter(this, tMessages, GeneralData.conversationContactsPair);
        ((ListView)findViewById(R.id.messageList)).setAdapter(messageListAdapter);

        // Start data updater
        dataUpdaterTask = new DataUpdaterTask(cloud);
        dataUpdaterTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        ((Button)findViewById(R.id.sendMessageButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText messageField = ((EditText)findViewById(R.id.messageField));
                String message = messageField.getText().toString();
                messageField.setText("");
                if (message.trim().length() != 0) {
                    ContactMessagePair contactMessagePair = new ContactMessagePair(friendUUID, new MessageData(message));
                    CommandData messageCommand = new CommandData(Commands.SendMessageToContact, contactMessagePair);
                    cloud.requestData(messageCommand);
                }
            }
        });

        ((Button)findViewById(R.id.backButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConversationActivity.this, ChatActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversation, menu);
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

    protected void onDestroy() {
        dataUpdaterTask.cancel(false);
        super.onDestroy();
    }

    private ArrayList<ContactMessagePair> chooseMessages(ArrayList<ContactMessagePair> messages) {
        if (messages == null) return null;
        ArrayList<ContactMessagePair> targetMessages = new ArrayList<ContactMessagePair>();
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).Contact.Id == myUUID.Id ||
                    messages.get(i).Contact.Id == friendUUID.Id) {
                targetMessages.add(messages.get(i));
            }
        }
        return targetMessages;
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
                            case Message:
                                // This is bad, I know...
                                ArrayList<ContactMessagePair> messages = storage.getMessages();
                                ArrayList<ContactMessagePair> tMessages = chooseMessages(messages);
                                messageListAdapter.updateContacts(tMessages);
                                break;
                        }
                    }
                }
            });
        }
    }
}
