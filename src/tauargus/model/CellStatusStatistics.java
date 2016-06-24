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

public class CellStatusStatistics {
    // Also make room to store totals at the end.
    int n = CellStatus.size() + 1;
    
    public int[] freq = new int[n];
    public int[] cellFreq = new int[n];
    public int[] holdingFreq = new int[n];
    public double[] cellResponse = new double[n];
    public double[] cellCost = new double[n];

    public int totalPrimaryUnsafe() {
        int total = 0;
        for (CellStatus cellStatus : CellStatus.values()) {
            if (cellStatus.isPrimaryUnsafe()) {
                total += freq[cellStatus.getValue()];
            }
        }
        return total;
    }
   public int totalSecondaryUnsafe(){
      int total = 0;
      for (CellStatus cellStatus : CellStatus.values()) {
        if (cellStatus.isSecundaryUnsafe()) {
            total += freq[cellStatus.getValue()];
         }
      }
     return total;
   }
}
