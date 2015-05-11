package java_laba.blablamessengerclient;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import cloud.Cloud;
import cloud.CloudException;
import cloud.DataSender;
import cloud.DataSenderException;
import data_storage.DataStorage;
import data_structures.ClientName;
import data_structures.CommandData;
import data_structures.Commands;
import data_structures.ResultData;

import static android.widget.Toast.*;

public class ChatActivity extends ActionBarActivity {

    private Socket client;
    private static final String ipAddress = "192.168.137.138";
    private String ip;
    private static final int port = 4444;
    private DataSender dataSender;
    private DataStorage storage;
    private Cloud cloud;

    private Button sendMessageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("MyLog", "onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendMessageButton = (Button)findViewById(R.id.sendMessage);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText editText = (EditText)findViewById(R.id.ipAddressField);
                ip = editText.getText().toString();
                if (ip.length() != 15)
                {
                    ip = ipAddress;
                    showMessage("Using default IP Address");
                }
                MessageSenderTask senderTask = new MessageSenderTask();
                senderTask.execute();
            }
        });


        storage = new DataStorage();
        try
        {
            cloud = new Cloud(storage);
        }
        catch(CloudException e)
        {
            showError("Cloud: " + e.getMessage());
        }
        cloud.runDataListener();
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

    private class MessageSenderTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params){
            try
            {
                dataSender = new DataSender();
            }
            catch (DataSenderException e)
            {
                showError("DataSender: " + e.getMessage());
            }

            CommandData comData = new CommandData();
            comData.Command = Commands.RegisterClient;
            //comData.Data = new ClientName()
            dataSender.sendData(comData);

            return null;
        }
    }
}
