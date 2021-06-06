package com.example.mymallapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class myadapter extends RecyclerView.Adapter<myadapter.myviewholder> {

    List<CustomModel> imagesList;
    Context context;
    public myadapter(Context context, List<CustomModel> imagesList) {
        this.imagesList = imagesList;
        this.context = context;
    }

    @NonNull
    @Override
    public myadapter.myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerowlayout,parent,false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myadapter.myviewholder holder, int position) {
        holder.imageName.setText(imagesList.get(position).getImagesName());
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, imagesList.get(position).getImagesName()+" removed!", Toast.LENGTH_SHORT).show();
                imagesList.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public class myviewholder extends RecyclerView.ViewHolder{
       TextView imageName;
       ImageButton btnRemove;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            imageName = itemView.findViewById(R.id.txtImageName);
            btnRemove = itemView.findViewById(R.id.btnRemoveImage);


        }
    }
}
