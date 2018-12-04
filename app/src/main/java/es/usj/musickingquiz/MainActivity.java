package es.usj.musickingquiz;

import android.app.DownloadManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.AnimationRes;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

@EActivity
public class MainActivity extends AppCompatActivity {

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
        super.onStop();
        mp.stop();
    }

}







