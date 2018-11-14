package es.usj.musickingquiz;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


@EActivity
public class QuizPage extends AppCompatActivity {


    @ViewById
    TextView tvCountDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_page);

        new CountDownTimer(20000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvCountDown.setText("" + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }
            public void onFinish() {
                tvCountDown.setText("done!");
            }
        }.start();

    }
}
