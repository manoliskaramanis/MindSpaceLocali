package gr.ntua.tutorials.mindspacelocali.model;

/**
 * Created by manoliskaramanis on 23/11/16.
 */

public class Place {

    private String placeID;
    private String name;
    private String formattedCategories;
    private Double rating;
    private Double price;
    private Integer  openNow; //0 = close, 1 = open, 2 = posiiby open
    private Double distance;
    private String locality;
    private String formattedAddress;
    private Double latitude;
    private Double longitude;

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getFormattedCategories() {
        return formattedCategories;
    }

    public void setFormattedCategories(String formattedCategories) {
        this.formattedCategories = formattedCategories;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Integer openNow) {
        this.openNow = openNow;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
