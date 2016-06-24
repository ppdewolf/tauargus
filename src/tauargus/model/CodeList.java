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

import tauargus.utils.TauArgusUtils;

public class CodeList {

    private static class Code {
        String code;
        int level;
        int nChild;
    }
    
    private final int numberOfActiveCodes;
    private final Code[] activeCodes;
    
    public CodeList(final int varIndex, final boolean isHierarchical) {
        numberOfActiveCodes = TauArgusUtils.getNumberOfActiveCodes(varIndex);
        activeCodes = new Code[numberOfActiveCodes];
        if (isHierarchical) {
            int activeCodeIndex = 0;
            int codeIndex = 0;
            VarCodeProperties properties = new VarCodeProperties();
            while (properties.setCode(varIndex, codeIndex)) {
                if (properties.isActive()) {
                    activeCodes[activeCodeIndex]= new Code(); //whcg
                    activeCodes[activeCodeIndex].code = properties.getCode().trim();
                    activeCodes[activeCodeIndex].level = properties.getLevel();
                    activeCodes[activeCodeIndex].nChild = properties.getnChildren();
                    activeCodeIndex++;
                }
                codeIndex++;
            }
        } 
        else {
            VarCode varCode = new VarCode();
            for (int codeIndex=0; codeIndex<numberOfActiveCodes; codeIndex++) {
                varCode.setCode(varIndex, codeIndex);
                
                activeCodes[codeIndex] = new Code();  //whcg
                activeCodes[codeIndex].code = varCode.getCodeString().trim();
                activeCodes[codeIndex].level = 1;
                activeCodes[codeIndex].nChild = 0;
            }
        }
    }
    
    public int search(String code) {
        code = code.trim();
        if (code.equals("")) {
            return 0;
        } else {
            for (int activeCodeIndex = 0; activeCodeIndex < numberOfActiveCodes; activeCodeIndex++) {
                if (code.equals(activeCodes[activeCodeIndex])) {
                    return activeCodeIndex;
                }
            }
            return -1;
        }
    }
    
    public int[] bogusRange(int activeCodeIndex) {
        int[] DA = new int[1];
        DA[0] = activeCodeIndex;
        
        int low = activeCodeIndex;
        while (low > 0 && activeCodes[low - 1].nChild == 1 && activeCodes[low - 1].level == activeCodes[low].level - 1) {
            low--;
        }
        int high = activeCodeIndex;
        while (high + 1 < numberOfActiveCodes && activeCodes[high].nChild == 1 && activeCodes[high + 1].level == activeCodes[high].level + 1) {
            high++;
        }
        int size = high - low + 1;
        DA = new int[size];
        for (int i=0; i<size; i++) {
            DA[i] = low + i;
        } 
        return DA;
    }
}
