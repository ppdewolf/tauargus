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

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JList;

public abstract class MouseJListDoubleClickedListener<E> extends MouseAdapter {

    protected JList<E> list;

    public MouseJListDoubleClickedListener(JList<E> l) {
        list = l;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            Rectangle r = list.getCellBounds(0, list.getLastVisibleIndex());
            if (r != null && r.contains(e.getPoint())) {
                int index = list.locationToIndex(e.getPoint());
                itemDoubleClicked(list, index);
            }
        }
    }
    
    public abstract void itemDoubleClicked(JList<E> list, int itemIndex);
}
