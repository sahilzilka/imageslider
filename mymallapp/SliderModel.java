package com.example.mymallapp;

import java.util.List;
public class SliderModel {

    // string for our image url.
    private String imgUrl;

    // empty constructor which is
    // required when using Firebase.
    public SliderModel() {
    }

    // Constructor
    public SliderModel(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    // Getter method.
    public String getImgUrl() {
        return imgUrl;
    }


    // Setter method.
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
