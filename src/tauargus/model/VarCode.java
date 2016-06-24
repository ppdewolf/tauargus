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

import tauargus.extern.dataengine.TauArgus;

public class VarCode {
    private TauArgus tauArgus = Application.getTauArgusDll();
    
    private int[] codeType = new int[1];
    private String[] codeString = new String[1];
    private int[] missing = new int[1];
    private int[] level = new int[1];
    
    public VarCode() {
    }
    
    public VarCode(int varIndex, int codeIndex) {
        setCode(varIndex, codeIndex);
    }
    
    public boolean setCode(int varIndex, int codeIndex) {
        return tauArgus.GetVarCode(varIndex, codeIndex, codeType, codeString, missing, level);
    }

    public int getCodeType() {
        return codeType[0];
    }
    
    public String getCodeString() {
        return codeString[0];
    }
    
    public boolean isMissing() {
        return missing[0] == 1;
    }
    
    public int getLevel() {
        return level[0];
    }
}
