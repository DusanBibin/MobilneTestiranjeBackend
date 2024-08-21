package com.example.projekatmobilne.fragments.host.requests.tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projekatmobilne.adapters.AccommodationRequestPreviewAdapter;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.FragmentAccommodationRequestsTabBinding;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.AccommodationRequestPreviewDTOPagedResponse;
import com.example.projekatmobilne.tools.ResponseParser;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AccommodationRequestsTabFragment extends Fragment {

    private FragmentAccommodationRequestsTabBinding binding;
    private Integer currentPage = 0;
    private Boolean isLastPage = false;
    private AccommodationRequestPreviewAdapter adapter;
    public AccommodationRequestsTabFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAccommodationRequestsTabBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerViewAccommodationsRequestsHost.setLayoutManager(linearLayoutManager);

        adapter = new AccommodationRequestPreviewAdapter(getActivity(), new ArrayList<>());
        binding.recyclerViewAccommodationsRequestsHost.setAdapter(adapter);

        binding.recyclerViewAccommodationsRequestsHost.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        adapter.clearData();
        loadPage();
    }

    private void loadPage() {

        binding.txtNoItemsRequests.setVisibility(View.GONE);
        Call<ResponseBody> call = ClientUtils.apiService.getOwnerAccommodationRequests(currentPage, 10);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                AccommodationRequestPreviewDTOPagedResponse responseDTO = ResponseParser.parseResponse(response, AccommodationRequestPreviewDTOPagedResponse.class, false);

                adapter.addMoreData(responseDTO.getContent());
                isLastPage = responseDTO.isLast();

                binding.recyclerViewAccommodationsRequestsHost.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                if(adapter.getDataList().isEmpty()) binding.txtNoItemsRequests.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }

        });

    }


}