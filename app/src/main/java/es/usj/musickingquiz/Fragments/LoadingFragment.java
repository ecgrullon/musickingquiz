package es.usj.musickingquiz.Fragments;


import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.usj.musickingquiz.Models.Settings;
import es.usj.musickingquiz.Models.Shared;
import es.usj.musickingquiz.Models.Songs;
import es.usj.musickingquiz.R;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class LoadingFragment extends Fragment {

    private TextView tvDownloading;

    ProgressBar progressBar;
    private long downloadID;
    private Settings settings = new Settings();
    private Shared shared = new Shared();
    private DownloadManager downloadManager;
    private OnFinishedLoadListener mListener;
    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            //que hacer cuando se complete una descarga
            inicializar();

        }
    };


    public LoadingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_loading, container, false);
        progressBar = v.findViewById(R.id.progressBar);

        tvDownloading = v.findViewById(R.id.tv_downloading);
        downloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
        getContext().registerReceiver(onComplete, new IntentFilter(downloadManager.ACTION_DOWNLOAD_COMPLETE));

        inicializar();

        return v;
    }

    private void inicializar() {
        if (shared.songsList == null) {
            if (checkIfFileExists(settings.jsonFileName))
                setSongsList();
            else
                downloadJsonFile();
        } else if (AreAllSongsDownloaded()) {
            getContext().unregisterReceiver(onComplete);
            doFinishedLoadCallback();
            shared.areAllSongsDownloaded = true;
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();

        }


    }

    protected void downloadJsonFile() {

        try {
            Uri Download_Uri = Uri.parse(settings.jsonFileURL);
            DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            request.setDestinationInExternalFilesDir(this.getContext(), DIRECTORY_DOWNLOADS, settings.jsonFileName);
            downloadManager.enqueue(request);
        } catch (Exception e) {
            tvDownloading.setText(getString(R.string.server_connection_failed));
        }

    }

    private void setSongsList() {
        Gson gson = new Gson();
        String jsonString = null;


        try {
            File file = this.getContext().getExternalFilesDir(DIRECTORY_DOWNLOADS);

            Uri uri = Uri.withAppendedPath(Uri.fromFile(file), settings.jsonFileName);
            InputStream is = getContext().getContentResolver().openInputStream(uri);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");


            Songs[] songs = gson.fromJson(jsonString, Songs[].class);

            ArrayList fullSongsList = new ArrayList(Arrays.asList(songs));
            Collections.shuffle(fullSongsList);

            List<Songs> tenSongs = fullSongsList.subList(0, settings.numberOfSongsToPlay);
            shared.songsList = new ArrayList(tenSongs);
            inicializar();
        } catch (Exception e) {
            Log.d("ERROR:", e.getMessage() + e.getCause());
        }
    }

    private boolean checkIfDownloadStatusIsCompleted() {

        boolean isCompleted = false;
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadID);
        Cursor cursor = downloadManager.query(query);

        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            cursor.close();

            if (status == DownloadManager.STATUS_SUCCESSFUL) isCompleted = true;
        }

        return isCompleted;
    }

    private void downloadSong(String songFileName) {
        try {
            Uri Download_Uri = Uri.withAppendedPath(Uri.parse(settings.resourcesURL), songFileName);
            DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            request.setDestinationInExternalFilesDir(this.getContext(), DIRECTORY_DOWNLOADS, songFileName);
            downloadID = downloadManager.enqueue(request);
        } catch (Exception e) {
//            Toast.makeText(this.getContext(), "No se ha podido descargar algunos de los archivos", Toast.LENGTH_LONG).show();
            tvDownloading.setText(getString(R.string.files_download_failed));
        }
    }

    private boolean checkIfFileExists(String fileName) {
        File file = new File(this.getContext().getExternalFilesDir(DIRECTORY_DOWNLOADS), fileName);
        if (file.exists()) return true;
        else return false;

    }


    private boolean AreAllSongsDownloaded() {
        progressBar.setProgress(settings.getPercentOfCompletionPerFile());
        for (Songs song : shared.songsList) {
            if (!checkIfFileExists(song.file)) {
                downloadSong(song.file);
                return false;
            } else {
                progressBar.setProgress(progressBar.getProgress() + settings.getPercentOfCompletionPerFile());
            }
        }
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFinishedLoadListener) {
            mListener = (OnFinishedLoadListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void doFinishedLoadCallback() {
        if (mListener != null) {
            mListener.onFinishedLoad();
        }
    }

    public interface OnFinishedLoadListener {
        public void onFinishedLoad();
    }

}
