package com.example.projekatmobilne.fragments.admin;

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
import com.example.projekatmobilne.activities.RegisterActivity;
import com.example.projekatmobilne.adapters.AccommodationHostViewAdapter;
import com.example.projekatmobilne.adapters.AdapterItems.AccommodationHostItem;
import com.example.projekatmobilne.adapters.ReportedCommentsViewAdapter;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.FragmentFavoritesTabBinding;
import com.example.projekatmobilne.databinding.FragmentReportedCommentsBinding;
import com.example.projekatmobilne.model.responseDTO.ComplaintDTO;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.AccommodationHostDTOPagedResponse;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.ComplaintsDTOPagedResponse;
import com.example.projekatmobilne.tools.ResponseParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReportedCommentsFragment extends Fragment {

    private FragmentReportedCommentsBinding binding;
    private List<ComplaintDTO> dataList;
    private ReportedCommentsViewAdapter adapter;

    private Integer currentPage = 0;
    private Boolean isLastPage = false;


    public ReportedCommentsFragment() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        dataList = new ArrayList<>();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerViewReportedComments.setLayoutManager(linearLayoutManager);


        adapter = new ReportedCommentsViewAdapter(getActivity(), dataList);
        binding.recyclerViewReportedComments.setAdapter(adapter);

        binding.recyclerViewReportedComments.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentReportedCommentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    private void loadPage() {
        binding.txtNoItemsComplaints.setVisibility(View.GONE);
        Call<ResponseBody> call = ClientUtils.apiService.getComplaints(currentPage, 10);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 200){
                    ComplaintsDTOPagedResponse responseDTO = ResponseParser.parseResponse(response, ComplaintsDTOPagedResponse.class, false);
                    dataList.addAll(responseDTO.getContent());
                    isLastPage = responseDTO.isLast();
                    adapter.notifyDataSetChanged();
                    binding.recyclerViewReportedComments.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                    if(dataList.isEmpty()) binding.txtNoItemsComplaints.setVisibility(View.VISIBLE);
                }

                if(response.code() == 400){
                    Map<String, String> map = ResponseParser.parseResponse(response, Map.class , true);
                    if(map.containsKey("message")) {
                        String errMessage = map.get("message");
                        Toast.makeText(getActivity(), errMessage, Toast.LENGTH_SHORT).show();
                    }
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