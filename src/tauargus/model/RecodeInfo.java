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

import java.util.Objects;

public class RecodeInfo {
    private final String recodeData;
    private final String missing1;
    private final String missing2;
    private final String codeList;

    public RecodeInfo(String recodeData, String missing1, String missing2, String codeList) {
        this.recodeData = recodeData;
        this.missing1 = missing1;
        this.missing2 = missing2;
        this.codeList = codeList;
    }

    public String getRecodeData() {
        return recodeData;
    }

    public String getMissing1() {
        return missing1;
    }

    public String getMissing2() {
        return missing2;
    }

    public String getCodeList() {
        return codeList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) {
            final RecodeInfo other = (RecodeInfo) obj;
            if (Objects.equals(recodeData, other.recodeData) && Objects.equals(missing1, other.missing1) && Objects.equals(missing2, other.missing2) && Objects.equals(codeList, other.codeList)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(recodeData);
        hash = 37 * hash + Objects.hashCode(missing1);
        hash = 37 * hash + Objects.hashCode(missing2);
        hash = 37 * hash + Objects.hashCode(codeList);
        return hash;
    }
}
