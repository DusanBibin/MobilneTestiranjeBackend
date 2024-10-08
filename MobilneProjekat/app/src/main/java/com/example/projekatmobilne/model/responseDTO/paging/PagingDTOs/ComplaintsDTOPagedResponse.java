package com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs;

import com.example.projekatmobilne.adapters.AdapterItems.AccommodationHostItem;
import com.example.projekatmobilne.model.responseDTO.ComplaintDTO;
import com.example.projekatmobilne.model.responseDTO.paging.Pageable;
import com.example.projekatmobilne.model.responseDTO.paging.Sort;

import java.util.List;

public class ComplaintsDTOPagedResponse {
    private List<ComplaintDTO> content;
    private Pageable pageable;
    private boolean last;
    private int totalPages;
    private int totalElements;
    private int size;
    private int number;
    private Sort sort;
    private int numberOfElements;
    private boolean first;
    private boolean empty;

    public ComplaintsDTOPagedResponse(List<ComplaintDTO> content, Pageable pageable, boolean last, int totalPages, int totalElements, int size, int number, Sort sort, int numberOfElements, boolean empty, boolean first) {
        this.content = content;
        this.pageable = pageable;
        this.last = last;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.size = size;
        this.number = number;
        this.sort = sort;
        this.numberOfElements = numberOfElements;
        this.empty = empty;
        this.first = first;
    }

    public List<ComplaintDTO> getContent() {
        return content;
    }

    public void setContent(List<ComplaintDTO> content) {
        this.content = content;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
}
