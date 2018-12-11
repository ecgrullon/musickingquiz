package es.usj.musickingquiz;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import org.androidannotations.annotations.EActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import es.usj.musickingquiz.Dialogs.SureDialog;
import es.usj.musickingquiz.Fragments.GameFragment;
import es.usj.musickingquiz.Models.Settings;
import es.usj.musickingquiz.Models.Shared;
import es.usj.musickingquiz.Models.Songs;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


@EActivity
public class QuizPage extends AppCompatActivity implements SureDialog.NoticeDialogListener {


    GameFragment gf;
    private Settings settings = new Settings();
    private Shared shared = new Shared();


    Toast notification;
    private ArrayList<Songs> answerOptions = null;
    private Songs currentSongToPlay;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_page);

        if (shared.areAllSongsDownloaded) {
            playNextSong();
            getAnswerOptions();
            setAnswers();
        }

    }

    private void setAnswers() {
        if (answerOptions != null) {
            gf = (GameFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);


            gf.setRadioButtons(answerOptions.get(0).getTitleAndAuthor(), answerOptions.get(1).getTitleAndAuthor(), answerOptions.get(2).getTitleAndAuthor());
        }
    }


    private void playNextSong() {
        currentSongToPlay = shared.playList.get(0);

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
            shared.playList.remove(0);
        } catch (Exception e) {
            Toast.makeText(this, "No se ha podido reproducir el archivo", Toast.LENGTH_LONG).show();
        }

    }

    private void startMediaPlayer(String songFileName) {
        if (mp != null) mp.release();
        try {
            Uri file = Uri.withAppendedPath(Uri.parse(this.getExternalFilesDir(DIRECTORY_DOWNLOADS).getPath()), songFileName);
            mp = MediaPlayer.create(this, file);
            mp.start();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_Message), Toast.LENGTH_LONG).show();
        }
    }


    private void getAnswerOptions() {

        ArrayList<Songs> CompleteList = new ArrayList(shared.songsList);
        answerOptions = new ArrayList();


        CompleteList.remove(currentSongToPlay);
        answerOptions.add(currentSongToPlay);

        Random random = new Random();
        int index = 0;

        while (answerOptions.size() < settings.answersNumber) {
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

        if (gf.getSelectedOption().equals(currentSongToPlay.getTitleAndAuthor())) {
            notification = Toast.makeText(this, "Respuesta Correcta", Toast.LENGTH_LONG);
        } else {
            notification = Toast.makeText(this, "Respuesta incorrecta.", Toast.LENGTH_LONG);

        }
        notification.setGravity(Gravity.BOTTOM, 0, 0);
        notification.show();
    }


}
