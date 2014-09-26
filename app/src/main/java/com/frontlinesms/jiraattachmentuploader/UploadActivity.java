package com.frontlinesms.jiraattachmentuploader;

import android.app.Activity;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;


public class UploadActivity extends Activity {

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

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri != null) {
                    updateImagePreview(imageUri);
                }
            }
        }
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
            Uri imageUri = data.getData();
            updateImagePreview(imageUri);
            TextView clickToChooseView = (TextView) findViewById(R.id.uploadTrigger);
		} else if(requestCode == RESULT_SETTINGS) {
			Context context = getApplicationContext();
			prefs = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    private HttpResponse uploadAttachment(Uri fileUri){
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("LINK TO SERVER");

        MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        if (filePath !=null) {
            File file = new File(filePath);
            Log.d("EDIT USER PROFILE", "UPLOAD: file length = " + file.length());
            Log.d("EDIT USER PROFILE", "UPLOAD: file exist = " + file.exists());
            mpEntity.addPart("avatar", new FileBody(file, "application/octet"));

            httppost.setEntity(mpEntity);
            try {
                HttpResponse response = httpclient.execute(httppost);
                return response;
            }catch(IOException ex){
                Log.e("UPLOAD_ERROR", ex.getMessage(), ex);
            }
        }
        return null;
    }

    private void updateImagePreview(Uri imageUri){
        imagePreview.setImageURI(imageUri);
        uploadButton.setEnabled(true);
        clickToChooseView.setVisibility(View.INVISIBLE);
    }
}
