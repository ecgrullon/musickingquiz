package es.usj.musickingquiz.Models;

public class Settings {

    //TODO asociar esta clase a un archivo de configuración editable
    public static final int answersNumber = 4;

    public static final String serverURL = "http://10.0.2.2:8080/";
    public static final String resourcesURL = "http://10.0.2.2:8080/MusicQuiz/resources/music";
    public static final String jsonFileURL = "http://10.0.2.2:8080/MusicQuiz/api/songs";
    public static final String jsonFileName = "songs.json";
    public static final int numberOfSongsToPlay = 10;

    public static int getPercentOfCompletionPerFile() {
        return 100 / (numberOfSongsToPlay + 1);
    }
}
