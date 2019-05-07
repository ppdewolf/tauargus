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
    private TreeMap<String,Double> Means = new TreeMap<>();
    private TreeMap<String,Double> Medians = new TreeMap<>();
    private TreeMap<String,Double> Maxs = new TreeMap<>();
    private TreeMap<String,double[]> Percentiles = new TreeMap<>();
    
    private void Initiate(TreeMap<String,Double> SDMap){
        SDMap.put("AD", 0.0);
        SDMap.put("RAD", 0.0);
        SDMap.put("DR", 0.0);
    }
    
    public CKMInfoLoss(){
        this.Means.clear();
        this.Medians.clear();
        this.Maxs.clear();
        this.Percentiles.clear();
        Initiate(this.Means);
        Initiate(this.Medians);
        Initiate(this.Maxs);
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
