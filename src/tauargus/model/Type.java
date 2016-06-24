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

public enum Type {
//  type                   description     micro  tab    num
    CATEGORICAL            ("explanatory", true,  true,  false ),
    RESPONSE               ("response",    true,  true,  true  ),
    CAT_RESP               ("exp./resp.",  true,  false, true  ),
    WEIGHT                 ("weight",      true,  false, true  ),
    HOLDING                ("holding",     true,  false, false ),
    REQUEST                ("request",     true,  false, false ),
    SHADOW                 ("shadow",      false, true,  true  ),
    COST                   ("cost",        false, true,  true  ),
    FREQUENCY              ("frequency",   false, true,  true  ),
    TOP_N                  ("top N",       false, true,  true  ),
    LOWER_PROTECTION_LEVEL ("lower PL",    false, true,  true  ),
    UPPER_PROTECTION_LEVEL ("upper PL",    false, true,  true  ),
    STATUS                 ("status",      false, true,  false );
    
    private final String description;
    private final boolean availableForMicrodata;
    private final boolean availableForTabularData;
    private final boolean numeric;

    private Type(String description, boolean availableForMicrodata, boolean availableForTabularData, boolean numeric) {
        this.description = description;
        this.availableForMicrodata = availableForMicrodata;
        this.availableForTabularData = availableForTabularData;
        this.numeric = numeric;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAvailableForMicrodata() {
        return availableForMicrodata;
    }

    public boolean isAvailableForTabularData() {
        return availableForTabularData;
    }

    public boolean isNumeric() {
        return numeric;
    }

    public boolean hasDecimals() {
        return isNumeric() && this != FREQUENCY;
    }

    public boolean isCategorical() {
        return this == CATEGORICAL || this == CAT_RESP;
    }
    
    public boolean isResponse() {
        return this == RESPONSE || this == CAT_RESP;
    }
}
