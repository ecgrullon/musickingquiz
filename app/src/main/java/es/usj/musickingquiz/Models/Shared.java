package es.usj.musickingquiz.Models;

import java.util.ArrayList;

public class Shared {

    public static boolean areAllSongsDownloaded = false;
    public static ArrayList<Songs> songsList = null;

    public static void reset() {
        areAllSongsDownloaded = false;
        songsList = null;
    }
}
