package es.usj.musickingquiz;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import es.usj.musickingquiz.Models.Shared;

@EActivity
public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, LoadingFragment.OnFinishedLoadListener {

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
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        verificarPermisos();

    }

    @Click(R.id.btnPlay)
    public void play(){
        Intent intent = new Intent(this, QuizPage_.class);
        startActivity(intent);
    }

    private void iniciarCarga() {
        LoadingFragment loadingFragment = new LoadingFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_root_layout, loadingFragment).commit();
    }

    @Override
    protected void onStart() {

        Shared.reset();

        l1.setAnimation(uptodown);
        l2.setAnimation(downtoup);
        ivHuman.setAnimation(downtoup);

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


    private void verificarPermisos() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

        } else {
            iniciarCarga();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    iniciarCarga();
                    // permission was granted, yay!

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}







