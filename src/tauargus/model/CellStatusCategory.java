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

public enum CellStatusCategory {
    UNKNOWN(""), 
    SAFE_PROTECTED("P"), 
    SAFE_NOT_PROTECTED("S"), 
    PRIMARY_UNSAFE("U"), 
    SECONDARY_UNSAFE("M");
    
    private final String symbol;

    private CellStatusCategory(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
