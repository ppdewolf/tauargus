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

//TODO: test for String format and add the possibility to add/read/write date/time format
package tauargus.model;;

//import tauargus.model.ArgusException;
//import argus.model.DataFilePair;
//import argus.utils.StrUtils;
//import argus.utils.Tokenizer;
//import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
//import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
//import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.swing.JOptionPane;
//import muargus.model.MetadataMu;
import argus.model.SpssVariable;
import com.ibm.statistics.plugin.Case;
import com.ibm.statistics.plugin.Cursor;
import com.ibm.statistics.plugin.DataUtil;
//import com.ibm.statistics.plugin.NumericMissingValueType;
import com.ibm.statistics.plugin.StatsException;
import com.ibm.statistics.plugin.StatsUtil;
//import com.ibm.statistics.plugin.Variable;
//import com.ibm.statistics.plugin.VariableFormat;
import java.awt.Frame;
import javax.swing.JFileChooser;
//import muargus.model.VariableMu;

/**
 *
 * @author pibd05
 */
public class SpssUtilsTau {

    public final static String tempDataFileExtension = ".dat";
    public final static String tempName = "temp";
    public final static int NUMERIC = 0;
    public static boolean fixed = true;
    public final static ArrayList<SpssVariable> spssVariables = new ArrayList<>();
    public static String spssDataFileName;
    public static File spssTempDataFiles;
    public static File safFile;
    public static File safeSpssFile;// = new File("C:\\Users\\Gebruiker\\Desktop\\safe.sav");
    
    /**
     * Gets the variables from spss. For every variable an instance of the
     * SpssVariable class is made containing all the information of this
     * variable.
     *
     * @param metadata Metadata file.
     * @param parent no longer needed
     * @return List List containing the SpssVariable instances.
     */
    public static boolean getVariablesFromSpss(String fileName) throws ArgusException{ //), Frame parent) {
  //      getSpssInstallationDirectory(parent);
        try{
            StatsUtil.start();
            StatsUtil.stop();
        }catch(Exception se){throw new ArgusException("Error opening SPSS.\nCheck your SPSS license?");}            

        spssVariables.clear();
        if (spssVariables.size() < 1) {
            try {
                StatsUtil.start();
                StatsUtil.submit("get file = \"" + fileName + "\".");
                Cursor c = new Cursor();
                for (int i = 0; i < StatsUtil.getVariableCount(); i++) {
                    SpssVariable variable = new SpssVariable(StatsUtil.getVariableName(i), StatsUtil.getVariableFormatDecimal(i),
                            StatsUtil.getVariableFormatWidth(i), StatsUtil.getVariableMeasurementLevel(i),
                            StatsUtil.getVariableType(i), StatsUtil.getVariableLabel(i), StatsUtil.getVariableAttributeNames(i),
                            StatsUtil.getVariableFormat(i));
                    // set numeric or string missings & value labels
                    if (variable.getVariableType() == SpssUtilsTau.NUMERIC) {
                        variable.setNumericValueLabels(c.getNumericValueLabels(i));
                        variable.setNumericMissings(StatsUtil.getNumericMissingValues(i));
                    } else {
                        variable.setStringValueLabels(c.getStringValueLabels(i));
                        variable.setStringMissings(StatsUtil.getStringMissingValues(i));
                    }
                    SpssUtilsTau.spssVariables.add(variable);
                }
 //               metadata.setRecordCount(StatsUtil.getCaseCount());
                StatsUtil.stop();
            } catch (StatsException e) {
              throw new ArgusException ("Reading the SPSS metadata failed\n" + e);              
            }
        }
        return true;
    }

    public static SpssVariable getSPSSVar (int i){
      SpssVariable hVar = null;
      if (i<spssVariables.size()){hVar = spssVariables.get(i);};
      return hVar;              
    }
    
       
    public static  int SpssVariableCount(){;
      return spssVariables.size()+1;
    }
    
    public static  boolean checkSpssMeta(Metadata metadata) throws ArgusException{
      int is, im, current = -1, isFound;
      for (im=0;im<metadata.variables.size();im++){
        isFound = -1;   
        for(is=0;is<SpssVariableCount()-1;is++){
          if (getSPSSVar(is).getName().equals(metadata.variables.get(im).name))
            {isFound = is;}
        }  
        if (isFound == -1){throw new ArgusException 
          ("Variable "+ metadata.variables.get(im).name+ " could not be found in the SPSS file" );}
        if (isFound < current) {throw new ArgusException ("Wrong order of the variables ("+
                                                           getSPSSVar(isFound).getName()+")");}
        if (getSPSSVar(isFound).getVariableLength() != metadata.variables.get(im).varLen){
           throw new ArgusException ("Wrong variable length for variable ("+
                                                           getSPSSVar(isFound).getName()+")");  
        }
        current = isFound;

        
        getSPSSVar(isFound).setSelected(true);
        }
            
        return true;
    }

