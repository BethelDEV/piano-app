package funs.games.page;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.piano.Preferences;
import funs.games.R;
import funs.games.MelodyHelper;
import funs.games.PianoConst;
import funs.games.bean.SongFile;

/**
 * {@link PianoActivity} Settings.
 * create an instance of this fragment.
 */
public class GfSettingsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gf_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initKeyBoardStyle(view);
        initKeyMelodyGuide(view);
        initSongTv(view);
//        TextView tv = view.findViewById(R.id.textView);
//        for (int i = 0; i < 99; i++) {
//            tv.append("\n");
//            tv.append(String.valueOf(i));
//        }
    }

    private void initSongTv(View view) {
        TextView nameTv = view.findViewById(R.id.song_name_tv);
        TextView titleTv = view.findViewById(R.id.song_title_tv);

        String selectedSongId = Preferences.getSelectedSongId(getContext());
        if (selectedSongId != null && selectedSongId.startsWith(MelodyHelper.LOCAL_SONG_PATH)) {
            SongFile song = MelodyHelper.getLocalSong(selectedSongId);
            if (song != null) nameTv.setText(song.getName());
        }

        View.OnClickListener clickListener = v -> {
            SongsActivity.startMe(getActivity());
            getActivity().finish();
        };
        nameTv.setOnClickListener(clickListener);
        titleTv.setOnClickListener(clickListener);
    }

    private void initKeyMelodyGuide(View view) {
        SwitchCompat guide = view.findViewById(R.id.switch_guide);
        guide.setChecked(Preferences.areMelodiesEnabled(getContext()));

        guide.setOnCheckedChangeListener((compoundButton, b) -> {
            Preferences.setMelodiesEnabled(getContext(), b);
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(PianoConst.ACTION_MELODY_GUIDE_MODE_CHANGED));
        });
    }

    private void initKeyBoardStyle(View view) {
        final RadioGroup group = view.findViewById(R.id.rg_kb_style);

        final int kbStyle = Preferences.getKeyboardStyle(getContext());
        final int btnId = (kbStyle == Preferences.KEYBOARD_STYLE_CLASSICAL) ? R.id.rb_kb_classical : R.id.rb_kb_colorful;
        RadioButton rBtn = group.findViewById(btnId);
        rBtn.setChecked(true);

        group.setOnCheckedChangeListener((radioGroup, i) -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            final int style = checkedId == R.id.rb_kb_classical ? Preferences.KEYBOARD_STYLE_CLASSICAL : Preferences.KEYBOARD_STYLE_COLORFUL;
            Preferences.setKeyboardStyle(getContext(), style);
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(PianoConst.ACTION_KEYBOARD_STYLE_CHANGED));

//            RadioButton button = radioGroup.findViewById(checkedId);
//            CUtils.toast(getContext(), button.getText());
        });

        View.OnClickListener clickListener = v -> {
            int rbID = 0;
            final int imgId = v.getId();
            if (imgId == R.id.img_classical) {
                rbID = R.id.rb_kb_classical;
            } else if (imgId == R.id.img_colorful) {
                rbID = R.id.rb_kb_colorful;
            }

            if (rbID != 0) {
                RadioButton rb = group.findViewById(rbID);
                if (null != rb) rb.setChecked(true);
            }
        };

        view.findViewById(R.id.img_classical).setOnClickListener(clickListener);
        view.findViewById(R.id.img_colorful).setOnClickListener(clickListener);
    }
}