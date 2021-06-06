package com.example.mymallapp;

import android.net.Uri;

public class CustomModel {

    String imagesName;
    Uri ImagesUri;

    public CustomModel(){

    }

    public CustomModel(String imageName, Uri imageUri) {
        this.imagesName = imageName;
        ImagesUri = imageUri;
    }

    public String getImagesName() {
        return imagesName;
    }

    public Uri getImagesUri() {
        return ImagesUri;
    }
}

