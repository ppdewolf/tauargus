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

public class Cell {
    public static final int UNKNOWN = -999999999;
    
    // Values for peepSortCell and peepSortHolding
    public static final int PEEP_EMPTY = -1;
    public static final int PEEP_NONE = 0;
    public static final int PEEP_CODE1 = 1;
    public static final int PEEP_CODE2 = 2;
    
    public double response; // original value of chosen response variable
    public double roundedResponse;
    public double CTAValue; // value of response after CTA suppression
    public double CKMValue; // value of response after cell key method application
    public double shadow; 
    public double cost; 
    public double cellkey;
    public double cellkeynozeros;
    public int freq;
    public CellStatus status;
    public boolean auditOk;
    public double[] maxScore = new double[TableSet.MAX_TOP_N_VAR]; // topN values
    public double[] maxScoreWeight = new double[TableSet.MAX_TOP_N_VAR]; // weight of topN value
    public int holdingFreq; 
    public double[] holdingMaxScore = new double[TableSet.MAX_TOP_N_VAR]; // topN values (holding)
    public int[] holdingNrPerMaxScore = new int[TableSet.MAX_TOP_N_VAR]; // weight of topN values (holding)
    public double peepCell; // value of biggest shadow value within peep attributions of cell
    public double peepHolding; // value of biggest shadow value within cell (holding)
    public int peepSortCell;
    public int peepSortHolding;
    public double lower;
    public double upper;
    public double realizedLower;
    public double realizedUpper;
    
    public void setStatusAndAuditByValue(int status) {
        this.auditOk = false;
        if (status < 0) {
            status = -status;
            if (status != CellStatus.SAFE_MANUAL.getValue()) {
                this.auditOk = true;
            }
        }
        this.status = CellStatus.findByValue(status);
    }
}
