package es.usj.musickingquiz.Fragments;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import es.usj.musickingquiz.Models.Settings;
import es.usj.musickingquiz.Models.Shared;
import es.usj.musickingquiz.Models.Songs;
import es.usj.musickingquiz.R;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class GameFragment extends Fragment {


    int aciertos = 0;
    TextView tvCountDown;
    private RadioGroup rgOptions;
    Toast notification;
    private Button btnGiveAnswer;
    private Settings settings = new Settings();
    private Shared shared = new Shared();
    private ArrayList<Songs> answerOptions = null;
    private Songs currentSongToPlay;
    private MediaPlayer mp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        tvCountDown = view.findViewById(R.id.tvCountDown);

        rgOptions = view.findViewById(R.id.rgOptions);

        btnGiveAnswer = view.findViewById(R.id.btnGiveAnswer);

        btnGiveAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Responder();
            }
        });

        if (shared.areAllSongsDownloaded) {
            playNextSong();
            getAnswerOptions();
            setAnswers();
        }
        new CountDownTimer(20000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvCountDown.setText("" + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                tvCountDown.setText("done!");
            }
        }.start();

        return view;
    }



    public String getSelectedOption() {
        int rbID = rgOptions.getCheckedRadioButtonId();
        RadioButton rbSelected = rgOptions.findViewById(rbID);
        String selectedString = "";
        if (rbSelected != null) selectedString = rbSelected.getText().toString();
        else selectedString = "";
        return selectedString;
    }

    private void setAnswers() {
        rgOptions.removeAllViews();
        if (answerOptions != null) {
            for (Songs songoption : answerOptions) {
                RadioButton rb = new RadioButton(getContext());
                rb.setText(songoption.getTitleAndAuthor());
                rb.setTextColor(ContextCompat.getColor(getContext(), R.color.lightorange));
                rb.setTextSize(16);
                rb.setButtonTintList(ContextCompat.getColorStateList(getContext(), R.color.radiobutton_color));
                rgOptions.addView(rb);
            }
        }
    }

    private void playNextSong() {
        currentSongToPlay = shared.playList.get(0);

        try {
            startMediaPlayer(currentSongToPlay.file);


            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    Responder();
                }
            });
            shared.playList.remove(0);
        } catch (Exception e) {
            Toast.makeText(this.getContext(), getString(R.string.error_Message), Toast.LENGTH_LONG).show();
        }

    }

    private void startMediaPlayer(String songFileName) {
        if (mp != null) mp.release();
        try {
            Uri file = Uri.withAppendedPath(Uri.parse(this.getContext().getExternalFilesDir(DIRECTORY_DOWNLOADS).getPath()), songFileName);
            mp = MediaPlayer.create(this.getContext(), file);
            mp.start();
        } catch (Exception e) {
            Toast.makeText(this.getContext(), getString(R.string.error_Message), Toast.LENGTH_LONG).show();
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

    public void Responder() {

        nextQuestion();
    }

    private void nextQuestion() {
        if (shared.playList.size() > 0) {
            validarRespuesta();
            playNextSong();
            getAnswerOptions();
            setAnswers();
        } else {
            mp.release();
            Toast.makeText(this.getContext(), "Se acabo, acertaste " + aciertos + " de " + settings.numberOfSongsToPlay, Toast.LENGTH_LONG).show();
        }
    }

    private void validarRespuesta() {

        if (getSelectedOption().equals(currentSongToPlay.getTitleAndAuthor())) {
            aciertos++;
            notification = Toast.makeText(this.getContext(), getString(R.string.respuesta_correcta), Toast.LENGTH_LONG);
        } else {
            notification = Toast.makeText(this.getContext(), getString(R.string.respuesta_incorrecta), Toast.LENGTH_LONG);

        }
        notification.setGravity(Gravity.BOTTOM, 0, 0);
        notification.show();
    }


    @Override
    public void onStop() {
        mp.stop();
        super.onStop();
    }



}
