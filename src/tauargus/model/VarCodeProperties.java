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

import org.apache.commons.lang3.StringUtils;
import tauargus.extern.dataengine.TauArgus;

public final class VarCodeProperties {
    
    private final TauArgus tauArgus = Application.getTauArgusDll();
    
    private final int[] parent = new int[1];
    private final int[] active = new int[1];
    private final int[] missing = new int[1];
    private final int[] level = new int[1];
    private final int[] nChildren = new int[1];
    private final String[] code = new String[1];
    
    public VarCodeProperties() {
    }
    
    public VarCodeProperties(int varIndex, int codeIndex) {
        setCode(varIndex, codeIndex);
    }
    
    public boolean setCode(int varIndex, int codeIndex) {
        boolean b = tauArgus.GetVarCodeProperties(varIndex, codeIndex, parent, active, missing, level, nChildren, code);
        if (StringUtils.isEmpty(code[0])) {
            code[0] = "Total";
        }
        return b;
    }

    public boolean isParent() {
        return parent[0] == 1;
    }

    public boolean isActive() {
        return active[0] == 1;
    }

    public boolean isMissing() {
        return missing[0] == 1;
    }

    public int getLevel() {
        return level[0];
    }

    public int getnChildren() {
        return nChildren[0];
    }

    public String getCode() {
        return code[0];
    }
}
