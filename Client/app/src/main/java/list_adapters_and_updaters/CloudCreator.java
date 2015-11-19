package list_adapters_and_updaters;

import android.app.ActionBar;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;

import cloud.Cloud;
import cloud.CloudException;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

/**
 * Created by VladVin on 17.05.2015.
 */
public class CloudCreator extends AsyncTask<Void, Void, Cloud> {
    private CloudException cloudException = null;

    @Override
    protected Cloud doInBackground(Void... params) {
        Cloud cloud = null;
        try
        {
            cloud = new Cloud();
        }
        catch(CloudException e)
        {
            cloudException = new CloudException("Cloud: " + e.getMessage());
        }
        if (cloud != null)
            cloud.start();

        return cloud;
    }

    public CloudException getCloudException() {
        return cloudException;
    }
}
