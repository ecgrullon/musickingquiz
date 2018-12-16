package es.usj.musickingquiz;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.EActivity;

import es.usj.musickingquiz.Dialogs.SureDialog;


@EActivity
public class QuizPage extends AppCompatActivity implements SureDialog.NoticeDialogListener {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_page);
    }

    @Override
    public void onBackPressed() {
        //mp.pause();
        SureDialog sureDialog = new SureDialog();

        sureDialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        finish();
        moveTaskToBack(true);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //mp.start();
    }
}
