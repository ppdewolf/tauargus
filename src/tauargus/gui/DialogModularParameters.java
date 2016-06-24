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
import tauargus.model.TableSet;

public class DialogModularParameters extends javax.swing.JDialog {


    // ***** Dialog Return Values *****
    public static final int CANCEL_OPTION = 1;
    public static final int APPROVE_OPTION = 0;
    
    private int returnValue = CANCEL_OPTION;
    private static TableSet tableSet;

    /**
     * Creates new form DialogModularParameters
     */
    public DialogModularParameters(java.awt.Frame parent, TableSet tableSet, boolean forOptimal, boolean modal) {
        super(parent, modal);
        this.tableSet = tableSet;
        initComponents();
        if (forOptimal)
        {
            labelModularParameters.setText("Options for the optimal suppression:");
            setTitle("Optimal options");
        }
        jLabelmaxTimeOptimal.setVisible(forOptimal);
        jTextmaxTimeOptimal.setHorizontalAlignment(jTextmaxTimeOptimal.RIGHT);
        jTextmaxTimeOptimal.setVisible(forOptimal);
        jLabelMaxminutes.setVisible(forOptimal);
        setLocationRelativeTo(parent);
    }

    public int showDialog() {
        setVisible(true);
        return returnValue;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelModularParameters = new javax.swing.JLabel();
        checkBoxSingleton = new javax.swing.JCheckBox();
        checkBoxSingletonMultiple = new javax.swing.JCheckBox();
        checkBoxMinFreq = new javax.swing.JCheckBox();
        panelCommand = new javax.swing.JPanel();
        buttonOk = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        jLabelmaxTimeOptimal = new javax.swing.JLabel();
        jTextmaxTimeOptimal = new javax.swing.JTextField();
        jLabelMaxminutes = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Modular options");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                DialogClosing(evt);
            }
        });

        labelModularParameters.setText("Options for the modular suppression:");

        checkBoxSingleton.setSelected(true);
        checkBoxSingleton.setText("Do Singletons");

        checkBoxSingletonMultiple.setSelected(true);
        checkBoxSingletonMultiple.setText("Do Singleton Multiple");

        checkBoxMinFreq.setSelected(true);
        checkBoxMinFreq.setText("Do Min Frequency");

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
            .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(buttonCancel)
        );
        panelCommandLayout.setVerticalGroup(
            panelCommandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCommandLayout.createSequentialGroup()
                .addComponent(buttonOk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonCancel))
        );

        jLabelmaxTimeOptimal.setText("Max computing time for Optimal");

        jTextmaxTimeOptimal.setText("10");

        jLabelMaxminutes.setText("minutes");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkBoxSingleton)
                            .addComponent(checkBoxSingletonMultiple)
                            .addComponent(labelModularParameters))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelCommand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkBoxMinFreq)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelmaxTimeOptimal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextmaxTimeOptimal, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelMaxminutes)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelModularParameters)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkBoxSingleton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkBoxSingletonMultiple))
                    .addComponent(panelCommand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxMinFreq)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelmaxTimeOptimal)
                    .addComponent(jTextmaxTimeOptimal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelMaxminutes))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        returnValue = CANCEL_OPTION;
        setVisible(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkActionPerformed
        tableSet.singletonSingletonCheck = checkBoxSingleton.isSelected();
        tableSet.singletonMultipleCheck = checkBoxSingletonMultiple.isSelected();
        tableSet.minFreqCheck = checkBoxMinFreq.isSelected();
       {tableSet.maxTimeOptimal = Integer.parseInt(jTextmaxTimeOptimal.getText());}
        setVisible(false);
        returnValue = APPROVE_OPTION;
    }//GEN-LAST:event_buttonOkActionPerformed

    private void DialogClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_DialogClosing
        setVisible(false);
    }//GEN-LAST:event_DialogClosing

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
            java.util.logging.Logger.getLogger(DialogModularParameters.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DialogModularParameters.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DialogModularParameters.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DialogModularParameters.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DialogModularParameters dialog = new DialogModularParameters(new javax.swing.JFrame(), tableSet, false, true);
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
    private javax.swing.JButton buttonOk;
    private javax.swing.JCheckBox checkBoxMinFreq;
    private javax.swing.JCheckBox checkBoxSingleton;
    private javax.swing.JCheckBox checkBoxSingletonMultiple;
    private javax.swing.JLabel jLabelMaxminutes;
    private javax.swing.JLabel jLabelmaxTimeOptimal;
    private javax.swing.JTextField jTextmaxTimeOptimal;
    private javax.swing.JLabel labelModularParameters;
    private javax.swing.JPanel panelCommand;
    // End of variables declaration//GEN-END:variables
}
