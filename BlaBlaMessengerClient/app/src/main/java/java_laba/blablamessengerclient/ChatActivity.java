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

import data_processor.DataSender;
import data_processor.DataSenderException;
import data_structures.CommandData;
import data_structures.Commands;
import data_structures.ResultData;

import static android.widget.Toast.*;

public class ChatActivity extends ActionBarActivity {

    private Socket client;
    private static final String ipAddress = "192.168.137.138";
    private String ip;
    private static final int port = 4444;

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
                    ShowMessage("Using default IP Address");
                }
                MessageSenderTask senderTask = new MessageSenderTask();
                senderTask.execute();
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

    private void ShowError(final String text){
        runOnUiThread(new Runnable() {
            public void run() {
                makeText(ChatActivity.this, text, LENGTH_LONG).show();
            }
        });
        finish();
    }

    private void ShowMessage(final String text){
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
                client = new Socket(ip, port);
                DataSender dataSender = null;
                try
                {
                    dataSender = new DataSender(client);

                }
                catch (DataSenderException e)
                {
                    ShowMessage("Cannot create DataSender: " + e.getMessage());
                }

                try
                {
                    CommandData dataClientName = new CommandData();
                    dataClientName.Command = Commands.RegisterClient;
                    data_structures.ClientName clientName = new data_structures.ClientName();
                    clientName.name = "VladVin";
                    dataClientName.Data = (data_structures.DataObject)clientName;
                    dataSender.sendData(dataClientName);
                    CommandData data = new CommandData();
                    data.Command = Commands.RefreshContacts;
                    dataSender.sendData(data);
                }
                catch (Exception e)
                {
                    ShowMessage("Cannot send data");
                }

                try
                {
                    ResultData messageUuid = dataSender.receiveMessage();
                    ResultData messageContacts = dataSender.receiveMessage();
                }
                catch (Exception e)
                {
                    ShowMessage("Cannot receive data");
                }
            }
            catch(UnknownHostException e)
            {
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }

            return null;
        }
    }
}
