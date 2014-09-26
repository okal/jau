package com.frontlinesms.jiraattachmentuploader;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView;
import android.widget.Toast;


public class UploadActivity extends ActionBarActivity {

    private static int REQUEST_LOAD_IMAGE = 1;
    private Button uploadButton;
    private ImageView imagePreview;
    private TextView clickToChooseView;
    private static final int RESULT_SETTINGS = 1;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        imagePreview = (ImageView) findViewById(R.id.imagePreview);

        clickToChooseView = (TextView) findViewById(R.id.uploadTrigger);
        clickToChooseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );

                startActivityForResult(intent, REQUEST_LOAD_IMAGE);
            };
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivityForResult(i, RESULT_SETTINGS);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();

            imagePreview.setImageURI(selectedImage);
            uploadButton.setEnabled(true);
            TextView clickToChooseView = (TextView) findViewById(R.id.uploadTrigger);
            clickToChooseView.setVisibility(View.INVISIBLE);
		} else if(requestCode = RESULT_SETTINGS) {
			Context context = getApplicationContext();
			prefs = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }
}
