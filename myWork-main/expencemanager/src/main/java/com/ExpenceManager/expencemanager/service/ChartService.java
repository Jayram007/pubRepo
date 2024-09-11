package com.ExpenceManager.expencemanager.service;

import com.ExpenceManager.expencemanager.dto.ViewDto;

import java.io.IOException;

public interface ChartService {
    void setCategoryChartData(ViewDto viewDto);

    void setYearlyChartData(ViewDto viewDto);

    void getMothlyChart(ViewDto viewDto, int year);

    public String setLineChartData(String type) throws IOException;
}
