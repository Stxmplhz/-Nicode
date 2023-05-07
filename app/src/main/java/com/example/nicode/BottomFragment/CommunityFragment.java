package com.example.nicode.BottomFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nicode.Activity.PreferenceManager;
import com.example.nicode.InsideCommunity.GrouplistActivity;
import com.example.nicode.InsideCommunity.RecentChatActivity;
import com.example.nicode.R;
import com.example.nicode.autoslider.communityevent_slideradapter;
import com.example.nicode.autoslider.homeevent_slideradapter;
import com.example.nicode.databinding.FragmentCommunityBinding;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

public class CommunityFragment extends Fragment {

    private FragmentCommunityBinding binding;
    private PreferenceManager preferenceManager;

    SliderView communityevent_sliderview;
    int[] images = {R.drawable.homeswipe2,
            R.drawable.communityswipe2,
            R.drawable.homeswipe3,
            R.drawable.homeswipe4
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCommunityBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.grouplistbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RecentChatActivity.class);
                startActivity(intent);
            }
        });

        communityevent_sliderview = binding.communityeventSlider;

        communityevent_slideradapter sliderAdapter = new communityevent_slideradapter(images);

        communityevent_sliderview.setSliderAdapter(sliderAdapter);
        communityevent_sliderview.setIndicatorAnimation(IndicatorAnimationType.WORM);
        communityevent_sliderview.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        communityevent_sliderview.startAutoCycle();

        binding.essay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWebsite("https://resourcecenter.thaihealth.or.th/index.php/media/OxjY");
            }
        });

        binding.essay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWebsite("https://resourcecenter.thaihealth.or.th/index.php/article/%E0%B8%81%E0%B8%B2%E0%B8%A3%E0%B8%AD%E0%B8%AD%E0%B8%81%E0%B8%81%E0%B8%B3%E0%B8%A5%E0%B8%B1%E0%B8%87%E0%B8%81%E0%B8%B2%E0%B8%A2%E0%B8%A7%E0%B8%B4%E0%B8%96%E0%B8%B5%E0%B9%83%E0%B8%AB%E0%B8%A1%E0%B9%88");
            }
        });

        binding.essay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWebsite("https://www.thaihealth.or.th/Content/36923-%E0%B8%AA%E0%B8%B9%E0%B8%9A%E0%B8%9A%E0%B8%B8%E0%B8%AB%E0%B8%A3%E0%B8%B5%E0%B9%88%E0%B9%83%E0%B8%99%E0%B8%9A%E0%B9%89%E0%B8%B2%E0%B8%99%20%E0%B8%9C%E0%B8%A5%E0%B8%A3%E0%B9%89%E0%B8%B2%E0%B8%A2%E0%B8%AA%E0%B8%B9%E0%B9%88%E0%B8%84%E0%B8%99%E0%B9%83%E0%B8%81%E0%B8%A5%E0%B9%89%E0%B8%8A%E0%B8%B4%E0%B8%94.html");
            }
        });

        binding.essay4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWebsite("https://www.thaihealth.or.th/Content/36920-%E0%B9%82%E0%B8%97%E0%B8%A9%E0%B8%82%E0%B8%AD%E0%B8%87%E0%B8%9A%E0%B8%B8%E0%B8%AB%E0%B8%A3%E0%B8%B5%E0%B9%88%20%E0%B8%A2%E0%B8%B4%E0%B9%88%E0%B8%87%E0%B8%AA%E0%B8%B9%E0%B8%9A%E0%B8%A2%E0%B8%B4%E0%B9%88%E0%B8%87%E0%B8%9B%E0%B9%88%E0%B8%A7%E0%B8%A2.html");
            }
        });


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void gotoWebsite(String s){
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }

}