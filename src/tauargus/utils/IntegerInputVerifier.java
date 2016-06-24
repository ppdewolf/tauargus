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

import javax.swing.JComponent;
import javax.swing.JTextField;

public class IntegerInputVerifier extends ArgusInputVerifier {

    @Override
    public boolean verify(JComponent comp) {
        JTextField textField = (JTextField) comp;
        try {
            Integer.parseInt(textField.getText());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public String getMessage(JTextField textField) {
        String labelText = SwingUtils.getLabelText(textField);
        String prefix = labelText != null ? labelText : "Value";
        return prefix + " should be integer.";
    }
}
