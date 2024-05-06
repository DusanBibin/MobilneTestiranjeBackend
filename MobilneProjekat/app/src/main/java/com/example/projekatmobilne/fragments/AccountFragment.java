package com.example.projekatmobilne.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
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

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AccountFragment extends Fragment {
    private FragmentAccountBinding binding;
    private Dialog changeDetailsDialog;
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

        setUpDialogs();
        Call<ResponseBody> call = ClientUtils.apiService.getUserData();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {



                if(response.code() == 400){
                    try {
                        System.out.println(response.body().string());
                        Toast.makeText(getActivity(), "KURINAAA", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

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
                Toast.makeText(getActivity(), "kurcinaa", Toast.LENGTH_SHORT).show();
                Log.e("Retrofit", "Request failed: " + t.getMessage());
                // You can also log the stack trace for more detailed information
                t.printStackTrace();
            }
        });

        binding.logoutBtn.setOnClickListener(v -> {
            JWTManager.clearUserData();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });


        binding.btnChangeDetails.setOnClickListener(v -> {
            changeDetailsDialog.show();
        });

    }

    private void setUpDialogs() {
        changeDetailsDialog = new Dialog(getActivity());
        changeDetailsDialog.setContentView(R.layout.custom_dialog_change_info);
        changeDetailsDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        changeDetailsDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.custom_dialog_bg));
        changeDetailsDialog.setCancelable(false);

//        changeDetailsDialog.findViewById(R.id.btnCancelInfo).setOnClickListener(v -> {
//            changeDetailsDialog.dismiss();
//        });
//
//        changeDetailsDialog.findViewById(R.id.btnConfirmInfo).setOnClickListener(v -> {
//            changeDetailsDialog.dismiss();
//        });

    }
}