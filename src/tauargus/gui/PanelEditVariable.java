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

package tauargus.gui;

import argus.utils.StrUtils;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.io.File;
import static java.lang.Integer.min;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.BadLocationException;
import org.apache.commons.lang3.StringUtils;
import tauargus.model.ArgusException;
import tauargus.model.Metadata;
import tauargus.model.Type;
import tauargus.model.Variable;
import tauargus.utils.SwingUtils;
import tauargus.utils.TauArgusUtils;

public class PanelEditVariable extends javax.swing.JPanel {

    /**
     * Creates new form PanelEditVariable
     */
    
    Variable currentVariable;
    Metadata metadata;
    int dataType;
    int dataOrigin;
    
    final Map<Type, javax.swing.JRadioButton> buttonMap;
    final Map<String, javax.swing.JRadioButton> CKMMap;
    final Map<String, javax.swing.JRadioButton> ScalingMap;
    
    public PanelEditVariable() {
        initComponents();
        
        setScaleTableModel();
        
        createComponentArrays();

        buttonMap = new EnumMap<>(Type.class);
        buttonMap.put(Type.CATEGORICAL, radioButtonExplanatory);
        buttonMap.put(Type.RESPONSE, radioButtonResponse);
        buttonMap.put(Type.CAT_RESP, radioButtonExpResp);
        buttonMap.put(Type.WEIGHT, radioButtonSampleWeight);
        buttonMap.put(Type.HOLDING, radioButtonHoldingIndicator);
        buttonMap.put(Type.REQUEST, radioButtonRequestProtection);
        buttonMap.put(Type.RECORD_KEY, radioButtonRecordKey);
        buttonMap.put(Type.SHADOW, radioButtonShadow);
        buttonMap.put(Type.COST, radioButtonCost);
        buttonMap.put(Type.FREQUENCY, radioButtonFrequency);
        buttonMap.put(Type.TOP_N, radioButtonTopN);
        buttonMap.put(Type.LOWER_PROTECTION_LEVEL, radioButtonLowerProtectionLevel);
        buttonMap.put(Type.UPPER_PROTECTION_LEVEL, radioButtonUpperProtectionLevel);
        buttonMap.put(Type.STATUS, radioButtonStatusIndicator);
        
        CKMMap = new HashMap<>();
        CKMMap.put("N", radioButtonNoCKM);
        CKMMap.put("T", radioButtonTopK);
        CKMMap.put("M", radioButtonMean);
        CKMMap.put("D", radioButtonSpread);
        CKMMap.put("V", radioButtonValue);
        
        ScalingMap = new HashMap<>();
        ScalingMap.put("F",radioButtonFlex);
        ScalingMap.put("N",radioButtonSimple);
    }

