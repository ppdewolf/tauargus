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

import javax.swing.JOptionPane;
import tauargus.extern.dataengine.TauArgus;
import tauargus.model.Application;
import tauargus.model.ArgusException;
import tauargus.model.Metadata;
import tauargus.model.TableSet;
import tauargus.model.Variable;
import tauargus.service.TableService;
import argus.utils.StrUtils;
import tauargus.utils.TauArgusUtils;

public class DialogRoundingParameters extends DialogBase {

    // ***** Dialog Return Values *****
    public static final int CANCEL_OPTION = 1;
    public static final int APPROVE_OPTION = 0;
    
    private TauArgus tauArgus = Application.getTauArgusDll();

    private TableSet tableSet;
    private boolean partitionAllowed;
    private long minRoundBase;
    private int NPart;
    private int SubTableSize;
    private int returnValue = CANCEL_OPTION;

    /**
     * Creates new form DialogRoundingParameters
     */
    public DialogRoundingParameters(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        organizePartitionOptions();
        setLocationRelativeTo(parent);
    }

    public int showDialog(TableSet table) {
        this.tableSet = table;
        partitionAllowed = (tableSet.expVar.get(0).hierarchical==Variable.HIER_NONE );
        panelAudit.setVisible(Application.isAnco());
        minRoundBase = computeMinRoundBase(tableSet);
        labelMinRoundingBase.setText("Min. roundingbase required= " + minRoundBase);
        textFieldRoundingBase.requestFocusInWindow();
        boolean b = table.expVar.size() > 1;
// We allow only partitions if the first variable is non-nierarchical        
// So NumberOfBlocks and the totals are disabled       
        checkBoxPartition.setEnabled(b && partitionAllowed);
        checkBoxNumberOfBlocks.setEnabled(b);
        checkBoxTotals.setEnabled(b);
        textFieldNumberOfBlocks.setEnabled(b);  
        checkBoxNumberOfBlocks.setVisible(false);
        checkBoxTotals.setVisible(false);
        textFieldNumberOfBlocks.setVisible(false);
        
        checkBoxPartition.setSelected(false);
//        checkBoxPartition.click();
        // checkBoxPartition.setEnabled(true) //(table.expVar.get(0).hierarchical == HIER_NONE)
        if (table.expVar.get(0).hierarchical != Variable.HIER_NONE) {
            checkBoxTotals.setEnabled(false);
            checkBoxNumberOfBlocks.setEnabled(false);
            textFieldNumberOfBlocks.setEnabled(false);
        }
        panelPartitions.setVisible(checkBoxPartition.isEnabled());
        labelPartNumber.setEnabled(false);
        labelPartTableSize.setEnabled(false);
        checkBoxTotals.setVisible(Application.isAnco());
        checkBoxNumberOfBlocks.setVisible(Application.isAnco());
        textFieldNumberOfBlocks.setVisible(Application.isAnco());
        SubTableSize = 1;
        for (int i = 0; i < table.expVar.size(); i++) {
            Variable variable = table.expVar.get(i);
            int varIndex = variable.index;
            int numberOfActiveCodes = TauArgusUtils.getNumberOfActiveCodes(varIndex);
            if (i == 0) {
                NPart = numberOfActiveCodes - 1;
            }
            else {
                SubTableSize = SubTableSize * numberOfActiveCodes;
            }
        }
        labelPartNumber.setText(NPart + " subtables");
        labelPartTableSize.setText(SubTableSize + " cells per subtable");
//        PrepareRoundVar;
//        TestSize();
        comboBoxNumSteps.setSelectedIndex(0);
//        chkOKButton;
        organizePartitionOptions();
        setVisible(true);

        return returnValue;
    }
    
