package com.example.mymallapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class showImageActivity extends AppCompatActivity {

    // creating variables for our adapter, array list,
    // firebase firestore and our sliderview.


    private SliderAdapter adapter;
    private ArrayList<SliderModel> sliderDataArrayList;
    private SliderView sliderView;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);


        // creating a new array list fr our array list.
        sliderDataArrayList = new ArrayList<>();
        // initializing or slider view and
        // firebase firestore instance.
        sliderView = findViewById(R.id.slider);
        db = FirebaseFirestore.getInstance();

        // calling our method to load images.
        loadImages();
    }

    private void loadImages() {
        // getting data from our collection and after
        // that calling a method for on success listener.


        CollectionReference userImagesRef =db.collection("userimages");
                DocumentReference docRef = userImagesRef.document("ZubVqRAS7T1ojq8v2qPo");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot =task.getResult();
                    if(documentSnapshot.exists()){
                        SliderModel model = documentSnapshot.toObject(SliderModel.class);
                        SliderModel slideModel = new SliderModel();
                        slideModel.setImgUrl(model.getImgUrl());
                        sliderDataArrayList.add(slideModel);
                        adapter = new SliderAdapter(showImageActivity.this,sliderDataArrayList);
                        sliderView.setSliderAdapter(adapter);
                        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);

                        // below line is for setting auto cycle duration.
                        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);

                        // below line is for setting
                        // scroll time animation
                        sliderView.setScrollTimeInSec(3);

                        // below line is for setting auto
                        // cycle animation to our slider
                        sliderView.setAutoCycle(true);

                        // below line is use to start
                        // the animation of our slider view.
                        sliderView.startAutoCycle();
                        Log.d(TAG, " such document",task.getException());





                    }else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
                }


        });


            }


   /* .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (QueryDocumentSnapshot document : task.getResult()) {


                            // after we get the data we are passing inside our object class.

                            sliderDataArrayList.add(new SlideModel(document.getData().toString(), ScaleTypes.FIT));
                            imageSlider.setImageList(sliderDataArrayList,ScaleTypes.FIT);


                        }

                    }
*/



}