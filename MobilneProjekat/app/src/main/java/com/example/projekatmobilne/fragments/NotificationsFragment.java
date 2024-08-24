package com.example.projekatmobilne.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.projekatmobilne.adapters.NotificationAdapter;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.FragmentNotificationsBinding;
import com.example.projekatmobilne.model.Notification;
import com.example.projekatmobilne.model.responseDTO.NotificationListDTO;
import com.example.projekatmobilne.tools.ResponseParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private List<Notification> dataList;
    private NotificationAdapter notificationAdapter;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerViewNotifications.setLayoutManager(linearLayoutManager);
        notificationAdapter = new NotificationAdapter(dataList, getActivity());
        binding.recyclerViewNotifications.setAdapter(notificationAdapter);
        loadPage();
    }

    @Override
    public void onResume() {
        super.onResume();
        dataList.clear();
        notificationAdapter.notifyDataSetChanged();
        loadPage();
    }

    private void loadPage(){
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.txtNoNotifications.setVisibility(View.GONE);
        Call<ResponseBody> call = ClientUtils.apiService.getNotifications();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                binding.progressBar.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    Type listType = new TypeToken<NotificationListDTO>(){}.getType();
                    NotificationListDTO notificationResponse = null;
                    try {
                        String responseBody = response.body().string();
                        notificationResponse = ResponseParser.parseTypeResponse(responseBody, listType, false);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d("NotificationsFragment", "Notification Response: " + notificationResponse);
                    dataList.addAll(notificationResponse.getContent());
                    notificationAdapter.setList(dataList);
                    notificationAdapter.notifyDataSetChanged();


                    if(dataList.isEmpty()){
                        binding.txtNoNotifications.setVisibility(View.VISIBLE);
                    }
                    else{
                        binding.recyclerViewNotifications.setVisibility(View.VISIBLE);
                        binding.progressBar.setVisibility(View.GONE);
                    }
                }else{
                    Toast.makeText(getActivity(), "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}