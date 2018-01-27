package np.com.nirbhay.musicaid.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import np.com.nirbhay.musicaid.R;
import np.com.nirbhay.musicaid.active_android.HappySongModel;
import np.com.nirbhay.musicaid.active_android.SadSongModel;
import np.com.nirbhay.musicaid.data_set.MusicDescription;

import static android.support.v4.content.res.ResourcesCompat.getDrawable;

/**
 * Created by sushant on 1/23/2018 at 9:17 AM.
 */

public class MainActivityRecyclerViewAdapter extends RecyclerView.Adapter<MainActivityRecyclerViewAdapter.ViewHolder> {
    private Context context;
    public static Context c;
    public static ArrayList<MusicDescription> mData;
    public static MediaPlayer mediaPlayer;
    private int FLAG;
    public static int playingPosition = -1;

    public MainActivityRecyclerViewAdapter(Context context, ArrayList<MusicDescription> data, int FLAG) {
        this.context = context;
        MainActivityRecyclerViewAdapter.c = context;
        mData = data;
        this.FLAG = FLAG;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recycler_music_description, parent, false);
        mediaPlayer = MediaPlayer.create(c, Uri.parse(mData.get(0).getMusicData()));
        mediaPlayer.setLooping(true);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView textView = holder.mTextView;
        ImageView imageView = holder.mImageView;
        final String musicDescription = mData.get(position).getMusicDescription();
        Bitmap albumArt = mData.get(position).getAlbumArt(context);
        if(albumArt !=null){
            imageView.setImageBitmap(albumArt);
        }else{
            imageView.setImageDrawable(getDrawable(context.getResources(), R.drawable.ic_audiofile,null));
        }
        textView.setText(musicDescription);
        final int finalPosition = holder.getAdapterPosition();
        final String path = mData.get(finalPosition).getMusicData();
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.err.println(finalPosition);
                startMusic(finalPosition);
            }
        });
        holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                switch (FLAG) {
                    case 1:
                        new SadSongModel().deleteData(mData.get(finalPosition).getMusicData());
                    case 2:
                        new HappySongModel().deleteData(mData.get(finalPosition).getMusicData());
                }
                mData.remove(finalPosition);
                notifyItemRemoved(finalPosition);
                notifyItemRangeChanged(finalPosition, getItemCount());
                mediaPlayer.release();
                return true;
            }
        });
    }

    public static void startMusic(int position) {
        if (mediaPlayer != null) {
            releasePlayer();
        }
        mediaPlayer = MediaPlayer.create(c, Uri.parse(mData.get(position).getMusicData()));
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        playingPosition = position;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void add(int position, MusicDescription item) {
        mData.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public static void releasePlayer() {
        try{
            mediaPlayer.release();
        }catch (Exception ignored){}
    }

    public static void nextSong() {
        int size = mData.size();
        size--;
        if (playingPosition == size) {
            playingPosition = 0;
        } else {
            playingPosition++;
        }
        startMusic(playingPosition);
        //TODO NEXT SONG
    }

    public static void previousSong() {
        int size = mData.size();
        size--;
        if (playingPosition == 0) {
            playingPosition = size;
        } else {
            playingPosition--;
        }
        startMusic(playingPosition);
        //TODO PREVIOUS SONG
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView mCardView;
        TextView mTextView;
        ImageView mImageView;

        ViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.cardViewMusicMain);
            mImageView = (ImageView) itemView.findViewById(R.id.imageAlbumArt);
            mTextView = (TextView) itemView.findViewById(R.id.textMusicDescription);
        }
    }
}