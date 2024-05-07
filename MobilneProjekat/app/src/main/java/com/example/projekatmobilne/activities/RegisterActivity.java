package com.example.projekatmobilne.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.ActivityRegisterBinding;
import com.example.projekatmobilne.model.Enum.Role;
import com.example.projekatmobilne.model.RegisterRequestDTO;
import com.example.projekatmobilne.tools.ResponseParser;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActivityRegisterBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = binding.toolbar;

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextInputLayout[] inputLayouts = {
                binding.inputLayoutName,
                binding.inputLayoutSurname,
                binding.inputLayoutAddress,
                binding.emailInputLayout,
                binding.phoneInputLayout,
                binding.passwordInputLayout,
                binding.passwordRepeatInputLayout
        };

        EditText[] editTexts = {
                binding.inputEditTextName,
                binding.inputEditTextSurname,
                binding.inputEditTextAddress,
                binding.emailInputEditText,
                binding.phoneInputEditText,
                binding.passwordInputEditText,
                binding.passwordRepeatInputEditText
        };


        binding.registerButton.setOnClickListener(v -> {

           if(!setEditTextInputs(inputLayouts, editTexts)) return;

           RegisterRequestDTO request = createDTO();

           Call<ResponseBody> call = ClientUtils.apiService.register(request);
           call.enqueue(new Callback<ResponseBody>() {
               @Override
               public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                   if(response.code() == 400){
                       Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);

                        if(map.containsKey("message")){
                            String errMessage = map.get("message");

                            if(errMessage.equals("User with this email already exists")) binding.emailInputLayout.setError(errMessage);
                            else binding.phoneInputLayout.setError(errMessage);
                        }
                   }



                   if(response.code() == 200){
                       String responseMessage =
                               ResponseParser.parseResponse(response, String.class, false);


                       Toast.makeText(RegisterActivity.this, responseMessage, Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                       startActivity(intent);
                       finish();

                   }
               }

               @Override
               public void onFailure(Call<ResponseBody> call, Throwable t) {
                   Toast.makeText(RegisterActivity.this, "There was a problem, try again later", Toast.LENGTH_SHORT).show();
               }
           });



        });



    }


    private boolean setEditTextInputs(TextInputLayout[] inputLayouts, EditText[] editTexts){
        boolean isValid = true;

        for (TextInputLayout inputLayout : inputLayouts) {
            inputLayout.setError(null);
        }

        for (int i = 0; i < editTexts.length; i++) {
            EditText editText = editTexts[i];
            TextInputLayout inputLayout = inputLayouts[i];
            if (editText.getText().toString().isEmpty()) {
                inputLayout.setError("This field cannot be empty");
                isValid = false;
            }
        }

        if (!binding.passwordInputEditText.getText().toString().equals(binding.passwordRepeatInputEditText.getText().toString())) {
            binding.passwordInputLayout.setError("Passwords do not match");
            binding.passwordRepeatInputLayout.setError("Passwords do not match");
            isValid = false;
        }


        if(binding.passwordInputEditText.getText().length() < 10) binding.passwordInputLayout.setError("This field must contain at least 10 characters");
        if(binding.passwordRepeatInputEditText.getText().length() < 10) binding.passwordRepeatInputLayout.setError("This field must contain at least 10 characters");


        return isValid;
    }

    private RegisterRequestDTO createDTO(){

        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setFirstName(binding.inputEditTextName.getText().toString());
        req.setLastName(binding.inputEditTextSurname.getText().toString());
        req.setAddress(binding.inputEditTextAddress.getText().toString());
        req.setEmail(binding.emailInputEditText.getText().toString());
        req.setPhoneNumber(binding.phoneInputEditText.getText().toString());
        req.setPassword(binding.passwordInputEditText.getText().toString());
        req.setRepeatPassword(binding.passwordRepeatInputEditText.getText().toString());

        Role role;
        if(binding.checkBoxGuest.isChecked()) role = Role.GUEST;
        else role = Role.OWNER;
        req.setRole(role);

        return req;
    }
}