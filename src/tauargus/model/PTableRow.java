/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tauargus.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/**
 *
 * @author Peter-Paul
 * Simple class that stores a row in a p-table for the Cellkey method on Frequency count tables
 */
public class PTableRow {
    
    private HashMap<Integer,Double> RowData;
    
    public void createRow(){
        RowData = new HashMap<>();
    }
    
    public HashMap<Integer,Double> GetRowData(){
        return RowData;
    }
}
