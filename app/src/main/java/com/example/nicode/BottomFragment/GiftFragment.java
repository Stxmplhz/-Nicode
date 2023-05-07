package com.example.nicode.BottomFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nicode.Activity.Constants;
import com.example.nicode.Activity.PreferenceManager;
import com.example.nicode.Donate.DonateAdapter;
import com.example.nicode.Donate.DonateModel;
import com.example.nicode.Donate.DonationExchangeActivity;
import com.example.nicode.Gift.GiftExchangeActivity;
import com.example.nicode.Gift.GiftAdapter;
import com.example.nicode.Gift.GiftModel;
import com.example.nicode.R;
import com.example.nicode.databinding.FragmentGiftBinding;

import java.util.ArrayList;

public class GiftFragment extends Fragment {

    private FragmentGiftBinding binding;

    RecyclerView recyclerView_gift,recyclerView_donate;

    private PreferenceManager preferenceManager;

    ArrayList<GiftModel> giftModels;
    ArrayList<DonateModel> donateModels;
    GiftAdapter giftAdapter;
    DonateAdapter donateAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGiftBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Context thiscontext = container.getContext();

        preferenceManager = new PreferenceManager(thiscontext);

        recyclerView_gift = binding.giftrecycler;

        Integer[] giftLogo = {R.drawable.giftbanner1,R.drawable.giftbanner2, R.drawable.giftbanner3
                , R.drawable.giftbanner4, R.drawable.giftbanner5};

        giftModels = new ArrayList<>();
        for (int i=0;i<giftLogo.length;i++) {
            GiftModel giftmodel = new GiftModel(giftLogo[i]);
            giftModels.add(giftmodel);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(thiscontext,LinearLayoutManager.HORIZONTAL,false);
        recyclerView_gift.setLayoutManager(layoutManager);
        recyclerView_gift.setItemAnimator(new DefaultItemAnimator());

        giftAdapter= new GiftAdapter(thiscontext, giftModels, new GiftAdapter.SelectedGift() {
            @Override
            public void selectedGift(GiftModel giftModel) {
                Intent intent = new Intent(getActivity(), GiftExchangeActivity.class);
                startActivity(intent);
            }
        });
        recyclerView_gift.setAdapter(giftAdapter);

        recyclerView_donate = binding.donaterecycler;

        Integer[] donateLogo = {R.drawable.donatebanner1,R.drawable.donatebanner2, R.drawable.donatebanner3
                , R.drawable.donatebanner4, R.drawable.donatebanner5};

        donateModels = new ArrayList<>();
        for (int i=0;i<donateLogo.length;i++) {
            DonateModel donatemodel = new DonateModel(donateLogo[i]);
            donateModels.add(donatemodel);
        }

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(thiscontext,LinearLayoutManager.HORIZONTAL,false);
        recyclerView_donate.setLayoutManager(layoutManager2);
        recyclerView_donate.setItemAnimator(new DefaultItemAnimator());

        donateAdapter= new DonateAdapter(thiscontext, donateModels, new DonateAdapter.SelectedDonation() {
            @Override
            public void selectedDonation(DonateModel donateModel) {
                Intent intent = new Intent(getActivity(), DonationExchangeActivity.class);
                startActivity(intent);
            }
        });
        recyclerView_donate.setAdapter(donateAdapter);

        binding.closeadbanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.closeadbanner.setVisibility(View.GONE);
                binding.adbannerImage.setVisibility(View.GONE);
            }
        });

        binding.nicocoinInbar.setText(String.valueOf(preferenceManager.getInt(Constants.KEY_NICOCOIN)));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
