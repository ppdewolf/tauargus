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
package tauargus.utils;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


/**
 *
 * @author Peter-Paul
 */
public class ScaleCellEditor extends DefaultCellEditor{
        
    public ScaleCellEditor(JTextField comp) {
        super(comp);
    }
    
    @Override
    public boolean stopCellEditing(){
        try{
            String editingValue = (String) getCellEditorValue();
            if (!editingValue.isEmpty()){
                double result = Double.parseDouble(editingValue);
            }
        }
        catch(ClassCastException ex){
            return false;
        }
        catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(null,"Scaling parameter should be numeric","Scaling Parameter warning",JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return super.stopCellEditing();
    }
}