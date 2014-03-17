
package com.iac.dlnaproject.activity;

import com.iac.dlnaproject.DLNAapp;
import com.iac.dlnaproject.MediaControllerService;
import com.iac.dlnaproject.R;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class StartPageActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        getSupportActionBar().hide();

        InitialTask task = new InitialTask();
        task.execute();
    }

    class InitialTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            //            startService(new Intent(DLNAapp.getApplication(),
            //                    MediaControllerService.class));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            startActivity(new Intent(StartPageActivity.this, HomeActivity.class));
            finish();
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            stopService(new Intent(DLNAapp.getApplication(),
                    MediaControllerService.class));
            super.onCancelled();
        }

    }

}
