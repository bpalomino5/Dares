package com.palomino.mrbp.dares;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.UiThread;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mr.bp on 12/5/15.
 */
public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;

    private List<Dare> data = Collections.emptyList();
    private final int VIDEO = 0, TEXT = 1;

    public MyAdapter(Context context, ArrayList<Dare> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) instanceof VideoDare) {
            return VIDEO;
        } else if (data.get(position) instanceof TextDare) {
            return TEXT;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case VIDEO:
                CardView videoCard = (CardView) inflater.inflate(R.layout.custom_cards, parent, false);
                viewHolder = new VideoViewHolder(videoCard);
                break;
            case TEXT:
                CardView textCard = (CardView) inflater.inflate(R.layout.text_dare_cards, parent, false);
                viewHolder = new TextViewHolder(textCard);
                break;
            default:
                CardView simpleCard = (CardView) inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                viewHolder = new SimpleViewHolder(simpleCard);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIDEO:
                VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
                configureVideoDare(videoViewHolder, position);
                break;
            case TEXT:
                TextViewHolder textViewHolder = (TextViewHolder) holder;
                configureTextDare(textViewHolder, position);
                break;
            default:
                SimpleViewHolder simpleViewHolder = (SimpleViewHolder) holder;
                configureSimpleDare(simpleViewHolder, position);
                break;
        }
    }

    private void configureSimpleDare(SimpleViewHolder simpleViewHolder, int position) {
        //not sure what to put here
        Log.d("MyAdapter", "configureSimpleDare");
    }

    private void configureTextDare(TextViewHolder holder, int position) {
        TextDare current = (TextDare) data.get(position);

        //get profile image and set it
        Drawable drawable = holder.profileImage.getRootView().getResources().getDrawable(current.profileDrawableId);
        holder.profileImage.setImageDrawable(drawable);

        //get profile name, set it
        holder.profileName.setText(current.profileName);

        //set post description
        holder.postDescription.setText(current.postDescription);
    }

    private void configureVideoDare(final VideoViewHolder holder, int position) {
        VideoDare current = (VideoDare) data.get(position);

        //        videoView.setVideoPath("http://techslides.com/demos/sample-videos/small.mp4");
//        holder.videoView.setVideoURI(Uri.parse("android.resource://" + holder.videoView.getRootView().getContext().getPackageName() + "/" + R.raw.small));
        holder.videoView.setVideoURI(Uri.parse("http://192.168.2.23:8888/dares/uploads/video.mp4"));
        MediaController mediaController = new MediaController(holder.videoView.getContext());
        mediaController.setAnchorView(holder.videoView);
        holder.videoView.setMediaController(mediaController);

        //listener for completion to set button visibility back on
        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                holder.playVideoButton.setVisibility(View.VISIBLE);
            }
        });

        //listener for button click to start video and set button invisible during its duration
        holder.playVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.INVISIBLE);
                holder.videoView.start();
            }
        });

        //get profile image and set it
        Drawable drawable = holder.profileImage.getRootView().getResources().getDrawable(current.profileDrawableId);
        holder.profileImage.setImageDrawable(drawable);

        //get profile name, set it
        holder.profileName.setText(current.profileName);

        //get video description, set it
        holder.description.setText(current.description);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageButton playVideoButton;
        VideoView videoView;
        ImageView profileImage;
        TextView profileName;
        TextView description;


        public MyViewHolder(CardView itemView) {
            super(itemView);
            playVideoButton = (ImageButton) itemView.findViewById(R.id.startVideoButton);
            videoView = (VideoView) itemView.findViewById(R.id.video_view);
            profileImage = (ImageView) itemView.findViewById(R.id.poster_profile_pic);
            profileName = (TextView) itemView.findViewById(R.id.poster_box);
            description = (TextView) itemView.findViewById(R.id.description_box);
        }
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageButton playVideoButton;
        VideoView videoView;
        ImageView profileImage;
        TextView profileName;
        TextView description;

        public VideoViewHolder(CardView itemView) {
            super(itemView);
            playVideoButton = (ImageButton) itemView.findViewById(R.id.startVideoButton);
            videoView = (VideoView) itemView.findViewById(R.id.video_view);
            profileImage = (ImageView) itemView.findViewById(R.id.poster_profile_pic);
            profileName = (TextView) itemView.findViewById(R.id.poster_box);
            description = (TextView) itemView.findViewById(R.id.description_box);
        }
    }

    class TextViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView profileName;
        TextView postDescription;

        public TextViewHolder(CardView itemView) {
            super(itemView);
            profileImage = (ImageView) itemView.findViewById(R.id.textdare_profile_pic);
            profileName = (TextView) itemView.findViewById(R.id.textdare_posterid);
            postDescription = (TextView) itemView.findViewById(R.id.textdare_post);
        }
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        public SimpleViewHolder(CardView itemView) {
            super(itemView);
        }
    }

    @UiThread
    public void clear() {
        data.clear();
//        notifyDataSetChanged();
    }

    // Add a list of items
    @UiThread
    public void addAll(List<Dare> dares) {
        data.addAll(dares);
        notifyDataSetChanged();
    }

    public void add(int profileDrawableId, String post, String username){
        data.add(new TextDare(profileDrawableId, username, post));
        notifyItemInserted(data.size()-1);
    }

}
