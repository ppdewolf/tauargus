/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tauargus.gui;

import argus.utils.SystemUtils;
import java.io.File;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import tauargus.model.ArgusException;
import tauargus.model.TableSet;
import tauargus.utils.TauArgusUtils;

/**
 *
 * @author pwof
 */
public class DialogChangePTable extends DialogBase{ //javax.swing.JDialog {

    private static final Logger logger = Logger.getLogger(DialogOpenMicrodata.class.getName());
    
    public static final int CANCEL_OPTION = 1;
    public static final int APPROVE_OPTION = 0;
    
    private int returnValue = CANCEL_OPTION;
    private TableSet tmpTableSet;
    private String ptableFREQ, ptableCONT, ptableSEP;
    
    /**
     * Creates new form DialogChangePTable
     */
    public DialogChangePTable(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    public int showDialog(TableSet tableSet){
        tmpTableSet=tableSet;
        
        try{ 
            ptableFREQ = tmpTableSet.cellkeyVar.PTableFile;
            if ( ptableFREQ.indexOf("\\",0)>0 || ptableFREQ.indexOf(":",0)>0 || ptableFREQ.indexOf(":",0)>0){}
            else { ptableFREQ = tmpTableSet.cellkeyVar.metadata.getFilePath(tmpTableSet.cellkeyVar.PTableFile);}
            textFieldPTable.setText(ptableFREQ);
        
            ptableCONT = tmpTableSet.cellkeyVar.PTableFileCont;
            if ( ptableCONT.indexOf("\\",0)>0 || ptableCONT.indexOf(":",0)>0 || ptableCONT.indexOf(":",0)>0){}
            else { ptableCONT = tmpTableSet.cellkeyVar.metadata.getFilePath(tmpTableSet.cellkeyVar.PTableFileCont);}
            textFieldPTableCont.setText(ptableCONT);
        
            ptableSEP = tmpTableSet.cellkeyVar.PTableFileSep;
            if ( ptableSEP.indexOf("\\",0)>0 || ptableSEP.indexOf(":",0)>0 || ptableSEP.indexOf(":",0)>0){}
            else { ptableSEP = tmpTableSet.cellkeyVar.metadata.getFilePath(tmpTableSet.cellkeyVar.PTableFileSep);}
            textFieldPTableSep.setText(ptableSEP);
        }
        catch (ArgusException ex){
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

        boolean IsFrequency = (tmpTableSet.respVar.type == tauargus.model.Type.FREQUENCY);
        
        labelPTable.setVisible(IsFrequency);
        textFieldPTable.setVisible(IsFrequency);
        buttonPTable.setVisible(IsFrequency);
        labelPTableCont.setVisible(!IsFrequency);
        textFieldPTableCont.setVisible(!IsFrequency);
        buttonPTableCont.setVisible(!IsFrequency);
        labelPTableSep.setVisible(!IsFrequency && tmpTableSet.respVar.CKMseparation);
        textFieldPTableSep.setVisible(!IsFrequency && tmpTableSet.respVar.CKMseparation);
        buttonPTableSep.setVisible(!IsFrequency && tmpTableSet.respVar.CKMseparation);
        pack();
        setVisible(true);
        return returnValue;
    }

    private boolean CheckFile(String FileName){
        if (FileName.trim().equals("")){
            JOptionPane.showMessageDialog(this,"Please specify ptabe file.");
            return false;
        }
        if (!TauArgusUtils.ExistFile(FileName)){
            JOptionPane.showMessageDialog(this,"Ptable file "+FileName+" does not exist.");
            return false;
        }
        return true;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filechooser = new javax.swing.JFileChooser();
        labelPTable = new javax.swing.JLabel();
        textFieldPTable = new javax.swing.JTextField();
        buttonPTable = new javax.swing.JButton();
        buttonOK = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        labelPTableCont = new javax.swing.JLabel();
        labelPTableSep = new javax.swing.JLabel();
        textFieldPTableCont = new javax.swing.JTextField();
        textFieldPTableSep = new javax.swing.JTextField();
        buttonPTableCont = new javax.swing.JButton();
        buttonPTableSep = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select ptable file(s)");
        setIconImage(null);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                DialogClosing(evt);
            }
        });

