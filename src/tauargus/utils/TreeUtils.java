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

import javax.swing.JTree;

public class TreeUtils {
    public static void expand(JTree tree, int depth) {
        for (int row = 0; row < tree.getRowCount(); row++) {
            if (tree.getPathForRow(row).getPathCount() < depth + 1) {
                tree.expandRow(row);
            }
        }    
    }

    /**
     * Collapse till a specified depth and also ensure that children of 
     * collapsed nodes are also collapsed.
     */
    public static void collapse(JTree tree, int depth) {
        for (int row = tree.getRowCount() - 1; row >= 0; row--) {
            if (tree.getPathForRow(row).getPathCount() >= depth + 1) {
                tree.collapseRow(row);
            }
        }    
    }

    /*
     * Tree is expanded so all nodes are visible till a specified depth 
     */
    public static void maxDepth(JTree tree, int depth) {
        collapse(tree, depth);
        expand(tree, depth);
    }
}
