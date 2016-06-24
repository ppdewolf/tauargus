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

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

public abstract class ArgusInputVerifier extends InputVerifier {
    @Override
    public boolean shouldYieldFocus(JComponent input) {
        return true;
    }

    public abstract String getMessage(JTextField textField);
}
