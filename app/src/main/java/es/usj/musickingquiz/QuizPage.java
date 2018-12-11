package es.usj.musickingquiz;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.androidannotations.annotations.EActivity;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import es.usj.musickingquiz.Dialogs.SureDialog;
import es.usj.musickingquiz.Models.Songs;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


@EActivity
public class QuizPage extends AppCompatActivity implements SureDialog.NoticeDialogListener, PlayerFragment.OnFragmentInteractionListener, FormFragment.OnFragmentInteractionListener {


    private DownloadManager downloadManager;
    private long downloadID;

    private static final int answersNumber = 3;

    private static final String resourcesURL ="http://10.0.2.2:8080/MusicQuiz/resources/music";
    private static final String jsonFileURL ="http://10.0.2.2:8080/MusicQuiz/api/songs";
    private static final String jsonFileName ="songs.json";
    private static final int numberOfSongsToDownload = 10;
    Toast notification;
    FormFragment ff;
    private ArrayList<Songs> songsList = null;
    private ArrayList<Songs> answerOptions = null;
    private ArrayList<Songs> playList = null;
    private Songs currentSongToPlay;
    private MediaPlayer mp;
    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            //que hacer cuando se complete una descarga
            inicializar();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_page);

        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(downloadManager.ACTION_DOWNLOAD_COMPLETE));

        inicializar();

    }

    private void inicializar() {
        if (playList == null) {
            if (checkIfFileExists(jsonFileName))
                setSongsList();
            else
                downloadJsonFile();
        } else if (AreAllSongsDownloaded()) {
            unregisterReceiver(onComplete);
            playNextSong();
            getAnswerOptions();
            setAnswers();
        }


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void setAnswers() {
        if (answerOptions != null) {
            ff = (FormFragment) getSupportFragmentManager().findFragmentById(R.id.fragment2);


            ff.setRadioButtons(answerOptions.get(0).getTitleAndAuthor(), answerOptions.get(1).getTitleAndAuthor(), answerOptions.get(2).getTitleAndAuthor());
        }
    }

    protected void downloadJsonFile() {

            try {
                Uri Download_Uri = Uri.parse(jsonFileURL);
                DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

                request.setDestinationInExternalFilesDir(this, DIRECTORY_DOWNLOADS, jsonFileName);
                downloadManager.enqueue(request);
            } catch (Exception e) {
                Toast.makeText(this, "No se ha podido conectar al servidor", Toast.LENGTH_LONG).show();
            }

    }

    private void setSongsList() {
        Gson gson = new Gson();
        String jsonString = null;


        try {
            File file = this.getExternalFilesDir(DIRECTORY_DOWNLOADS);

            Uri uri = Uri.withAppendedPath(Uri.fromFile(file), jsonFileName);
            InputStream is = getContentResolver().openInputStream(uri);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");


            Songs[] songs = gson.fromJson(jsonString, Songs[].class);

            ArrayList fullSongsList = new ArrayList(Arrays.asList(songs));
            Collections.shuffle(fullSongsList);

            List<Songs> tenSongs = fullSongsList.subList(0, numberOfSongsToDownload);


            songsList = new ArrayList(tenSongs);
            playList = new ArrayList(tenSongs);
            inicializar();
        } catch (Exception e) {
            Log.d("ERROR:", e.getMessage() + e.getCause());
        }
    }

    private void playNextSong()
    {
        currentSongToPlay = playList.get(0);

        try {
            startMediaPlayer(currentSongToPlay.file);


            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    try {
                        Responder(null);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            playList.remove(0);
        } catch (Exception e) {
            Toast.makeText(this, "No se ha podido reproducir el archivo", Toast.LENGTH_LONG).show();
        }

    }

    private void startMediaPlayer(String songFileName)
    {
        if (mp != null) mp.release();
        try {
            Uri file = Uri.withAppendedPath(Uri.parse(this.getExternalFilesDir(DIRECTORY_DOWNLOADS).getPath()), songFileName);
            mp = MediaPlayer.create(this, file);
            mp.start();
        }
        catch (Exception e)
        {
            Toast.makeText(this, getString(R.string.error_Message), Toast.LENGTH_LONG).show();
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

    private void downloadSong(String songFileName) {
        try {
            Uri Download_Uri = Uri.withAppendedPath(Uri.parse(resourcesURL), songFileName);
            DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

            request.setDestinationInExternalFilesDir(this, DIRECTORY_DOWNLOADS, songFileName);
            downloadID = downloadManager.enqueue(request);
        } catch (Exception e) {
            Toast.makeText(this, "No se ha podido descargar algunos de los archivos", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkIfFileExists(String fileName)
    {
        File file = new File(this.getExternalFilesDir(DIRECTORY_DOWNLOADS), fileName);
        if(file.exists()) return true; else return false;

    }

    private void getAnswerOptions() {

        ArrayList<Songs> CompleteList = new ArrayList(songsList);
        answerOptions = new ArrayList();


        CompleteList.remove(currentSongToPlay);
        answerOptions.add(currentSongToPlay);

        Random random = new Random();
        int index = 0;

        while (answerOptions.size() < answersNumber) {
            index = random.nextInt(CompleteList.size());

            Songs song = CompleteList.get(index);
            CompleteList.remove(song);
            answerOptions.add(song);
        }

        Collections.shuffle(answerOptions);


    }

    @Override
    protected void onStop() {

        mp.stop();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        mp.pause();
        SureDialog sureDialog = new SureDialog();

        sureDialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        super.onBackPressed();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        mp.start();
    }

    public void Responder(View view) throws InterruptedException {

        validarRespuesta();
        playNextSong();
        getAnswerOptions();
        setAnswers();
    }

    private void validarRespuesta() {

        if (ff.getSelectedOption().equals(currentSongToPlay.getTitleAndAuthor())) {
            notification = Toast.makeText(this, "Respuesta Correcta", Toast.LENGTH_LONG);
            notification.setGravity(Gravity.BOTTOM, 0, 0);
            notification.show();
            ;
//            return true;
        } else {
            notification = Toast.makeText(this, "Respuesta incorrecta.", Toast.LENGTH_LONG);
            notification.setGravity(Gravity.BOTTOM, 0, 0);
            notification.show();
            ;
//            return false;
        }
    }

    private boolean AreAllSongsDownloaded() {
        for (Songs song : playList) {
            if (!checkIfFileExists(song.file)) {
                downloadSong(song.file);
                return false;
            }
        }
        return true;
    }

}
