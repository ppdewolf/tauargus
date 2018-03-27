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

import java.text.DecimalFormat;
import tauargus.model.Cell;
import tauargus.model.TableSet;

public class PanelCellDetails extends javax.swing.JPanel {

    private TableSet table;
    private Cell cell;
    private DecimalFormat integerFormatter;
    private DecimalFormat doubleFormatter;
    
    /**
     * Creates new form PanelCellDetails
     */
    public PanelCellDetails() {
        initComponents();
    }
    
    public void update(TableSet table, Cell cell, DecimalFormat integerFormatter, DecimalFormat doubleFormatter)  {
        this.table = table;
        this.cell = cell;
        checkBoxHoldingLevel.setVisible(table.metadata.containsHoldingVariable());
        setFormatters(integerFormatter, doubleFormatter);
    }
    
    public void setFormatters(DecimalFormat integerFormatter, DecimalFormat doubleFormatter) {
        this.integerFormatter = integerFormatter;
        this.doubleFormatter = doubleFormatter;
        
        if (table != null && cell != null) {
            update();
        }
    }

    private String getToolTipText(boolean isLowerRange, double range, double response) {
        StringBuilder s = new StringBuilder();
        s.append(isLowerRange ? "Lower Range: " : "UpperRange: ").append(doubleFormatter.format(range));
        if (response != 0) {
            s.append("; Percentage: ").append(integerFormatter.format(100 * range / response)).append("%");
        }
        return s.toString();
    }

    private void update() {
        boolean visible;
        
        textFieldValue.setText(doubleFormatter.format(cell.response));

        visible = table.rounded || table.ctaProtect;
        labelAdjustedValue.setVisible(visible);
        textFieldAdjustedValue.setVisible(visible);
        if (table.rounded) {
            labelAdjustedValue.setText("Rounded");
            textFieldAdjustedValue.setText(integerFormatter.format(cell.roundedResponse));
        } else if (table.ctaProtect) {
            labelAdjustedValue.setText("CTA-Adjusted");
            textFieldAdjustedValue.setText(doubleFormatter.format(cell.CTAValue));
        }

        textFieldStatus.setText(cell.status.getDescription());
        textFieldShadow.setText(doubleFormatter.format(cell.shadow));
        if (table.costFunc == TableSet.COST_DIST) {
            textFieldCost.setText("dist");
        } else {
            textFieldCost.setText(doubleFormatter.format(cell.cost));
        }
        
        // fill top n box
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        visible =  table.piepRule[checkBoxHoldingLevel.isSelected() ? 1 : 0];
        labelRequest.setVisible(visible);
        textFieldRequest.setVisible(visible);
        if (checkBoxHoldingLevel.isSelected()) {
            int topX = Math.min(cell.holdingFreq, Math.min(table.numberOfHoldingTopNNeeded(), 10));
            for (int i=0; i<topX; i++) {
                sb.append("<p align=\"right\">").append(doubleFormatter.format(cell.holdingMaxScore[i])).append("</p>");
            }
            textFieldRequest.setText(doubleFormatter.format(cell.peepHolding));
            textFieldContributions.setText(integerFormatter.format(cell.holdingFreq));
        } else {
            int topX = Math.min(cell.freq, Math.min(table.numberOfTopNNeeded(), 10));
            for (int i=0; i<topX; i++) {
                sb.append("<p align=\"right\">").append(doubleFormatter.format(cell.maxScore[i])).append("</p>");
            }
            textFieldRequest.setText(doubleFormatter.format(cell.peepCell));
            textFieldContributions.setText(integerFormatter.format(cell.freq));
        }
        sb.append("</html>");
        labelTopNValue.setText(sb.toString());
        
        visible = cell.status.isPrimaryUnsafe();
        labelProtectionInterval.setVisible(visible);
        textFieldProtectionLower.setVisible(visible);
        textFieldProtectionUpper.setVisible(visible);
        textFieldProtectionLower.setText(doubleFormatter.format(cell.response - cell.lower)); 
        textFieldProtectionUpper.setText(doubleFormatter.format(cell.response + cell.upper)); 
        textFieldProtectionLower.setToolTipText(getToolTipText(true, cell.lower, cell.response));
        textFieldProtectionUpper.setToolTipText(getToolTipText(false, cell.upper, cell.response));

        visible = table.hasBeenAudited && !cell.status.isSafe();
        textFieldRealizedLower.setVisible(visible);
        textFieldRealizedUpper.setVisible(visible);
        labelAuditInterval.setVisible(visible);
        textFieldRealizedLower.setText(doubleFormatter.format(cell.realizedLower));
        textFieldRealizedUpper.setText(doubleFormatter.format(cell.realizedUpper));
        textFieldRealizedLower.setToolTipText(getToolTipText(true, cell.response - cell.realizedLower, cell.response));
        textFieldRealizedUpper.setToolTipText(getToolTipText(false, cell.realizedUpper - cell.response, cell.response));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelValue = new javax.swing.JLabel();
        textFieldValue = new javax.swing.JTextField();
        labelAdjustedValue = new javax.swing.JLabel();
        textFieldAdjustedValue = new javax.swing.JTextField();
        textFieldStatus = new javax.swing.JTextField();
        labelStatus = new javax.swing.JLabel();
        labelShadow = new javax.swing.JLabel();
        textFieldShadow = new javax.swing.JTextField();
        textFieldCost = new javax.swing.JTextField();
        textFieldContributions = new javax.swing.JTextField();
        labelTopNValue = new javax.swing.JLabel();
        textFieldRequest = new javax.swing.JTextField();
        labelProtectionInterval = new javax.swing.JLabel();
        textFieldProtectionUpper = new javax.swing.JTextField();
        labelAuditInterval = new javax.swing.JLabel();
        textFieldRealizedUpper = new javax.swing.JTextField();
        textFieldRealizedLower = new javax.swing.JTextField();
        textFieldProtectionLower = new javax.swing.JTextField();
        labelRequest = new javax.swing.JLabel();
        checkBoxHoldingLevel = new javax.swing.JCheckBox();
        labelTopN = new javax.swing.JLabel();
        labelContributions = new javax.swing.JLabel();
        labelCost = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(273, 345));

