package com.example.projekatmobilne.fragments.host.requests;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.databinding.FragmentAccommodationsHostBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class AccommodationsHostFragment extends Fragment {

    private FragmentAccommodationsHostBinding binding;

    public AccommodationsHostFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_accommodations_host, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(getActivity(), R.id.fragmentContainerViewAccommodations);
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomNavigationViewAccommodations);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
}