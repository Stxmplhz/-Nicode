package com.example.nicode.BottomFragment;

import static java.lang.Math.ceil;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.nicode.Activity.Constants;
import com.example.nicode.Activity.PreferenceManager;
import com.example.nicode.R;
import com.example.nicode.databinding.FragmentHealthBinding;
import com.example.nicode.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.smarteist.autoimageslider.SliderView;

public class HealthFragment extends Fragment {

    private FirebaseFirestore Fdatabase;
    private PreferenceManager preferenceManager;
    private int LungProgress, AbilityProgress, MoneyProgress,NumStopRoll_now,NumStopRoll_want
            ,MoneySaveNow,MoneyThatwanttoSave,Dayfromstart,Day,LungProgress_new,MoneyProgress_new,AbilityProgress_new,AbilityProgress_2,AbilityProgress_progress,LungProgress_progress;
    private String StopRoll_now,StopRoll_want,AllDayFromStart_Str;
    private FragmentHealthBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHealthBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Fdatabase = FirebaseFirestore.getInstance();

        Context thiscontext = container.getContext();

        preferenceManager = new PreferenceManager(thiscontext);

        binding.nicocoinInbar.setText(String.valueOf(preferenceManager.getInt(Constants.KEY_NICOCOIN)));
        binding.smokinglyzerValue.setText(String.valueOf(preferenceManager.getInt(Constants.KEY_SMOKINGLYZER)));

        NumStopRoll_now = preferenceManager.getInt(Constants.KEY_CIGARETTESTOPROLL);
        NumStopRoll_want = preferenceManager.getInt(Constants.KEY_CIGARETTEROLLPERMONTH);
        Dayfromstart = preferenceManager.getInt(Constants.KEY_DAYFROMSTART);
        if (Dayfromstart == 0){
            Day = 1;
        }else {
            Day = Dayfromstart;
        }

        LungProgress = (int) Math.ceil(NumStopRoll_now/ NumStopRoll_want) * 100;
        LungProgress_new = (int) Math.ceil(LungProgress);
        binding.progressBarLung.setProgress(NumStopRoll_now);
        binding.progressBarLung.setMax(NumStopRoll_want);
        LungProgress_progress = (int) Math.ceil(binding.progressBarLung.getProgress());

        AbilityProgress = (NumStopRoll_want/31)*Day;;
        AbilityProgress_new = (int) Math.ceil(NumStopRoll_now/AbilityProgress) *100;
        binding.progressBarAbility.setProgress(NumStopRoll_now);
        binding.progressBarAbility.setMax(AbilityProgress);
        AbilityProgress_progress = (int) Math.ceil(binding.progressBarAbility.getProgress());

        MoneySaveNow = (NumStopRoll_now * 70) / 20;
        MoneyThatwanttoSave = (NumStopRoll_want * 70) / 20;
        MoneyProgress = (MoneySaveNow/MoneyThatwanttoSave) * 100;
        binding.progressBarBath.setProgress(NumStopRoll_now);
        binding.progressBarBath.setMax(NumStopRoll_want);
        //1ซอง = 20 มวน = 70 บาท

        String MoneySaveNowStr = String.valueOf(MoneySaveNow);
        String LungProgressStr = String.valueOf(LungProgress_progress);
        String AbilityProgressStr = String.valueOf(100);

        binding.MoneySaveText.setText(MoneySaveNowStr + ".00");

        //binding.percentAbility.setText(AbilityProgressStr);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}