package com.example.projekatmobilne.fragments.guest.reservations.tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.adapters.AccommodationHostViewAdapter;
import com.example.projekatmobilne.adapters.AdapterItems.AccommodationHostItem;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.FragmentAccommodationsHostTabBinding;
import com.example.projekatmobilne.databinding.FragmentFavoritesTabBinding;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.AccommodationHostDTOPagedResponse;
import com.example.projekatmobilne.tools.ResponseParser;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FavoritesTabFragment extends Fragment {

    private FragmentFavoritesTabBinding binding;
    private List<AccommodationHostItem> dataList;
    private AccommodationHostViewAdapter adapter;

    private Integer currentPage = 0;
    private Boolean isLastPage = false;

    public FavoritesTabFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }

    @Override
    public void onResume() {
        super.onResume();


        dataList.clear();
        adapter.notifyDataSetChanged();
        loadPage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavoritesTabBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }


    private void loadPage() {
        binding.txtNoItemsAccommodations.setVisibility(View.GONE);
        Call<ResponseBody> call = ClientUtils.apiService.getFavorites(currentPage, 10);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                AccommodationHostDTOPagedResponse responseDTO = ResponseParser.parseResponse(response, AccommodationHostDTOPagedResponse.class, false);
                dataList.addAll(responseDTO.getContent());
                isLastPage = responseDTO.isLast();
                adapter.notifyDataSetChanged();
                binding.recyclerViewAccommodationsHost.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                if(dataList.isEmpty()) binding.txtNoItemsAccommodations.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });

    }


}