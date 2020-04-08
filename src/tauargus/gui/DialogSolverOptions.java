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

//import tauargus.utils.ExecUtils;
import argus.utils.SystemUtils;

/**
 *
 * @author Peter-Paul
 */
public class DialogSolverOptions extends javax.swing.JDialog {

    /*
     * Creates new form DialogSolverOptions
     * Mode ideally the defaults are set once (e.g. in OptiSuppress)
    */ 
    public DialogSolverOptions(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        jTextFieldZero.setText(SystemUtils.getRegString("solveroptions", "zero", "0.0000001"));
        jTextFieldZero1.setText(SystemUtils.getRegString("solveroptions", "zero1", "0.0000001"));
        jTextFieldZero2.setText(SystemUtils.getRegString("solveroptions", "zero2", "0.0000000001"));
        jTextFieldInfinity.setText(SystemUtils.getRegString("solveroptions", "infinity", "2140000000"));
        jTextFieldFeasTol.setText(SystemUtils.getRegString("solveroptions", "feastol","0.000001"));
        jTextFieldOptTol.setText(SystemUtils.getRegString("solveroptions", "opttol","0.000000001"));
        jTextFieldMinViola.setText(SystemUtils.getRegString("solveroptions", "minviola","0.0001"));
        jTextFieldMaxSlack.setText(SystemUtils.getRegString("solveroptions", "maxslack","0.01"));
        
        jTextFieldMaxColslp.setText(SystemUtils.getRegString("solveroptions", "maxcolslp","50000"));
        jTextFieldMaxRowslp.setText(SystemUtils.getRegString("solveroptions", "maxrowslp","15000"));
        jTextFieldMaxCutsPool.setText(SystemUtils.getRegString("solveroptions", "maxcutspool","500000"));
        jTextFieldMaxCutsIter.setText(SystemUtils.getRegString("solveroptions", "maxcutsiter","50"));
        
        jTextFieldCTATolerance.setText(SystemUtils.getRegString("solveroptions", "ctatolerance","0.00001"));
        
        jCheckBoxApplyScaling.setSelected(SystemUtils.getRegBoolean("solveroptions", "applyscaling",false));
        jCheckBoxApplyScaling.setVisible(false);
        //This option is not active any more.
        jTextFieldRounderZero.setText(SystemUtils.getRegString("optimal", "jjRoundZero","0.0000001"));
        jTextFieldRounderInfinity.setText(SystemUtils.getRegString("optimal", "jjRoundInf",  "21400000000000"));
        jTextFieldRounderMinViola.setText(SystemUtils.getRegString("optimal", "jjRoundMinViola", "0.0001"));
        jTextFieldRounderMaxSlack.setText(SystemUtils.getRegString("optimal", "jjRoundMaxSlack", "0.01"));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTextFieldZero = new javax.swing.JTextField();
        jLabelZero = new javax.swing.JLabel();
        jLabelZero1 = new javax.swing.JLabel();
        jLabelzero2 = new javax.swing.JLabel();
        jLabelInfinity = new javax.swing.JLabel();
        jTextFieldZero1 = new javax.swing.JTextField();
        jTextFieldZero2 = new javax.swing.JTextField();
        jTextFieldInfinity = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabelMaxColslp = new javax.swing.JLabel();
        jTextFieldMaxColslp = new javax.swing.JTextField();
        jLabelMaxRowslp = new javax.swing.JLabel();
        jTextFieldMaxRowslp = new javax.swing.JTextField();
        jLabelMaxCutsPool = new javax.swing.JLabel();
        jTextFieldMaxCutsPool = new javax.swing.JTextField();
        jLabelMaxCutsIter = new javax.swing.JLabel();
        jTextFieldMaxCutsIter = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabelMinViola = new javax.swing.JLabel();
        jTextFieldMinViola = new javax.swing.JTextField();
        jLabelMaxSlack = new javax.swing.JLabel();
        jTextFieldMaxSlack = new javax.swing.JTextField();
        jLabelFeasTol = new javax.swing.JLabel();
        jTextFieldFeasTol = new javax.swing.JTextField();
        jLabelOptTol = new javax.swing.JLabel();
        jTextFieldOptTol = new javax.swing.JTextField();
        jCheckBoxApplyScaling = new javax.swing.JCheckBox();
        jButtonDefaults = new javax.swing.JButton();
        jButtonOK = new javax.swing.JButton();
        jLabelCTATolerance = new javax.swing.JLabel();
        jTextFieldCTATolerance = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldRounderZero = new javax.swing.JTextField();
        jTextFieldRounderInfinity = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldRounderMinViola = new javax.swing.JTextField();
        jTextFieldRounderMaxSlack = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Solver Options");
        setIconImage(null);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Zero and Infinity"));
        jPanel1.setName(""); // NOI18N
        jPanel1.setOpaque(false);

        jTextFieldZero.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldZero.setText("0.0000001");

        jLabelZero.setText("Zero");

        jLabelZero1.setText("Zero1");

        jLabelzero2.setText("Zero2");

        jLabelInfinity.setText("Infinity");

        jTextFieldZero1.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldZero1.setText("0.0000001");

        jTextFieldZero2.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldZero2.setText("0.0000000001");

        jTextFieldInfinity.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldInfinity.setText("21400000000000");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelInfinity)
                    .addComponent(jLabelzero2)
                    .addComponent(jLabelZero1)
                    .addComponent(jLabelZero))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextFieldZero2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldZero1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldZero, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldInfinity))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldZero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelZero))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelZero1)
                    .addComponent(jTextFieldZero1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelzero2)
                    .addComponent(jTextFieldZero2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldInfinity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelInfinity))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Problem sizes"));

        jLabelMaxColslp.setText("MaxColslp");

        jTextFieldMaxColslp.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldMaxColslp.setText("50000");

        jLabelMaxRowslp.setText("MaxRowslp");

        jTextFieldMaxRowslp.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldMaxRowslp.setText("15000");

        jLabelMaxCutsPool.setText("MaxCutsPool");

        jTextFieldMaxCutsPool.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldMaxCutsPool.setText("500000");

        jLabelMaxCutsIter.setText("MaxCutsIter");

        jTextFieldMaxCutsIter.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldMaxCutsIter.setText("50");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelMaxCutsIter)
                    .addComponent(jLabelMaxCutsPool)
                    .addComponent(jLabelMaxRowslp)
                    .addComponent(jLabelMaxColslp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextFieldMaxCutsPool, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(jTextFieldMaxCutsIter)
                    .addComponent(jTextFieldMaxRowslp, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldMaxColslp, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelMaxColslp)
                    .addComponent(jTextFieldMaxColslp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelMaxRowslp)
                    .addComponent(jTextFieldMaxRowslp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelMaxCutsPool)
                    .addComponent(jTextFieldMaxCutsPool, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelMaxCutsIter)
                    .addComponent(jTextFieldMaxCutsIter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Precision"));

        jLabelMinViola.setText("MinViola");

        jTextFieldMinViola.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldMinViola.setText("0.0001");

        jLabelMaxSlack.setText("MaxSlack");

        jTextFieldMaxSlack.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldMaxSlack.setText("0.01");

        jLabelFeasTol.setText("FeasTol");

        jTextFieldFeasTol.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldFeasTol.setText("0.000001");

        jLabelOptTol.setText("OptTol");

        jTextFieldOptTol.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldOptTol.setText("0.000000001");
        jTextFieldOptTol.setToolTipText("");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelOptTol)
                    .addComponent(jLabelFeasTol)
                    .addComponent(jLabelMaxSlack)
                    .addComponent(jLabelMinViola))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldMaxSlack)
                    .addComponent(jTextFieldMinViola, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextFieldOptTol, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                    .addComponent(jTextFieldFeasTol, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelMinViola)
                    .addComponent(jTextFieldMinViola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelMaxSlack)
                    .addComponent(jTextFieldMaxSlack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelFeasTol)
                    .addComponent(jTextFieldFeasTol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelOptTol)
                    .addComponent(jTextFieldOptTol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jCheckBoxApplyScaling.setText("Apply Scaling");

        jButtonDefaults.setText("Restore Defaults");
        jButtonDefaults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDefaultsActionPerformed(evt);
            }
        });

        jButtonOK.setText("OK");
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });

        jLabelCTATolerance.setText("CTA Tolerance");

        jTextFieldCTATolerance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldCTATolerance.setText("0.00001");

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Rounder parameters"));
        jPanel4.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanel4.setOpaque(false);

        jLabel1.setText("Zero");

        jLabel2.setText("Infinity");

        jTextFieldRounderZero.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldRounderZero.setText("0.0000001");

        jTextFieldRounderInfinity.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldRounderInfinity.setText("21400000000000");

        jLabel3.setText("MinViola");

        jLabel4.setText("MaxSlack");

        jTextFieldRounderMinViola.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldRounderMinViola.setText("0.0001");
        jTextFieldRounderMinViola.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldRounderMinViolaActionPerformed(evt);
            }
        });

        jTextFieldRounderMaxSlack.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldRounderMaxSlack.setText("0.01");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldRounderZero)
                    .addComponent(jTextFieldRounderInfinity, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldRounderMinViola, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .addComponent(jTextFieldRounderMaxSlack))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldRounderZero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldRounderMinViola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jTextFieldRounderInfinity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldRounderMaxSlack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxApplyScaling)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabelCTATolerance)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldCTATolerance, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButtonDefaults)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonOK)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBoxApplyScaling)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelCTATolerance)
                            .addComponent(jTextFieldCTATolerance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(52, 52, 52)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonOK)
                            .addComponent(jButtonDefaults))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(53, Short.MAX_VALUE))
        );

        jPanel1.getAccessibleContext().setAccessibleDescription("");
        jPanel4.getAccessibleContext().setAccessibleName("Rounder parameters");
        jPanel4.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        SystemUtils.putRegString("solveroptions", "zero", jTextFieldZero.getText());
        SystemUtils.putRegString("solveroptions", "zero1", jTextFieldZero1.getText());
        SystemUtils.putRegString("solveroptions", "zero2", jTextFieldZero2.getText());
        SystemUtils.putRegString("solveroptions", "infinity", jTextFieldInfinity.getText());
        SystemUtils.putRegString("solveroptions", "feastol", jTextFieldFeasTol.getText());
        SystemUtils.putRegString("solveroptions", "opttol", jTextFieldOptTol.getText());
        SystemUtils.putRegString("solveroptions", "minviola", jTextFieldMinViola.getText());
        SystemUtils.putRegString("solveroptions", "maxslack", jTextFieldMaxSlack.getText());
        
        SystemUtils.putRegString("solveroptions", "maxcolslp", jTextFieldMaxColslp.getText());
        SystemUtils.putRegString("solveroptions", "maxrowslp", jTextFieldMaxRowslp.getText());
        SystemUtils.putRegString("solveroptions", "maxcutspool", jTextFieldMaxCutsPool.getText());
        SystemUtils.putRegString("solveroptions", "maxcutsiter", jTextFieldMaxCutsIter.getText());

        SystemUtils.putRegString("solveroptions", "ctatolerance", jTextFieldCTATolerance.getText());
       
        SystemUtils.putRegString("optimal", "jjRoundZero",jTextFieldRounderZero.getText());
        SystemUtils.putRegString("optimal", "jjRoundInf", jTextFieldRounderInfinity.getText());
        SystemUtils.putRegString("optimal", "jjRoundMinViola", jTextFieldRounderMinViola.getText());
        SystemUtils.putRegString("optimal", "jjRoundMaxSlack", jTextFieldRounderMaxSlack.getText());

        setVisible(false);
        dispose();
    }//GEN-LAST:event_jButtonOKActionPerformed

    private void jButtonDefaultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDefaultsActionPerformed
        jTextFieldZero.setText("0.0000001");
        jTextFieldZero1.setText("0.0000001");
        jTextFieldZero2.setText("0.0000000001");
        jTextFieldInfinity.setText("2140000000");
        jTextFieldFeasTol.setText("0.000001");
        jTextFieldOptTol.setText("0.000000001");
        jTextFieldMinViola.setText("0.0001");
        jTextFieldMaxSlack.setText("0.01");
        
        jTextFieldMaxColslp.setText("50000");
        jTextFieldMaxRowslp.setText("15000");
        jTextFieldMaxCutsPool.setText("500000");
        jTextFieldMaxCutsIter.setText("50");
        
        jTextFieldCTATolerance.setText("0.00001");
    }//GEN-LAST:event_jButtonDefaultsActionPerformed

    private void jTextFieldRounderMinViolaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldRounderMinViolaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldRounderMinViolaActionPerformed

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(DialogSolverOptions.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(DialogSolverOptions.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(DialogSolverOptions.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(DialogSolverOptions.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the dialog */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                DialogSolverOptions dialog = new DialogSolverOptions(new javax.swing.JFrame(), true);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                    @Override
//                    public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//                
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDefaults;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JCheckBox jCheckBoxApplyScaling;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelCTATolerance;
    private javax.swing.JLabel jLabelFeasTol;
    private javax.swing.JLabel jLabelInfinity;
    private javax.swing.JLabel jLabelMaxColslp;
    private javax.swing.JLabel jLabelMaxCutsIter;
    private javax.swing.JLabel jLabelMaxCutsPool;
    private javax.swing.JLabel jLabelMaxRowslp;
    private javax.swing.JLabel jLabelMaxSlack;
    private javax.swing.JLabel jLabelMinViola;
    private javax.swing.JLabel jLabelOptTol;
    private javax.swing.JLabel jLabelZero;
    private javax.swing.JLabel jLabelZero1;
    private javax.swing.JLabel jLabelzero2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField jTextFieldCTATolerance;
    private javax.swing.JTextField jTextFieldFeasTol;
    private javax.swing.JTextField jTextFieldInfinity;
    private javax.swing.JTextField jTextFieldMaxColslp;
    private javax.swing.JTextField jTextFieldMaxCutsIter;
    private javax.swing.JTextField jTextFieldMaxCutsPool;
    private javax.swing.JTextField jTextFieldMaxRowslp;
    private javax.swing.JTextField jTextFieldMaxSlack;
    private javax.swing.JTextField jTextFieldMinViola;
    private javax.swing.JTextField jTextFieldOptTol;
    private javax.swing.JTextField jTextFieldRounderInfinity;
    private javax.swing.JTextField jTextFieldRounderMaxSlack;
    private javax.swing.JTextField jTextFieldRounderMinViola;
    private javax.swing.JTextField jTextFieldRounderZero;
    private javax.swing.JTextField jTextFieldZero;
    private javax.swing.JTextField jTextFieldZero1;
    private javax.swing.JTextField jTextFieldZero2;
    // End of variables declaration//GEN-END:variables
}