    private void organizePartitionOptions() {
        boolean b = checkBoxPartition.isSelected();
        checkBoxNumberOfBlocks.setEnabled(b);
        textFieldNumberOfBlocks.setEnabled(b && checkBoxNumberOfBlocks.isSelected());
        checkBoxTotals.setEnabled(b);
        checkBoxNumberOfBlocks.setVisible(false);
        checkBoxTotals.setVisible(false);
        textFieldNumberOfBlocks.setVisible(false);
        panelPartitions.setEnabled(b);
        labelPartNumber.setEnabled(b);
        labelPartTableSize.setEnabled(b);
        panelPartitions.setVisible(b);
//        labelPartNumber.setVisible(b);
//        labelPartTableSize.setVisible(b);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupStoppingRule = new javax.swing.ButtonGroup();
        buttonGroupAudit = new javax.swing.ButtonGroup();
        labelMinRoundingBase = new javax.swing.JLabel();
        labelRoundingBase = new javax.swing.JLabel();
        labelNumSteps = new javax.swing.JLabel();
        labelTime = new javax.swing.JLabel();
        textFieldRoundingBase = new javax.swing.JTextField();
        comboBoxNumSteps = new javax.swing.JComboBox();
        textMaxRoundTime = new javax.swing.JTextField();
        checkBoxPartition = new javax.swing.JCheckBox();
        checkBoxNumberOfBlocks = new javax.swing.JCheckBox();
        checkBoxTotals = new javax.swing.JCheckBox();
        panelCommand = new javax.swing.JPanel();
        buttonOk = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        textFieldNumberOfBlocks = new javax.swing.JTextField();
        panelStoppingRule = new javax.swing.JPanel();
        radioButtonStopRapid = new javax.swing.JRadioButton();
        radioButtonStopFeasible = new javax.swing.JRadioButton();
        radioButtonOptimal = new javax.swing.JRadioButton();
        panelPartitions = new javax.swing.JPanel();
        labelPartNumber = new javax.swing.JLabel();
        labelPartTableSize = new javax.swing.JLabel();
        panelAudit = new javax.swing.JPanel();
        radioButtonAuditNo = new javax.swing.JRadioButton();
        radioButtonAuditCheck = new javax.swing.JRadioButton();
        radioButtonAuditFull = new javax.swing.JRadioButton();
        labelTimeUnits = new javax.swing.JLabel();
        checkBoxUnitCost = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Rounding");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                DialogClosing(evt);
            }
        });

        labelMinRoundingBase.setText("     ");

        labelRoundingBase.setText("Rounding base:");

        labelNumSteps.setText("Number of steps allowed:");

        labelTime.setText("Max computing time:");

        comboBoxNumSteps.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6" }));

        textMaxRoundTime.setText("10");

        checkBoxPartition.setText("Partitions");
        checkBoxPartition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxPartitionActionPerformed(evt);
            }
        });

        checkBoxNumberOfBlocks.setText("Number Of Blocks");
        checkBoxNumberOfBlocks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxNumberOfBlocksActionPerformed(evt);
            }
        });

        checkBoxTotals.setText("Add intermediate totals");

        buttonOk.setText("OK");
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });

        buttonCancel.setText("Cancel");
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelCommandLayout = new javax.swing.GroupLayout(panelCommand);
        panelCommand.setLayout(panelCommandLayout);
        panelCommandLayout.setHorizontalGroup(
            panelCommandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCommandLayout.createSequentialGroup()
                .addComponent(buttonCancel)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(buttonOk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelCommandLayout.setVerticalGroup(
            panelCommandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCommandLayout.createSequentialGroup()
                .addComponent(buttonOk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonCancel))
        );

        panelStoppingRule.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Stopping Rule", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        buttonGroupStoppingRule.add(radioButtonStopRapid);
        radioButtonStopRapid.setText("First RAPID only");

        buttonGroupStoppingRule.add(radioButtonStopFeasible);
        radioButtonStopFeasible.setText("first feasible only");

        buttonGroupStoppingRule.add(radioButtonOptimal);
        radioButtonOptimal.setSelected(true);
        radioButtonOptimal.setText("Optimal solution");

        javax.swing.GroupLayout panelStoppingRuleLayout = new javax.swing.GroupLayout(panelStoppingRule);
        panelStoppingRule.setLayout(panelStoppingRuleLayout);
        panelStoppingRuleLayout.setHorizontalGroup(
            panelStoppingRuleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStoppingRuleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelStoppingRuleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radioButtonStopRapid)
                    .addComponent(radioButtonStopFeasible)
                    .addComponent(radioButtonOptimal))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelStoppingRuleLayout.setVerticalGroup(
            panelStoppingRuleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStoppingRuleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(radioButtonStopRapid)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonStopFeasible)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonOptimal)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelPartitions.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Partitions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        labelPartNumber.setText(" ");

        labelPartTableSize.setText(" ");

        javax.swing.GroupLayout panelPartitionsLayout = new javax.swing.GroupLayout(panelPartitions);
        panelPartitions.setLayout(panelPartitionsLayout);
        panelPartitionsLayout.setHorizontalGroup(
            panelPartitionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPartitionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPartitionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelPartNumber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelPartTableSize, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelPartitionsLayout.setVerticalGroup(
            panelPartitionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPartitionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelPartNumber)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelPartTableSize)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelAudit.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Audit", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        buttonGroupAudit.add(radioButtonAuditNo);
        radioButtonAuditNo.setSelected(true);
        radioButtonAuditNo.setText("No Audit");

        buttonGroupAudit.add(radioButtonAuditCheck);
        radioButtonAuditCheck.setText("Check only");

        buttonGroupAudit.add(radioButtonAuditFull);
        radioButtonAuditFull.setText("Full Audit");

        javax.swing.GroupLayout panelAuditLayout = new javax.swing.GroupLayout(panelAudit);
        panelAudit.setLayout(panelAuditLayout);
        panelAuditLayout.setHorizontalGroup(
            panelAuditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAuditLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelAuditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radioButtonAuditNo)
                    .addComponent(radioButtonAuditCheck)
                    .addComponent(radioButtonAuditFull))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelAuditLayout.setVerticalGroup(
            panelAuditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAuditLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(radioButtonAuditNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonAuditCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonAuditFull)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        labelTimeUnits.setText("mins.");

        checkBoxUnitCost.setSelected(true);
        checkBoxUnitCost.setText("Unit cost function");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelMinRoundingBase, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelRoundingBase)
                            .addComponent(labelNumSteps)
                            .addComponent(labelTime))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(textFieldRoundingBase, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(panelCommand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(checkBoxPartition)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(checkBoxNumberOfBlocks)
                                        .addGap(18, 18, 18)
                                        .addComponent(textFieldNumberOfBlocks, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(checkBoxTotals)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(textMaxRoundTime, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(comboBoxNumSteps, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(labelTimeUnits))
                                    .addComponent(checkBoxUnitCost))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelStoppingRule, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelPartitions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelAudit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelMinRoundingBase)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelRoundingBase)
                            .addComponent(textFieldRoundingBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(panelCommand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelNumSteps)
                    .addComponent(comboBoxNumSteps, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTime)
                    .addComponent(textMaxRoundTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelTimeUnits))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxUnitCost)
                .addGap(5, 5, 5)
                .addComponent(checkBoxPartition)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkBoxNumberOfBlocks)
                    .addComponent(textFieldNumberOfBlocks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkBoxTotals)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelStoppingRule, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelPartitions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelAudit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void checkBoxPartitionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxPartitionActionPerformed
        organizePartitionOptions();
        
    }//GEN-LAST:event_checkBoxPartitionActionPerformed

    private void checkBoxNumberOfBlocksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxNumberOfBlocksActionPerformed
        organizePartitionOptions();
    }//GEN-LAST:event_checkBoxNumberOfBlocksActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void DialogClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_DialogClosing
        setVisible(false);
    }//GEN-LAST:event_DialogClosing

    private boolean testRoundingBase(){
        long rb = 0;
        if (textFieldRoundingBase.getText().trim().equals("")){ rb = 0;}
        else{
          try{
            rb = StrUtils.toInteger(textFieldRoundingBase.getText());
          } 
          catch(Exception ex){}
//          catch (ArgusException ex){}
        }
        return minRoundBase <= rb;
    }
    
    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkActionPerformed
        if (!testRoundingBase()){
         JOptionPane.showMessageDialog(this, "Please fill in a correct rounding base");   
        }
        else {
        setVisible(false);
        storeRoundingParameters();
        returnValue = APPROVE_OPTION;
        }
    }//GEN-LAST:event_buttonOkActionPerformed

    private void storeRoundingParameters(){
       tableSet.roundBase = Integer.parseInt(textFieldRoundingBase.getText());
       tableSet.roundMaxTime = Integer.parseInt(textMaxRoundTime.getText());
       tableSet.roundMaxStep = comboBoxNumSteps.getSelectedIndex();
       tableSet.roundUnitCost = checkBoxUnitCost.isSelected();
       tableSet.roundPartitions = 0;
       if (checkBoxPartition.isSelected()){tableSet.roundPartitions = 1;}
       tableSet.roundAddIntermediateTotals = checkBoxTotals.isSelected();
       tableSet.roundStoppingRule = 2;
       if (radioButtonStopRapid.isSelected()){tableSet.roundStoppingRule = 0;}
       if (radioButtonStopFeasible.isSelected()){tableSet.roundStoppingRule = 1;}
       if (checkBoxTotals.isSelected()){tableSet.roundPartitions = 2;}
       tableSet.roundNumberofBlocks = 0;
       if (checkBoxNumberOfBlocks.isSelected()) 
            {tableSet.roundNumberofBlocks = Integer.parseInt(textFieldNumberOfBlocks.getText());
             tableSet.roundPartitions = 3;}
}
    
    public long computeMinRoundBase(TableSet table) {
        long minRoundBase;
//        TableSet table = TableService.getTable(tableIndex);
        if (table.respVar == Application.getFreqVar()) {
            minRoundBase = table.minFreq[table.holding ? 1 : 0];
        }
        else {
            minRoundBase = (long)tauArgus.MaximumProtectionLevel(table.index);
        }
        if (minRoundBase < 1) minRoundBase = 1;
        return minRoundBase;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DialogRoundingParameters.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DialogRoundingParameters.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DialogRoundingParameters.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DialogRoundingParameters.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DialogRoundingParameters dialog = new DialogRoundingParameters(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.ButtonGroup buttonGroupAudit;
    private javax.swing.ButtonGroup buttonGroupStoppingRule;
    private javax.swing.JButton buttonOk;
    private javax.swing.JCheckBox checkBoxNumberOfBlocks;
    private javax.swing.JCheckBox checkBoxPartition;
    private javax.swing.JCheckBox checkBoxTotals;
    private javax.swing.JCheckBox checkBoxUnitCost;
    private javax.swing.JComboBox comboBoxNumSteps;
    private javax.swing.JLabel labelMinRoundingBase;
    private javax.swing.JLabel labelNumSteps;
    private javax.swing.JLabel labelPartNumber;
    private javax.swing.JLabel labelPartTableSize;
    private javax.swing.JLabel labelRoundingBase;
    private javax.swing.JLabel labelTime;
    private javax.swing.JLabel labelTimeUnits;
    private javax.swing.JPanel panelAudit;
    private javax.swing.JPanel panelCommand;
    private javax.swing.JPanel panelPartitions;
    private javax.swing.JPanel panelStoppingRule;
    private javax.swing.JRadioButton radioButtonAuditCheck;
    private javax.swing.JRadioButton radioButtonAuditFull;
    private javax.swing.JRadioButton radioButtonAuditNo;
    private javax.swing.JRadioButton radioButtonOptimal;
    private javax.swing.JRadioButton radioButtonStopFeasible;
    private javax.swing.JRadioButton radioButtonStopRapid;
    private javax.swing.JTextField textFieldNumberOfBlocks;
    private javax.swing.JTextField textFieldRoundingBase;
    private javax.swing.JTextField textMaxRoundTime;
    // End of variables declaration//GEN-END:variables
}
