package com.example.mobilnetestiranjebackend.helpers;

import com.example.mobilnetestiranjebackend.DTOs.AccommodationSearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class PageConverter {
    public static <T> Page<T> convertListToPage(int page, int size, List<T> accommodationList){
        Pageable pageRequest = PageRequest.of(page, size);

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), accommodationList.size());

        List<T> pageContent;
        if(start > end) pageContent = new ArrayList<>();
        else pageContent = accommodationList.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, accommodationList.size());
    }
}
