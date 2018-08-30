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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Locale;
import java.util.TreeMap;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import tauargus.model.CellStatus;
import tauargus.model.CellStatusStatistics;
import tauargus.model.TableSet;
import tauargus.model.Variable;
import tauargus.utils.TableColumnResizer;
import tauargus.utils.TauArgusUtils;

public class DialogTableSummary extends javax.swing.JDialog {

    public DialogTableSummary(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        tableExpVars.getTableHeader().setReorderingAllowed(false); 
        tableExpVars.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.RIGHT);
                if (column==0){
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                return comp;                
            }
        });

        tableSummary.getTableHeader().setReorderingAllowed(false);      
        tableSummary.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row == table.getModel().getRowCount() - 1) {
                    comp.setFont(comp.getFont().deriveFont(Font.BOLD));
                }
                setHorizontalAlignment(SwingConstants.RIGHT);
                if (column==0){
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                return comp;                
            }
        });
        setLocationRelativeTo(parent);
    }

    void showDialog(final TableSet tableSet) {
        setTitle("Summary for table no: " + (tableSet.index + 1) + " (" + 
                 tableSet.toString() + ")");
        
        setExpVarsModel(tableSet);
        
        labelResponseVar.setText(tableSet.respVar.name);
        labelShadowVar.setText(tableSet.shadowVar == null ? "" : tableSet.shadowVar.name);
        labelCostVar.setText(tableSet.costVar == null ? "" : tableSet.costVar.name);
        switch (tableSet.suppressed){
            case TableSet.SUP_NO :      labelStatus.setText("Not protected"); break;
            case TableSet.SUP_JJ_OPT:   labelStatus.setText("Protected by optimal"); break;
            case TableSet.SUP_GHMITER:  labelStatus.setText("Protected by hypercube"); break;
            case TableSet.SUP_HITAS:    labelStatus.setText("Protected by modular"); break;
            case TableSet.SUP_NETWORK:  labelStatus.setText("Protected by network"); break;
            case TableSet.SUP_SINGLETON:labelStatus.setText("Singleton"); break;
            case TableSet.SUP_ROUNDING: labelStatus.setText("Table has been rounded"); break;
            case TableSet.SUP_MARGINAL: labelStatus.setText("Protected by marginal"); break;
            case TableSet.SUP_UWE:      labelStatus.setText("Protected by UWE"); break;
            case TableSet.SUP_CTA:      labelStatus.setText("Protected by CTA"); break;
            case TableSet.SUP_CKM:      labelStatus.setText("<html>Protecetd by<br>Cell Key Method</html>");break;
            default:                    labelStatus.setText("Unknown"); break;
        }
               
        if (tableSet.suppressed == TableSet.SUP_CKM){
            setSummaryCKM(tableSet);
        }
        else{
            setSummaryStandard(tableSet);
        }
        
        TableColumnResizer.adjustColumnPreferredWidths(tableExpVars, false);
        TableColumnResizer.adjustColumnPreferredWidths(tableSummary, false);
        if (tableSet.suppressed == TableSet.SUP_CKM){
            TableColumnResizer.FixColumnWidth(tableSummary.getColumnModel().getColumn(0), 40);
        }

        ResizeSummaryDialog();

        setVisible(true);
    }
    
    private void ResizeSummaryDialog(){
        java.awt.Dimension d = new java.awt.Dimension(0,0);
        tableSummary.setPreferredScrollableViewportSize(tableSummary.getPreferredSize());
        d = tableSummary.getPreferredSize();
        d.width = d.width+10;
        scrollPaneSummary.setPreferredSize(d);
        scrollPaneSummary.setSize(d);
        
        d.width = (scrollPaneSummary.getWidth() + scrollPaneExpVars.getWidth() + 75);
        d.height = getHeight();
        setPreferredSize(d);
        setSize(d);
    }
    
    private void setSummaryCKM(final TableSet tableSet){
        //Set TableModel
        tableSummary.setModel(new AbstractTableModel() {
            TreeMap<Integer,Long> CKMInfo = tableSet.getCKMStats();
            int rangeD = CKMInfo.size();
            Integer[] TreeMapKeys = CKMInfo.keySet().toArray(new Integer[rangeD]);
            
            @Override
            public int getRowCount() {return (rangeD + 2);}
                
            @Override
            public int getColumnCount() {return 3;}
                
            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                switch(columnIndex) {
                    case 1:
                        if (rowIndex == rangeD){return "Empty";}
                        if (rowIndex == (rangeD + 1)) {return "Total";}
                        else {return TreeMapKeys[rowIndex];}
                    case 2:
                        if (rowIndex == rangeD) return tableSet.numberOfEmpty();
                        else if (rowIndex == rangeD + 1) return tableSet.numberOfCells();
                            else return Long.toString(CKMInfo.get(TreeMapKeys[rowIndex]));
                    default:
                        return "";
                }
            }
                
            @Override
            public String getColumnName(int column) {
                switch(column) {
                    case 1:  return "Noise";
                    case 2:  return "#Cells";
                    default: return "";
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {return false;}
        });
        
        // Change alignment of columns
        tableSummary.setDefaultRenderer(Object.class, new CKMcellRenderer());
    }    
    
    private void setSummaryStandard(final TableSet tableSet){
        //Set TableModel
        tableSummary.setModel(new AbstractTableModel() {
            private CellStatusStatistics statistics = tableSet.getCellStatusStatistics();
            int d = tableSet.respVar.nDecimals;

            @Override
            public int getRowCount() {return CellStatus.size();}

            @Override
            public int getColumnCount() {return 5;}

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                double x; String hs;
                rowIndex++;
                switch(columnIndex) {
                    case 0:
                        if (rowIndex == CellStatus.size()) {return "Total";}
                        else {return CellStatus.findByValue(rowIndex).getDescription();}
                    case 1:
                        return statistics.freq[rowIndex];
                    case 2:
                        return statistics.cellFreq[rowIndex];
                    case 3:
                        //return statistics.cellResponse[rowIndex];
                        x = statistics.cellResponse[rowIndex];
                        hs = String.format(Locale.US, "%."+d+"f", x);
                        return hs;
                    case 4:
                        x = statistics.cellCost[rowIndex];
                        hs = String.format(Locale.US, "%."+d+"f", x);
                        return hs;
                    default:
                        return "";
                }
            }

            @Override
            public String getColumnName(int column) {
                switch(column) {
                    case 0:  return "Status";
                    case 1:  return "#Cells";
                    case 2:  return "#Rec";
                    case 3:  return "Sum resp";
                    case 4:  return "Sum cost";
                    default: return "";
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {return false;}
        });
    }

    private void setExpVarsModel(final TableSet tableSet){
        tableExpVars.setModel(new AbstractTableModel() {
        
            @Override
            public int getRowCount() {return tableSet.expVar.size();}

            @Override
            public int getColumnCount() {return 2;}

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Variable variable = tableSet.expVar.get(rowIndex);
                if (columnIndex == 0) {return variable.name;} 
                else {return TauArgusUtils.getNumberOfActiveCodes(variable.index);}
            }

            @Override
            public String getColumnName(int column) {
                if (column == 0) {return "Expl. var";} 
                else {return "#Codes";}
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {return false;}
            
        });
    }
    
    private class CKMcellRenderer extends DefaultTableCellRenderer{
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row == table.getModel().getRowCount() - 1) {
                    comp.setFont(comp.getFont().deriveFont(Font.BOLD));
                }
                setHorizontalAlignment(SwingConstants.RIGHT);
                
                int R, G, B = 255; // darkest: (85,85,255) brightest: (235,235,255)
                int maxColor = Math.max(Math.abs((Integer) table.getValueAt(0, 1)),
                                        Math.abs((Integer) table.getValueAt(table.getModel().getRowCount() - 3,1)));
                Object colorvalue;
                colorvalue = table.getValueAt(row,1);
                if (column==0 & row < table.getModel().getRowCount() - 2 & !colorvalue.equals(0)){
                    R = (int) (235 - (235-85)*(Math.abs((Integer) colorvalue) - 1)/(maxColor-1));
                    G = R;
                    comp.setBackground(new Color(R,G,B));
                }
                else comp.setBackground(Color.white);
                
                comp.setForeground(Color.black);
                
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

        scrollPaneExpVars = new javax.swing.JScrollPane();
        tableExpVars = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        labelResponseVar = new javax.swing.JLabel();
        labelShadowVar = new javax.swing.JLabel();
        labelCostVar = new javax.swing.JLabel();
        scrollPaneSummary = new javax.swing.JScrollPane();
        tableSummary = new javax.swing.JTable();
        buttonClose = new javax.swing.JButton();
        labelStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Summary for table no: ");
        setIconImages(null);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                DialogClosing(evt);
            }
        });

        tableExpVars.setModel(new javax.swing.table.DefaultTableModel(
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
        scrollPaneExpVars.setViewportView(tableExpVars);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("Respons Var");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("Shadow Var");
        jLabel2.setMaximumSize(new java.awt.Dimension(60, 14));
        jLabel2.setMinimumSize(new java.awt.Dimension(60, 14));
        jLabel2.setName(""); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(60, 14));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText("Cost Var");
        jLabel3.setMaximumSize(new java.awt.Dimension(60, 14));
        jLabel3.setMinimumSize(new java.awt.Dimension(60, 14));
        jLabel3.setPreferredSize(new java.awt.Dimension(60, 14));

        labelResponseVar.setText(" ");

        labelShadowVar.setText(" ");

        labelCostVar.setText(" ");

        scrollPaneSummary.setPreferredSize(new java.awt.Dimension(454, 404));

        tableSummary.setModel(new javax.swing.table.DefaultTableModel(
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
        scrollPaneSummary.setViewportView(tableSummary);

        buttonClose.setText("Close");
        buttonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCloseActionPerformed(evt);
            }
        });

        labelStatus.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(scrollPaneExpVars, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(buttonClose)
                            .addComponent(scrollPaneSummary, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(labelShadowVar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labelCostVar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labelResponseVar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scrollPaneExpVars, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(labelResponseVar))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelShadowVar))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelCostVar))
                        .addGap(18, 18, 18)
                        .addComponent(labelStatus))
                    .addComponent(scrollPaneSummary, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonClose)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCloseActionPerformed
        setVisible(false);
    }//GEN-LAST:event_buttonCloseActionPerformed

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
            java.util.logging.Logger.getLogger(DialogTableSummary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DialogTableSummary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DialogTableSummary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DialogTableSummary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DialogTableSummary dialog = new DialogTableSummary(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton buttonClose;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel labelCostVar;
    private javax.swing.JLabel labelResponseVar;
    private javax.swing.JLabel labelShadowVar;
    private javax.swing.JLabel labelStatus;
    private javax.swing.JScrollPane scrollPaneExpVars;
    private javax.swing.JScrollPane scrollPaneSummary;
    private javax.swing.JTable tableExpVars;
    private javax.swing.JTable tableSummary;
    // End of variables declaration//GEN-END:variables
}
