/*
 * Argus Open Source
 * Software to apply Statistical Disclosure Control techniques
 *
 * Copyright 2014 Statistics Netherlands
 *
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the European Union Public Licence 
 * (EUPL) version 1.1, as published by the European Commission.
 *
 * You can find the text of the EUPL v1.1 on
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * This software is distributed on an "AS IS" basis without 
 * warranties or conditions of any kind, either express or implied.
 */
package tauargus.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ECDFGraphBuilder {
    int numberXsteps = 10;
    
    public ChartPanel CreateChart(String Name, double[] X, double increment){
        
        JFreeChart chart = ChartFactory.createXYLineChart(
                            "ECDF of " + Name, //chartTitle
                            "",                //xAxisLabel
                            "Fraction",        //yAxisLabel
                            createDataset(X, increment),
                            PlotOrientation.VERTICAL, 
                            false,             //showLegend 
                            true,              //createTooltip
                            false);            //createURL)
        chart.getTitle().setFont(chart.getTitle().getFont().deriveFont(Font.PLAIN));
        
        XYPlot mainPlot = chart.getXYPlot();
        NumberAxis xAxis = (NumberAxis) mainPlot.getDomainAxis();
        NumberAxis yAxis = (NumberAxis) mainPlot.getRangeAxis();
        
        double xstepsize = (X[X.length-1]-X[0])/numberXsteps;
        Range range = new Range(0, X[X.length-1]+xstepsize/2);
        xAxis.setAutoRange(false);
        xAxis.setRange(range);
        
        yAxis.setTickUnit(new NumberTickUnit(0.1));
        xAxis.setTickUnit(new NumberTickUnit(xstepsize));
        
        NumberFormat dfx=NumberFormat.getNumberInstance();
        dfx.setMinimumFractionDigits(3);
        dfx.setMaximumFractionDigits(3);
        xAxis.setNumberFormatOverride(dfx);
        NumberFormat dfy=NumberFormat.getNumberInstance();
        dfy.setMinimumFractionDigits(1);
        dfy.setMaximumFractionDigits(1);
        yAxis.setNumberFormatOverride(dfy);
        
        xAxis.setTickLabelFont(new Font("Dialog", Font.PLAIN, 11));
        yAxis.setTickLabelFont(new Font("Dialog", Font.PLAIN, 11));
        xAxis.setTickMarkPaint(Color.BLACK);
        yAxis.setTickMarkPaint(Color.BLACK);
        
        XYItemRenderer renderer = mainPlot.getRenderer();
        float dash[] = {5.0f};
        renderer.setSeriesStroke(1, new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.BLACK);
        mainPlot.setRenderer(renderer);

        return new ChartPanel(chart);
    }
    
    private XYDataset createDataset(double[] X, double increment) {
        int nX = X.length-1;
        int start_i = last(X, 0, nX, 0.0, nX + 1);
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("1");
        
        series.add(0.0,0.0);
        series.add(0.0,start_i*increment);
        for (int i=start_i;i<nX-1;i++){
            if (X[i]<X[i+1]){
                series.add(X[i], i*increment);
                series.add(X[i+1], i*increment);
            }
        }
        series.add(X[nX],nX*increment);
        series.add(X[nX]+(X[nX]-X[0])/(2*numberXsteps),nX*increment);
      
        dataset.addSeries(series);
      
        XYSeries series2 = new XYSeries("2");
        series2.add(0,1);
        series2.add(X[nX]+(X[nX]-X[0])/(2*numberXsteps),1);
      
        dataset.addSeries(series2);
      
        return dataset;
    }
    
    // returns the index of LAST occurrence of a value <= x in arr[0..n-1]
    // between index low and index high
    private static int last(double arr[], int low, int high, double x, int n) 
    { 
        if (high >= low) 
        { 
            int mid = low + (high - low)/2; 
            //if (( mid == n-1 || x < arr[mid+1]) && Math.abs(arr[mid] - x) <= EPSILON) 
            if (( mid == n-1 || x < arr[mid+1]) && arr[mid]<=x) 
                 return mid; 
            else if (x < arr[mid]) 
                return last(arr, low, (mid -1), x, n); 
            else
                return last(arr, (mid + 1), high, x, n); 
        } 
    return -1; 
    } 

}
