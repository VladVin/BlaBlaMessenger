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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import cloud.Cloud;
import data_structures.CommandData;
import data_structures.Commands;
import data_structures.ContactName;
import list_adapters_and_updaters.CloudCreator;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;


public class LoginActivity extends ActionBarActivity {
    public final static String USERNAME_MESSAGE = "com.vladvin.blabladmessengerclient.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        CloudCreator cloudCreator = new CloudCreator();
        if (GeneralData.cloud == null) {
            try {
                GeneralData.cloud = cloudCreator.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        else {
            showMessage(cloudCreator.getCloudException().getMessage());
        }

        ((Button)findViewById(R.id.signInButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (GeneralData.cloud != null) {
                    String userName = ((EditText)findViewById(R.id.nameField)).getText().toString();
                    if (userName.trim().length() != 0) {

//                            new LoginThread(new ContactName(userName)).start();
                        CommandData registerData = new CommandData(Commands.RegisterContact, new ContactName(userName));
                        GeneralData.cloud.requestData(registerData);
//                            Thread.sleep(3000);
                        Intent intent = new Intent(v.getContext(), ChatActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        showMessage("Enter you name, please");
                    }
                }
                else {
                    showMessage("Cloud has not been created yet");
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    private class LoginThread extends Thread {
        private ContactName name = null;

        public LoginThread(ContactName contactName) {
            name = contactName;
        }

        public void run() {
            CommandData registerData = new CommandData(Commands.RegisterContact, name);
            GeneralData.cloud.requestData(registerData);
        }
    }

    private void showError(final String text){
        runOnUiThread(new Runnable() {
            public void run() {
                makeText(LoginActivity.this, text, LENGTH_LONG).show();
            }
        });
        finish();
    }

    private void showMessage(final String text){
        runOnUiThread(new Runnable() {
            public void run()
            {
                makeText(LoginActivity.this, text, LENGTH_LONG).show();
            }
        });
    }
}
