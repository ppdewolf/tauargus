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
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import tauargus.model.CKMInfoLoss;
import tauargus.model.ECDFGraphBuilder;
import tauargus.utils.TableColumnResizer;


public class ShowMoreInfoLoss extends javax.swing.JDialog {

    CKMInfoLoss InfoLoss;
    /**
     * Creates new form ShowMoreInfoLoss
     */
    public ShowMoreInfoLoss(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    void showDialog(CKMInfoLoss InfoLoss){
        this.InfoLoss = InfoLoss;
        setTitle("Information loss measures calculated over all cells");
        
        java.awt.Dimension d = new java.awt.Dimension(0,0);
        
        SetPart1Model();
        Part1.setDefaultRenderer(Object.class, new InfoCellRenderer());
        Part1.getTableHeader().setFont(Part1.getFont().deriveFont(Font.BOLD));

        SetPart2Model();
        Part2.setDefaultRenderer(Object.class, new InfoCellRenderer());
        Part2.getTableHeader().setFont(Part2.getFont().deriveFont(Font.BOLD));

        labelNFalseZero.setText(Integer.toString(InfoLoss.GetFalseZeros()));
        labelNFalseNonzero.setText(Integer.toString(InfoLoss.GetFalseNonzeros()));
        
        SetECDFModel(ECDF_AD, "AD");
        ECDF_AD.setDefaultRenderer(Object.class, new InfoCellRenderer2());
        ECDF_AD.getTableHeader().setFont(ECDF_AD.getFont().deriveFont(Font.BOLD));

        SetECDFModel(ECDF_RAD, "RAD");
        ECDF_RAD.setDefaultRenderer(Object.class, new InfoCellRenderer2());
        ECDF_RAD.getTableHeader().setFont(ECDF_RAD.getFont().deriveFont(Font.BOLD));

        SetECDFModel(ECDF_DR, "DR");
        ECDF_DR.setDefaultRenderer(Object.class, new InfoCellRenderer2());
        ECDF_DR.getTableHeader().setFont(ECDF_DR.getFont().deriveFont(Font.BOLD));
        
        TableColumnResizer.adjustColumnPreferredWidths(ECDF_AD, false);
        TableColumnResizer.adjustColumnPreferredWidths(ECDF_RAD, false);
        TableColumnResizer.adjustColumnPreferredWidths(ECDF_DR, false);
        
        setVisible(true);
    }
    
    private void SetPart1Model(){
        Part1.setModel(new AbstractTableModel(){
            @Override
            public int getRowCount() {return 4;}
            @Override
            public int getColumnCount() {return 4;}
            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                switch(columnIndex) {
                    case 0:
                        switch(rowIndex){
                            case 0: return "Min";
                            case 1: return "Mean";
                            case 2: return "Median";
                            case 3: return "Max";
                            default: return "";
                        }
                    default:
                        switch(rowIndex){
                            case 0: return String.format("%.5f",InfoLoss.GetMins(getColumnName(columnIndex)));
                            case 1: return String.format("%.5f",InfoLoss.GetMean(getColumnName(columnIndex)));
                            case 2: return String.format("%.5f",InfoLoss.GetMedian(getColumnName(columnIndex)));
                            case 3: return String.format("%.5f",InfoLoss.GetMaxs(getColumnName(columnIndex)));
                            default: return "";
                        }
                }
            }
            @Override
            public String getColumnName(int column) {
                switch(column) {
                    case 0:  return "Descriptives";
                    case 1:  return "AD";
                    case 2:  return "RAD";
                    case 3:  return "DR";
                    default: return "";
                }
            }
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {return false;}
            }
        );
    }
    
