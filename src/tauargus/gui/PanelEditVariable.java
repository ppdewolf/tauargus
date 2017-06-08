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

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import org.apache.commons.lang3.StringUtils;
import tauargus.model.ArgusException;
import tauargus.model.Metadata;
import tauargus.model.Type;
import tauargus.model.Variable;
import argus.utils.StrUtils;
import tauargus.utils.SwingUtils;
import tauargus.utils.TauArgusUtils;

public class PanelEditVariable extends javax.swing.JPanel {

    /**
     * Creates new form PanelEditVariable
     */
    
    Variable currentVariable;
    
    public PanelEditVariable() {
        initComponents();
        createComponentArrays();

        buttonMap = new EnumMap<Type, javax.swing.JRadioButton>(Type.class);
        buttonMap.put(Type.CATEGORICAL, radioButtonExplanatory);
        buttonMap.put(Type.RESPONSE, radioButtonResponse);
        buttonMap.put(Type.CAT_RESP, radioButtonExpResp);
        buttonMap.put(Type.WEIGHT, radioButtonSampleWeight);
        buttonMap.put(Type.HOLDING, radioButtonHoldingIndicator);
        buttonMap.put(Type.REQUEST, radioButtonRequestProtection);
        buttonMap.put(Type.SHADOW, radioButtonShadow);
        buttonMap.put(Type.COST, radioButtonCost);
        buttonMap.put(Type.FREQUENCY, radioButtonFrequency);
        buttonMap.put(Type.TOP_N, radioButtonTopN);
        buttonMap.put(Type.LOWER_PROTECTION_LEVEL, radioButtonLowerProtectionLevel);
        buttonMap.put(Type.UPPER_PROTECTION_LEVEL, radioButtonUpperProtectionLevel);
        buttonMap.put(Type.STATUS, radioButtonStatusIndicator);
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

        for (int i=0; i<Variable.MAX_NUMBER_OF_REQUESTS; i++) {
            textFieldRequestCode[i].setText("");
        }
        textFieldStatusSafe.setText("");
        textFieldStatusUnsafe.setText("");
        textFieldStatusProtect.setText("");

        // Clear selections...
        checkBoxDistance.setSelected(false);
        buttonGroupCodelist.clearSelection();
        buttonGroupHierType.clearSelection();

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
            try{variable.nDecimals = StrUtils.toInteger(textFieldDecimals.getText());}catch (Exception ex){}
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
            codelistPanelSetEnabled(type.isCategorical());
            hierarchyPanelSetEnabled(type.isCategorical());
            labelDecimals.setEnabled((type.hasDecimals()) && !(dataType == Metadata.DATA_FILE_TYPE_SPSS));
            textFieldDecimals.setEnabled(labelDecimals.isEnabled());   
            enableForSPSS(DialogSpecifyMetadata.SpssSelected); 
        }
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
        panelAttributes = new javax.swing.JPanel();
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
        panelMissings = new javax.swing.JPanel();
        labelMissing1 = new javax.swing.JLabel();
        textFieldMissing1 = new javax.swing.JTextField();
        labelMissing2 = new javax.swing.JLabel();
        textFieldMissing2 = new javax.swing.JTextField();
        labelTotalCode = new javax.swing.JLabel();
        textFieldTotalCode = new javax.swing.JTextField();
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

        fileChooser.setDialogTitle("");

        panelAttributes.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Attributes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        labelName.setLabelFor(textFieldName);
        labelName.setText("Name:");

