package com.example.projekatmobilne.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.activities.AccommodationDetailsActivity;
import com.example.projekatmobilne.adapters.AccommodationHostViewAdapter;
import com.example.projekatmobilne.adapters.AccommodationSearchAdapter;
import com.example.projekatmobilne.adapters.ReservationHostViewAdapter;
import com.example.projekatmobilne.clients.ClientUtils;
import com.example.projekatmobilne.databinding.FragmentReservationsHostBinding;
import com.example.projekatmobilne.model.Enum.ReservationStatus;
import com.example.projekatmobilne.model.requestDTO.AvailabilityDTO;
import com.example.projekatmobilne.model.requestDTO.ReservationDTO;
import com.example.projekatmobilne.model.responseDTO.AccommodationDifferencesDTO;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.AccommodationHostDTOPagedResponse;
import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.ReservationHostDTOPagedResponse;
import com.example.projekatmobilne.tools.ResponseParser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReseravationHostFragment extends Fragment {

    private FragmentReservationsHostBinding binding;

    private Integer currentPage = 0;
    private Boolean isLastPage = false;
    private ReservationHostViewAdapter adapter;
    private LocalDate minDate, maxDate;
    private ReservationStatus reservationStatus = null;
    private List<ReservationDTO> dataList;

    public ReseravationHostFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReservationsHostBinding.inflate(inflater, container, false);
         return binding.getRoot();
    }

    private void loadPage(){

        //binding.btnSearch.setVisibility(View.INVISIBLE);
        //binding.progressBarSearch.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.VISIBLE);

        binding.txtNoItemsReservations.setVisibility(View.GONE);

        String search = binding.searchReservations.getQuery().toString();


        String selectedStatus = (String) binding.spinner.getSelectedItem();
        if(!selectedStatus.equals("ANY")) reservationStatus = ReservationStatus.valueOf(selectedStatus);
        else reservationStatus = null;


        Call<ResponseBody> call = ClientUtils.apiService.getReservations(search, minDate, maxDate, reservationStatus, currentPage, 5);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                 ReservationHostDTOPagedResponse responseDTO = ResponseParser.parseResponse(response, ReservationHostDTOPagedResponse.class, false);


                dataList.addAll(responseDTO.getContent());
                isLastPage = responseDTO.isLast();
                adapter.notifyDataSetChanged();
                binding.recyclerViewReservationsHost.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                if(dataList.isEmpty()) binding.txtNoItemsReservations.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "There was a problem, try again later", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerViewReservationsHost.setLayoutManager(linearLayoutManager);


        adapter = new ReservationHostViewAdapter(getActivity(), dataList);
        binding.recyclerViewReservationsHost.setAdapter(adapter);


        binding.inputEditTextMinDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    String date = dayOfMonth + "-" + (month+1) + "-" + year;
                    minDate = LocalDate.of(year, month+1, dayOfMonth);
                    binding.inputEditTextMinDate.setText(date);
                }
            }, LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, LocalDate.now().getDayOfMonth());

            final Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dialog.show();
        });

        binding.inputEditTextMaxDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    String date = dayOfMonth + "-" + (month+1) + "-" + year;
                    maxDate = LocalDate.of(year, month+1, dayOfMonth);
                    binding.inputEditTextMaxDate.setText(date);
                }
            }, LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, LocalDate.now().getDayOfMonth());

            final Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dialog.show();
        });
        setupSpinner();

        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dataList = new ArrayList<>();
                isLastPage = false;
                currentPage = 0;
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
                binding.recyclerViewReservationsHost.setLayoutManager(gridLayoutManager);


                adapter = new ReservationHostViewAdapter(getActivity(), dataList);
                binding.recyclerViewReservationsHost.setAdapter(adapter);
                loadPage();

            }
        });
        binding.recyclerViewReservationsHost.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    private void setupSpinner() {
        List<String> options = List.of("ANY", ReservationStatus.ACCEPTED.toString(),
                ReservationStatus.PENDING.toString(),
                ReservationStatus.CANCELED.toString(),
                ReservationStatus.DECLINED.toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinner.setAdapter(adapter);
    }
}