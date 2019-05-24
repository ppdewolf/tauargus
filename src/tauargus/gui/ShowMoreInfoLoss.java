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
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import tauargus.model.CKMInfoLoss;


public class ShowMoreInfoLoss extends javax.swing.JDialog {

    /**
     * Creates new form ShowMoreInfoLoss
     */
    public ShowMoreInfoLoss(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    void showDialog(CKMInfoLoss InfoLoss){
        setTitle("Information loss measures calculated over all cells");
        
        java.awt.Dimension d = new java.awt.Dimension(0,0);
        
        SetPart1Model(InfoLoss);
        Part1.setDefaultRenderer(Object.class, new InfoCellRenderer());
        Part1.getTableHeader().setFont(Part1.getFont().deriveFont(Font.BOLD));

        SetPart2Model(InfoLoss);
        Part2.setDefaultRenderer(Object.class, new InfoCellRenderer());
        Part2.getTableHeader().setFont(Part2.getFont().deriveFont(Font.BOLD));

        setVisible(true);
    }
    
    private void SetPart1Model(CKMInfoLoss InfoLoss){
        Part1.setModel(new AbstractTableModel(){
            @Override
            public int getRowCount() {return 3;}
            @Override
            public int getColumnCount() {return 4;}
            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                switch(columnIndex) {
                    case 0:
                        switch(rowIndex){
                            case 0: return "Mean";
                            case 1: return "Median";
                            case 2: return "Max";
                            default: return "";
                        }
                    default:
                        switch(rowIndex){
                            case 0: return String.format("%.5f",InfoLoss.GetMean(getColumnName(columnIndex)));
                            case 1: return String.format("%.5f",InfoLoss.GetMedian(getColumnName(columnIndex)));
                            case 2: return String.format("%.5f",InfoLoss.GetMaxs(getColumnName(columnIndex)));
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
    
    private void SetPart2Model(CKMInfoLoss InfoLoss){
        Part2.setModel(new AbstractTableModel(){
            @Override
            public int getRowCount() {return 6;}
            @Override
            public int getColumnCount() {return 4;}
            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                switch(columnIndex) {
                    case 0:
                        switch(rowIndex){
                            case 0: return "P60";
                            case 1: return "P70";
                            case 2: return "P80";
                            case 3: return "P90";
                            case 4: return "P95";
                            case 5: return "P99";
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
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        Part1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        Part2 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        Part1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
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
        Part1.setColumnSelectionAllowed(true);
        Part1.setRowSelectionAllowed(false);
        Part1.getTableHeader().setResizingAllowed(false);
        Part1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Part1);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jScrollPane2.setToolTipText("");
        jScrollPane2.setEnabled(false);

        Part2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
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
        Part2.setColumnSelectionAllowed(true);
        Part2.setRowSelectionAllowed(false);
        Part2.getTableHeader().setResizingAllowed(false);
        Part2.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Part2);

        jLabel3.setText("<html>\n<table>\n<tr>\n<td><b>AD</b></td>\n<td>Absolute Difference</td>\n<td>| pert - orig | </td>\n</tr>\n<tr>\n<td><b>RAD</b></td>\n<td>Relative Absolute Difference</td>\n<td>( | pert - orig | ) / orig </td>\n</tr>\n<tr>\n<td><b>DR</b></td>\n<td>Absolute Difference of Square Root</td>\n<td>| sqrt(pert) - sqrt(orig) | </td>\n</tr>\n</table>");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Part1;
    private javax.swing.JTable Part2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
