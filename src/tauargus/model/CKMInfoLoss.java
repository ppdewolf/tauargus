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

import java.util.TreeMap;

// This class stores InformationLoss measures when applying 
// noise addition with CKM
public class CKMInfoLoss{
    private final TreeMap<String,Double> Means = new TreeMap<>();
    private final TreeMap<String,Double> Medians = new TreeMap<>();
    private final TreeMap<String,Double> Maxs = new TreeMap<>();
    private final TreeMap<String,Double> Mins = new TreeMap<>();
    private final TreeMap<String,double[]> Percentiles = new TreeMap<>();
    private final TreeMap<String,ECDF> ECDFcounts = new TreeMap<>();
    private final TreeMap<String,double[]> Diffs = new TreeMap<>();
    
    private int false_zeros;
    private int false_nonzeros;
    private int numberofcells;
    private int numberofempty;
    
    private void Initiate(TreeMap<String,Double> SDMap){
        SDMap.put("AD", 0.0);
        SDMap.put("RAD", 0.0);
        SDMap.put("DR", 0.0);
        SDMap.put("ADnonempty",0.0);
        SDMap.put("RADnonempty",0.0);
        SDMap.put("DRnonempty",0.0);
    }

    public CKMInfoLoss(){
        this.false_zeros = 0;
        this.false_nonzeros = 0;
        this.numberofcells = 0;
        this.Means.clear();
        this.Medians.clear();
        this.Maxs.clear();
        this.Percentiles.clear();
        this.ECDFcounts.clear();
        this.Diffs.clear();
        
        Initiate(this.Means);
        Initiate(this.Medians);
        Initiate(this.Maxs);
        Initiate(this.Mins);
    }
    
    public void setDiffs(String Name, double[] diffs){
        this.Diffs.put(Name,diffs);
    }
    
    public double[] GetDiffs(String Name){
        return this.Diffs.get(Name);
    }
    
    public void SetNumberOfCells(int n){
        this.numberofcells = n;
    }
    
    public int GetNumberOfCells(){
        return this.numberofcells;
    }

    public void SetNumberOfEmpty(int n){
        this.numberofempty = n;
    }
    
    public int GetNumberOfEmpty(){
        return this.numberofempty;
    }
    
    public void SetECDFcounts(String Name, ECDF ecdf){
        this.ECDFcounts.put(Name, ecdf);
    }
    
    public ECDF GetECDFcounts(String Name){
        return this.ECDFcounts.get(Name);
    }    
    
    public void SetFalseZeros(int Value){
        this.false_zeros = Value;
    }
    
    public int GetFalseZeros(){
        return this.false_zeros;
    }
    
    public void SetFalseNonzeros(int Value){
        this.false_nonzeros = Value;
    }
    
    public int GetFalseNonzeros(){
        return this.false_nonzeros;
    }
    
    public void SetMean(String Name, double Value){
        this.Means.put(Name, Value);
    }

    public double GetMean(String Name){
        return this.Means.get(Name);
    }

    public void SetMedian(String Name, double Value){
        this.Medians.put(Name, Value);
    }

    public double GetMedian(String Name){
        return this.Medians.get(Name);
    }

    public void SetMins(String Name, double Value){
        this.Mins.put(Name, Value);
    }

    public double GetMins(String Name){
        return this.Mins.get(Name);
    }
    
    public void SetMaxs(String Name, double Value){
        this.Maxs.put(Name, Value);
    }

    public double GetMaxs(String Name){
        return this.Maxs.get(Name);
    }
    
    public void SetPercentiles(String Name, double... percents){
        this.Percentiles.put(Name, new double[percents.length]);
        this.Percentiles.put(Name, percents);
    }
    
    public double[] GetPercentiles(String Name){
        return this.Percentiles.get(Name);
    }
}
