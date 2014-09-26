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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView;
import android.widget.Toast;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

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
    private EditText ticketNumberInput;
    private Uri fileUri;
    private static final int RESULT_SETTINGS = 1;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        imagePreview = (ImageView) findViewById(R.id.imagePreview);
        ticketNumberInput = (EditText) findViewById(R.id.ticketNumberInput);

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

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAttachment(fileUri);
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

    private void uploadAttachment(Uri fileUri){
        try {
            Context context = getApplicationContext();
            prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String url = prefs.getString("jira_url","");
            String username= prefs.getString("jira_username", "");
            String password = prefs.getString("jira_password", "");


            url += ("rest/api/2/issue/"+ticketNumberInput.getText()+"/attachments");

            HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
                    .header("X-Atlassian-Token", "nocheck")
                    .basicAuth(username, password)
                    .field("file", new File(fileUri.getPath()))
                    .asJson();
        }catch (UnirestException ex){
            Log.e("UPLOAD_ERR", ex.getMessage());
        }
        //return null;
    }

    private void updateImagePreview(Uri imageUri){
        fileUri = imageUri;
        imagePreview.setImageURI(imageUri);
        uploadButton.setEnabled(true);
        clickToChooseView.setVisibility(View.INVISIBLE);
    }
}
