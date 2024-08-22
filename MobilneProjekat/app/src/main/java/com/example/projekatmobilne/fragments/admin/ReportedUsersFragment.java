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
import android.util.Log;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.adapters.ReportUserAdapter;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.FragmentReportedUsersBinding;
import com.example.projekatmobilne.model.responseDTO.ReportDTO;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.ReportUserDTOPagedResponse;
import com.example.projekatmobilne.tools.ResponseParser;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportedUsersFragment extends Fragment {

    private FragmentReportedUsersBinding binding;
    private List<ReportDTO> dataList;
    private Integer currentPage = 0;
    private Boolean isLastPage = false;
    private ReportUserAdapter reportUserAdapter;
    public ReportedUsersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentReportedUsersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerViewReportedUsersAdmin.setLayoutManager(linearLayoutManager);
        reportUserAdapter = new ReportUserAdapter(getActivity(), new ArrayList<>());
        binding.recyclerViewReportedUsersAdmin.setAdapter(reportUserAdapter);

        binding.recyclerViewReportedUsersAdmin.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLastPage && layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == reportUserAdapter.getItemCount() - 1) {
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
        reportUserAdapter.notifyDataSetChanged();
        loadPage();
    }

    private void loadPage(){
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.txtNoItemsRequests.setVisibility(View.GONE);
        Call<ResponseBody> call = ClientUtils.apiService.getUserReportsRequests(currentPage, 10);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ReportUserDTOPagedResponse responseDTO = ResponseParser.parseResponse(response, ReportUserDTOPagedResponse.class, false);

                dataList.addAll(responseDTO.getContent());
                reportUserAdapter.setList(dataList);
                isLastPage = responseDTO.isLast();
                reportUserAdapter.notifyDataSetChanged();
                binding.recyclerViewReportedUsersAdmin.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                if(dataList.isEmpty()) binding.txtNoItemsRequests.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}