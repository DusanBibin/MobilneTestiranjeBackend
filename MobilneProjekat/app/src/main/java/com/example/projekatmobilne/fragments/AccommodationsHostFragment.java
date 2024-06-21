package com.example.projekatmobilne.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projekatmobilne.activities.CreateAccommodationActivity;
import com.example.projekatmobilne.adapters.AccommodationHostViewAdapter;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.FragmentAccommodationsHostBinding;
import com.example.projekatmobilne.adapters.AdapterItems.AccommodationHostItem;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.AccommodationHostDTOPagedResponse;
import com.example.projekatmobilne.tools.ResponseParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;


public class AccommodationsHostFragment extends Fragment {

    private FragmentAccommodationsHostBinding binding;
    private List<AccommodationHostItem> dataList;
    private AccommodationHostViewAdapter adapter;

    private Integer currentPage = 0;
    private Boolean isLastPage = false;
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
        dataList = new ArrayList<>();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerViewAccommodationsHost.setLayoutManager(linearLayoutManager);


        adapter = new AccommodationHostViewAdapter(getActivity(), dataList);
        binding.recyclerViewAccommodationsHost.setAdapter(adapter);

        binding.recyclerViewAccommodationsHost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLastPage && layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == adapter.getItemCount() - 1) {
                    currentPage++;
                    loadPage();
                }
            }
        });

        loadPage();
        binding.floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateAccommodationActivity.class);
            startActivity(intent);
        });

    }

    private void loadPage() {
        Call<ResponseBody> call = ClientUtils.apiService.getOwnerAccommodations(currentPage, 10);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                AccommodationHostDTOPagedResponse responseDTO = ResponseParser.parseResponse(response, AccommodationHostDTOPagedResponse.class, false);
                dataList.addAll(responseDTO.getContent());
                isLastPage = responseDTO.isLast();
                adapter.notifyDataSetChanged();
                binding.recyclerViewAccommodationsHost.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }
}