        textFieldName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldNameActionPerformed(evt);
            }
        });
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
                .addContainerGap(83, Short.MAX_VALUE)
                .addGroup(panelBasicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelStartingPosition, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelLength, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelDecimals, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelBasicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(textFieldLength, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                    .addComponent(textFieldDecimals, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(textFieldStartingPosition, javax.swing.GroupLayout.Alignment.TRAILING)))
            .addGroup(panelBasicLayout.createSequentialGroup()
                .addComponent(labelName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldName))
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
                    .addComponent(textFieldDecimals, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        panelVariableType.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        buttonGroupVariableType.add(radioButtonExplanatory);
        radioButtonExplanatory.setText("Explanatory");
        radioButtonExplanatory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonResponse);
        radioButtonResponse.setText("Response");
        radioButtonResponse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonSampleWeight);
        radioButtonSampleWeight.setText("Sample weight");
        radioButtonSampleWeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonHoldingIndicator);
        radioButtonHoldingIndicator.setText("Holding indicator");
        radioButtonHoldingIndicator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonRequestProtection);
        radioButtonRequestProtection.setText("Request protection");
        radioButtonRequestProtection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonShadow);
        radioButtonShadow.setText("Shadow");
        radioButtonShadow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonCost);
        radioButtonCost.setText("Cost");
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
        radioButtonFrequency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonTopN);
        radioButtonTopN.setText("Top n");
        radioButtonTopN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonStatusIndicator);
        radioButtonStatusIndicator.setText("Status indicator");
        radioButtonStatusIndicator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        buttonGroupVariableType.add(radioButtonExpResp);
        radioButtonExpResp.setText("Exp. / Resp.");
        radioButtonExpResp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonTypeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelVariableTypeLayout = new javax.swing.GroupLayout(panelVariableType);
        panelVariableType.setLayout(panelVariableTypeLayout);
        panelVariableTypeLayout.setHorizontalGroup(
            panelVariableTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVariableTypeLayout.createSequentialGroup()
                .addGroup(panelVariableTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelVariableTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(radioButtonHoldingIndicator)
                        .addComponent(radioButtonRequestProtection)
                        .addComponent(radioButtonShadow)
                        .addComponent(radioButtonCost)
                        .addComponent(radioButtonLowerProtectionLevel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(radioButtonUpperProtectionLevel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(radioButtonFrequency)
                        .addComponent(radioButtonTopN)
                        .addComponent(radioButtonSampleWeight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(radioButtonResponse, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(radioButtonExplanatory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(radioButtonStatusIndicator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(radioButtonExpResp))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelVariableTypeLayout.setVerticalGroup(
            panelVariableTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVariableTypeLayout.createSequentialGroup()
                .addComponent(radioButtonExplanatory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonResponse)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonExpResp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(radioButtonSampleWeight)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonHoldingIndicator)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonRequestProtection)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonShadow)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonCost)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonLowerProtectionLevel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonUpperProtectionLevel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonFrequency)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonTopN)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonStatusIndicator)
                .addContainerGap())
        );

        panelMissings.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Missings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        labelMissing1.setLabelFor(textFieldMissing1);
        labelMissing1.setText("1:");

        labelMissing2.setLabelFor(textFieldMissing2);
        labelMissing2.setText("2:");

        javax.swing.GroupLayout panelMissingsLayout = new javax.swing.GroupLayout(panelMissings);
        panelMissings.setLayout(panelMissingsLayout);
        panelMissingsLayout.setHorizontalGroup(
            panelMissingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMissingsLayout.createSequentialGroup()
                .addGroup(panelMissingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelMissing1)
                    .addComponent(labelMissing2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMissingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldMissing2, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                    .addComponent(textFieldMissing1))
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
                    .addComponent(labelMissing2)))
        );

        labelTotalCode.setText("Code for total:");

        panelRequestCodes.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Request codes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        labelRequestCode1.setLabelFor(textFieldRequestCode1);
        labelRequestCode1.setText("1:");

        labelRequestCode2.setLabelFor(textFieldRequestCode2);
        labelRequestCode2.setText("2:");

        javax.swing.GroupLayout panelRequestCodesLayout = new javax.swing.GroupLayout(panelRequestCodes);
        panelRequestCodes.setLayout(panelRequestCodesLayout);
        panelRequestCodesLayout.setHorizontalGroup(
            panelRequestCodesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRequestCodesLayout.createSequentialGroup()
                .addGroup(panelRequestCodesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelRequestCode2)
                    .addComponent(labelRequestCode1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRequestCodesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldRequestCode1)
                    .addComponent(textFieldRequestCode2))
                .addContainerGap())
        );
        panelRequestCodesLayout.setVerticalGroup(
            panelRequestCodesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRequestCodesLayout.createSequentialGroup()
                .addGroup(panelRequestCodesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelRequestCode1)
                    .addComponent(textFieldRequestCode1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRequestCodesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldRequestCode2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelRequestCode2)))
        );

        panelStatusIndicator.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Status indicators", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        labelStatusUnsafe.setLabelFor(textFieldStatusUnsafe);
        labelStatusUnsafe.setText("Unsafe:");

        labelStatusSafe.setLabelFor(textFieldStatusSafe);
        labelStatusSafe.setText("Safe:");

        textFieldStatusSafe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldStatusSafeActionPerformed(evt);
            }
        });

        labelProtect.setLabelFor(textFieldStatusProtect);
        labelProtect.setText("Protect:");

        javax.swing.GroupLayout panelStatusIndicatorLayout = new javax.swing.GroupLayout(panelStatusIndicator);
        panelStatusIndicator.setLayout(panelStatusIndicatorLayout);
        panelStatusIndicatorLayout.setHorizontalGroup(
            panelStatusIndicatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStatusIndicatorLayout.createSequentialGroup()
                .addGroup(panelStatusIndicatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labelProtect, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelStatusUnsafe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelStatusSafe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelStatusIndicatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldStatusUnsafe)
                    .addComponent(textFieldStatusSafe)
                    .addComponent(textFieldStatusProtect))
                .addContainerGap())
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
                .addGroup(panelStatusIndicatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelProtect)
                    .addComponent(textFieldStatusProtect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        checkBoxDistance.setText("Distance for suppression weight");
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
                .addComponent(checkBoxDistance)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldDistanceFunction1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldDistanceFunction2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldDistanceFunction3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldDistanceFunction4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldDistanceFunction5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelDistanceLayout.setVerticalGroup(
            panelDistanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDistanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(textFieldDistanceFunction1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(textFieldDistanceFunction2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(textFieldDistanceFunction3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(textFieldDistanceFunction4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(textFieldDistanceFunction5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(checkBoxDistance))
        );

        javax.swing.GroupLayout panelAttributesLayout = new javax.swing.GroupLayout(panelAttributes);
        panelAttributes.setLayout(panelAttributesLayout);
        panelAttributesLayout.setHorizontalGroup(
            panelAttributesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAttributesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelAttributesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelDistance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelAttributesLayout.createSequentialGroup()
                        .addComponent(panelBasic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelVariableType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelAttributesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelStatusIndicator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelRequestCodes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelMissings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelAttributesLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(labelTotalCode)
                                .addContainerGap(151, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelAttributesLayout.createSequentialGroup()
                                .addComponent(textFieldTotalCode)
                                .addContainerGap())))))
        );
        panelAttributesLayout.setVerticalGroup(
            panelAttributesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAttributesLayout.createSequentialGroup()
                .addGroup(panelAttributesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelBasic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelAttributesLayout.createSequentialGroup()
                        .addComponent(panelMissings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelTotalCode)
                        .addGap(1, 1, 1)
                        .addComponent(textFieldTotalCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelRequestCodes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelStatusIndicator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panelVariableType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(panelDistance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        javax.swing.GroupLayout panelCodelistLayout = new javax.swing.GroupLayout(panelCodelist);
        panelCodelist.setLayout(panelCodelistLayout);
        panelCodelistLayout.setHorizontalGroup(
            panelCodelistLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCodelistLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCodelistLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCodelistLayout.createSequentialGroup()
                        .addComponent(textFieldCodeListFileName, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonCodeListFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelCodelistLayout.createSequentialGroup()
                        .addGroup(panelCodelistLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(radioButtonCodelistAutomatic, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(radioButtonCodelistFilename, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelCodelistLayout.setVerticalGroup(
            panelCodelistLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCodelistLayout.createSequentialGroup()
                .addComponent(radioButtonCodelistAutomatic)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonCodelistFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCodelistLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldCodeListFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonCodeListFileName)))
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
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(labelLeadingString)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                        .addGap(0, 134, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelHierarchyLayout.setVerticalGroup(
            panelHierarchyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHierarchyLayout.createSequentialGroup()
                .addComponent(radioButtonHierNone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelCodelist, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelHierarchy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelAttributes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelAttributes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panelCodelist, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelHierarchy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void radioButtonTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonTypeActionPerformed
        panelSetEnabled(true);
        enableForSPSS (DialogSpecifyMetadata.SpssSelected);
    }//GEN-LAST:event_radioButtonTypeActionPerformed

    private void checkBoxDistanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDistanceActionPerformed
        distancePanelSetEnabled(true);
    }//GEN-LAST:event_checkBoxDistanceActionPerformed

    private void textFieldStatusSafeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldStatusSafeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textFieldStatusSafeActionPerformed

    private void textFieldNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textFieldNameActionPerformed

    private void textFieldNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldNameFocusLost
        // TODO add your handling code here:
        currentVariable.name = textFieldName.getText();
 //       DialogSpecifyMetadata.SetSpecificElement(currentVariable);
        
 //      try{
 //       save(currentVariable);
 //       } catch (ArgusException ex){};
        
    }//GEN-LAST:event_textFieldNameFocusLost

    private Metadata metadata;
    private int dataType;
    
    private Map<Type, javax.swing.JRadioButton> buttonMap;
    
    private javax.swing.JTextField[] textFieldDistanceFunction;
    private javax.swing.JTextField[] textFieldHierLevel;
    private javax.swing.JTextField[] textFieldMissing;
    private javax.swing.JTextField[] textFieldRequestCode;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCodeListFileName;
    private javax.swing.ButtonGroup buttonGroupCodelist;
    private javax.swing.ButtonGroup buttonGroupHierType;
    private javax.swing.ButtonGroup buttonGroupVariableType;
    private javax.swing.JButton buttonHierFileName;
    private javax.swing.JCheckBox checkBoxDistance;
    private javax.swing.JFileChooser fileChooser;
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
    private javax.swing.JPanel panelAttributes;
    private javax.swing.JPanel panelBasic;
    private javax.swing.JPanel panelCodelist;
    private javax.swing.JPanel panelDistance;
    private javax.swing.JPanel panelHierarchy;
    private javax.swing.JPanel panelMissings;
    private javax.swing.JPanel panelRequestCodes;
    private javax.swing.JPanel panelStatusIndicator;
    private javax.swing.JPanel panelVariableType;
    private javax.swing.JRadioButton radioButtonCodelistAutomatic;
    private javax.swing.JRadioButton radioButtonCodelistFilename;
    private javax.swing.JRadioButton radioButtonCost;
    private javax.swing.JRadioButton radioButtonExpResp;
    private javax.swing.JRadioButton radioButtonExplanatory;
    private javax.swing.JRadioButton radioButtonFrequency;
    private javax.swing.JRadioButton radioButtonHierData;
    private javax.swing.JRadioButton radioButtonHierFile;
    private javax.swing.JRadioButton radioButtonHierNone;
    private javax.swing.JRadioButton radioButtonHoldingIndicator;
    private javax.swing.JRadioButton radioButtonLowerProtectionLevel;
    private javax.swing.JRadioButton radioButtonRequestProtection;
    private javax.swing.JRadioButton radioButtonResponse;
    private javax.swing.JRadioButton radioButtonSampleWeight;
    private javax.swing.JRadioButton radioButtonShadow;
    private javax.swing.JRadioButton radioButtonStatusIndicator;
    private javax.swing.JRadioButton radioButtonTopN;
    private javax.swing.JRadioButton radioButtonUpperProtectionLevel;
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
    private javax.swing.JTextField textFieldRequestCode1;
    private javax.swing.JTextField textFieldRequestCode2;
    private javax.swing.JTextField textFieldStartingPosition;
    private javax.swing.JTextField textFieldStatusProtect;
    private javax.swing.JTextField textFieldStatusSafe;
    private javax.swing.JTextField textFieldStatusUnsafe;
    private javax.swing.JTextField textFieldTotalCode;
    // End of variables declaration//GEN-END:variables
}
