package es.usj.musickingquiz.Fragments;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import es.usj.musickingquiz.Dialogs.finishDialog;
import es.usj.musickingquiz.Models.Settings;
import es.usj.musickingquiz.Models.Shared;
import es.usj.musickingquiz.Models.Songs;
import es.usj.musickingquiz.R;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class GameFragment extends Fragment {


    int aciertos = 0;
    private RadioGroup rgOptions;
    Toast notification;
    private Button btnGiveAnswer;
    private Settings settings = new Settings();
    private Shared shared = new Shared();
    private ArrayList<Songs> answerOptions = null;

    int previousSelection = -1;
    private Songs currentSongToPlay;
    private MediaPlayer mp;
    Chronometer chronometer;
    private ArrayList<Songs> playList = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        rgOptions = view.findViewById(R.id.rgOptions);

        btnGiveAnswer = view.findViewById(R.id.btnGiveAnswer);
        btnGiveAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responder();
            }
        });

        if (shared.areAllSongsDownloaded) {
            loadPlayList();
            playNextSong();
            getAnswerOptions();
            setAnswers();
        }
        chronometer = view.findViewById(R.id.chronometer);
        chronometer.start();
        return view;
    }

    private void loadPlayList() {
        playList = new ArrayList(shared.songsList);
        Collections.shuffle(playList);
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
                addNewRadioButton(songoption.getTitleAndAuthor());
            }
        }
    }

    private void addNewRadioButton(String value) {
        final RadioButton rb = new RadioButton(getContext());
        rb.setText(value);
        rb.setTextColor(ContextCompat.getColor(getContext(), R.color.lightorange));
        rb.setTextSize(16);
        rb.setPaddingRelative(5, 5, 5, 5);
        rb.setButtonTintList(ContextCompat.getColorStateList(getContext(), R.color.radiobutton_color));
        rb.setClickable(true);
        rb.setFocusable(true);
        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rb.getId() == previousSelection) {
                    responder();
                }
                previousSelection = rgOptions.getCheckedRadioButtonId();
            }
        });

        rgOptions.addView(rb);
    }

    private void playNextSong() {
        currentSongToPlay = playList.get(0);

        try {
            startMediaPlayer(currentSongToPlay.file);


            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    responder();
                }
            });
            playList.remove(0);
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

    public void responder() {

        nextQuestion();
    }

    private void nextQuestion() {
        if (playList.size() > 0) {
            validarRespuesta();
            playNextSong();
            getAnswerOptions();
            setAnswers();
        } else {
            finalizarJuego();
        }
    }

    private void finalizarJuego() {
        mp.stop();
        chronometer.stop();
        btnGiveAnswer.setEnabled(false);
        callFinishDialog();
    }

    private void validarRespuesta() {

        if (getSelectedOption().equals(currentSongToPlay.getTitleAndAuthor())) {
            aciertos++;
            notification = Toast.makeText(this.getContext(), getString(R.string.respuesta_correcta), Toast.LENGTH_LONG);
        } else {
            notification = Toast.makeText(this.getContext(), getString(R.string.respuesta_incorrecta), Toast.LENGTH_LONG);

        }
        notification.setGravity(Gravity.BOTTOM, 0, 50);
        notification.show();
    }


    @Override
    public void onStop() {
        mp.stop();
        super.onStop();
    }

    private void callFinishDialog() {
        finishDialog fragment = finishDialog.newInstance(String.valueOf(aciertos), String.valueOf(settings.numberOfSongsToPlay), String.valueOf(chronometer.getText()));
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.activity_quiz_root_layout, fragment).commit();
    }


}