    private Type buttonToType(JRadioButton button) {
        for (Map.Entry<Type, javax.swing.JRadioButton> entry : buttonMap.entrySet()) {
            if (entry.getValue().equals(button)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    private javax.swing.JRadioButton typeToButton(Type type) {
        return buttonMap.get(type);
    }
    
    private String buttonToCKMType(JRadioButton button, Map<String, javax.swing.JRadioButton> searchMap){
        for (Map.Entry<String, javax.swing.JRadioButton> entry : searchMap.entrySet()) {
            if (entry.getValue().equals(button)) {
                return entry.getKey();
            }
        }
        return "";
    }
    
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
        for (Map.Entry<Type, javax.swing.JRadioButton> entry : buttonMap.entrySet()) {
            if (metadata.dataOrigin == Metadata.DATA_ORIGIN_MICRO) {
                entry.getValue().setVisible(entry.getKey().isAvailableForMicrodata());
            } else {
                entry.getValue().setVisible(entry.getKey().isAvailableForTabularData());
            }
        }
    }
    
    public void setDataType(int dataType) {
        this.dataType = dataType;
        labelStartingPosition.setEnabled(dataType == Metadata.DATA_FILE_TYPE_FIXED);
        textFieldStartingPosition.setEnabled(dataType == Metadata.DATA_FILE_TYPE_FIXED);
    }
    
    public void setDataOrigin(int dataOrigin) {
        this.dataOrigin = dataOrigin;
    }
    
    public void enableForSPSS (Boolean isSPSS){
        textFieldStartingPosition.setEnabled(!isSPSS);
        textFieldLength.setEnabled(!isSPSS);
        textFieldDecimals.setEnabled(!isSPSS);
        textFieldMissing1.setEnabled(!isSPSS);
        textFieldMissing2.setEnabled(!isSPSS);
        textFieldName.setEnabled(!isSPSS);
        textFieldMissing1.setEnabled(!isSPSS);
        textFieldMissing2.setEnabled(!isSPSS); 
    }

    public void load(Variable variable) {
        // Empty all text fields except name and length...
        currentVariable = variable;
        textFieldStartingPosition.setText("");
        textFieldDecimals.setText("");
        textFieldTotalCode.setText("");
        for (int i=0; i<Variable.MAX_NUMBER_OF_MISSINGS; i++) {
            textFieldMissing[i].setText("");
        }
        for (int i=0; i<Variable.MAX_NUMBER_OF_DIST; i++) {
            textFieldDistanceFunction[i].setText("");
        }
        textFieldCodeListFileName.setText("");
        for (int i=0; i<Variable.MAX_NUMBER_OF_HIER_LEVELS; i++) {
            textFieldHierLevel[i].setText("");
        }
        textFieldLeadingString.setText("");
        textFieldHierFileName.setText("");
        textFieldPTableFreqFileName.setText("");
        textFieldPTableContFileName.setText("");
        textFieldPTableSepFileName.setText("");

        for (int i=0; i<Variable.MAX_NUMBER_OF_REQUESTS; i++) {
            textFieldRequestCode[i].setText("");
        }
        textFieldStatusSafe.setText("");
        textFieldStatusUnsafe.setText("");
        textFieldStatusProtect.setText("");
        textFieldCKMTopK.setText(Integer.toString(variable.CKMTopK));
        
        // Clear selections...
        checkBoxDistance.setSelected(false);
        buttonGroupCodelist.clearSelection();
        buttonGroupHierType.clearSelection();
        buttonGroupPTable.clearSelection();
        buttonGroupCKM.clearSelection();
        buttonGroupScaling.clearSelection();

        // Set basic attributes...
        textFieldName.setText(variable.name);
        if (dataType != Metadata.DATA_FILE_TYPE_FREE) {
            textFieldStartingPosition.setText(Integer.toString(variable.bPos));
        }
        textFieldLength.setText(Integer.toString(variable.varLen));

        JRadioButton radioButton = typeToButton(variable.type);
        radioButton.setSelected(true);

        if (variable.hasDecimals()) {
            textFieldDecimals.setText(Integer.toString(variable.nDecimals));
        }       
        
        switch (variable.type) {
            case CATEGORICAL:
            case CAT_RESP:
                textFieldTotalCode.setText(variable.totCode);
                for (int i=0; i<Variable.MAX_NUMBER_OF_MISSINGS; i++) {
                    textFieldMissing[i].setText(variable.missing[i]);
                }
                // Codelist...
                radioButtonCodelistAutomatic.setSelected(true);
                if (StringUtils.isNotBlank(variable.codeListFile)) {
                    radioButtonCodelistFilename.setSelected(true);
                    textFieldCodeListFileName.setText(variable.codeListFile);
                }
                // Distance function...
                checkBoxDistance.setSelected(variable.hasDistanceFunction);
                if (variable.hasDistanceFunction) {
                    for (int i=0; i<Variable.MAX_NUMBER_OF_DIST; i++) {
                        textFieldDistanceFunction[i].setText(Integer.toString(variable.distanceFunction[i]));
                    }
                }
                // Hieracrhy...
                if (variable.hierarchical == Variable.HIER_NONE) {
                    radioButtonHierNone.setSelected(true);
                } 
                else if (variable.hierarchical == Variable.HIER_LEVELS) {
                    radioButtonHierData.setSelected(true);
                    for (int i = 0, nHierLevels = 0; nHierLevels < variable.hierLevelsSum; i++) {
                        textFieldHierLevel[i].setText(Integer.toString(variable.hierLevels[i]));
                        nHierLevels += variable.hierLevels[i];
                    }
                }
                else {
                    radioButtonHierFile.setSelected(true);
                    textFieldLeadingString.setText(variable.leadingString);
                    textFieldHierFileName.setText(variable.hierFileName);
                }
                break;

            case REQUEST:
                for (int i=0; i<Variable.MAX_NUMBER_OF_REQUESTS; i++) {
                    textFieldRequestCode[i].setText(variable.requestCode[i]);
                }
                break;
                
            case STATUS:
                textFieldStatusSafe.setText(metadata.safeStatus);
                textFieldStatusUnsafe.setText(metadata.unSafeStatus);
                textFieldStatusProtect.setText(metadata.protectStatus);
                break;
                
            case RESPONSE:
                JRadioButton CKMradioButton = CKMMap.get(variable.CKMType);
                CKMradioButton.setSelected(true);
                textFieldCKMTopK.setText(Integer.toString(variable.CKMTopK));
                checkBoxIncludeZeros.setSelected(variable.zerosincellkey);
                checkBoxParity.setSelected(variable.CKMapply_even_odd);
                checkBoxSeparation.setSelected(variable.CKMseparation);
                if (!variable.CKMType.equals("N")){
                    switch (variable.CKMscaling){
                        case "F": radioButtonFlex.setSelected(true);
                            break;
                        case "N": radioButtonSimple.setSelected(true);
                            break;
                        default: radioButtonSimple.setSelected(true);
                            break;
                    }
                }
                break;
                
            case RECORD_KEY:
                // Only show file name, without directory-structure-name
                textFieldPTableFreqFileName.setText(variable.PTableFile.substring(variable.PTableFile.lastIndexOf("\\")+1));
                textFieldPTableContFileName.setText(variable.PTableFileCont.substring(variable.PTableFileCont.lastIndexOf("\\")+1));
                textFieldPTableSepFileName.setText(variable.PTableFileSep.substring(variable.PTableFileSep.lastIndexOf("\\")+1));
                if (StringUtils.isBlank(variable.PTableFile)) {
                    textFieldPTableFreqFileName.setText("");
                }
                if (StringUtils.isBlank(variable.PTableFileCont)) {
                    textFieldPTableContFileName.setText("");
                }
                if (StringUtils.isBlank(variable.PTableFileSep)) {
                    textFieldPTableSepFileName.setText("");
                }
                break;
        } 
        
        panelSetEnabled(true);
    }
    
    public void save(Variable variable) throws ArgusException {
        variable.name = textFieldName.getText();
        variable.bPos = 0;
        if (metadata.dataOrigin == Metadata.DATA_ORIGIN_MICRO) {
           if(metadata.dataFileType != Metadata.DATA_FILE_TYPE_FREE){ 
            variable.bPos = Integer.parseInt(textFieldStartingPosition.getText());
           } 
        }
        variable.varLen = Integer.parseInt(textFieldLength.getText());
        variable.type = buttonToType(SwingUtils.getSelectedButton(buttonGroupVariableType));

        if (variable.hasDecimals()) {
            try{variable.nDecimals = StrUtils.toInteger(textFieldDecimals.getText());}catch (argus.model.ArgusException ex){}
        }
        
        if (variable.isCategorical()) {
            variable.totCode = textFieldTotalCode.getText();

            variable.missing = new String[Variable.MAX_NUMBER_OF_MISSINGS];
            for (int i=0; i<Variable.MAX_NUMBER_OF_MISSINGS; i++) {
                if (metadata.dataFileType == Metadata.DATA_FILE_TYPE_FIXED) {
                    if (textFieldMissing[i].getText().length() > variable.varLen) {
                        textFieldMissing[i].requestFocusInWindow();
                        throw new ArgusException("length missing code is too long");
                    }
                }
                variable.missing[i] = textFieldMissing[i].getText();
                variable.missing[i] = variable.normaliseMissing(variable.missing[i]);
            }

            variable.hasDistanceFunction = checkBoxDistance.isSelected() && StringUtils.isNotBlank(textFieldDistanceFunction[0].getText());
            if (variable.hasDistanceFunction) {
                variable.distanceFunction = new int[Variable.MAX_NUMBER_OF_DIST];
                for (int i=0; i<variable.distanceFunction.length; i++) {
                    String value = textFieldDistanceFunction[i].getText();
                    if (StringUtils.isBlank(value)) {
                        if (i==0) {
                            variable.distanceFunction[0] = 1;
                        }
                        else {
                            variable.distanceFunction[i] = variable.distanceFunction[i-1];
                        }
                    }
                    else {
                        variable.distanceFunction[i] = Integer.parseInt(textFieldDistanceFunction[i].getText());
                    }
                }
            }
            if (radioButtonCodelistFilename.isSelected()) {
                if (StringUtils.isBlank(textFieldCodeListFileName.getText())) {
                    throw new ArgusException("Codelist is selected, but no codelist file is specified");
                }
                variable.codeListFile = textFieldCodeListFileName.getText();
            }
            variable.hierarchical = Variable.HIER_NONE;
            if (radioButtonHierData.isSelected() && StringUtils.isNotBlank(textFieldHierLevel[0].getText())) {
                variable.hierarchical = Variable.HIER_LEVELS;
                variable.hierLevels = new int[Variable.MAX_NUMBER_OF_HIER_LEVELS];
                variable.hierLevelsSum = 0;
                for (int i=0; i<Variable.MAX_NUMBER_OF_HIER_LEVELS; i++) {
                    if (StringUtils.isBlank(textFieldHierLevel[i].getText())) {
                        variable.hierLevels[i] = 0;
                    }
                    else {
                        variable.hierLevels[i] = Integer.parseInt(textFieldHierLevel[i].getText());
                        variable.hierLevelsSum += variable.hierLevels[i];
                    }
                }
            }
            if (radioButtonHierFile.isSelected() 
                    && StringUtils.isNotBlank(textFieldHierFileName.getText())
                    && StringUtils.isNotBlank(textFieldLeadingString.getText())) {
                variable.hierarchical = Variable.HIER_FILE;
                variable.hierFileName = StringUtils.strip(textFieldHierFileName.getText());
                variable.leadingString = StringUtils.strip(textFieldLeadingString.getText());
            }
        }

        if (variable.type == Type.REQUEST) {
            variable.requestCode = new String[Variable.MAX_NUMBER_OF_REQUESTS];
            for (int i=0; i<Variable.MAX_NUMBER_OF_REQUESTS; i++) {
                variable.requestCode[i] = textFieldRequestCode[i].getText();
            }
        }
        
        if (variable.type == Type.STATUS) {
            try {
                metadata.safeStatus = textFieldStatusSafe.getText(0, 1).toUpperCase();
                metadata.unSafeStatus = textFieldStatusUnsafe.getText(0, 1).toUpperCase();
                metadata.protectStatus = textFieldStatusProtect.getText(0, 1).toUpperCase();
            }
            catch (BadLocationException ex) {
                throw new ArgusException("Statuses (Safe/Unsafe/Protect) should consist of one character");
            }
        }
        
        if (variable.isResponse()){
            variable.CKMType = buttonToCKMType(SwingUtils.getSelectedButton(buttonGroupCKM), CKMMap);
            if (variable.CKMType.equals("T")) {
                try{
                    int TopK = Integer.parseInt(textFieldCKMTopK.getText());
                    if (TopK < 1 || TopK > 5){
                        textFieldCKMTopK.setText("");
                        textFieldCKMTopK.requestFocusInWindow();
                        throw new ArgusException("TopK should be integer in {1,...,5}");
                    }
                    variable.CKMTopK = TopK;
                }
                catch (NumberFormatException e){
                    textFieldCKMTopK.setText("");
                    textFieldCKMTopK.requestFocusInWindow();
                    throw new ArgusException("TopK should be integer in {1,...,5}");
                }
            }
            else variable.CKMTopK = 1;
            
            variable.zerosincellkey = checkBoxIncludeZeros.isSelected();
            variable.CKMapply_even_odd = checkBoxParity.isSelected();
            variable.CKMseparation = checkBoxSeparation.isSelected();
            variable.CKMscaling = buttonToCKMType(SwingUtils.getSelectedButton(buttonGroupScaling), ScalingMap);
            if (!variable.CKMType.equals("N")){
                switch (variable.CKMscaling){
                    case "F":
                        variable.CKMsigma0 = Double.parseDouble(tableScaleParams.getValueAt(1,0).toString());
                        variable.CKMsigma1 = Double.parseDouble(tableScaleParams.getValueAt(1,1).toString());
                        variable.CKMxstar = Double.parseDouble(tableScaleParams.getValueAt(1,2).toString());
                        variable.CKMq = Double.parseDouble(tableScaleParams.getValueAt(1,3).toString());
                        variable.CKMepsilon = new double[variable.CKMTopK];
                        variable.CKMepsilon[0] = 1.0;
                        for (int i=1; i<variable.CKMTopK;i++) 
                            variable.CKMepsilon[i] = Double.parseDouble(tableScaleParams.getValueAt(3,i-1).toString());
                        break;
                    case "N":
                        variable.CKMsigma1 = Double.parseDouble(tableScaleParams.getValueAt(1,0).toString());
                        variable.CKMepsilon = new double[variable.CKMTopK];
                        variable.CKMepsilon[0] = 1.0;
                        for (int i=1; i<variable.CKMTopK;i++) 
                            variable.CKMepsilon[i] = Double.parseDouble(tableScaleParams.getValueAt(3,i-1).toString());
                        break;
                }
            }
        }
        
        if (variable.type == Type.RECORD_KEY) {
            variable.PTableFile = textFieldPTableFreqFileName.getText();
            variable.PTableFileCont = textFieldPTableContFileName.getText();
            variable.PTableFileSep = textFieldPTableSepFileName.getText();
        }
    }

    /**
     * In the GUI designer we can't create arrays of components. We design the
     * individual components and this function will put them in arrays
     */
    private void createComponentArrays() {
        textFieldDistanceFunction = new javax.swing.JTextField[] { 
            textFieldDistanceFunction1, 
            textFieldDistanceFunction2,
            textFieldDistanceFunction3,
            textFieldDistanceFunction4,
            textFieldDistanceFunction5
        };
        
        textFieldHierLevel = new javax.swing.JTextField[] { 
            textFieldHierLevel1,
            textFieldHierLevel2,
            textFieldHierLevel3,
            textFieldHierLevel4,
            textFieldHierLevel5,
            textFieldHierLevel6,
            textFieldHierLevel7,
            textFieldHierLevel8,
            textFieldHierLevel9,
            textFieldHierLevel10
        };

        textFieldMissing = new javax.swing.JTextField[] { 
            textFieldMissing1,
            textFieldMissing2
        };

        textFieldRequestCode = new javax.swing.JTextField[] { 
            textFieldRequestCode1,
            textFieldRequestCode2
        };
    }

    /**
     * Enables or disables the supplied container and all components inside the
     * container depending on the value of the parameter <code>b</code>.
     *
     * @param container  The container that will be enabled or disabled
     * 
     * @param b  If <code>true</code>, the container is enabled; otherwise the
     * container is disabled
     */
    private void containerSetAllEnabled(Container container, boolean b) {
        container.setEnabled(b);
        Component[] components = container.getComponents();
        for (Component component : components) {
            component.setEnabled(b);
            if (component instanceof Container) {
                containerSetAllEnabled((Container)component, b);
            }
        }
    }
    
    private void distancePanelSetEnabled(boolean enabled) {
        containerSetAllEnabled(panelDistance, enabled);
        if (enabled) {
            for (javax.swing.JTextField textField : textFieldDistanceFunction) {
                textField.setEnabled(checkBoxDistance.isSelected());
            }
        } 
    }
    
    private void ptablePanelSetEnabled(boolean enabled) {
        containerSetAllEnabled(panelPTable, enabled);
        panelPTable.setEnabled(enabled);
    }
    
    private void codelistPanelSetEnabled(boolean enabled) {
        containerSetAllEnabled(panelCodelist, enabled);
        if (enabled) {
            textFieldCodeListFileName.setEnabled(radioButtonCodelistFilename.isSelected());
            buttonCodeListFileName.setEnabled(radioButtonCodelistFilename.isSelected());
        } 
    }
    
    private void hierarchyPanelSetEnabled(boolean enabled) {
        containerSetAllEnabled(panelHierarchy, enabled);
        if (enabled) {
            for (javax.swing.JTextField textField : textFieldHierLevel) {
                textField.setEnabled(radioButtonHierData.isSelected());
            }
            textFieldLeadingString.setEnabled(radioButtonHierFile.isSelected());
            textFieldHierFileName.setEnabled(radioButtonHierFile.isSelected());
            buttonHierFileName.setEnabled(radioButtonHierFile.isSelected());
        } 
    }
    
    public void panelSetEnabled(boolean enabled) {
        containerSetAllEnabled(this, enabled);
        if (enabled) {
            Type type = buttonToType(SwingUtils.getSelectedButton(buttonGroupVariableType));
            labelTotalCode.setVisible(type.isCategorical());
            textFieldTotalCode.setVisible(type.isCategorical());
            panelMissings.setVisible(type.isCategorical());
            // Currently only available for microdata input
            if (dataOrigin == Metadata.DATA_ORIGIN_MICRO){
                panelCKM.setVisible(type.isResponse());
                textFieldCKMTopK.setEnabled(radioButtonTopK.isSelected());
                panelPTable.setVisible(type.isRecordKey() || radioButtonRecordKey.isSelected());
                ptablePanelSetEnabled(type.isRecordKey() || radioButtonRecordKey.isSelected());
                int TopK = Integer.parseInt(textFieldCKMTopK.getText());
                
                if (TopK > 1){
                    checkBoxParity.setSelected(false);
                    checkBoxParity.setEnabled(false);
                }
                else checkBoxParity.setEnabled(true); // If CKMType = M, D or V then TopK = 1 as well
                
                setScaleTable(tableScaleParams,buttonToCKMType(SwingUtils.getSelectedButton(buttonGroupScaling), ScalingMap),Integer.parseInt(textFieldCKMTopK.getText()));
                panelScaling.setVisible(!radioButtonNoCKM.isSelected());
                panelCKMadditional.setVisible(!radioButtonNoCKM.isSelected());
        
            }
            else { // CKM with tabular input not (yet) possible
                panelCKM.setVisible(false);
                panelPTable.setVisible(false);
            }
            
            panelRequestCodes.setVisible(radioButtonRequestProtection.isSelected());
            panelStatusIndicator.setVisible(radioButtonStatusIndicator.isSelected());
            if (radioButtonStatusIndicator.isSelected()){
                textFieldStatusSafe.setText(metadata.safeStatus);
                textFieldStatusUnsafe.setText(metadata.unSafeStatus);
                textFieldStatusProtect.setText(metadata.protectStatus);
            }
            labelStartingPosition.setEnabled(dataType == Metadata.DATA_FILE_TYPE_FIXED);
            textFieldStartingPosition.setEnabled(dataType == Metadata.DATA_FILE_TYPE_FIXED);
            distancePanelSetEnabled(type.isCategorical());
            panelDistance.setVisible(type.isCategorical());
            codelistPanelSetEnabled(type.isCategorical());
            panelCodelist.setVisible(type.isCategorical());
            hierarchyPanelSetEnabled(type.isCategorical());
            panelHierarchy.setVisible(type.isCategorical());
            labelDecimals.setEnabled((type.hasDecimals()) && !(dataType == Metadata.DATA_FILE_TYPE_SPSS));
            textFieldDecimals.setEnabled(labelDecimals.isEnabled());   
            enableForSPSS(DialogSpecifyMetadata.SpssSelected); 
        }
        SwingUtilities.getWindowAncestor(this).pack();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        buttonGroupVariableType = new javax.swing.ButtonGroup();
        buttonGroupCodelist = new javax.swing.ButtonGroup();
        buttonGroupHierType = new javax.swing.ButtonGroup();
        buttonGroupPTable = new javax.swing.ButtonGroup();
        buttonGroupCKM = new javax.swing.ButtonGroup();
        buttonGroupScaling = new javax.swing.ButtonGroup();
        panelBasic = new javax.swing.JPanel();
        labelName = new javax.swing.JLabel();
        textFieldName = new javax.swing.JTextField();
        labelStartingPosition = new javax.swing.JLabel();
        textFieldStartingPosition = new javax.swing.JTextField();
        labelLength = new javax.swing.JLabel();
        textFieldLength = new javax.swing.JTextField();
        labelDecimals = new javax.swing.JLabel();
        textFieldDecimals = new javax.swing.JTextField();
        panelVariableType = new javax.swing.JPanel();
        radioButtonExplanatory = new javax.swing.JRadioButton();
        radioButtonResponse = new javax.swing.JRadioButton();
        radioButtonSampleWeight = new javax.swing.JRadioButton();
        radioButtonHoldingIndicator = new javax.swing.JRadioButton();
        radioButtonRequestProtection = new javax.swing.JRadioButton();
        radioButtonShadow = new javax.swing.JRadioButton();
        radioButtonCost = new javax.swing.JRadioButton();
        radioButtonLowerProtectionLevel = new javax.swing.JRadioButton();
        radioButtonUpperProtectionLevel = new javax.swing.JRadioButton();
        radioButtonFrequency = new javax.swing.JRadioButton();
        radioButtonTopN = new javax.swing.JRadioButton();
        radioButtonStatusIndicator = new javax.swing.JRadioButton();
        radioButtonExpResp = new javax.swing.JRadioButton();
        radioButtonRecordKey = new javax.swing.JRadioButton();
        panelMissings = new javax.swing.JPanel();
        labelMissing1 = new javax.swing.JLabel();
        textFieldMissing1 = new javax.swing.JTextField();
        labelMissing2 = new javax.swing.JLabel();
        textFieldMissing2 = new javax.swing.JTextField();
        panelRequestCodes = new javax.swing.JPanel();
        labelRequestCode1 = new javax.swing.JLabel();
        textFieldRequestCode1 = new javax.swing.JTextField();
        labelRequestCode2 = new javax.swing.JLabel();
        textFieldRequestCode2 = new javax.swing.JTextField();
        panelStatusIndicator = new javax.swing.JPanel();
        labelStatusUnsafe = new javax.swing.JLabel();
        textFieldStatusUnsafe = new javax.swing.JTextField();
        labelStatusSafe = new javax.swing.JLabel();
        textFieldStatusSafe = new javax.swing.JTextField();
        labelProtect = new javax.swing.JLabel();
        textFieldStatusProtect = new javax.swing.JTextField();
        panelCKM = new javax.swing.JPanel();
        panelCKMType = new javax.swing.JPanel();
        radioButtonSpread = new javax.swing.JRadioButton();
        radioButtonMean = new javax.swing.JRadioButton();
        radioButtonValue = new javax.swing.JRadioButton();
        radioButtonTopK = new javax.swing.JRadioButton();
        radioButtonNoCKM = new javax.swing.JRadioButton();
        textFieldCKMTopK = new javax.swing.JTextField();
        panelScaling = new javax.swing.JPanel();
        radioButtonFlex = new javax.swing.JRadioButton();
        radioButtonSimple = new javax.swing.JRadioButton();
        tableScaleParams = new javax.swing.JTable();
        panelCKMadditional = new javax.swing.JPanel();
        checkBoxParity = new javax.swing.JCheckBox();
        checkBoxSeparation = new javax.swing.JCheckBox();
        checkBoxIncludeZeros = new javax.swing.JCheckBox();
        panelDistance = new javax.swing.JPanel();
        checkBoxDistance = new javax.swing.JCheckBox();
        textFieldDistanceFunction1 = new javax.swing.JTextField();
        textFieldDistanceFunction2 = new javax.swing.JTextField();
        textFieldDistanceFunction3 = new javax.swing.JTextField();
        textFieldDistanceFunction4 = new javax.swing.JTextField();
        textFieldDistanceFunction5 = new javax.swing.JTextField();
        panelCodelist = new javax.swing.JPanel();
        radioButtonCodelistAutomatic = new javax.swing.JRadioButton();
        radioButtonCodelistFilename = new javax.swing.JRadioButton();
        textFieldCodeListFileName = new javax.swing.JTextField();
        buttonCodeListFileName = new javax.swing.JButton();
        labelTotalCode = new javax.swing.JLabel();
        textFieldTotalCode = new javax.swing.JTextField();
        panelHierarchy = new javax.swing.JPanel();
        radioButtonHierNone = new javax.swing.JRadioButton();
        radioButtonHierData = new javax.swing.JRadioButton();
        radioButtonHierFile = new javax.swing.JRadioButton();
        textFieldHierLevel1 = new javax.swing.JTextField();
        textFieldHierLevel2 = new javax.swing.JTextField();
        textFieldHierLevel3 = new javax.swing.JTextField();
        textFieldHierLevel4 = new javax.swing.JTextField();
        textFieldHierLevel5 = new javax.swing.JTextField();
        textFieldHierLevel6 = new javax.swing.JTextField();
        textFieldHierLevel7 = new javax.swing.JTextField();
        textFieldHierLevel8 = new javax.swing.JTextField();
        labelLeadingString = new javax.swing.JLabel();
        textFieldLeadingString = new javax.swing.JTextField();
        textFieldHierFileName = new javax.swing.JTextField();
        buttonHierFileName = new javax.swing.JButton();
        textFieldHierLevel9 = new javax.swing.JTextField();
        textFieldHierLevel10 = new javax.swing.JTextField();
        panelPTable = new javax.swing.JPanel();
        textFieldPTableFreqFileName = new javax.swing.JTextField();
        buttonPTableFreqFileName = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        textFieldPTableContFileName = new javax.swing.JTextField();
        buttonPTableContFileName = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        textFieldPTableSepFileName = new javax.swing.JTextField();
        buttonPTableSepFileName = new javax.swing.JButton();

        fileChooser.setDialogTitle("");

        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Attributes"));
        setName(""); // NOI18N

        panelBasic.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Variable"));

        labelName.setLabelFor(textFieldName);
        labelName.setText("Name:");

        textFieldName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                textFieldNameFocusLost(evt);
            }
        });

