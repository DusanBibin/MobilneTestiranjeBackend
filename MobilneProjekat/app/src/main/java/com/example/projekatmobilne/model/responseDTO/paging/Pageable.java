package com.example.projekatmobilne.model.responseDTO.paging;

public class Pageable {
    private Sort sort;
    private int offset;
    private int pageNumber;
    private int pageSize;
    private boolean unpaged;
    private boolean paged;

    public Pageable(Sort sort, int offset, int pageNumber, int pageSize, boolean unpaged, boolean paged) {
        this.sort = sort;
        this.offset = offset;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.unpaged = unpaged;
        this.paged = paged;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isUnpaged() {
        return unpaged;
    }

    public void setUnpaged(boolean unpaged) {
        this.unpaged = unpaged;
    }

    public boolean isPaged() {
        return paged;
    }

    public void setPaged(boolean paged) {
        this.paged = paged;
    }
}
