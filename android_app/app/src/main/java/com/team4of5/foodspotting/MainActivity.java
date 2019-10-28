package com.team4of5.foodspotting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        if(CurrentUser.CurrentUser().init(this.getFilesDir())){
            CurrentUser.CurrentUser().setLogin(true);
        }
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
        //new BackgroundAsyncTask(this).execute();
    }

    /*class BackgroundAsyncTask extends AsyncTask<Void, Void, Boolean> {

        Activity activity;

        public BackgroundAsyncTask(Activity activity){
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return true;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
        }
    }*/

}
