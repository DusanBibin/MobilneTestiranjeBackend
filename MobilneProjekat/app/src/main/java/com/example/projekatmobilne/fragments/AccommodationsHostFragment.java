package com.example.projekatmobilne.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.activities.CreateAccommodationActivity;
import com.example.projekatmobilne.activities.HomeActivity;
import com.example.projekatmobilne.activities.LoginActivity;
import com.example.projekatmobilne.databinding.FragmentAccommodationsHostBinding;
import com.example.projekatmobilne.databinding.FragmentAccountBinding;


public class AccommodationsHostFragment extends Fragment {

    private FragmentAccommodationsHostBinding binding;

    public AccommodationsHostFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccommodationsHostBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateAccommodationActivity.class);
            startActivity(intent);
        });

    }
}