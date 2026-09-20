package com.robindas.bloodbridge.DTO;

import java.util.List;

/**
 * Generic DTO to handle Spring Data JPA paginated responses.
 * @param <T> The type of content in the list.
 */
public class PaginatedResponse<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int size;
    private int number; // Current page index
    private boolean last;

    public PaginatedResponse() {}

    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public boolean isLast() { return last; }
    public void setLast(boolean last) { this.last = last; }
}