    /**
     * Generates temporary data from the spss data file.
     *
     * @param metadata Metadata file.
     */
    public static void exportSpssData(String fn) {
        if (SpssUtilsTau.fixed) {
            SpssUtilsTau.writeFixedFormat(fn);
        } else {
            SpssUtilsTau.writeFreeFormat("Komt nog wel");
        }
    }

    /**
     *
     * @param metadata
     * @return
     */
    private static String getFilter() {
        // check if variables are selected
        boolean noVariablesSelected = true;
        for (SpssVariable variable : SpssUtilsTau.spssVariables) {
            if (variable.isSelected()) {
                noVariablesSelected = false;
                break;
            }
        }

        /* Make a String of variable names to use as a filter. If no variables
         are selected (when a .rda file exists), use all variables specified
         in the metadata, otherwise only the selected variables */
        String variableFilter = "";
        for (SpssVariable v : SpssUtilsTau.spssVariables) {
            if (noVariablesSelected || v.isSelected()) {
                variableFilter += v.getName() + " ";
            }
        }

        return variableFilter;
    }

    /**
     *
     * @param metadata
     */
    private static void writeFixedFormat(String fn) {
        try {
            // start spss and make an instance of dataUtil
            StatsUtil.start();
            // Sets the temporary filename
    //        SpssUtilsTau.spssTempDataFiles = File.createTempFile(SpssUtilsTau.tempName, SpssUtilsTau.tempDataFileExtension);
    //        SpssUtilsTau.spssTempDataFiles.deleteOnExit();
            // Sets the temporary filename
            SpssUtilsTau.spssTempDataFiles = new File (Application.getTempFile("SPSSTemp.dat"));
            /* make te commands for spss to write the data from the selected variables to a fixed format data file. */
            String[] command = {"SET DECIMAL=DOT.",
                "get file = '" + fn + "'.",
                "WRITE BOM=NO OUTFILE= '" + SpssUtilsTau.spssTempDataFiles.getPath()
                + "'/" + SpssUtilsTau.getFilter() + ".",
                "EXECUTE."
            };
            StatsUtil.submit(command);
            StatsUtil.stop();
 //           SpssUtilsTau.setNewDataFile(metadata);
        } catch (StatsException ex) {
 //           Logger.getLogger(SelectCombinationsController.class.getName()).log(Level.SEVERE, null, ex);
 //       } catch (IOException ex) {
 //           Logger.getLogger(SpssUtilsTau.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

 

    // WARNING: this has not been properly tested
    /**
     *
     * @param metadata
     */
    private static void writeFreeFormat(String fileName) {
        try {
            // start spss and make an instance of dataUtil
            StatsUtil.start();

            // Get the spss data file
//            String fileName = metadata.getFileNames().getDataFileName();
            StatsUtil.submit("get file = '" + fileName + "'.");

            // Make an array of variable names to use as a filter
            ArrayList<String> variables = new ArrayList<>();
            for (SpssVariable v : SpssUtilsTau.spssVariables) {
                if (v.isSelected()) {
                    variables.add(v.getName());
                }
            }
            DataUtil dataUtil = new DataUtil();
            dataUtil.setVariableFilter(variables.toArray(new String[variables.size()]));

            // fetch an array of cases. One case contains the data for the variables specified in the filter.
            Case[] data = dataUtil.fetchCases(true, 0);

            try {
                // Sets the temporary filename
                SpssUtilsTau.spssTempDataFiles = File.createTempFile(SpssUtilsTau.tempName, SpssUtilsTau.tempDataFileExtension);
                SpssUtilsTau.spssTempDataFiles.deleteOnExit();
                try (PrintWriter writer = new PrintWriter(spssTempDataFiles)) {
                    for (Case c : data) {
                        writer.println(c.toString().substring(1, c.toString().length() - 1).replace("null", "").replace(',', ';'));
                    }
                }
            } catch (FileNotFoundException ex) {
 //               Logger.getLogger(SelectCombinationsController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SpssUtilsTau.class.getName()).log(Level.SEVERE, null, ex);
            }
            StatsUtil.stop();
        } catch (StatsException ex) {
 //           Logger.getLogger(SelectCombinationsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 

    private static void getSpssInstallationDirectory(Frame parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Set IBM SPSS directory");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.showOpenDialog(null);
        System.out.println(fileChooser.getSelectedFile().getPath());
    }

}
