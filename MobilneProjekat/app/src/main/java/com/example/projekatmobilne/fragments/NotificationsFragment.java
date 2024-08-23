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
                Log.d("NotificationsFragment", "onResponse: " + response.body().toString());
                if(response.isSuccessful()){

                    NotificationListDTO notificationResponse = ResponseParser.parseResponse(response, NotificationListDTO.class, false);
                    dataList.addAll(notificationResponse.getContent());
                    notificationAdapter.notifyDataSetChanged();

                    if(dataList.isEmpty()){
                        binding.txtNoNotifications.setVisibility(View.VISIBLE);
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