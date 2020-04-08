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

package tauargus.model;

import java.awt.Color;

public enum CellStatus {
 /* enum value               val category                               description                    foreground color */
    UNKNOWN                 ( 0, CellStatusCategory.UNKNOWN,            "",                            Color.yellow),
    SAFE                    ( 1, CellStatusCategory.SAFE_NOT_PROTECTED, "Safe",                        Color.black),
    SAFE_MANUAL             ( 2, CellStatusCategory.SAFE_NOT_PROTECTED, "Safe (manual)",               Color.gray),
    UNSAFE_RULE             ( 3, CellStatusCategory.PRIMARY_UNSAFE,     "Unsafe",                      Color.red),
    UNSAFE_PEEP             ( 4, CellStatusCategory.PRIMARY_UNSAFE,     "Unsafe (request)",            Color.red),
    UNSAFE_FREQ             ( 5, CellStatusCategory.PRIMARY_UNSAFE,     "Unsafe (freq)",               Color.red),
    UNSAFE_ZERO             ( 6, CellStatusCategory.PRIMARY_UNSAFE,     "Unsafe (zero cell)",          Color.red),
    UNSAFE_SINGLETON        ( 7, CellStatusCategory.PRIMARY_UNSAFE,     "Unsafe (singleton)",          Color.red),
    UNSAFE_SINGLETON_MANUAL ( 8, CellStatusCategory.PRIMARY_UNSAFE,     "Unsafe (singleton) (manual)", Color.pink),
    UNSAFE_MANUAL           ( 9, CellStatusCategory.PRIMARY_UNSAFE,     "Unsafe (manual)",             Color.red), // light red
    PROTECT_MANUAL          (10, CellStatusCategory.SAFE_PROTECTED,     "Protected",                   new Color(0,153,0)),//Color.green.darker()),
    SECONDARY_UNSAFE        (11, CellStatusCategory.SECONDARY_UNSAFE,   "Secondary",                   Color.blue),
    SECONDARY_UNSAFE_MANUAL (12, CellStatusCategory.SECONDARY_UNSAFE,   "Secondary (from manual)",     new Color(51,153,255)),//Color.cyan), // "Secondary (CTA)", light blue
    EMPTY_NONSTRUCT         (13, CellStatusCategory.SAFE_NOT_PROTECTED, "Empty (non-struct.)",         Color.gray),
    EMPTY                   (14, CellStatusCategory.SAFE_PROTECTED,     "Empty",                       Color.black);

    private final int value;
    private final CellStatusCategory cellStatusCategory;
    private final String description;
    private final Color foreGroundColor;
    
    private static final int size;
    private static final CellStatus[] cellStatusMap;
    static {
        CellStatus[] cellStatuses = values();
        int maxValue = -1;
        for (CellStatus cellStatus : cellStatuses) {
            if (cellStatus.value > maxValue) {
                maxValue = cellStatus.value;
            }
        }
        size = maxValue + 1;
        cellStatusMap = new CellStatus[size];
        for (CellStatus cellStatus : cellStatuses) {
            cellStatusMap[cellStatus.value] = cellStatus;
        }
    }

    CellStatus(int value, CellStatusCategory category, String description, Color foreGroundColor) {
        this.value = value;
        this.cellStatusCategory = category;
        this.description = description;
        this.foreGroundColor = foreGroundColor;
    }   
    
    public int getValue() {
        return value;
    }

    public CellStatusCategory getCategory() {
        return cellStatusCategory;
    }

    public String getDescription() {
        return description;
    }

    public Color getForegroundColor() {
        return foreGroundColor;
    }
    
    public boolean isEmpty() {
        return this == EMPTY || this == EMPTY_NONSTRUCT;
    }
    
    /*
     * Denotes if a cell's status is safe and may not be changed by secondary 
     * suppression 
     */
    public boolean isSafeProtected() {
        return cellStatusCategory == CellStatusCategory.SAFE_PROTECTED;
    }

    /*
     * Denotes if a cell is safe and may be used for secondary suppression 
     */
    public boolean isSafeNotProtected() {
        return cellStatusCategory == CellStatusCategory.SAFE_NOT_PROTECTED;
    }

    /*
     * Denotes if a cell is suppressed by safety rules
     */
    public boolean isPrimaryUnsafe() {
        return cellStatusCategory == CellStatusCategory.PRIMARY_UNSAFE;
    }

    /*
     * Denotes if a cell is suppressed by a suppression method, so primary 
     * unsafe cells can't be recalculated
     */
    public boolean isSecundaryUnsafe() {
        return cellStatusCategory == CellStatusCategory.SECONDARY_UNSAFE;
    }
    
    public boolean isSafe() {
        return cellStatusCategory == CellStatusCategory.SAFE_PROTECTED || cellStatusCategory == CellStatusCategory.SAFE_NOT_PROTECTED;
    }
    
    public boolean isUnsafe() {
        return cellStatusCategory == CellStatusCategory.PRIMARY_UNSAFE || cellStatusCategory == CellStatusCategory.SECONDARY_UNSAFE;
    }
    
    public static CellStatus findByValue(int value) {
         return cellStatusMap[value];
    }

    public static int size() {
        return size;
    }
} 
