package es.usj.musickingquiz.Fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import es.usj.musickingquiz.R;

public class GameFragment extends Fragment {


    View view;
    TextView tvCountDown;
    private RadioButton rbtn1;
    private RadioButton rbtn2;
    private RadioButton rbtn3;
    private RadioGroup rgOptions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_game, container, false);
        tvCountDown = view.findViewById(R.id.tvCountDown);


        rgOptions = view.findViewById(R.id.rgOptions);

        rbtn1 = view.findViewById(R.id.rbtn1);
        rbtn2 = view.findViewById(R.id.rbtn2);
        rbtn3 = view.findViewById(R.id.rbtn3);


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

    public void setRadioButtons(String option1, String option2, String option3) {
        rbtn1.setText(option1);
        rbtn2.setText(option2);
        rbtn3.setText(option3);
    }

    public String getSelectedOption() {
        int rbID = rgOptions.getCheckedRadioButtonId();
        RadioButton rbSeleccted = view.findViewById(rbID);
        String selectedString = "";
        if (rbSeleccted != null) selectedString = rbSeleccted.getText().toString();
        else selectedString = "";
        return selectedString;
    }
}
