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

import javax.swing.DefaultListSelectionModel;

/**
 * Keeps a single item selected and doesn't deselect a selected item if it is 
 * clicked and the cltr-key is pressed down.
 */
public class SingleListSelectionModel extends DefaultListSelectionModel {

    @Override
    public void removeSelectionInterval(int index0, int index1) {
        // we don't want the control deselect stuff
        if (!getValueIsAdjusting()) {
            super.removeSelectionInterval(index0, index1);
        }
    }

    @Override
    public int getSelectionMode() {
        return SINGLE_SELECTION;
    }
}