        labelPTable.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        labelPTable.setText("ptable file FREQ:");
        labelPTable.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        textFieldPTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                textFieldPTableFocusLost(evt);
            }
        });

        buttonPTable.setText("...");
        buttonPTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPTableActionPerformed(evt);
            }
        });

        buttonOK.setText("OK");
        buttonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOKActionPerformed(evt);
            }
        });

        buttonCancel.setText("Cancel");
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        labelPTableCont.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        labelPTableCont.setText("ptable file CONT:");

        labelPTableSep.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        labelPTableSep.setText("ptable file SEP:");

        textFieldPTableCont.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                textFieldPTableContFocusLost(evt);
            }
        });

        textFieldPTableSep.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                textFieldPTableSepFocusLost(evt);
            }
        });

        buttonPTableCont.setText("...");
        buttonPTableCont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPTableContActionPerformed(evt);
            }
        });

        buttonPTableSep.setText("...");
        buttonPTableSep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPTableSepActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonOK, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelPTableSep, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelPTableCont, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelPTable, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textFieldPTable, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(textFieldPTableCont, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(textFieldPTableSep, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(buttonPTable, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                            .addComponent(buttonPTableCont, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(buttonPTableSep, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelPTable)
                    .addComponent(textFieldPTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonPTable))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelPTableCont)
                    .addComponent(textFieldPTableCont, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonPTableCont))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelPTableSep)
                    .addComponent(textFieldPTableSep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonPTableSep))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonOK)
                    .addComponent(buttonCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        returnValue = CANCEL_OPTION;
        setVisible(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void buttonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOKActionPerformed
        
        boolean IsFrequency = (tmpTableSet.respVar.type == tauargus.model.Type.FREQUENCY);
        
        if ((IsFrequency && !CheckFile(textFieldPTable.getText())) ||
             ((!IsFrequency && !CheckFile(textFieldPTableCont.getText())) ||
               (!IsFrequency && tmpTableSet.respVar.CKMseparation && !CheckFile(textFieldPTableSep.getText())))) return;
        
        TauArgusUtils.putDataDirInRegistry(ptableFREQ);
        tmpTableSet.cellkeyVar.PTableFile=ptableFREQ;
        TauArgusUtils.putDataDirInRegistry(ptableCONT);
        tmpTableSet.cellkeyVar.PTableFileCont=ptableCONT;
        TauArgusUtils.putDataDirInRegistry(ptableSEP);
        tmpTableSet.cellkeyVar.PTableFileSep=ptableSEP;

        returnValue = APPROVE_OPTION;
        setVisible(false);
    }//GEN-LAST:event_buttonOKActionPerformed

    private void buttonPTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPTableActionPerformed
        TauArgusUtils.getDataDirFromRegistry(filechooser);
        filechooser.setDialogTitle("Select ptable file for frequency variable");
        filechooser.setSelectedFile(new File(""));
        filechooser.resetChoosableFileFilters();
        if (filechooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            ptableFREQ = filechooser.getSelectedFile().toString();
            textFieldPTable.setText(ptableFREQ);
        }
    }//GEN-LAST:event_buttonPTableActionPerformed

    private void DialogClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_DialogClosing
        setVisible(false);
    }//GEN-LAST:event_DialogClosing

    private void buttonPTableContActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPTableContActionPerformed
        TauArgusUtils.getDataDirFromRegistry(filechooser);
        filechooser.setDialogTitle("Select ptable file for continuous variable");
        filechooser.setSelectedFile(new File(""));
        filechooser.resetChoosableFileFilters();
        if (filechooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            ptableCONT = filechooser.getSelectedFile().toString();
            textFieldPTableCont.setText(ptableCONT);
        }
    }//GEN-LAST:event_buttonPTableContActionPerformed

    private void buttonPTableSepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPTableSepActionPerformed
        TauArgusUtils.getDataDirFromRegistry(filechooser);
        filechooser.setDialogTitle("Select ptable file for continuous variable, small values");
        filechooser.setSelectedFile(new File(""));
        filechooser.resetChoosableFileFilters();
        if (filechooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            ptableSEP = filechooser.getSelectedFile().toString();
            textFieldPTableSep.setText(ptableSEP);
        }
    }//GEN-LAST:event_buttonPTableSepActionPerformed

    private void textFieldPTableFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldPTableFocusLost
        ptableFREQ = textFieldPTable.getText();
    }//GEN-LAST:event_textFieldPTableFocusLost

    private void textFieldPTableContFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldPTableContFocusLost
        ptableCONT = textFieldPTableCont.getText();
    }//GEN-LAST:event_textFieldPTableContFocusLost

    private void textFieldPTableSepFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldPTableSepFocusLost
        ptableSEP = textFieldPTableSep.getText();
    }//GEN-LAST:event_textFieldPTableSepFocusLost

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
            java.util.logging.Logger.getLogger(DialogChangePTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DialogChangePTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DialogChangePTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DialogChangePTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DialogChangePTable dialog = new DialogChangePTable(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton buttonOK;
    private javax.swing.JButton buttonPTable;
    private javax.swing.JButton buttonPTableCont;
    private javax.swing.JButton buttonPTableSep;
    private javax.swing.JFileChooser filechooser;
    private javax.swing.JLabel labelPTable;
    private javax.swing.JLabel labelPTableCont;
    private javax.swing.JLabel labelPTableSep;
    private javax.swing.JTextField textFieldPTable;
    private javax.swing.JTextField textFieldPTableCont;
    private javax.swing.JTextField textFieldPTableSep;
    // End of variables declaration//GEN-END:variables
}
