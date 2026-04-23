package funs.games.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import funs.games.R;
import funs.games.bean.SongFile;

/**
 * @ProjectName: pianoli_HG
 * @Package: funs.games.adapter
 * @ClassName: SongsAdapter
 * @Description: RecyclerView  Adapter
 */
public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.VH> {

    private final List<SongFile> mSongs;// = PianoConst.localSongs;
    private String selectedSongId;

    public SongsAdapter(Context context, List<SongFile> songs, String selectedId) {
        mSongs = songs;
        this.selectedSongId = selectedId;//Preferences.getSelectedSongId(context);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        SongFile song = mSongs.get(position);
        holder.nameTv.setText(song.getName());

        int visibility = TextUtils.equals(selectedSongId, song.getId()) ? View.VISIBLE : View.INVISIBLE;
        holder.checkImg.setVisibility(visibility);

        holder.itemView.setOnClickListener(view -> {
            if (TextUtils.equals(selectedSongId, song.getId())) return;

            selectedSongId = song.getId();
            // notify activity
            if (null != itemClickListener) itemClickListener.onItemClicked(selectedSongId);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return null != mSongs ? mSongs.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        private final ImageView checkImg;
        private final TextView nameTv;

        public VH(@NonNull View itemView) {
            super(itemView);
            checkImg = itemView.findViewById(R.id.imageViewCheckMark);
            nameTv = itemView.findViewById(R.id.textView);
        }
    }

    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClicked(String songId);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