        labelValue.setText("Value");

        textFieldValue.setEditable(false);
        textFieldValue.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        labelAdjustedValue.setText("Value");

        textFieldAdjustedValue.setEditable(false);
        textFieldAdjustedValue.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        textFieldStatus.setEditable(false);
        textFieldStatus.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        labelStatus.setText("Status");

        labelShadow.setText("Shadow");

        textFieldShadow.setEditable(false);
        textFieldShadow.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        textFieldCost.setEditable(false);
        textFieldCost.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        textFieldContributions.setEditable(false);
        textFieldContributions.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        labelTopNValue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelTopNValue.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelTopNValue.setAlignmentX(0.5F);
        labelTopNValue.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(192, 192, 192)), javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        labelTopNValue.setPreferredSize(new java.awt.Dimension(6, 20));

        textFieldRequest.setEditable(false);
        textFieldRequest.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        labelProtectionInterval.setText("Protection interval (low/up value)");

        textFieldProtectionUpper.setEditable(false);
        textFieldProtectionUpper.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        labelAuditInterval.setText("Audit interval (low/up value)");

        textFieldRealizedUpper.setEditable(false);
        textFieldRealizedUpper.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        textFieldRealizedLower.setEditable(false);
        textFieldRealizedLower.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        textFieldProtectionLower.setEditable(false);
        textFieldProtectionLower.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        labelRequest.setText("Request");

        checkBoxHoldingLevel.setText("<html>Holding<br>level</html>");
        checkBoxHoldingLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxHoldingLevelActionPerformed(evt);
            }
        });

        labelTopN.setText("Top n of shadow");

        labelContributions.setText("#contributions");

        labelCost.setText("Cost");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelAuditInterval, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelProtectionInterval, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(labelTopN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labelContributions)
                            .addComponent(labelValue)
                            .addComponent(labelAdjustedValue)
                            .addComponent(labelStatus)
                            .addComponent(labelShadow)
                            .addComponent(labelCost)
                            .addComponent(checkBoxHoldingLevel)
                            .addComponent(labelRequest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textFieldStatus)
                            .addComponent(textFieldShadow)
                            .addComponent(textFieldAdjustedValue, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(textFieldCost, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelTopNValue, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(textFieldRequest, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(textFieldContributions, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(textFieldValue, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(textFieldRealizedLower)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldRealizedUpper))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(textFieldProtectionLower)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldProtectionUpper)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelValue, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldAdjustedValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelAdjustedValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldShadow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelShadow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldCost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelCost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelContributions)
                    .addComponent(textFieldContributions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelTopNValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelTopN))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxHoldingLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelRequest)
                    .addComponent(textFieldRequest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelProtectionInterval)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldProtectionLower, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldProtectionUpper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelAuditInterval)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldRealizedLower, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldRealizedUpper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void checkBoxHoldingLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxHoldingLevelActionPerformed
        update();
    }//GEN-LAST:event_checkBoxHoldingLevelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkBoxHoldingLevel;
    private javax.swing.JLabel labelAdjustedValue;
    private javax.swing.JLabel labelAuditInterval;
    private javax.swing.JLabel labelContributions;
    private javax.swing.JLabel labelCost;
    private javax.swing.JLabel labelProtectionInterval;
    private javax.swing.JLabel labelRequest;
    private javax.swing.JLabel labelShadow;
    private javax.swing.JLabel labelStatus;
    private javax.swing.JLabel labelTopN;
    private javax.swing.JLabel labelTopNValue;
    private javax.swing.JLabel labelValue;
    private javax.swing.JTextField textFieldAdjustedValue;
    private javax.swing.JTextField textFieldContributions;
    private javax.swing.JTextField textFieldCost;
    private javax.swing.JTextField textFieldProtectionLower;
    private javax.swing.JTextField textFieldProtectionUpper;
    private javax.swing.JTextField textFieldRealizedLower;
    private javax.swing.JTextField textFieldRealizedUpper;
    private javax.swing.JTextField textFieldRequest;
    private javax.swing.JTextField textFieldShadow;
    private javax.swing.JTextField textFieldStatus;
    private javax.swing.JTextField textFieldValue;
    // End of variables declaration//GEN-END:variables
}
