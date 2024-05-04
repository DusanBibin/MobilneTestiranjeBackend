package com.example.projekatmobilne.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.activities.HomeActivity;
import com.example.projekatmobilne.activities.LoginActivity;
import com.example.projekatmobilne.activities.RegisterActivity;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.ActivityLoginBinding;
import com.example.projekatmobilne.databinding.FragmentAccountBinding;
import com.example.projekatmobilne.model.AuthenticationResponseDTO;
import com.example.projekatmobilne.model.UserDTOResponse;
import com.example.projekatmobilne.tools.JWTManager;
import com.example.projekatmobilne.tools.ResponseParser;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AccountFragment extends Fragment {
    private FragmentAccountBinding binding;
    public AccountFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Call<ResponseBody> call = ClientUtils.apiService.getUserData();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                if(response.code() == 400){
                    Toast.makeText(getActivity(), "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                }
                if(response.code() == 200) {
                    UserDTOResponse responseDTO =
                            ResponseParser.parseResponse(response, UserDTOResponse.class, false);
                    System.out.println(responseDTO);

                    binding.nameValueTxt.setText(responseDTO.getFirstName());
                    binding.surnameValueTxt.setText(responseDTO.getLastName());
                    binding.addressValueTxt.setText(responseDTO.getAddress());
                    binding.emailValueTxt.setText(responseDTO.getEmail());
                    binding.phoneValueTxt.setText(responseDTO.getPhoneNumber());
                    binding.roleValueTxt.setText(responseDTO.getRole().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "There was a problem, try again later", Toast.LENGTH_SHORT).show();
            }
        });


        binding.logoutBtn.setOnClickListener(v -> {
            JWTManager.clearUserData();
            Intent intent = new Intent(requireContext(), LoginActivity.class); // Replace with your Activity2 class name
            startActivity(intent);
            getActivity().finish();
        });

    }
}