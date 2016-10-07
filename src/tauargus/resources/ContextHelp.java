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

// hoe doen we het met open microdata

package tauargus.resources;

/**
 *
 * @author pibd05
 */
public enum ContextHelp {

   MAIN_FRAME("tauargus.gui.FrameMain", "MainFrame"), 
     
     OPEN_MICRO("tauargus.gui.DialogOpenMicrodata", "OpenMicrodata"),
     OPEN_TABULAR("tauargus.gui.DialogOpenTable", "OpenTable"),
     OPEN_TABULARSET("tauargus.gui.DialogOpenTableSet", "OpenTableSet"),
//     OPEN_BATCH
     
     SPECIFY_METADATA("tauargus.gui.DialogSpecifyMetadata", "SpecifyMetaFile"),
     SPECIFY_TABLES_MICRO("tauargus.gui.DialogSpecifyTablesMicro", "SpecifyTablesMicro"),
     SPECIFY_TABLES_TABULAR("tauargus.gui.DialogSpecifyTablesTabular", "SpecifyTablesTabular"),
    
     MODIFY_SELECTTABLE("tauargus.gui.DialogSelectTable", "SelectTable"),
     MODIFY_LINKEDTABLES("tauargus.gui.DialogLinkedTables", "LinkedTables"),
     
     OUTPUT_SAVETABLE("tauargus.gui.DialogSaveTable", "SaveTable"),
//     OUTPUT_VIEWREPORT("tauargus.gui.
     OUTPUT_GENERATEAPRIORI("tauargus.gui.DialogAPriori", "GenerateApriori"),
     OUTPUT_WRITEBATCH("tauargus.gui.DialogWriteBatchFile", "WriteBatchFile"),
     
     HELP_OPTIONS("tauargus.gui.DialogOptions", "HelpOptions"),
     
     OTHER_GLOBARRECODE("tauargus.gui.DialogGlobalRecode", "GlobalRecode"),
     OTHER_APRIORI("tauargus.gui.DialogReadApriori", "ReadApriori"),
     
     SUPPRESS_HYPERCUBE("tauargus.gui.DialogHypercubeParameters", "Hypercube"),
//     SUPPRESS_MODULAR("tauargus.gui.
//     SUPPRESS_OPTIMAL("tauargus.gui.
     SUPPRESS_NETWORK("tauargus.gui.DialogNetwork", "Network"),
//     SUPPRESS_CTA("tauargus.gui.
     SUPPRESS_ROUND("tauargus.gui.DialogRoundingParameters", "ControlledRound");
//     SUPPRESS_AUDIT("tauargus.gui.");
            

    public String nameddest;
    public String className;

    private ContextHelp(String className, String name) {
        this.nameddest = name;
        this.className = className;
    }

    public static String fromClassName(String className) {
        for (ContextHelp help : ContextHelp.values()) {
            if (help.className.equalsIgnoreCase(className)) {
                return help.nameddest;
            }
        }
        return null;
    }
    
 //   public static String fromClassName(String className, boolean isHousehold) {
 //       if(isHousehold){
 //           return ContextHelp.HOUSEHOLD_RISK_SPECIFICATION.nameddest;
 //       } else {
 //           return ContextHelp.INDIVIDUAL_RISK_SPECIFICATION.nameddest;
 //       }
 //   }

}
