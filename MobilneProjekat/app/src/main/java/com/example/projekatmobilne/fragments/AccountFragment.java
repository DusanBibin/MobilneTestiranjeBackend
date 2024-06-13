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
import com.example.projekatmobilne.model.requestDTO.ChangePasswordDTO;
import com.example.projekatmobilne.model.requestDTO.UserDTO;
import com.example.projekatmobilne.model.responseDTO.UserDTOResponse;
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

    private EditText nameEdit;
    private EditText surnameEdit;
    private EditText addressEdit;
    private TextInputLayout nameInput;
    private TextInputLayout surnameInput;
    private TextInputLayout addressInput;

    private EditText passwordEdit;
    private EditText newPasswordEdit;
    private EditText repeatPasswordEdit;
    private TextInputLayout passwordInput;
    private TextInputLayout newPasswordInput;
    private TextInputLayout repeatPasswordInput;


    private EditText confirmDeletionEdit;
    private TextInputLayout confirmDeletionInput;


    private EditText confirmationCodeEdit;
    private EditText newEmailEdit;

    private TextInputLayout confirmationCodeInput;
    private TextInputLayout newEmailInput;

    private FragmentAccountBinding binding;
    private Dialog changeDetailsDialog, changePasswordDialog, deleteAccountDialog, confirmCodeDialog;
    private UserDTOResponse data;
    public AccountFragment() {

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

        setUpDeleteAccountDialog();
        setUpEditInfoDialog();
        setUpEditPasswordDialog();
        setUpConfirmCodeDialog();
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
                    data = responseDTO;

                    binding.txtNameValue.setText(responseDTO.getFirstName());
                    binding.txtSurnameValue.setText(responseDTO.getLastName());
                    binding.txtAddressValue.setText(responseDTO.getAddress());
                    binding.txtEmailValue.setText(responseDTO.getEmail());
                    binding.txtPhoneValue.setText(responseDTO.getPhoneNumber());
                    binding.txtRoleValue.setText(responseDTO.getRole().toString());

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

        binding.btnLogout.setOnClickListener(v -> {
            JWTManager.clearUserData();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        binding.btnChangeDetails.setOnClickListener(v -> {
            changeDetailsDialog.show();
            nameInput.setError(null);
            surnameInput.setError(null);
            addressInput.setError(null);
            nameEdit.setText(data.getFirstName());
            surnameEdit.setText(data.getLastName());
            addressEdit.setText(data.getAddress());
        });


        binding.btnChangePassword.setOnClickListener(v -> {
            changePasswordDialog.show();
            passwordInput.setError(null);
            repeatPasswordInput.setError(null);
            newPasswordInput.setError(null);
        });

        if(JWTManager.getRole().equals("ADMIN")) binding.btnDeleteAccount.setVisibility(View.INVISIBLE);
        binding.btnDeleteAccount.setOnClickListener(v -> {
            deleteAccountDialog.show();
            confirmDeletionInput.setError(null);
        });


        binding.btnChangeEmail.setOnClickListener(v -> {
            confirmationCodeInput.setError(null);
            newEmailInput.setError(null);

            Call<ResponseBody> callEmail = ClientUtils.apiService.sendCodeEmail();
            callEmail.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    confirmCodeDialog.show();
                    try {
                        if(response.code() == 200){
                            Toast.makeText(getActivity(), response.body().string(), Toast.LENGTH_SHORT).show();
                        }

                        if(response.code() == 400){
                            Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);
                            if(map.containsKey("message")){
                                String errMessage = map.get("message");
                                Toast.makeText(getActivity(), errMessage, Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(getActivity(), "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        });




    }

    private void setUpConfirmCodeDialog() {
        confirmCodeDialog = new Dialog(getActivity());
        confirmCodeDialog.setContentView(R.layout.custom_dialog_confirm_code);
        confirmCodeDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        confirmCodeDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.custom_dialog_bg));

        confirmationCodeEdit = confirmCodeDialog.findViewById(R.id.inputEditTextConfirmCode);
        confirmationCodeInput = confirmCodeDialog.findViewById(R.id.inputLayoutConfirmCode);

        newEmailEdit = confirmCodeDialog.findViewById(R.id.inputEditTextNewEmail);
        newEmailInput = confirmCodeDialog.findViewById(R.id.inputLayoutNewEmail);


        confirmCodeDialog.findViewById(R.id.btnCancelInfo).setOnClickListener(v -> {
            confirmCodeDialog.dismiss();
        });

        confirmCodeDialog.findViewById(R.id.btnConfirmInfo).setOnClickListener(v -> {
            confirmationCodeInput.setError(null);
            newEmailInput.setError(null);

            boolean isValid = true;
            if (confirmationCodeEdit.getText().toString().isEmpty()) {
                confirmationCodeInput.setError("This field cannot be empty"); isValid = false;
            }
            if (newEmailEdit.getText().toString().isEmpty()) {
                newEmailInput.setError("This field cannot be empty"); isValid = false;
            }
            if(!isValid) return;

            String email = JWTManager.getEmail();
            String code = confirmationCodeEdit.getText().toString();
            String newEmail = newEmailEdit.getText().toString();

            Call<ResponseBody> call = ClientUtils.apiService.validateCode(code, email, newEmail);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.code() == 200){
                        Toast.makeText(getActivity(), "USPEH", Toast.LENGTH_SHORT).show();
                    }

                    if(response.code() == 400){
                        Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);

                        if(map.containsKey("message")){
                            String errMessage = map.get("message");
                            if(errMessage.equals("Email is not valid") || errMessage.equals("User with this email already exists")){
                                newEmailInput.setError(errMessage);
                            }

                            if(errMessage.equals("The code has expired, please try again") || errMessage.equals("Code is incorrect")){
                                confirmationCodeInput.setError(errMessage);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getActivity(), "There was a problem try again later", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        });
    }

    private void setUpDeleteAccountDialog() {
        deleteAccountDialog = new Dialog(getActivity());
        deleteAccountDialog.setContentView(R.layout.custom_dialog_delete_account);
        deleteAccountDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        deleteAccountDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.custom_dialog_bg));

        confirmDeletionEdit = deleteAccountDialog.findViewById(R.id.inputEditTextConfirmDelete);
        confirmDeletionInput = deleteAccountDialog.findViewById(R.id.inputLayoutConfirmDelete);


        deleteAccountDialog.findViewById(R.id.btnCancelInfo).setOnClickListener(v -> {
            deleteAccountDialog.dismiss();
        });


        deleteAccountDialog.findViewById(R.id.btnConfirmInfo).setOnClickListener(v -> {
            confirmDeletionInput.setError(null);

            if (!confirmDeletionEdit.getText().toString().equals("DELETE")) {
                confirmDeletionInput.setError("Please type DELETE to confirm deletion"); return;
            }

            Call<ResponseBody> call;
            if(JWTManager.getRole().equals("GUEST")) call = ClientUtils.apiService.deleteGuest();
            else call = ClientUtils.apiService.deleteOwner();

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if(response.code() == 400){

                        Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);

                        if(map.containsKey("message")){
                            String errMessage = map.get("message");
                            confirmDeletionInput.setError(errMessage);

                        }

                    }
                    if(response.code() == 200) {
                        String responseMessage =
                                ResponseParser.parseResponse(response, String.class, false);

                        Toast.makeText(getActivity(), responseMessage, Toast.LENGTH_SHORT).show();

                        deleteAccountDialog.dismiss();
                        JWTManager.clearUserData();
                        Intent intent = new Intent(requireContext(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getActivity(), "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });



        });
    }

    private void setUpEditPasswordDialog()  {
        changePasswordDialog = new Dialog(getActivity());
        changePasswordDialog.setContentView(R.layout.custom_dialog_change_password);
        changePasswordDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        changePasswordDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.custom_dialog_bg));

        passwordEdit = changePasswordDialog.findViewById(R.id.inputEditTextPassword);
        newPasswordEdit = changePasswordDialog.findViewById(R.id.inputEditTextNewPassword);
        repeatPasswordEdit = changePasswordDialog.findViewById(R.id.inputEditTextRepeatPassword);
        passwordInput = changePasswordDialog.findViewById(R.id.inputLayoutPassword);
        newPasswordInput = changePasswordDialog.findViewById(R.id.inputLayoutNewPassword);
        repeatPasswordInput = changePasswordDialog.findViewById(R.id.inputLayoutRepeatPassword);

        changePasswordDialog.findViewById(R.id.btnCancelInfo).setOnClickListener(v -> {
            changePasswordDialog.dismiss();
        });

        changePasswordDialog.findViewById(R.id.btnConfirmInfo).setOnClickListener(v -> {

            passwordInput.setError(null);
            newPasswordInput.setError(null);
            repeatPasswordInput.setError(null);

            boolean isValid = true;
            if (passwordEdit.getText().toString().isEmpty()) {
                passwordInput.setError("This field cannot be empty"); isValid = false;
            }
            if (newPasswordEdit.getText().toString().isEmpty()) {
                newPasswordInput.setError("This field cannot be empty"); isValid = false;
            }
            if (repeatPasswordEdit.getText().toString().isEmpty()) {
                repeatPasswordInput.setError("This field cannot be empty"); isValid = false;
            }
            if (!newPasswordEdit.getText().toString().equals(repeatPasswordEdit.getText().toString())) {
                newPasswordInput.setError("Passwords do not match");
                repeatPasswordInput.setError("Passwords do not match");
                isValid = false;
            }


            if(!isValid) return;


            ChangePasswordDTO request = new ChangePasswordDTO();
            request.setCurrentPassword(passwordEdit.getText().toString());
            request.setNewPassword(newPasswordEdit.getText().toString());
            request.setRepeatNewPassword(repeatPasswordEdit.getText().toString());


            Call<ResponseBody> call = ClientUtils.apiService.changePassword(request);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    if(response.code() == 400){

                        Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);

                        if(map.containsKey("message")){
                            String errMessage = map.get("message");
                            if(errMessage.equals("Password is incorrect")){
                                passwordInput.setError(errMessage);
                            }
                            if(errMessage.equals("You cannot put your old password as your new one")){
                                passwordInput.setError(errMessage);
                            }
                        }


                        if(map.containsKey("currentPassword")){
                            passwordInput.setError(map.get("currentPassword"));
                        }
                        if(map.containsKey("newPassword")){
                            newPasswordInput.setError(map.get("newPassword"));
                        }
                        if(map.containsKey("repeatNewPassword")){
                            repeatPasswordInput.setError(map.get("repeatNewPassword"));
                        }

                    }
                    if(response.code() == 200) {
                        String responseMessage =
                                ResponseParser.parseResponse(response, String.class, false);


                        Toast.makeText(getActivity(), responseMessage, Toast.LENGTH_SHORT).show();
                        changePasswordDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(getActivity(), "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                }
            });


        });
    }

    private void setUpEditInfoDialog() {
        changeDetailsDialog = new Dialog(getActivity());
        changeDetailsDialog.setContentView(R.layout.custom_dialog_change_info);
        changeDetailsDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        changeDetailsDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.custom_dialog_bg));

        nameEdit = changeDetailsDialog.findViewById(R.id.inputEditTextName);
        surnameEdit = changeDetailsDialog.findViewById(R.id.inputEditTextSurname);
        addressEdit = changeDetailsDialog.findViewById(R.id.inputEditTextAddress);
        nameInput = changeDetailsDialog.findViewById(R.id.inputLayoutName);
        surnameInput = changeDetailsDialog.findViewById(R.id.inputLayoutSurname);
        addressInput = changeDetailsDialog.findViewById(R.id.inputLayoutAddress);

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

                        data.setFirstName(responseDTO.getFirstName());
                        data.setLastName(responseDTO.getLastName());
                        data.setAddress(responseDTO.getAddress());

                        binding.txtNameValue.setText(responseDTO.getFirstName());
                        binding.txtSurnameValue.setText(responseDTO.getLastName());
                        binding.txtAddressValue.setText(responseDTO.getAddress());

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