package com.alvarenga.enrique.droidclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    /** Class logging TAG */
    private static final String TAG        = "MainActivity";
    static final String USER_MESSAGE_STATE = "userMessageState";

    private EditText userMessage;
    private Button uploadToServer;

    private File appDirectory;
    /** Username to be used to connect to the AWS instance */
    private String userName = "ec2-user";
    final static String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DroidClient/";

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(USER_MESSAGE_STATE, String.valueOf(userMessage.getText()));
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Recover the instance state */
        if (savedInstanceState != null) {
            /* Reload the app */
            Log.d(TAG, "onCreate: Restoring state");
            userMessage.setText(savedInstanceState.getString(USER_MESSAGE_STATE));
        } else {
            Log.d(TAG, "onCreate: New activity");
            setContentView(R.layout.activity_main);
        }

        createStorageLocation();
        isWriteStoragePermissionGranted();
        isReadStoragePermissionGranted();

        /* Initialize components */
        userMessage = findViewById(R.id.userMessageInput);
        uploadToServer = findViewById(R.id.uploadToServerBtn);
        uploadToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Attempting to upload files to Server");
                sftpFileUpload();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void sftpFileUpload(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Session session;
                Channel channel;
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                try {
                    JSch ssh = new JSch();
                    ssh.addIdentity(filePath + "open-dev.pem");
                    session = ssh.getSession(userName, "3.15.207.215", 22);
                    session.setConfig(config);
                    session.connect();
                    channel = session.openChannel("sftp");
                    channel.connect();
                    ChannelSftp sftp = (ChannelSftp) channel;
                    SftpATTRS attrs = null;

                    try {
                        /* Check if the destination directory exists */
                        attrs = sftp.stat("./Android-Files");
                    } catch (Exception e) {
                        System.out.println(sftp.pwd() + "/Android-Files not found");
                    }

                    if (attrs != null) {
                        Log.d(TAG, "run: Destination directory exist: "+ attrs.isDir());
                    } else {
                        Log.d(TAG, "run: Creating Destination directory");
                        sftp.mkdir("./Android-Files");
                    }

                    sftp.put(filePath + "Sample.txt", "/home/ec2-user/Android-Files");
                } catch (JSchException e) {
                    e.printStackTrace();
                } catch (SftpException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    /**
     * Creates the application directory where the files and user configuration will
     * be stored.
     */
    public void createStorageLocation(){
        try {
            Log.d(TAG, "Creating storage location: " + filePath);
            appDirectory = new File(filePath);

            if (!appDirectory.exists()) {   /* Check if directory exists */
                if(!appDirectory.mkdirs()){ /* Attempt to create the directory */
                    Toast.makeText(this,"Critical - Failed to create config directory",
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "createStorageLocation: " + filePath);
                    return;
                }
            }

            /* Application configuration file */
            appDirectory = new File(filePath + "Sample.txt");
            if (!appDirectory.exists()) {           /* Check if a configuration file already exists */
                if (!appDirectory.createNewFile()){ /* Attempt to create the app configuration file */
                    Toast.makeText(this,"Critical - Failed to create config file",
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "createStorageLocation: " + appDirectory);
                    return;
                }
                /* Successfully created file */
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d(TAG, "External storage2");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission

                }else{
                    // Do nothing
                }
                break;

            case 3:
                Log.d(TAG, "External storage1");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission

                }else{
                    // Do nothing
                }
                break;
        }
    }

    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Read Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Read Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Read Permission is granted");
            return true;
        }
    }

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Write Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Write Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted2");
            return true;
        }
    }
}