        labelStartingPosition.setLabelFor(textFieldStartingPosition);
        labelStartingPosition.setText("Starting position:");

        labelLength.setLabelFor(textFieldLength);
        labelLength.setText("Length:");

        labelDecimals.setLabelFor(textFieldDecimals);
        labelDecimals.setText("Decimals:");

        javax.swing.GroupLayout panelBasicLayout = new javax.swing.GroupLayout(panelBasic);
        panelBasic.setLayout(panelBasicLayout);
        panelBasicLayout.setHorizontalGroup(
            panelBasicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBasicLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBasicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBasicLayout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addGroup(panelBasicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelStartingPosition, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelLength, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelDecimals, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelBasicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textFieldStartingPosition, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(textFieldLength, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(textFieldDecimals, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelBasicLayout.createSequentialGroup()
                        .addComponent(labelName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        panelBasicLayout.setVerticalGroup(
            panelBasicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBasicLayout.createSequentialGroup()
                .addGroup(panelBasicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelName)
                    .addComponent(textFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelBasicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelStartingPosition)
                    .addComponent(textFieldStartingPosition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelBasicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelLength)
                    .addComponent(textFieldLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelBasicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDecimals)
                    .addComponent(textFieldDecimals, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelVariableType.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        buttonGroupVariableType.add(radioButtonExplanatory);
        radioButtonExplanatory.setText("Explanatory");
        radioButtonExplanatory.setMaximumSize(new java.awt.Dimension(131, 23));
        radioButtonExplanatory.setMinimumSize(new java.awt.Dimension(131, 23));
        radioButtonExplanatory.setPreferredSize(new java.awt.Dimension(131, 23));
        radioButtonExplanatory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonResponse);
        radioButtonResponse.setText("Response");
        radioButtonResponse.setMaximumSize(new java.awt.Dimension(131, 23));
        radioButtonResponse.setMinimumSize(new java.awt.Dimension(131, 23));
        radioButtonResponse.setPreferredSize(new java.awt.Dimension(131, 23));
        radioButtonResponse.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radioButtonResponseItemStateChanged(evt);
            }
        });
        radioButtonResponse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonSampleWeight);
        radioButtonSampleWeight.setText("Sample weight");
        radioButtonSampleWeight.setMaximumSize(new java.awt.Dimension(131, 23));
        radioButtonSampleWeight.setMinimumSize(new java.awt.Dimension(131, 23));
        radioButtonSampleWeight.setPreferredSize(new java.awt.Dimension(131, 23));
        radioButtonSampleWeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonHoldingIndicator);
        radioButtonHoldingIndicator.setText("Holding indicator");
        radioButtonHoldingIndicator.setMaximumSize(new java.awt.Dimension(131, 23));
        radioButtonHoldingIndicator.setMinimumSize(new java.awt.Dimension(131, 23));
        radioButtonHoldingIndicator.setPreferredSize(new java.awt.Dimension(131, 23));
        radioButtonHoldingIndicator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonRequestProtection);
        radioButtonRequestProtection.setText("Request protection");
        radioButtonRequestProtection.setMaximumSize(new java.awt.Dimension(131, 23));
        radioButtonRequestProtection.setMinimumSize(new java.awt.Dimension(131, 23));
        radioButtonRequestProtection.setPreferredSize(new java.awt.Dimension(131, 23));
        radioButtonRequestProtection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonShadow);
        radioButtonShadow.setText("Shadow");
        radioButtonShadow.setMaximumSize(new java.awt.Dimension(131, 23));
        radioButtonShadow.setMinimumSize(new java.awt.Dimension(131, 23));
        radioButtonShadow.setPreferredSize(new java.awt.Dimension(131, 23));
        radioButtonShadow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonCost);
        radioButtonCost.setText("Cost");
        radioButtonCost.setMaximumSize(new java.awt.Dimension(131, 23));
        radioButtonCost.setMinimumSize(new java.awt.Dimension(131, 23));
        radioButtonCost.setPreferredSize(new java.awt.Dimension(131, 23));
        radioButtonCost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonLowerProtectionLevel);
        radioButtonLowerProtectionLevel.setText("Lower protection level");
        radioButtonLowerProtectionLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonUpperProtectionLevel);
        radioButtonUpperProtectionLevel.setText("Upper protection level");
        radioButtonUpperProtectionLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonFrequency);
        radioButtonFrequency.setText("Frequency");
        radioButtonFrequency.setMaximumSize(new java.awt.Dimension(131, 23));
        radioButtonFrequency.setMinimumSize(new java.awt.Dimension(131, 23));
        radioButtonFrequency.setPreferredSize(new java.awt.Dimension(131, 23));
        radioButtonFrequency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonTopN);
        radioButtonTopN.setText("Top n");
        radioButtonTopN.setMaximumSize(new java.awt.Dimension(131, 23));
        radioButtonTopN.setMinimumSize(new java.awt.Dimension(131, 23));
        radioButtonTopN.setPreferredSize(new java.awt.Dimension(131, 23));
        radioButtonTopN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonStatusIndicator);
        radioButtonStatusIndicator.setText("Status indicator");
        radioButtonStatusIndicator.setMaximumSize(new java.awt.Dimension(131, 23));
        radioButtonStatusIndicator.setMinimumSize(new java.awt.Dimension(131, 23));
        radioButtonStatusIndicator.setPreferredSize(new java.awt.Dimension(131, 23));
        radioButtonStatusIndicator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonExpResp);
        radioButtonExpResp.setText("Exp. / Resp.");
        radioButtonExpResp.setMaximumSize(new java.awt.Dimension(131, 23));
        radioButtonExpResp.setMinimumSize(new java.awt.Dimension(131, 23));
        radioButtonExpResp.setPreferredSize(new java.awt.Dimension(131, 23));
        radioButtonExpResp.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radioButtonExpRespItemStateChanged(evt);
            }
        });
        radioButtonExpResp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonRecordKey);
        radioButtonRecordKey.setText("Record Key");
        radioButtonRecordKey.setMaximumSize(new java.awt.Dimension(131, 23));
        radioButtonRecordKey.setMinimumSize(new java.awt.Dimension(131, 23));
        radioButtonRecordKey.setPreferredSize(new java.awt.Dimension(131, 23));
        radioButtonRecordKey.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radioButtonRecordKeyItemStateChanged(evt);
            }
        });
        radioButtonRecordKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelVariableTypeLayout = new javax.swing.GroupLayout(panelVariableType);
        panelVariableType.setLayout(panelVariableTypeLayout);
        panelVariableTypeLayout.setHorizontalGroup(
            panelVariableTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVariableTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(radioButtonHoldingIndicator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(radioButtonRequestProtection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(radioButtonShadow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(radioButtonCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(radioButtonLowerProtectionLevel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(radioButtonUpperProtectionLevel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(radioButtonFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(radioButtonTopN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(radioButtonSampleWeight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(radioButtonResponse, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(radioButtonExplanatory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(radioButtonStatusIndicator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(radioButtonExpResp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(radioButtonRecordKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        panelVariableTypeLayout.setVerticalGroup(
            panelVariableTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVariableTypeLayout.createSequentialGroup()
                .addComponent(radioButtonExplanatory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonResponse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonExpResp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(radioButtonSampleWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonHoldingIndicator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonRequestProtection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonRecordKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonShadow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonLowerProtectionLevel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonUpperProtectionLevel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonTopN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonStatusIndicator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        panelMissings.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Missings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));
        panelMissings.setPreferredSize(new java.awt.Dimension(240, 71));

        labelMissing1.setLabelFor(textFieldMissing1);
        labelMissing1.setText("1:");

        labelMissing2.setLabelFor(textFieldMissing2);
        labelMissing2.setText("2:");

        javax.swing.GroupLayout panelMissingsLayout = new javax.swing.GroupLayout(panelMissings);
        panelMissings.setLayout(panelMissingsLayout);
        panelMissingsLayout.setHorizontalGroup(
            panelMissingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMissingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMissingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMissingsLayout.createSequentialGroup()
                        .addComponent(labelMissing1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldMissing1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMissingsLayout.createSequentialGroup()
                        .addComponent(labelMissing2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldMissing2, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelMissingsLayout.setVerticalGroup(
            panelMissingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMissingsLayout.createSequentialGroup()
                .addGroup(panelMissingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldMissing1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelMissing1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMissingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldMissing2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelMissing2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelRequestCodes.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Request codes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));
        panelRequestCodes.setPreferredSize(new java.awt.Dimension(240, 71));

        labelRequestCode1.setLabelFor(textFieldRequestCode1);
        labelRequestCode1.setText("1:");

        labelRequestCode2.setLabelFor(textFieldRequestCode2);
        labelRequestCode2.setText("2:");

        javax.swing.GroupLayout panelRequestCodesLayout = new javax.swing.GroupLayout(panelRequestCodes);
        panelRequestCodes.setLayout(panelRequestCodesLayout);
        panelRequestCodesLayout.setHorizontalGroup(
            panelRequestCodesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRequestCodesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRequestCodesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRequestCodesLayout.createSequentialGroup()
                        .addComponent(labelRequestCode1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldRequestCode1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelRequestCodesLayout.createSequentialGroup()
                        .addComponent(labelRequestCode2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldRequestCode2, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelRequestCodesLayout.setVerticalGroup(
            panelRequestCodesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRequestCodesLayout.createSequentialGroup()
                .addGroup(panelRequestCodesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelRequestCode1)
                    .addComponent(textFieldRequestCode1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRequestCodesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelRequestCode2)
                    .addComponent(textFieldRequestCode2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        panelStatusIndicator.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Status indicators", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));
        panelStatusIndicator.setPreferredSize(new java.awt.Dimension(240, 97));

        labelStatusUnsafe.setLabelFor(textFieldStatusUnsafe);
        labelStatusUnsafe.setText("Unsafe:");

        labelStatusSafe.setLabelFor(textFieldStatusSafe);
        labelStatusSafe.setText("Safe:");

        labelProtect.setLabelFor(textFieldStatusProtect);
        labelProtect.setText("Protect:");

        javax.swing.GroupLayout panelStatusIndicatorLayout = new javax.swing.GroupLayout(panelStatusIndicator);
        panelStatusIndicator.setLayout(panelStatusIndicatorLayout);
        panelStatusIndicatorLayout.setHorizontalGroup(
            panelStatusIndicatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStatusIndicatorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelStatusIndicatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labelProtect, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelStatusUnsafe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelStatusSafe, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelStatusIndicatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldStatusUnsafe, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldStatusSafe, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldStatusProtect, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelStatusIndicatorLayout.setVerticalGroup(
            panelStatusIndicatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelStatusIndicatorLayout.createSequentialGroup()
                .addGroup(panelStatusIndicatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelStatusSafe)
                    .addComponent(textFieldStatusSafe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelStatusIndicatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldStatusUnsafe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelStatusUnsafe))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelStatusIndicatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelProtect)
                    .addComponent(textFieldStatusProtect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        panelCKM.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "CKM"));
        panelCKM.setName(""); // NOI18N

        panelCKMType.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Type"));

        buttonGroupCKM.add(radioButtonSpread);
        radioButtonSpread.setText("Spread");
        radioButtonSpread.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupCKM.add(radioButtonMean);
        radioButtonMean.setText("Mean");
        radioButtonMean.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupCKM.add(radioButtonValue);
        radioButtonValue.setText("Cell value");
        radioButtonValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupCKM.add(radioButtonTopK);
        radioButtonTopK.setText("Top K");
        radioButtonTopK.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radioButtonTopKItemStateChanged(evt);
            }
        });

        buttonGroupCKM.add(radioButtonNoCKM);
        radioButtonNoCKM.setText("Not allowed");
        radioButtonNoCKM.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radioButtonNoCKMItemStateChanged(evt);
            }
        });
        radioButtonNoCKM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        textFieldCKMTopK.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        textFieldCKMTopK.setText("1");
        textFieldCKMTopK.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                textFieldCKMTopKFocusLost(evt);
            }
        });
        textFieldCKMTopK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldCKMTopKActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelCKMTypeLayout = new javax.swing.GroupLayout(panelCKMType);
        panelCKMType.setLayout(panelCKMTypeLayout);
        panelCKMTypeLayout.setHorizontalGroup(
            panelCKMTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCKMTypeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCKMTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radioButtonNoCKM)
                    .addGroup(panelCKMTypeLayout.createSequentialGroup()
                        .addGroup(panelCKMTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(radioButtonMean)
                            .addGroup(panelCKMTypeLayout.createSequentialGroup()
                                .addComponent(radioButtonTopK)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(textFieldCKMTopK, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(26, 26, 26)
                        .addGroup(panelCKMTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(radioButtonValue, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(radioButtonSpread, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        panelCKMTypeLayout.setVerticalGroup(
            panelCKMTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCKMTypeLayout.createSequentialGroup()
                .addComponent(radioButtonNoCKM)
                .addGap(0, 0, 0)
                .addGroup(panelCKMTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioButtonTopK)
                    .addComponent(radioButtonSpread)
                    .addComponent(textFieldCKMTopK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCKMTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radioButtonValue)
                    .addComponent(radioButtonMean))
                .addGap(0, 0, 0))
        );

        panelScaling.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Use scaling"));

        buttonGroupScaling.add(radioButtonFlex);
        radioButtonFlex.setText("Flex function");
        radioButtonFlex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonFlexActionPerformed(evt);
            }
        });

        buttonGroupScaling.add(radioButtonSimple);
        radioButtonSimple.setText("Simple");
        radioButtonSimple.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonSimpleActionPerformed(evt);
            }
        });

        tableScaleParams.setBackground(new java.awt.Color(240, 240, 240));
        tableScaleParams.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));
        tableScaleParams.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tableScaleParams.setColumnSelectionAllowed(true);
        tableScaleParams.setGridColor(new java.awt.Color(200, 200, 200));
        tableScaleParams.setSurrendersFocusOnKeystroke(true);
        tableScaleParams.getTableHeader().setReorderingAllowed(false);

        javax.swing.GroupLayout panelScalingLayout = new javax.swing.GroupLayout(panelScaling);
        panelScaling.setLayout(panelScalingLayout);
        panelScalingLayout.setHorizontalGroup(
            panelScalingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelScalingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelScalingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelScalingLayout.createSequentialGroup()
                        .addGroup(panelScalingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(radioButtonFlex)
                            .addComponent(radioButtonSimple))
                        .addGap(118, 118, 118))
                    .addComponent(tableScaleParams, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        panelScalingLayout.setVerticalGroup(
            panelScalingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelScalingLayout.createSequentialGroup()
                .addComponent(radioButtonFlex)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonSimple)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tableScaleParams, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tableScaleParams.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        panelCKMadditional.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Additional settings"));

        checkBoxParity.setText("Treat even and odd differently");

        checkBoxSeparation.setText("Treat small cells differently");

        checkBoxIncludeZeros.setText("Include zero contributions in cell key");

        javax.swing.GroupLayout panelCKMadditionalLayout = new javax.swing.GroupLayout(panelCKMadditional);
        panelCKMadditional.setLayout(panelCKMadditionalLayout);
        panelCKMadditionalLayout.setHorizontalGroup(
            panelCKMadditionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCKMadditionalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCKMadditionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxIncludeZeros)
                    .addComponent(checkBoxParity)
                    .addComponent(checkBoxSeparation))
                .addContainerGap())
        );
        panelCKMadditionalLayout.setVerticalGroup(
            panelCKMadditionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCKMadditionalLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(checkBoxIncludeZeros)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxParity)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxSeparation)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelCKMLayout = new javax.swing.GroupLayout(panelCKM);
        panelCKM.setLayout(panelCKMLayout);
        panelCKMLayout.setHorizontalGroup(
            panelCKMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCKMLayout.createSequentialGroup()
                .addGroup(panelCKMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelCKMType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelScaling, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelCKMadditional, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelCKMLayout.setVerticalGroup(
            panelCKMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCKMLayout.createSequentialGroup()
                .addComponent(panelCKMType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelScaling, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(panelCKMadditional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        panelDistance.setPreferredSize(new java.awt.Dimension(375, 23));

        checkBoxDistance.setText(" Distance for suppression costs");
        checkBoxDistance.setPreferredSize(null);
        checkBoxDistance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDistanceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDistanceLayout = new javax.swing.GroupLayout(panelDistance);
        panelDistance.setLayout(panelDistanceLayout);
        panelDistanceLayout.setHorizontalGroup(
            panelDistanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDistanceLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkBoxDistance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(textFieldDistanceFunction1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldDistanceFunction2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldDistanceFunction3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldDistanceFunction4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldDistanceFunction5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelDistanceLayout.setVerticalGroup(
            panelDistanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDistanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(textFieldDistanceFunction1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(textFieldDistanceFunction2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(textFieldDistanceFunction3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(textFieldDistanceFunction4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(textFieldDistanceFunction5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(checkBoxDistance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        panelCodelist.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Codelist", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        buttonGroupCodelist.add(radioButtonCodelistAutomatic);
        radioButtonCodelistAutomatic.setText("Automatic");
        radioButtonCodelistAutomatic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCodelistActionPerformed(evt);
            }
        });

        buttonGroupCodelist.add(radioButtonCodelistFilename);
        radioButtonCodelistFilename.setText("Codelist filename");
        radioButtonCodelistFilename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCodelistActionPerformed(evt);
            }
        });

        buttonCodeListFileName.setText("...");
        buttonCodeListFileName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCodeListFileNameActionPerformed(evt);
            }
        });

        labelTotalCode.setText("Code for total:");

        javax.swing.GroupLayout panelCodelistLayout = new javax.swing.GroupLayout(panelCodelist);
        panelCodelist.setLayout(panelCodelistLayout);
        panelCodelistLayout.setHorizontalGroup(
            panelCodelistLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCodelistLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCodelistLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCodelistLayout.createSequentialGroup()
                        .addGroup(panelCodelistLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(radioButtonCodelistAutomatic, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(radioButtonCodelistFilename, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelTotalCode)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldTotalCode, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(textFieldCodeListFileName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonCodeListFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelCodelistLayout.setVerticalGroup(
            panelCodelistLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCodelistLayout.createSequentialGroup()
                .addGroup(panelCodelistLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCodelistLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(textFieldTotalCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelTotalCode))
                    .addComponent(radioButtonCodelistAutomatic))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonCodelistFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCodelistLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldCodeListFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonCodeListFileName))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelHierarchy.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Hierarchy", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        buttonGroupHierType.add(radioButtonHierNone);
        radioButtonHierNone.setText("Non hierarchical");
        radioButtonHierNone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonHierActionPerformed(evt);
            }
        });

        buttonGroupHierType.add(radioButtonHierData);
        radioButtonHierData.setText("Levels from microdata");
        radioButtonHierData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonHierActionPerformed(evt);
            }
        });

        buttonGroupHierType.add(radioButtonHierFile);
        radioButtonHierFile.setText("Levels from file");
        radioButtonHierFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonHierActionPerformed(evt);
            }
        });

        labelLeadingString.setLabelFor(textFieldLeadingString);
        labelLeadingString.setText("Leading string:");

        buttonHierFileName.setText("...");
        buttonHierFileName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonHierFileNameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelHierarchyLayout = new javax.swing.GroupLayout(panelHierarchy);
        panelHierarchy.setLayout(panelHierarchyLayout);
        panelHierarchyLayout.setHorizontalGroup(
            panelHierarchyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHierarchyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelHierarchyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelHierarchyLayout.createSequentialGroup()
                        .addComponent(textFieldHierFileName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonHierFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelHierarchyLayout.createSequentialGroup()
                        .addGroup(panelHierarchyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelHierarchyLayout.createSequentialGroup()
                                .addComponent(radioButtonHierFile, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelLeadingString)
                                .addGap(10, 10, 10)
                                .addComponent(textFieldLeadingString, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(radioButtonHierNone, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelHierarchyLayout.createSequentialGroup()
                                .addComponent(radioButtonHierData, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldHierLevel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldHierLevel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldHierLevel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldHierLevel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldHierLevel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldHierLevel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldHierLevel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldHierLevel8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldHierLevel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldHierLevel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelHierarchyLayout.setVerticalGroup(
            panelHierarchyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHierarchyLayout.createSequentialGroup()
                .addComponent(radioButtonHierNone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelHierarchyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(radioButtonHierData, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldHierLevel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldHierLevel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldHierLevel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldHierLevel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldHierLevel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldHierLevel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldHierLevel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldHierLevel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldHierLevel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldHierLevel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(panelHierarchyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(radioButtonHierFile)
                    .addComponent(labelLeadingString)
                    .addComponent(textFieldLeadingString, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelHierarchyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldHierFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonHierFileName))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelPTable.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "p-tables"));

        buttonPTableFreqFileName.setText("...");
        buttonPTableFreqFileName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPTableFreqFileNameActionPerformed(evt);
            }
        });

        jLabel1.setText("p-table for frequency count tables");

        jLabel2.setText("p-table for magnitude tables");

        buttonPTableContFileName.setText("...");
        buttonPTableContFileName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPTableContFileNameActionPerformed(evt);
            }
        });

        jLabel3.setText("p-table file for small values in magnitude tables");

        buttonPTableSepFileName.setText("...");
        buttonPTableSepFileName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPTableSepFileNameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPTableLayout = new javax.swing.GroupLayout(panelPTable);
        panelPTable.setLayout(panelPTableLayout);
        panelPTableLayout.setHorizontalGroup(
            panelPTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPTableLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPTableLayout.createSequentialGroup()
                        .addGroup(panelPTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPTableLayout.createSequentialGroup()
                        .addGroup(panelPTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(textFieldPTableSepFileName)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelPTableLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(textFieldPTableFreqFileName, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textFieldPTableContFileName, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelPTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(buttonPTableFreqFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(buttonPTableContFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(buttonPTableSepFileName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        panelPTableLayout.setVerticalGroup(
            panelPTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPTableLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldPTableFreqFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonPTableFreqFileName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldPTableContFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonPTableContFileName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldPTableSepFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonPTableSepFileName))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(panelHierarchy, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelCodelist, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(panelBasic, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelVariableType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(panelDistance, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(panelMissings, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                            .addComponent(panelStatusIndicator, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                            .addComponent(panelRequestCodes, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                            .addComponent(panelCKM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(panelPTable, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelBasic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelVariableType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelDistance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelMissings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(panelRequestCodes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(panelStatusIndicator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelCKM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addComponent(panelCodelist, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(panelHierarchy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(panelPTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buttonHierFileNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonHierFileNameActionPerformed
        TauArgusUtils.getDataDirFromRegistry(fileChooser);
        fileChooser.setDialogTitle("Hierarchy file");
        fileChooser.setSelectedFile(new File(""));
        fileChooser.resetChoosableFileFilters();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Hierarchy file (*.hrc)", "hrc"));
        if (fileChooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            textFieldHierFileName.setText(fileChooser.getSelectedFile().toString());
            TauArgusUtils.putDataDirInRegistry(fileChooser.getSelectedFile().toString());
        }
    }//GEN-LAST:event_buttonHierFileNameActionPerformed

    private void buttonCodeListFileNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCodeListFileNameActionPerformed
        TauArgusUtils.getDataDirFromRegistry(fileChooser);
        fileChooser.setDialogTitle("Code List file");
        fileChooser.setSelectedFile(new File(""));
        fileChooser.resetChoosableFileFilters();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Code List file (*.cdl)", "cdl"));
        if (fileChooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            textFieldCodeListFileName.setText(fileChooser.getSelectedFile().toString());
            TauArgusUtils.putDataDirInRegistry(fileChooser.getSelectedFile().toString());
        }
    }//GEN-LAST:event_buttonCodeListFileNameActionPerformed
   
    private void radioButtonCodelistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonCodelistActionPerformed
        codelistPanelSetEnabled(true);
    }//GEN-LAST:event_radioButtonCodelistActionPerformed

    private void radioButtonHierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonHierActionPerformed
        hierarchyPanelSetEnabled(true);
    }//GEN-LAST:event_radioButtonHierActionPerformed

    private void buttonPTableFreqFileNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPTableFreqFileNameActionPerformed
        TauArgusUtils.getDataDirFromRegistry(fileChooser);
        fileChooser.setDialogTitle("p-table file");
        fileChooser.setSelectedFile(new File(""));
        fileChooser.resetChoosableFileFilters();
        fileChooser.setFileFilter(new FileNameExtensionFilter("ptable file (*.csv, *.txt)", "csv", "txt"));
        if (fileChooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            textFieldPTableFreqFileName.setText(fileChooser.getSelectedFile().toString().substring(fileChooser.getSelectedFile().toString().lastIndexOf("\\")+1));
            TauArgusUtils.putDataDirInRegistry(fileChooser.getSelectedFile().toString());
        }
    }//GEN-LAST:event_buttonPTableFreqFileNameActionPerformed

    private void buttonPTableContFileNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPTableContFileNameActionPerformed
        TauArgusUtils.getDataDirFromRegistry(fileChooser);
        fileChooser.setDialogTitle("p-table file for magnitude tables");
        fileChooser.setSelectedFile(new File(""));
        fileChooser.resetChoosableFileFilters();
        fileChooser.setFileFilter(new FileNameExtensionFilter("ptable file (*.csv, *.txt)", "csv", "txt"));
        if (fileChooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            textFieldPTableContFileName.setText(fileChooser.getSelectedFile().toString().substring(fileChooser.getSelectedFile().toString().lastIndexOf("\\")+1));
            TauArgusUtils.putDataDirInRegistry(fileChooser.getSelectedFile().toString());
        }
    }//GEN-LAST:event_buttonPTableContFileNameActionPerformed

    private void buttonPTableSepFileNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPTableSepFileNameActionPerformed
        TauArgusUtils.getDataDirFromRegistry(fileChooser);
        fileChooser.setDialogTitle("p-table file for small values in magnitude tables");
        fileChooser.setSelectedFile(new File(""));
        fileChooser.resetChoosableFileFilters();
        fileChooser.setFileFilter(new FileNameExtensionFilter("ptable file (*.csv, *.txt)", "csv", "txt"));
        if (fileChooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            textFieldPTableSepFileName.setText(fileChooser.getSelectedFile().toString().substring(fileChooser.getSelectedFile().toString().lastIndexOf("\\")+1));
            TauArgusUtils.putDataDirInRegistry(fileChooser.getSelectedFile().toString());
        }
    }//GEN-LAST:event_buttonPTableSepFileNameActionPerformed

    private void radioButtonTopKItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radioButtonTopKItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            textFieldCKMTopK.setEnabled(true);
            checkBoxParity.setEnabled(Integer.parseInt(textFieldCKMTopK.getText())==1);
            if (buttonGroupScaling.getSelection() == null) displayScaling("N");
            else displayScaling(buttonToCKMType(SwingUtils.getSelectedButton(buttonGroupScaling), ScalingMap));
        }
        else if (evt.getStateChange() == ItemEvent.DESELECTED) {
            textFieldCKMTopK.setEnabled(false);
            checkBoxParity.setEnabled(true);
        }
    }//GEN-LAST:event_radioButtonTopKItemStateChanged

    private void checkBoxDistanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDistanceActionPerformed
        distancePanelSetEnabled(true);
    }//GEN-LAST:event_checkBoxDistanceActionPerformed

    private void radioButtonTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonTypeActionPerformed
        panelSetEnabled(true);
        if (buttonGroupScaling.getSelection() == null) displayScaling("N");
        else displayScaling(buttonToCKMType(SwingUtils.getSelectedButton(buttonGroupScaling), ScalingMap));
        enableForSPSS (DialogSpecifyMetadata.SpssSelected);
    }//GEN-LAST:event_radioButtonTypeActionPerformed

    private void textFieldNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldNameFocusLost
        currentVariable.name = textFieldName.getText();
    }//GEN-LAST:event_textFieldNameFocusLost

    private void radioButtonResponseItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radioButtonResponseItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            JRadioButton CKMradioButton = CKMMap.get(currentVariable.CKMType);
            CKMradioButton.setSelected(true);            
            textFieldCKMTopK.setText(Integer.toString(currentVariable.CKMTopK));
            checkBoxIncludeZeros.setSelected(currentVariable.zerosincellkey);
            checkBoxParity.setSelected(currentVariable.CKMapply_even_odd);
            checkBoxSeparation.setSelected(currentVariable.CKMseparation);    
        }
        SwingUtilities.getWindowAncestor(this).pack();
    }//GEN-LAST:event_radioButtonResponseItemStateChanged

    private void radioButtonExpRespItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radioButtonExpRespItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            JRadioButton CKMradioButton = CKMMap.get(currentVariable.CKMType);
            CKMradioButton.setSelected(true);            
            textFieldCKMTopK.setText(Integer.toString(currentVariable.CKMTopK));
            checkBoxIncludeZeros.setSelected(currentVariable.zerosincellkey);
            checkBoxParity.setSelected(currentVariable.CKMapply_even_odd);
            checkBoxSeparation.setSelected(currentVariable.CKMseparation);    
        }
        SwingUtilities.getWindowAncestor(this).pack();
    }//GEN-LAST:event_radioButtonExpRespItemStateChanged

    private void radioButtonRecordKeyItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radioButtonRecordKeyItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            panelPTable.setVisible(true);
            ptablePanelSetEnabled(true);
        }
        SwingUtilities.getWindowAncestor(this).pack();
    }//GEN-LAST:event_radioButtonRecordKeyItemStateChanged

    private void radioButtonNoCKMItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radioButtonNoCKMItemStateChanged
        panelScaling.setVisible(evt.getStateChange() != ItemEvent.SELECTED);
        panelCKMadditional.setVisible(evt.getStateChange() != ItemEvent.SELECTED);
        SwingUtilities.getWindowAncestor(this).pack();
    }//GEN-LAST:event_radioButtonNoCKMItemStateChanged

    private void radioButtonFlexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonFlexActionPerformed
        displayScaling("F");
    }//GEN-LAST:event_radioButtonFlexActionPerformed

    private void radioButtonSimpleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonSimpleActionPerformed
        displayScaling("N");
    }//GEN-LAST:event_radioButtonSimpleActionPerformed

    private void textFieldCKMTopKFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldCKMTopKFocusLost
        displayScaling(currentVariable.CKMscaling);
        checkBoxParity.setEnabled(Integer.parseInt(textFieldCKMTopK.getText()) == 1);
    }//GEN-LAST:event_textFieldCKMTopKFocusLost

    private void textFieldCKMTopKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldCKMTopKActionPerformed
        displayScaling(currentVariable.CKMscaling);
        checkBoxParity.setEnabled(Integer.parseInt(textFieldCKMTopK.getText()) == 1);
    }//GEN-LAST:event_textFieldCKMTopKActionPerformed

    private void displayScaling(String ScalingType){
        int TopK;
        try{
            if (radioButtonTopK.isSelected()){
                TopK = Integer.parseInt(textFieldCKMTopK.getText());
                if (TopK < 1 || TopK > 5){
                    throw new NumberFormatException();
                }
            }
            else{
                TopK = 1;
            }
            setScaleTable(tableScaleParams,ScalingType,TopK);
        }
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "TopK should be integer in {1,...,5}", null, JOptionPane.ERROR_MESSAGE);
            textFieldCKMTopK.setText("");
            textFieldCKMTopK.requestFocusInWindow();
        }
    }
   
    private void setScaleTable(JTable Tab, String ScalingType, int NumEps){
        Tab.setValueAt((ScalingType.equals("F")) ? "<html><b>&sigma;<sub>0</sub></b></html>" : "<html><b>&sigma;<sub>1</sub></b></html>", 0, 0);
        Tab.setValueAt((ScalingType.equals("F")) ? "<html><b>&sigma;<sub>1</sub></b></html>" : "", 0, 1);
        Tab.setValueAt((ScalingType.equals("F")) ? "<html><b>x*</b></html>" : "", 0, 2);
        Tab.setValueAt((ScalingType.equals("F")) ? "<html><b>q</b></html>" : "", 0, 3);
        Tab.setValueAt((1 < NumEps) ? "<html><b>&epsilon;<sub>2</sub></b></html>" : "", 2, 0);
        Tab.setValueAt((2 < NumEps) ? "<html><b>&epsilon;<sub>3</sub></b></html>" : "", 2, 1);
        Tab.setValueAt((3 < NumEps) ? "<html><b>&epsilon;<sub>4</sub></b></html>" : "", 2, 2);
        Tab.setValueAt((4 < NumEps) ? "<html><b>&epsilon;<sub>5</sub></b></html>" : "", 2, 3);
        
        switch (ScalingType){
            case "F": radioButtonFlex.setSelected(true);
                      tableScaleParams.setValueAt(currentVariable.CKMsigma0,1,0);
                      tableScaleParams.setValueAt(currentVariable.CKMsigma1,1,1);
                      tableScaleParams.setValueAt(currentVariable.CKMxstar,1,2);
                      tableScaleParams.setValueAt(currentVariable.CKMq,1,3);
                      break;
            case "N": radioButtonSimple.setSelected(true);
                      tableScaleParams.setValueAt(currentVariable.CKMsigma1,1,0);
                      tableScaleParams.setValueAt("",1,1);
                      tableScaleParams.setValueAt("",1,2);
                      tableScaleParams.setValueAt("",1,3);
                      break;
            }
            int maxEps = min(NumEps,currentVariable.CKMTopK);
            for (int i=1;i<maxEps;i++) tableScaleParams.setValueAt(currentVariable.CKMepsilon[i],3,i-1); 
            for (int i=maxEps; i<NumEps; i++) tableScaleParams.setValueAt("-1.0",3,i-1); 
            for (int i=NumEps; i<5;i++) tableScaleParams.setValueAt("",3,i-1);
    }
    
    public void setScaleTableModel(){
        tableScaleParams.setModel(new AbstractTableModel(){
            private Object[][] values = {
                {"","","",""},
                {"","","",""},
                {"","","",""},                
                {"","","",""}
                };
            
            @Override
            public int getRowCount() {return 4;}
            
            @Override
            public int getColumnCount() {return 4;}

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return values[rowIndex][columnIndex];
            }
            
            @Override
            public void setValueAt(Object obj, int rowIndex, int columnIndex) {
                values[rowIndex][columnIndex] = obj;
                fireTableCellUpdated(rowIndex, columnIndex);
            }
   
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex){
                return (rowIndex==1 || rowIndex==3);
            }
            
        });
    }    
    
    private javax.swing.JTextField[] textFieldDistanceFunction;
    private javax.swing.JTextField[] textFieldHierLevel;
    private javax.swing.JTextField[] textFieldMissing;
    private javax.swing.JTextField[] textFieldRequestCode;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCodeListFileName;
    private javax.swing.ButtonGroup buttonGroupCKM;
    private javax.swing.ButtonGroup buttonGroupCodelist;
    private javax.swing.ButtonGroup buttonGroupHierType;
    private javax.swing.ButtonGroup buttonGroupPTable;
    private javax.swing.ButtonGroup buttonGroupScaling;
    private javax.swing.ButtonGroup buttonGroupVariableType;
    private javax.swing.JButton buttonHierFileName;
    private javax.swing.JButton buttonPTableContFileName;
    private javax.swing.JButton buttonPTableFreqFileName;
    private javax.swing.JButton buttonPTableSepFileName;
    private javax.swing.JCheckBox checkBoxDistance;
    private javax.swing.JCheckBox checkBoxIncludeZeros;
    private javax.swing.JCheckBox checkBoxParity;
    private javax.swing.JCheckBox checkBoxSeparation;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel labelDecimals;
    private javax.swing.JLabel labelLeadingString;
    private javax.swing.JLabel labelLength;
    private javax.swing.JLabel labelMissing1;
    private javax.swing.JLabel labelMissing2;
    private javax.swing.JLabel labelName;
    private javax.swing.JLabel labelProtect;
    private javax.swing.JLabel labelRequestCode1;
    private javax.swing.JLabel labelRequestCode2;
    private javax.swing.JLabel labelStartingPosition;
    private javax.swing.JLabel labelStatusSafe;
    private javax.swing.JLabel labelStatusUnsafe;
    private javax.swing.JLabel labelTotalCode;
    private javax.swing.JPanel panelBasic;
    private javax.swing.JPanel panelCKM;
    private javax.swing.JPanel panelCKMType;
    private javax.swing.JPanel panelCKMadditional;
    private javax.swing.JPanel panelCodelist;
    private javax.swing.JPanel panelDistance;
    private javax.swing.JPanel panelHierarchy;
    private javax.swing.JPanel panelMissings;
    private javax.swing.JPanel panelPTable;
    private javax.swing.JPanel panelRequestCodes;
    private javax.swing.JPanel panelScaling;
    private javax.swing.JPanel panelStatusIndicator;
    private javax.swing.JPanel panelVariableType;
    private javax.swing.JRadioButton radioButtonCodelistAutomatic;
    private javax.swing.JRadioButton radioButtonCodelistFilename;
    private javax.swing.JRadioButton radioButtonCost;
    private javax.swing.JRadioButton radioButtonExpResp;
    private javax.swing.JRadioButton radioButtonExplanatory;
    private javax.swing.JRadioButton radioButtonFlex;
    private javax.swing.JRadioButton radioButtonFrequency;
    private javax.swing.JRadioButton radioButtonHierData;
    private javax.swing.JRadioButton radioButtonHierFile;
    private javax.swing.JRadioButton radioButtonHierNone;
    private javax.swing.JRadioButton radioButtonHoldingIndicator;
    private javax.swing.JRadioButton radioButtonLowerProtectionLevel;
    private javax.swing.JRadioButton radioButtonMean;
    private javax.swing.JRadioButton radioButtonNoCKM;
    private javax.swing.JRadioButton radioButtonRecordKey;
    private javax.swing.JRadioButton radioButtonRequestProtection;
    private javax.swing.JRadioButton radioButtonResponse;
    private javax.swing.JRadioButton radioButtonSampleWeight;
    private javax.swing.JRadioButton radioButtonShadow;
    private javax.swing.JRadioButton radioButtonSimple;
    private javax.swing.JRadioButton radioButtonSpread;
    private javax.swing.JRadioButton radioButtonStatusIndicator;
    private javax.swing.JRadioButton radioButtonTopK;
    private javax.swing.JRadioButton radioButtonTopN;
    private javax.swing.JRadioButton radioButtonUpperProtectionLevel;
    private javax.swing.JRadioButton radioButtonValue;
    private javax.swing.JTable tableScaleParams;
    private javax.swing.JTextField textFieldCKMTopK;
    private javax.swing.JTextField textFieldCodeListFileName;
    private javax.swing.JTextField textFieldDecimals;
    private javax.swing.JTextField textFieldDistanceFunction1;
    private javax.swing.JTextField textFieldDistanceFunction2;
    private javax.swing.JTextField textFieldDistanceFunction3;
    private javax.swing.JTextField textFieldDistanceFunction4;
    private javax.swing.JTextField textFieldDistanceFunction5;
    private javax.swing.JTextField textFieldHierFileName;
    private javax.swing.JTextField textFieldHierLevel1;
    private javax.swing.JTextField textFieldHierLevel10;
    private javax.swing.JTextField textFieldHierLevel2;
    private javax.swing.JTextField textFieldHierLevel3;
    private javax.swing.JTextField textFieldHierLevel4;
    private javax.swing.JTextField textFieldHierLevel5;
    private javax.swing.JTextField textFieldHierLevel6;
    private javax.swing.JTextField textFieldHierLevel7;
    private javax.swing.JTextField textFieldHierLevel8;
    private javax.swing.JTextField textFieldHierLevel9;
    private javax.swing.JTextField textFieldLeadingString;
    private javax.swing.JTextField textFieldLength;
    private javax.swing.JTextField textFieldMissing1;
    private javax.swing.JTextField textFieldMissing2;
    private javax.swing.JTextField textFieldName;
    private javax.swing.JTextField textFieldPTableContFileName;
    private javax.swing.JTextField textFieldPTableFreqFileName;
    private javax.swing.JTextField textFieldPTableSepFileName;
    private javax.swing.JTextField textFieldRequestCode1;
    private javax.swing.JTextField textFieldRequestCode2;
    private javax.swing.JTextField textFieldStartingPosition;
    private javax.swing.JTextField textFieldStatusProtect;
    private javax.swing.JTextField textFieldStatusSafe;
    private javax.swing.JTextField textFieldStatusUnsafe;
    private javax.swing.JTextField textFieldTotalCode;
    // End of variables declaration//GEN-END:variables
}
