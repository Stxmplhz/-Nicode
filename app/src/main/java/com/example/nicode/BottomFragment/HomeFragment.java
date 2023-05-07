package com.example.nicode.BottomFragment;

import androidx.annotation.NonNull;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nicode.Activity.Constants;
import com.example.nicode.Activity.PreferenceManager;
import com.example.nicode.Bluetooth.GetCoinActivity;
import com.example.nicode.EventHomeSwipe.EventAdapter;
import com.example.nicode.EventHomeSwipe.EventModel;
import com.example.nicode.R;
import com.example.nicode.databinding.FragmentHomeBinding;
import com.example.nicode.autoslider.homeevent_slideradapter;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private static final String TAG ="HomeFragment";

    private FragmentHomeBinding binding;
    private PreferenceManager preferenceManager;

    private FirebaseFirestore Fdatabase;

    SliderView homeevent_sliderview;
    int[] images = {R.drawable.homeswipe1,
            R.drawable.homeswipe2,
            R.drawable.homeswipe3,
            R.drawable.homeswipe4
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Context thiscontext = container.getContext();

        preferenceManager = new PreferenceManager(thiscontext);
        Fdatabase = FirebaseFirestore.getInstance();
        DocumentReference NicodeWatchUpdateRef = Fdatabase.collection(Constants.KEY_COLLECTION_USERS).document(Constants.KEY_USER_ID);

        int Now_smokinglyzer = preferenceManager.getInt(Constants.KEY_SMOKINGLYZER);
        int Now_nicocoin = preferenceManager.getInt(Constants.KEY_NICOCOIN);

        binding.bluetoothzone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NicodeWatchUpdateRef.update(Constants.KEY_STATISTIC_SMOKINGLYZER,FieldValue.arrayUnion(Now_smokinglyzer));
                NicodeWatchUpdateRef.update(Constants.KEY_STATISTIC_NICOCOIN,FieldValue.arrayUnion(Now_nicocoin));
                Intent intent = new Intent(getActivity(), GetCoinActivity.class);
                startActivity(intent);
            }
        });

        int AlldayinMonth = preferenceManager.getInt(Constants.KEY_ALLDAYFORMONTH);
        int Dayfromstart = preferenceManager.getInt(Constants.KEY_DAYFROMSTART);
        int Dayleft = AlldayinMonth - Dayfromstart;
        String Dayleft_Str = Integer.toString(Dayleft);
        binding.nicocoinInbar.setText(String.valueOf(preferenceManager.getInt(Constants.KEY_NICOCOIN)));
        binding.textDayleft.setText(Dayleft_Str);

        homeevent_sliderview = binding.HomeeventSlider;

        homeevent_slideradapter sliderAdapter = new homeevent_slideradapter(images);

        homeevent_sliderview.setSliderAdapter(sliderAdapter);
        homeevent_sliderview.setIndicatorAnimation(IndicatorAnimationType.WORM);
        homeevent_sliderview.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        homeevent_sliderview.startAutoCycle();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
