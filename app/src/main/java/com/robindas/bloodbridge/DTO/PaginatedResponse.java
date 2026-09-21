package com.robindas.bloodbridge.DTO;

import java.util.List;

/**
 * Generic DTO to wrap Spring Data Page response.
 */
public class PaginatedResponse<T> {

    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int size;
    private int number;

    public PaginatedResponse() {

    }

    public List<T> getContent() {
        return content;
    }
    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }
    public void setTotalElements(long totalElements) {
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

}
