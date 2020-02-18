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

import java.util.ArrayList;
import java.util.List;
import tauargus.model.APriori.Mapping;

/*!
    \brief A simple class for one cell in the table
    The class corresponds to one of the mapping as visually defined in DialogApriori.
    It is used by APriori for reading/writing the apriori file and for applying the apriori file
    \author whcg
 */
public class APrioriCell
{
    public List<String> Key = new ArrayList<String>();  //< The key of the cell 
    public CellStatus Status = CellStatus.UNKNOWN;      //< The status as derived by TauArgus
    private APriori parent = null;                      //< The parent APriori class
        
    
    public static String TotalString = "\"\"";          //< when writing: use this
    
    //! when reading: also accept "total" (case insensitive)
    public static boolean IsTotal(String s )
    {
        return  s.toLowerCase().trim().equals("total")      || 
                s.toLowerCase().trim().equals("\"total\"")  || 
                s.trim().isEmpty();
    }
    
    
    
    /*!
        The number of columns of a cell
    */
    public int GetColumnCount()
    {
       int columnCount = Key.size();
        if( parent.SelectedColumnsCount < columnCount)
            columnCount = parent.SelectedColumnsCount;

        return columnCount;
    }
    
    
    
    
    /*!
        When generating an apriori (AP) file from a table T , the user can specify 
        which columns must appear in the apriori file.
        If a T column is not present in AP, VariableMappedAtOutputColumn will contain a 0 for that column. 
    
        A cell is included if *all* key column
    
        - are mapped
        - are not mapped, but are a total
    
        Suppose the save table has the following columns
    
        | A | B  |
        | ----: | :----:  |
        | 10    | 10        |
        | 1000  | 1000    |

        And suppose you want to select just the first column:
    
        \image html d:\argus\doc\images\screenshot_columnselection.png
    
        Than `VariableMappedAtOutputColumn` (1-based!) becomes:
    
        | Column | MappedTo  |
        | ----: | :----:  |
        | 1    | 1        |
        | 2  | 0    |

    */
    public Boolean MustBeIncluded()
    {
        for(int col=0; col<Key.size(); col++)  // de INput columns 
        {
            boolean IsMapped = (parent.VariableMappedAtOutputColumn(col+1)>=0);
            boolean IsTotalCol = IsTotal(Key.get(col));

            if( !(IsMapped || !IsMapped && IsTotalCol) )
                return false;
            
            if( Omitted())
                return false;
        }
        
        return true;
    }

    

    private Boolean Omitted()
    {
        Mapping m = parent.Mappings.get(Status.getValue()-1);
        return( m.newstat == APriori.ChangeStatus.Omit &&  !m.useCost);
        
    }
    
    
    
    /*!
        Generate one (comma-separated) line of the apriory file:
    
        1. the key columns values
        2. the status of that cell
        3. the action on that cell (..)
        4. (optionally) the new cost value for that cell.
    
    */
    public String Output(String sep, int outDim)
    {
        if( !MustBeIncluded() && !Omitted())
            return "";
        
        String Result = "";
        
        int ColumnCount = GetColumnCount();
        ColumnCount = outDim;
                
        List<String> newkey = new ArrayList<String>(ColumnCount);

        for(int i=0; i<ColumnCount; i++)
              newkey.add("");
        
                
        for(int i=0; i<ColumnCount; i++)  // i = index of the "new" keys
        {
            int index = parent.ColumnMap.get(i+1); // return 1-based OLD column position ( 0 = Other)
            if( index >0 ) // i.e. a chosen (and mapped) column
            {
                String val = Key.get(index-1);
                if( IsTotal(val))
                    val = TotalString;
                
                newkey.set(i, val);              // subtract 1 to get 0-based position
            }
            else
                newkey.set(i, TotalString);
        }

        String KeyStr = "";
        for(String s : newkey)
            KeyStr+=s+sep;
        KeyStr = KeyStr.substring(0,KeyStr.length()-1);
        
        Mapping m = parent.Mappings.get(Status.getValue()-1);

        if(MustBeIncluded()){
          if (m.newstat != APriori.ChangeStatus.Omit) {             
            Result= KeyStr + sep + m.newstat.getSymbol();
        }
        
          if( m.useCost)// cost must be written also id omitted
            { if( !Result.equals(""))
                Result+=System.lineSeparator();//"\n";
              Result+=KeyStr + sep + "c" + sep + m.costValue.toString();
            }
        }
        
        return Result;
    }
    
    
    
    @Override 
    public String toString()
    {
        return Output(",", 1);
    }
    
    
    
    private String RemoveQuotes(String s)
    {
        s = s.trim();
        if( s.charAt(0) == '\"' && s.charAt(s.length()-1)=='\"')
            s = s.substring(1, s.length()-1);
            
        return s;
    }
    
    /*!
      assumption: 
        - all cell may be indices *may* be quoted
        - then follows the cell value
        - possibly followed by status values
    */
    public APrioriCell(String line, String sep, int FirstNumericField, APriori p) throws ArgusException
    {
        String[] Parts = line.split(sep);
        
        if( Parts.length==1 )//i.e. wrong separator
            throw new ArgusException("Invalid separator while reading reading safe file: '"+sep+"'");
                    
        for(int col=0; col<FirstNumericField; col++)
        {
            boolean hasQuotes = Parts[col].indexOf('"')>=0;
            String pp = RemoveQuotes(Parts[col]);
            
            Key.add(pp);
        }

        String cellValue = Parts[Key.size()];
        
        if( Parts.length - Key.size() ==2) // i.e. status is stored as well as the last value
            Status = CellStatus.findByValue(Integer.parseInt(Parts[Parts.length-1]));
        
        if( Status == CellStatus.UNKNOWN)
        {
            int x = 2;
        }
        
        parent = p;
    }
}
