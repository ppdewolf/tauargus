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

public enum SuppressionMethod {
    GHMITER("Hypercube"),
    HITAS("Modular optimisation"),
    OPTIMAL("Full optimisation"),
    ROUNDING("Rounding"),
    NETWORK ("Network"),
    MARGINAL("marginal suppression"),
    UWE("UWE optimisation"),
    CTA("CTA_solution"),
    CELLKEY("Cell Key Method");

    SuppressionMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAdditivityDesirable() {
        return this == HITAS || this == OPTIMAL;
    }
    
    public boolean canUseDistanceFunction() {
        return this == SuppressionMethod.HITAS;
    }
    
    public boolean isUsingSuppliedCostFunction() {
        return this != ROUNDING;
    }
    
    public boolean isCellKey(){
        return this == CELLKEY;
    }
    
    public boolean isCosmetic() {
        return this == ROUNDING;
    }

    public boolean isMinMaxTableValueNeeded()
    {
        return this == HITAS || this == OPTIMAL || this == GHMITER;
    }

    private final String description;
}
