package es.usj.musickingquiz.Dialogs;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import es.usj.musickingquiz.R;


public class finishDialog extends Fragment {


    private static String fd_aciertos = "", fd_questionsNumber = "", fd_timeApproached = "";

    Button btnSalir;
    Button btnVolverAJugar;
    CardView cvPopup;
    RelativeLayout rl_root;
    TextView tvScore;

    MediaPlayer mp;

    public finishDialog() {
        // Required empty public constructor
    }

    public static finishDialog newInstance(String aciertos, String questionsNumber, String timeApproached) {
        finishDialog fd = new finishDialog();
        fd_aciertos = aciertos;
        fd_questionsNumber = questionsNumber;
        fd_timeApproached = timeApproached;
        return fd;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dialog_finish, container, false);
        cvPopup = view.findViewById(R.id.cv_finish_popup);
        cvPopup.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.uptodown));

        tvScore = view.findViewById(R.id.tv_score);

        btnSalir = view.findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salir();
            }
        });

        btnVolverAJugar = view.findViewById(R.id.btn_volver_a_jugar);
        btnVolverAJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeThisFragment();
                getActivity().recreate();
            }
        });

        rl_root = view.findViewById(R.id.rl_finish_dialog_root);
        rl_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        reproducirAudio();
        setScore();
        return view;
    }

    public void setScore() {
        tvScore.setText(fd_aciertos + " de " + fd_questionsNumber + " en " + fd_timeApproached);
    }

    private void salir() {
        getActivity().finish();
        getActivity().moveTaskToBack(true);
    }

    private void reproducirAudio() {

        if (mp != null) mp.release();
        mp = MediaPlayer.create(this.getContext(), R.raw.bubblegumpuzzler);
        mp.setLooping(true);
        mp.start();
    }

    @Override
    public void onStop() {
        mp.stop();
        super.onStop();
    }

    private void removeThisFragment() {
        mp.stop();
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
