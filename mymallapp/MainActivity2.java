package com.example.mymallapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity2 extends AppCompatActivity {

    private static final int READ_PERMISSION_CODE = 69;
    private static final int PICK_IMAGE_REQUEST_CODE = 9;

    ImageView no_images;
    FloatingActionButton btnPickImages , btnUploadImages;
    RecyclerView recyclerView;
    List<CustomModel> imagesList;
    List<String> savedimagesUrl;
    myadapter adapter;
    CoreHelper coreHelper;
    FirebaseStorage storage;
    FirebaseFirestore firestore;
    CollectionReference reference;
    Button show_image_slider;
    int counter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firestore =  FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        reference = firestore.collection("userimages");

        savedimagesUrl = new ArrayList<>();

        no_images = findViewById(R.id.no_image);
        btnPickImages =findViewById(R.id.fabChooseImage);
        btnUploadImages = findViewById(R.id.fabUploadImage);
        imagesList = new ArrayList<>();
        coreHelper = new CoreHelper(this);
        show_image_slider =(Button)findViewById(R.id.button2);
        
        //code to show the list of images

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new myadapter(this,imagesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        // this code will set the adapter
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
            @Override
            public void onChanged() {
                super.onChanged();
                if(adapter.getItemCount() != 0){
                    no_images.setVisibility(View.GONE);
                }else{
                    no_images.setVisibility(View.VISIBLE);
                }
            }
        });

        //code to show the list of images

        btnPickImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPermissionAndPickImages();
            }
        });
        btnUploadImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImages(v);
            }
        });
        
        show_image_slider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openImageSliderActivity();
            }
        });



    }

    private void openImageSliderActivity() {
        Intent intent = new Intent(this, showImageActivity.class);
        startActivity(intent);
    }

    private void uploadImages(View v) {
        if((imagesList.size() != 0)) {
            final ProgressDialog progressDialog =new ProgressDialog(this);
            progressDialog.setMessage("uplaoded 0/" + imagesList.size());
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            final StorageReference storageReference = storage.getReference();
            for(int i= 0; i<imagesList.size();i++){
                final int index = i;
                storageReference.child("UserImages/").child(imagesList.get(i).getImagesName()).putFile(imagesList.get(i).getImagesUri()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {
                            storageReference.child("UserImages/").child(imagesList.get(index).getImagesName()).getDownloadUrl()
                                    .addOnCompleteListener(new OnCompleteListener<Uri>(){

                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            counter ++;
                                            progressDialog.setMessage("Uploaded" + counter + imagesList.size());
                                            if(task.isSuccessful()){
                                                savedimagesUrl.add(task.getResult().toString());
                                            }else{
                                                //this is to delete the image if the download url is not complete
                                                storageReference.child("UserImages/").child(imagesList.get(index).getImagesName()).delete();
                                                Toast.makeText(MainActivity2.this, "Could'nt save "+imagesList.get(index).getImagesName(), Toast.LENGTH_SHORT).show();
                                            }
                                            if(counter == imagesList.size()){
                                                saveImageDataToFirestore(progressDialog);
                                            }

                                        }
                                    });

                        }else{
                            progressDialog.setMessage("Uploaded" +counter+ "/" +imagesList.size());
                            counter++;
                            Toast.makeText(MainActivity2.this, "could'nt upload"+imagesList.get(index).getImagesName(), Toast.LENGTH_SHORT).show();


                        }
                    }
                });

            }
        }else{
            coreHelper.createSnackBar(v, "Please add some images first.", "", null, Snackbar.LENGTH_SHORT);
        }

    }

    private void saveImageDataToFirestore(final ProgressDialog progressDialog) {
        progressDialog.setMessage("Saving uploaded images...");
        Map<String, Object> dataMap = new HashMap<>();
        //Below line of code will put your images list as an array in fireStore
        dataMap.put("images",savedimagesUrl);

        reference.document().set(dataMap)
                .addOnSuccessListener(new OnSuccessListener<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        progressDialog.dismiss();
                        coreHelper.createAlert("Success", "Images uploaded and saved successfully!", "OK", "", null, null, null);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                coreHelper.createAlert("Error", "Images uploaded but we couldn't save them to database.", "OK", "", null, null, null);
                Log.e("MainActivity:SaveData", e.getMessage());

            }
        });


    }

    private void verifyPermissionAndPickImages() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                pickimages();
            }else{
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_PERMISSION_CODE);
            }
            // no need to check permissions bellow marshmallow
            pickimages();
        }
    }

    private void pickimages() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(intent,PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_PERMISSION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickimages();
                }else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_IMAGE_REQUEST_CODE:
                if(resultCode == RESULT_OK && data!=null){
                    ClipData clipData = data.getClipData();
                    if(clipData != null){
                        for(int i = 0;i<clipData.getItemCount();i++){
                            Uri uri = clipData.getItemAt(i).getUri();
                            imagesList.add(new CustomModel(coreHelper.getfileNameFromUri(uri),uri));
                            adapter.notifyDataSetChanged();
                        }
                    }else {
                        // this is if the user has selected only one image
                        Uri uri = data.getData();
                        imagesList.add(new CustomModel(coreHelper.getfileNameFromUri(uri), uri));
                        adapter.notifyDataSetChanged();
                    }
                }
        }
    }

}