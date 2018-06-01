/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tauargus.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import javax.swing.JOptionPane;

/**
 *
 * @author pwof
 */
public class PTable {
// private
    private int maxNi=0;
    private int minDiff=0;
    private int maxDiff=0;
    private ArrayList<Map<Integer,Double>> Data = new ArrayList<>();
    
    private void SetmaxNi(){
        this.maxNi = this.Data.size() - 1; // ptable index i runs from 0 to MaxNi
    }
    
    private void SetminDiff(){
        int diff = 2140000000;
        int i;
        
        for(i = 0; i < this.Data.size(); i++){
            for (Map.Entry<Integer,Double> pair : this.Data.get(i).entrySet()){
                diff = Math.min(diff, pair.getKey() - i);
            }
        }
        this.minDiff = diff;
    }

    private void SetmaxDiff(){
        int diff = -2140000000;
        int i;
        
        for(i = 0; i < this.Data.size(); i++){
            for (Map.Entry<Integer,Double> pair : this.Data.get(i).entrySet()){
                diff = Math.max(diff, pair.getKey() - i);
            }
        }
        this.maxDiff = diff;
    }
    
    private void Finilize(){
        SetmaxNi();
        SetminDiff();
        SetmaxDiff();
    }
    
//public
    public int getmaxNi()   {return this.maxNi;};
    public int getminDiff() {return this.minDiff;};
    public int getmaxDiff() {return this.maxDiff;};
    
    public Map<Integer,Double> GetRow(int index){
        return this.Data.get(index + 1);
    }

/**
 * Reads information on ptable from file
 * File with ptable:
 *      free format with separator ";" 
 *      names on first line
 *      each next line starts with three values i, j and pij
 * @param FileName
 * @return true if no error
 */    
    public boolean ReadFromFile(String FileName){
        String line;
        int i, j, i0;
        double bound;
        Map<Integer,Double> row;
        
        try{
            BufferedReader ptable_in = new BufferedReader(new FileReader(FileName));
            line = ptable_in.readLine(); // Disregard first line: contains only names
            line = ptable_in.readLine();
            if (line==null) {
                JOptionPane.showMessageDialog(null,"Error: ptable in file is empty");
                return false;
            }

            row = new TreeMap<>(); // Using TreeMap to keep the Map sorted over its Keys
            i0 = Integer.parseInt(line.substring(0,line.indexOf(";")));
            line = line.substring(line.indexOf(";")+1);
            j = Integer.parseInt(line.substring(0,line.indexOf(";")));
            line = line.substring(line.indexOf(";")+1);
            row.put(j,Double.parseDouble(line.substring(0,line.indexOf(";"))));

            while ((line = ptable_in.readLine()) != null)
            {
                i = Integer.parseInt(line.substring(0,line.indexOf(";")));
                line = line.substring(line.indexOf(";")+1);
                
                if (!Objects.equals(i, i0)){
                    // Replace pij with cumulative pij
                    bound = 0;
                    for (Map.Entry<Integer,Double> pair : row.entrySet()){
                        bound += pair.getValue();
                        row.put(pair.getKey(), bound);
                    }
                    this.Data.add(row);
                    row = new TreeMap<>();
                    i0 = i;
                }
                j = Integer.parseInt(line.substring(0,line.indexOf(";")));
                line = line.substring(line.indexOf(";")+1);
                row.put(j,Double.parseDouble(line.substring(0,line.indexOf(";"))));
            }
            
            // Replace pij with cumulative pij
            bound = 0;
            for (Map.Entry<Integer,Double> pair : row.entrySet()){
                bound += pair.getValue();
                row.put(pair.getKey(), bound);
            }
            
            this.Data.add(row);
            this.Finilize();
            return true;
        }
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(null,ex);
            return false;
        }
    }
}
