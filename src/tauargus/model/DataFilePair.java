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

public class DataFilePair {
    private final String dataFileName;
    private final String metaFileName;
    
    public DataFilePair(String dataFileName, String metaFileName) {
        this.dataFileName = dataFileName;
        this.metaFileName = metaFileName;
    }
    
    public String getDataFileName() {
        return dataFileName;
    }

    public String getMetaFileName() {
        return metaFileName;
    }
}
