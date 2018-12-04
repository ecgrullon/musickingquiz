package es.usj.musickingquiz;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.androidannotations.annotations.EActivity;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import es.usj.musickingquiz.Models.Songs;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


@EActivity
public class QuizPage extends AppCompatActivity implements PlayerFragment.OnFragmentInteractionListener, FormFragment.OnFragmentInteractionListener {


    private DownloadManager downloadManager;
    private long downloadID;
    private boolean randomized = false;


    private static final String resourcesURL ="http://10.0.2.2:8080/MusicQuiz/resources/music";
    private static final String jsonFileURL ="http://10.0.2.2:8080/MusicQuiz/api/songs";
    private static final String jsonFileName ="songs.json";

    List<Songs> songsList = null;
    Songs currentSongToPlay;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_page);
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        downloadJsonFile();
        setSongsList();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }



    protected void downloadJsonFile() {
        if (!checkIfFileExists(jsonFileName)) {
            try {
                Uri Download_Uri = Uri.parse(jsonFileURL);
                DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

                request.setDestinationInExternalFilesDir(this, DIRECTORY_DOWNLOADS, jsonFileName);
                downloadManager.enqueue(request);
            } catch (Exception e) {
                //Message
            }
        }

    }


    private void setSongsList() {
        Gson gson = new Gson();
        String jsonString = null;


        try {
            File file = this.getExternalFilesDir(DIRECTORY_DOWNLOADS);

            Uri uri = Uri.withAppendedPath(Uri.fromFile(file), jsonFileName);
            String url = uri.getPath();
            InputStream is = QuizPage.this.getAssets().open(url);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");


            Songs[] songs = gson.fromJson(jsonString, Songs[].class);
            songsList = Arrays.asList(songs);
            Log.d("PruebaJson", songs[0].name);
        } catch (Exception e) {
            Log.d("ERROR:", e.getMessage() + e.getCause());
        }
    }

    private void playRandomSong()
    {
        if(songsList != null) {
            if (!randomized) {
                Collections.shuffle(songsList);
                randomized = true;
            }

            currentSongToPlay = songsList.get(0);

            if(checkIfFileExists(currentSongToPlay.file)) {
                startMediaPlayer(currentSongToPlay.file);
                songsList.remove(0);
            }


        }
    }

    private void startMediaPlayer(String songFileName)
    {
        try {
            Uri file = Uri.withAppendedPath(Uri.parse(Environment.getExternalStorageDirectory().getPath()),songFileName);
            mp = MediaPlayer.create(this, file);
            mp.start();
        }
        catch (Exception e)
        {
            Toast.makeText(this,getString(R.string.error_Message),Toast.LENGTH_LONG);
        }
    }

    private void  downloadSong(Songs song)
    {
        try {
            Uri Download_Uri = Uri.withAppendedPath(Uri.parse(resourcesURL),song.file);
            DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

            request.setDestinationInExternalFilesDir(this, DIRECTORY_DOWNLOADS, song.file);
            downloadID = downloadManager.enqueue(request);
        } catch (Exception e) {
            Log.d("ERROR:", e.getMessage() + e.getCause());
        }
    }

    private boolean checkIfDownloadStatusIsCompleted() {

        boolean isCompleted =false;
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadID);
        Cursor cursor = downloadManager.query(query);

        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            cursor.close();

            if (status == DownloadManager.STATUS_SUCCESSFUL) isCompleted = true;
        }

        return isCompleted;
    }

    private boolean checkIfFileExists(String fileName)
    {
        File file = new File(Environment.getExternalStorageDirectory().getPath(),fileName);
        if(file.exists()) return true; else return false;

    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
           //que hacer cuando se complete una descarga
        }
    };
}
