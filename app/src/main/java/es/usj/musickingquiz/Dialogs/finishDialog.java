package es.usj.musickingquiz.Dialogs;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import es.usj.musickingquiz.R;


public class finishDialog extends Fragment {


    private static String fd_aciertos = "", fd_questionsNumber = "", fd_timeApproached = "";

    Button btnSalir;
    Button btnVolverAJugar;
    RelativeLayout rl_root;
    TextView tvScore;

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
                Intent i = getActivity().getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().finish();
                startActivity(i);
            }
        });

        rl_root = view.findViewById(R.id.rl_finish_dialog_root);
        rl_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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
}
