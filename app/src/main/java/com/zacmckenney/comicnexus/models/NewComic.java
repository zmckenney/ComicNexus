package com.zacmckenney.comicnexus.models;

/**
 * Created by Zac on 9/10/16.
 */
public class NewComic {
    private int id;
    private String title;
    private int pageCount;
    private double price;

    private String thumbnailPath;
    private String thumbnailXLargeAppend = "/portrait_uncanny.";

    private int issueNumber;
    private String description;
    private String onSaleDate;
    private String[] creators;
    private String detailUrl;


    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String[] getCreators() {
        return creators;
    }

    public void setCreators(String[] creators) {
        this.creators = creators;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(int issueNumber) {
        this.issueNumber = issueNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOnSaleDate() {
        return onSaleDate;
    }

    public void setOnSaleDate(String onSaleDate) {
        this.onSaleDate = onSaleDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailParameters(String thumbnailPath, String thumbnailExtension) {
        this.thumbnailPath = thumbnailPath + thumbnailXLargeAppend + thumbnailExtension;
    }

    public void setThumbnailFromDB(String thumbnailPath){
        this.thumbnailPath = thumbnailPath;
    }
}
