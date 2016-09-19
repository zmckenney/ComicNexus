package com.zacmckenney.comicnexus.models;

/**
 * Created by Zac on 9/12/16.
 */
public class SearchResult {

    private int id;
    private String title;
    private int startYear;
    private String type;
    private String url;

    private Creators[] creators;


    public Creators[] getCreators() {
        return creators;
    }

    public void setCreators(Creators[] creators) {
        this.creators = creators;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
