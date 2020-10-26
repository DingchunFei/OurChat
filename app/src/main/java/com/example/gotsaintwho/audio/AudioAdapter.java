package com.example.gotsaintwho.audio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gotsaintwho.R;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {
    //create Adapter
    private File[] allAudioFiles;
    public AudioAdapter(File[] allFile){
        //assign the vale file from adpater
        this.allAudioFiles = allFile;
    }
    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate view holder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_audio_item,parent,false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        //get and assign value to the single audio item
        //name of the file
        holder.item_title.setText(allAudioFiles[position].getName());
        holder.item_date.setText(allAudioFiles[position].lastModified() +"");

    }

    @Override
    public int getItemCount() {
        //total length of file in the array
        return allAudioFiles.length;
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder {
    //get all the single audio item
        private ImageView item_button_image ;
        private TextView item_title;
        private TextView item_date;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            //initialize all the single audio item
            item_button_image = itemView.findViewById(R.id.single_item_button_image);
            item_title = itemView.findViewById(R.id.single_item_title);
            item_date = itemView.findViewById(R.id.single_item_date);
        }

    }

}
