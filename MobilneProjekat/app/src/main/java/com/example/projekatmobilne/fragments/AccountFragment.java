package com.example.projekatmobilne.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.activities.LoginActivity;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.FragmentAccountBinding;
import com.example.projekatmobilne.model.UserDTO;
import com.example.projekatmobilne.model.UserDTOResponse;
import com.example.projekatmobilne.tools.JWTManager;
import com.example.projekatmobilne.tools.ResponseParser;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AccountFragment extends Fragment {

    EditText nameEdit;
    EditText surnameEdit;
    EditText addressEdit;
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

        setUpEditInfoDialog();
        Call<ResponseBody> call = ClientUtils.apiService.getUserData();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                if(response.code() == 403) {
                    try {
                        Toast.makeText(getActivity(), response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                if(response.code() == 400){
                    try {
                        System.out.println(response.body().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
                if(response.code() == 200) {
                    UserDTOResponse responseDTO =
                            ResponseParser.parseResponse(response, UserDTOResponse.class, false);

                    binding.nameValueTxt.setText(responseDTO.getFirstName());
                    binding.surnameValueTxt.setText(responseDTO.getLastName());
                    binding.addressValueTxt.setText(responseDTO.getAddress());
                    binding.emailValueTxt.setText(responseDTO.getEmail());
                    binding.phoneValueTxt.setText(responseDTO.getPhoneNumber());
                    binding.roleValueTxt.setText(responseDTO.getRole().toString());

                    nameEdit.setText(responseDTO.getFirstName());
                    surnameEdit.setText(responseDTO.getLastName());
                    addressEdit.setText(responseDTO.getAddress());

                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "There was a problem, try again later", Toast.LENGTH_SHORT).show();
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

    private void setUpEditInfoDialog() {
        changeDetailsDialog = new Dialog(getActivity());
        changeDetailsDialog.setContentView(R.layout.custom_dialog_change_info);
        changeDetailsDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        changeDetailsDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.custom_dialog_bg));

        nameEdit = changeDetailsDialog.findViewById(R.id.nameInputEditText);
        surnameEdit = changeDetailsDialog.findViewById(R.id.surnameInputEditText);
        addressEdit = changeDetailsDialog.findViewById(R.id.addressInputEditText);
        TextInputLayout nameInput = changeDetailsDialog.findViewById(R.id.nameInputLayout);
        TextInputLayout surnameInput = changeDetailsDialog.findViewById(R.id.surnameInputLayout);
        TextInputLayout addressInput = changeDetailsDialog.findViewById(R.id.addressInputLayout);

        changeDetailsDialog.findViewById(R.id.btnCancelInfo).setOnClickListener(v -> {
            changeDetailsDialog.dismiss();
        });

        changeDetailsDialog.findViewById(R.id.btnConfirmInfo).setOnClickListener(v -> {

            nameInput.setError(null);
            surnameInput.setError(null);
            addressInput.setError(null);

            boolean isValid = true;
            if (nameEdit.getText().toString().isEmpty()) {
                nameInput.setError("This field cannot be empty"); isValid = false;
            }
            if (surnameEdit.getText().toString().isEmpty()) {
                surnameInput.setError("This field cannot be empty"); isValid = false;
            }
            if (addressEdit.getText().toString().isEmpty()) {
                addressInput.setError("This field cannot be empty"); isValid = false;
            }
            if(!isValid) return;

            UserDTO request = new UserDTO();
            request.setFirstName(nameEdit.getText().toString());
            request.setLastName(surnameEdit.getText().toString());
            request.setAddress(addressEdit.getText().toString());


            Call<ResponseBody> call = ClientUtils.apiService.changeUserData(request);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    System.out.println("KOD JE: " + response.code());

                    if(response.code() == 400){

                        Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);

                        if(map.containsKey("firstName")){
                            nameInput.setError(map.get("email"));
                        }
                        if(map.containsKey("lastName")){
                            surnameInput.setError(map.get("lastName"));
                        }
                        if(map.containsKey("address")){
                            addressInput.setError(map.get("address"));
                        }

                    }
                    if(response.code() == 200) {
                        UserDTO responseDTO =
                                ResponseParser.parseResponse(response, UserDTO.class, false);
                        nameEdit.setText(responseDTO.getFirstName());
                        surnameEdit.setText(responseDTO.getLastName());
                        addressEdit.setText(responseDTO.getAddress());

                        binding.nameValueTxt.setText(responseDTO.getFirstName());
                        binding.surnameValueTxt.setText(responseDTO.getLastName());
                        binding.addressValueTxt.setText(responseDTO.getAddress());

                        Toast.makeText(getActivity(), "Successfully changed user info", Toast.LENGTH_SHORT).show();
                        changeDetailsDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(getActivity(), "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                }
            });


        });


    }
}