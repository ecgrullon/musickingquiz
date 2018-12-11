package es.usj.musickingquiz.Models;

public class Songs {
    public int id;
    public String name;
    public String author;
    public String file;


    public String getTitleAndAuthor() {
        return name + "/" + author;
    }
}
