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

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.*;

public class TableColumnResizer {

    public static void adjustColumnPreferredWidths(JTable table, boolean rowHeaders) {
        // strategy - get max width for cells in column and make that the preferred width
        TableColumnModel columnModel = table.getColumnModel();
        for (int col = 0; col < table.getColumnCount(); col++) {

            TableColumn tableColumn = table.getColumnModel().getColumn(col);
            Object value = tableColumn.getHeaderValue();
            TableCellRenderer renderer = tableColumn.getHeaderRenderer();
            if (renderer == null) {
                renderer = table.getTableHeader().getDefaultRenderer();
            }
            Component c = renderer.getTableCellRendererComponent(table, value, false, false, -1, col);
            int maxWidth = c.getPreferredSize().width;

            int count = (rowHeaders && col != 0) ? 1 : table.getRowCount();
            for (int row = 0; row < count; row++) {
                value = table.getValueAt(row, col);
                TableCellRenderer rend = table.getCellRenderer(row, col);
                Component comp = rend.getTableCellRendererComponent(table,
                        value, false, false, row, col);
                maxWidth = Math.max(comp.getPreferredSize().width, maxWidth);
            }

            TableColumn column = columnModel.getColumn(col);
            column.setPreferredWidth(maxWidth + 6);
        }
    }
}
