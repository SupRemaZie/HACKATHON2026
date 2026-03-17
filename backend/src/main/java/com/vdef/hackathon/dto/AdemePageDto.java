package com.vdef.hackathon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AdemePageDto {

    private long total;
    private List<AdemeLineDto> results;

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public List<AdemeLineDto> getResults() { return results; }
    public void setResults(List<AdemeLineDto> results) { this.results = results; }
}