    private void SetPart2Model(){
        Part2.setModel(new AbstractTableModel(){
            @Override
            public int getRowCount() {return 11;}
            @Override
            public int getColumnCount() {return 4;}
            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                switch(columnIndex) {
                    case 0:
                        switch(rowIndex){
                            case 0: return "P10";
                            case 1: return "P20";
                            case 2: return "P30";
                            case 3: return "P40";
                            case 4: return "P50";
                            case 5: return "P60";
                            case 6: return "P70";
                            case 7: return "P80";
                            case 8: return "P90";
                            case 9: return "P95";
                            case 10: return "P99";
                            default: return "";
                        }
                    default:
                        return String.format("%.5f",InfoLoss.GetPercentiles(getColumnName(columnIndex))[rowIndex]);
                }
            }
            @Override
            public String getColumnName(int column) {
                switch(column) {
                    case 0:  return "Percentiles";
                    case 1:  return "AD";
                    case 2:  return "RAD";
                    case 3:  return "DR";
                    default: return "";
                }
            }
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {return false;}
            }
        );
    }
    
    private void SetECDFModel(JTable ECDFTable, String Name){
        ECDFTable.setModel(new AbstractTableModel(){
            @Override
            public int getRowCount() {return InfoLoss.GetECDFcounts(Name).getBreaks().length + 1;}
            @Override
            public int getColumnCount() {return 3;}
            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (rowIndex == InfoLoss.GetECDFcounts(Name).getBreaks().length){
                    switch(columnIndex) {
                        case 0: return "Inf";
                        case 1: return String.format("%d",InfoLoss.GetNumberOfCells());
                        case 2: return String.format("%.2f",100.0);
                        default: return "";
                    }
                }
                else{
                    switch(columnIndex) {
                        case 0: return String.format("%.2f",InfoLoss.GetECDFcounts(Name).getBreaks()[rowIndex]);
                        case 1: return String.format("%d",InfoLoss.GetECDFcounts(Name).getCounts()[rowIndex]);
                        case 2: return String.format("%.2f",100.0*((double)InfoLoss.GetECDFcounts(Name).getCounts()[rowIndex]/(double)InfoLoss.GetNumberOfCells()));
                        default: return "";
                    }
                }
            }
            @Override
            public String getColumnName(int column) {
                switch(column) {
                    case 0:  return "Value";
                    case 1:  return "Count";
                    case 2:  return "Fraction (%)";
                    default: return "";
                }
            }
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {return false;}
            }
        );
    }
    
    private class InfoCellRenderer extends DefaultTableCellRenderer{
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.RIGHT);
                if (column==0){
                    comp.setFont(comp.getFont().deriveFont(Font.BOLD));
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                return comp;
            }
    }
    
    private class InfoCellRenderer2 extends DefaultTableCellRenderer{
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.RIGHT);
                return comp;
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

        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        labelNFalseNonzero = new javax.swing.JLabel();
        labelNFalseZero = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Part1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        Part2 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        ECDF_AD = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        ECDF_RAD = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        ECDF_DR = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jButtonADgraph = new javax.swing.JButton();
        jButtonRADgraph = new javax.swing.JButton();
        jButtonDRgraph = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel4.setText("<html><b>False non-zero-cells</b> (zero changed to non-zero)</html>");

        jLabel1.setText("<html><b>False zero-cells</b> (non-zero changed to zero)</html>");

        jLabel3.setText("<html> <table> <tr> <td><b>AD</b></td> <td>Absolute Difference</td> <td>| pert - orig | </td> </tr> <tr> <td><b>RAD</b></td> <td>Relative Absolute Difference</td> <td>( | pert - orig | ) / orig </td> </tr> <tr> <td><b>DR</b></td> <td>Absolute Difference of Square Root</td> <td>| sqrt(pert) - sqrt(orig) | </td> </tr> </table>");

        labelNFalseNonzero.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelNFalseNonzero.setText("0");

        labelNFalseZero.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelNFalseZero.setText("0");

        Part1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"min", null, null, null},
                {"mean", null, null, null},
                {"median", null, null, null},
                {"max", null, null, null}
            },
            new String [] {
                "", "AD", "RAD", "DR"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Part1.setRowSelectionAllowed(false);
        Part1.getTableHeader().setResizingAllowed(false);
        Part1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Part1);

        jScrollPane2.setToolTipText("");
        jScrollPane2.setEnabled(false);

        Part2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"P10", null, null, null},
                {"P20", null, null, null},
                {"P30", null, null, null},
                {"P40", null, null, null},
                {"P50", null, null, null},
                {"P60", null, null, null},
                {"P70", null, null, null},
                {"P80", null, null, null},
                {"P90", null, null, null},
                {"P95", null, null, null},
                {"P99", null, null, null}
            },
            new String [] {
                "", "AD", "RAD", "DR"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Part2.setRowSelectionAllowed(false);
        Part2.getTableHeader().setResizingAllowed(false);
        Part2.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Part2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(labelNFalseNonzero, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                                    .addComponent(labelNFalseZero, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelNFalseNonzero))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelNFalseZero))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jScrollPane3.setViewportView(ECDF_AD);

        jLabel2.setText("<html> <b>ECDF</b> (Empirical Cumulative Distribution Function) <br><br> Number and fraction of cells with Difference less than or equal to Value</html>");

        jLabel5.setText("<html><b>AD</b></html>");

        jScrollPane4.setViewportView(ECDF_RAD);

        jLabel6.setText("<html><b>RAD</b></html>");

        jScrollPane5.setViewportView(ECDF_DR);

        jLabel7.setText("<html><b>DR</b></html>");

        jButtonADgraph.setText("Show graphic");
        jButtonADgraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonADgraphActionPerformed(evt);
            }
        });

        jButtonRADgraph.setText("Show graphic");
        jButtonRADgraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRADgraphActionPerformed(evt);
            }
        });

        jButtonDRgraph.setText("Show graphic");
        jButtonDRgraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDRgraphActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jButtonADgraph)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonRADgraph)))
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(206, 206, 206)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.CENTER, jPanel2Layout.createSequentialGroup()
                .addGap(473, 473, 473)
                .addComponent(jButtonDRgraph)
                .addGap(62, 62, 62))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel2))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonADgraph)
                    .addComponent(jButtonRADgraph)
                    .addComponent(jButtonDRgraph))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonADgraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonADgraphActionPerformed
        ShowECDFGraph ECDFChart = new ShowECDFGraph((JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, this), true);
        ECDFChart.showChart("AD",InfoLoss.GetDiffs("AD"),InfoLoss.GetNumberOfCells());
        ECDFChart.setVisible(true);
    }//GEN-LAST:event_jButtonADgraphActionPerformed

    private void jButtonRADgraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRADgraphActionPerformed
        ShowECDFGraph ECDFChart = new ShowECDFGraph((JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, this), true);
        ECDFChart.showChart("RAD",InfoLoss.GetDiffs("RAD"),InfoLoss.GetNumberOfCells());
        ECDFChart.setVisible(true);
    }//GEN-LAST:event_jButtonRADgraphActionPerformed

    private void jButtonDRgraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDRgraphActionPerformed
        ShowECDFGraph ECDFChart = new ShowECDFGraph((JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, this), true);
        ECDFChart.showChart("DR",InfoLoss.GetDiffs("DR"),InfoLoss.GetNumberOfCells());
        ECDFChart.setVisible(true);
    }//GEN-LAST:event_jButtonDRgraphActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable ECDF_AD;
    private javax.swing.JTable ECDF_DR;
    private javax.swing.JTable ECDF_RAD;
    private javax.swing.JTable Part1;
    private javax.swing.JTable Part2;
    private javax.swing.JButton jButtonADgraph;
    private javax.swing.JButton jButtonDRgraph;
    private javax.swing.JButton jButtonRADgraph;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel labelNFalseNonzero;
    private javax.swing.JLabel labelNFalseZero;
    // End of variables declaration//GEN-END:variables
}
