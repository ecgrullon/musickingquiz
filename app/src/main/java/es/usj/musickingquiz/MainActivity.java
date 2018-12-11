package es.usj.musickingquiz;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.AnimationRes;

import es.usj.musickingquiz.Fragments.LoadingFragment;

@EActivity
public class MainActivity extends AppCompatActivity implements LoadingFragment.OnFinishedLoadListener {

    //Variables
    @ViewById
    LinearLayout l1;
    @ViewById
    LinearLayout l2;

    @ViewById
    ImageView ivHuman;

    @ViewById
    Button btnPlay;

    @AnimationRes
    Animation uptodown;
    @AnimationRes
    Animation downtoup;

    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoadingFragment loadingFragment = new LoadingFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_root_layout, loadingFragment).commit();

        l1.setAnimation(uptodown);
        l2.setAnimation(downtoup);
        ivHuman.setAnimation(downtoup);

    }

    @Click(R.id.btnPlay)
    public void play(){
        Intent intent = new Intent(this, QuizPage_.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mp = MediaPlayer.create(this, R.raw.bubblegumpuzzler);
        mp.setLooping(true);
        mp.start();
    }

    @Override
    protected void onStop() {
        mp.stop();
        super.onStop();

    }

    @Override
    public void onFinishedLoad() {
        btnPlay.setEnabled(true);
    }
}







