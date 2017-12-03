package com.mindorks.tensorflowexample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String DBG_TAG = "MAIN";
    private static final String FILE_NAME_SUFFIX = "_nohash_0";

    private final Context mContext = this;

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 100;
    private static String[] PERMISSIONS_RECORD = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private TextView mTimerDisplay;
    private long timerCount;
    private MediaRecorder mediaRecorder = null;
    private boolean isRecording = false;
    private Button recordButton;
    private Button playButton;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private SeekBar controller;
    private TextView volumeTextView;
    private float currentVolume;
    private Handler handlerTimer;
    private Runnable runnableTimer;
    private Handler handlerRecorder;
    private Runnable runnableRecorder;
    private String categoryStr;
    private Spinner mCategorySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(DBG_TAG, "BUILD ABI = " + Build.SUPPORTED_ABIS[0]);

        findViewById(R.id.tenser_flow_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SpeechActivity.class));
            }
        });

        mCategorySpinner = (Spinner) findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(adapter);
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryStr = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mTimerDisplay = (TextView) findViewById(R.id.timer_display);

        recordButton = (Button) findViewById(R.id.btn_record);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    stopAudioRecording();
                } else {
                    startAudioRecoding();
                }
            }
        });

        playButton = (Button) findViewById(R.id.btn_play_music);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    mediaPlayer.pause();
                    isPlaying = false;
                    playButton.setText(R.string.player_play);
                } else {
                    mediaPlayer.start();
                    isPlaying = true;
                    playButton.setText(R.string.player_pause);
                }
            }
        });


        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        mediaPlayer.setVolume(currentVolume, currentVolume);

        controller = (SeekBar) findViewById(R.id.controller_volume);
        volumeTextView = (TextView) findViewById(R.id.text_volume);
        volumeTextView.setText(controller.getProgress() + " / 100");
        currentVolume = (float) controller.getProgress() / 100;
        controller.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentVolume = (float) controller.getProgress() / 100;
                volumeTextView.setText(controller.getProgress() + " / 100");
                mediaPlayer.setVolume(currentVolume, currentVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void startAudioRecoding() {
        if (checkPermission()) {

//            categoryStr = ((EditText) findViewById(R.id.category_string)).getText().toString();

            Log.d(DBG_TAG, "categoryStr = " + categoryStr);

            Toast.makeText(this, "Access granted", Toast.LENGTH_LONG).show();

            isRecording = true;

            // Update button
            recordButton.setText(R.string.stop_record);

            mediaRecorder = new MediaRecorder();


            handlerRecorder = new Handler();
            // Define the code block to be executed
            runnableRecorder = new Runnable() {
                @Override
                public void run() {
                    try {

                        File recordOutputFile = getOutputFile();

                        if (recordOutputFile != null) {

                            mediaRecorder.reset();
                            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            mediaRecorder.setAudioSamplingRate(16000);
                            mediaRecorder.setMaxDuration(5000);
                            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                            mediaRecorder.setOutputFile(recordOutputFile.getPath());

                            mediaRecorder.prepare();
                            mediaRecorder.start();

                            // Run the above code block on the main thread after 10 seconds
                            handlerRecorder.postDelayed(runnableRecorder, 10000);

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            handlerRecorder.post(runnableRecorder);

            handlerTimer = new Handler();
            runnableTimer = new Runnable() {
                @Override
                public void run() {
                    timerCount++;
                    mTimerDisplay.setText(String.format(Locale.getDefault(), "%02d:%02d",
                            timerCount / 60, timerCount % 60));
                    handlerTimer.postDelayed(runnableTimer, 1000);
                }
            };
            handlerTimer.post(runnableTimer);

        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_RECORD, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }
    }


    private void stopAudioRecording() {
        // stops the recording activity
        if (mediaRecorder != null) {
            isRecording = false;
            mediaRecorder.stop();
            recordButton.setText(R.string.start_record);

            handlerRecorder.removeCallbacks(runnableRecorder);
            handlerTimer.removeCallbacks(runnableTimer);
        }
    }

    private File getOutputFile() {

        if (checkPermission()) {

            Log.d(DBG_TAG, "READ/WRITE PERMISSION_GRANTED");
            SimpleDateFormat dateFormat = new SimpleDateFormat("hhmmss", Locale.US);

            File file = new File (Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/CSE570_RECORDING/");
            if(!file.exists() && file.mkdir()) Log.d(DBG_TAG, "Create a new folder");

            File category = new File (Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/CSE570_RECORDING/" + categoryStr + "/");
            if(!category.exists() && category.mkdir()) Log.d(DBG_TAG, "Create a new folder " + categoryStr);

            Log.d(DBG_TAG, "hash code = " + Integer.toHexString(dateFormat.format(Calendar.getInstance().getTime()).hashCode()));

            return new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/CSE570_RECORDING/" + categoryStr + "/"
                    + Integer.toHexString((new Double(Math.random())).hashCode())
                    + FILE_NAME_SUFFIX
                    + ".wav");

        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_RECORD, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }

        return null;
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                Log.d(DBG_TAG, Arrays.asList(permissions).toString());
                Log.d(DBG_TAG, Arrays.asList(grantResults).toString());
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(DBG_TAG, "Granted to record");
                } else {
                    Log.d(DBG_TAG, "No permission on audio recording");
                }
            }
            break;
        }
    }
}
