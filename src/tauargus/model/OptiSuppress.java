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

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import tauargus.extern.dataengine.TauArgus;
import tauargus.extern.tauhitas.HiTaSCtrl;
//import tauargus.extern.rounder.Rounder;
import tauargus.extern.taurounder.taurounder;
import java.io.FileNotFoundException;
import tauargus.utils.TauArgusUtils;
import tauargus.utils.Tokenizer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
//import tauargus.extern.rounder.ActivityListener;
import tauargus.extern.taurounder.RProgressListener;
import tauargus.extern.taurounder.RCallback;
import tauargus.service.TableService;
import tauargus.utils.ExecUtils;
import argus.utils.SystemUtils;
import java.beans.PropertyChangeEvent;


  
/**
 *
 * @author ahnl
 * Class to run modular/Hitas , optimal (FullJJ),Network, CTA and the rounder
 * They have a lot in common.
 * 
 */
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import tauargus.extern.tauhitas.ICallback;
import tauargus.extern.tauhitas.IProgressListener;
import tauargus.extern.taurounder.RounderCtrl;
import tauargus.gui.DialogStopTime;
import tauargus.gui.PanelTable;
import tauargus.utils.StrUtils;
import static tauargus.utils.TauArgusUtils.ShowWarningMessage;

public class OptiSuppress {
//TODO    Keuze moet noguit het options scherm/registry kommen

    
    public static double JJZero, JJZero1, JJZero2, JJFeasTol, JJOptTol, JJMinViola, JJMaxSlack,CTATolerance;
    public static int JJInfinity, JJMaxColslp, JJMaxRowslp, JJMaxCutsPool, JJMaxCutsIter;
             
    private static String solverName[]={"NO","XPRESS", "CPLEX", "SCIP" }; 
    private static String dots = "....................";
    private static final TauArgus tauArgus = Application.getTauArgusDll();    
    private static final HiTaSCtrl tauHitas = Application.getTauHitasDll();
    private static final RounderCtrl rounder = Application.getRounder();
    
    private static int UB, LB, TimeSoFar, nSuppressed;
    private static double DUB, DLB, Diff;
    private static BufferedWriter out;
    private static final Logger logger = Logger.getLogger(OptiSuppress.class.getName());
    
    static DialogStopTime dialog = new DialogStopTime(null,true);
    static DialogStopTime rdialog = new DialogStopTime(null,true);
    
    static ICallback jCallback = new ICallback(){
            @Override
            public int SetStopTime(){
                dialog.SetUB(UB);
                dialog.SetLB(LB);
                dialog.SetDiff(Diff);
                dialog.SetTimeSoFar(TimeSoFar);
                dialog.SetNSuppressed(nSuppressed);
                dialog.setVisible(true);
                return dialog.AddTime;
            }
    };
    
    static RCallback jRCallback = new RCallback(){
            @Override
            public int SetExtraTime(){
                rdialog.HideForRounder();
                rdialog.SetUB(DUB);
                rdialog.SetLB(DLB);
                rdialog.SetDiff(100.0*(DUB-DLB)/DLB);
                rdialog.setVisible(true);
                return rdialog.AddTime;
            }
    };
        
    public static void loadJJParamFromRegistry(){
      String hs;
      //Initialize the JJ-parameters first, then get them from the registry.
      //If getting them from the registry fials, they still have the default value.  
      JJZero = 0.0000001;
      JJZero1 = 0.0000001;
      JJZero2 = 0.0000000001;
      JJInfinity = 2140000000;
      JJFeasTol = 0.000001;
      JJOptTol = 0.000000001;
      JJMaxSlack = 0.01;
      
      JJMaxColslp = 50000;
      JJMaxRowslp = 15000;
      JJMaxCutsPool = 500000;
      JJMaxCutsIter = 50;
      CTATolerance  = 0.00001;
     
      try{
      hs = SystemUtils.getRegString("solveroptions", "zero", "0.0000001" );     JJZero = StrUtils.toDouble(hs);
      hs = SystemUtils.getRegString("solveroptions", "zero1", "0.0000001" );    JJZero1 = StrUtils.toDouble(hs);
      hs = SystemUtils.getRegString("solveroptions", "zero2", "0.0000000001" ); JJZero2 = StrUtils.toDouble(hs);
      hs = SystemUtils.getRegString("solveroptions", "infinity", "2140000000" ); JJInfinity = StrUtils.toInteger(hs);
      hs = SystemUtils.getRegString("solveroptions", "feastol", "0.000001" );  JJFeasTol = StrUtils.toDouble(hs);
      hs = SystemUtils.getRegString("solveroptions", "opttol", "0.000000001" );   JJOptTol = StrUtils.toDouble(hs);
      hs = SystemUtils.getRegString("solveroptions", "minviola", "0.0001" ); JJMinViola = StrUtils.toDouble(hs);
      hs = SystemUtils.getRegString("solveroptions", "maxslack", "0.01" ); JJMaxSlack = StrUtils.toDouble(hs);
     
      hs = SystemUtils.getRegString("solveroptions", "maxcolslp", "50000" ); JJMaxColslp = StrUtils.toInteger(hs);
      hs = SystemUtils.getRegString("solveroptions", "maxrowslp", "15000"); JJMaxRowslp = StrUtils.toInteger(hs);
      hs = SystemUtils.getRegString("solveroptions", "maxcutspool", "500000" ); JJMaxCutsPool = StrUtils.toInteger(hs);
      hs = SystemUtils.getRegString("solveroptions", "maxcutsiter", "50" ); JJMaxCutsIter = StrUtils.toInteger(hs);
      
      hs = SystemUtils.getRegString("solveroptions", "ctatolerance", "0.00001" ); CTATolerance = StrUtils.toDouble(hs);
              
      } catch (ArgusException ex){ }             
    }
    
    private static void setJJParamIntauHitas(){
      tauHitas.SetJJconstantsDbl(101, JJZero);
      tauHitas.SetJJconstantsDbl(102, JJZero1);
      tauHitas.SetJJconstantsDbl(103, JJZero2);
      tauHitas.SetJJconstantsDbl(104, JJInfinity);
      tauHitas.SetJJconstantsDbl(111, JJFeasTol);
      tauHitas.SetJJconstantsDbl(112, JJOptTol);
      tauHitas.SetJJconstantsDbl(109, JJMinViola);
      tauHitas.SetJJconstantsDbl(110, JJMaxSlack);
      tauHitas.SetJJconstantsInt(105, JJMaxColslp);
      tauHitas.SetJJconstantsInt(106, JJMaxRowslp);
      tauHitas.SetJJconstantsInt(107, JJMaxCutsPool);
      tauHitas.SetJJconstantsInt(108, JJMaxCutsIter);
      
//#define JJZERO          101
//#define JJZERO1         102
//#define JJZERO2         103
//#define JJINF           104
//#define JJMAXCOLSLP     105
//#define JJMAXROWSLP     106
//#define JJMAXCUTSPOOL   107
//#define JJMAXCUTSITER   108
//#define JJMINVIOLA      109
//#define JJMAXSLACK      110
//#define JJFEASTOL       111
//#define JJOPTTOL        112        
        
    }
/**
 * The network solution is only possible for 2-dim tables and 
 * only the first RespVar can be hierarchical 
 * The version for 2 non-hierarchical variables has not been included.
 * @param tableSet
 * @throws ArgusException 
 */    
    public static void TestNetwork(TableSet tableSet)throws ArgusException{
        if (tableSet.expVar.size() !=2){
          throw new  ArgusException("The network is only possible for a 2 dim table");
        }
        if (tableSet.expVar.get(0).hierarchical == Variable.HIER_NONE  ){
          throw new  ArgusException("The network needs a hierarchy in the first dimension");
        }
        if (tableSet.expVar.get(1).hierarchical != Variable.HIER_NONE  ){
          throw new  ArgusException("The network does not allow a hierarchy in the second dimension");
        }
        return;
    }
 /**
  * Applies the network solution (by Jordi) 
  * Writes the table in a special format (AMPL);
  * runs the program (main1H2D)
  * loads the secondaries
  * @param tableSet
  * @throws ArgusException 
  */   
    public static void RunNetwork(TableSet tableSet)throws ArgusException{
        double ms = 0; int[] errorCode = new int[1]; String hs = ""; int result;
        ArrayList<String> commandline = new ArrayList<>();
        Date startDate = new Date();  
        String temp = Application.getTempDir();
        SystemUtils.writeLogbook("Start of Network protecton procedure");   
        if (!tauArgus.WriteHierarchicalTableInAMPLFormat(Application.getTempFile("NetInH.tmp"), temp, tableSet.index, ms, errorCode)){
            throw new ArgusException("Error preparing intermediate file\n" + 
                                     "for the hierarchical network solution (" + errorCode[0] + ")");
        };
       try {
         hs = SystemUtils.getApplicationDirectory(OptiSuppress.class).getCanonicalPath();
         } 
       catch (Exception ex) {}
//       hs = "\"" + hs + "\\main1H2D.exe\" \"" + Application.getTempFile("NetInH.tmp") + "\" "+ 
//             "\"" + temp + "\" ";
       hs = "\"" + hs + "/main1H2D.exe\"";
       commandline.add(StrUtils.unQuote(hs));
       commandline.add(Application.getTempFile("NetInH.tmp"));
       commandline.add(temp);
       
       if (tableSet.networkSolverType == 2) {
           //hs = hs + " -s  d ";
           commandline.add("-s");
           commandline.add("d");
       }
       if (tableSet.networkPrimariesOrder == 2) { //hs = hs + " -m  a ";}
           commandline.add("-m");
           commandline.add("a");
       }
       if (tableSet.networkPrimariesOrder == 3) {//hs = hs + " -m  d ";}
           commandline.add("-m");
           commandline.add("d");      
       }
       TauArgusUtils.writeBatchFileForExec("RunNetwork", commandline);
//       result = ExecUtils.execCommand(hs, temp, false, "Run Network");
       result = ExecUtils.execCommand(commandline, Application.getTempDir(),false,"Run Network program");
       if (result != 0) { //something wrong
         if ((result >= -6) && (result < 0 )){
             throw new ArgusException(tauArgus.GetErrorString(4500 - result));
         }
         throw new ArgusException("Error occurred running the 1H2D Network solution (code = " + result + ")");    
       }    
       if (!tauArgus.SetSecondaryFromHierarchicalAMPL( Application.getTempFile("sec_1H2D.out"), tableSet.index, errorCode)){
        throw new ArgusException("Error occurred while reading the results of network; code (" + errorCode[0] + ")");     
       }
       tableSet.suppressed =  TableSet.SUP_NETWORK;  
       if (tableSet.networkSolverType == 1){tableSet.suppressINFO = "PPRN solver used<br>";}
       if (tableSet.networkSolverType == 2){tableSet.suppressINFO = "Dijkstra solver used<br>";}
       if (tableSet.networkPrimariesOrder == 1){tableSet.suppressINFO += "Normal order";}
       if (tableSet.networkPrimariesOrder == 2){tableSet.suppressINFO += "Ascending order";}
       if (tableSet.networkPrimariesOrder == 3){tableSet.suppressINFO += "Descending order";}
       tableSet.nSecond = tableSet.CountSecondaries(); 
       Date endDate = new Date();
       long diff = endDate.getTime()-startDate.getTime();
       tableSet.processingTime = (int) diff / 1000;
       SystemUtils.writeLogbook("End of Network protection procedure; number of secondaries: "+tableSet.nSecond);   
    }
    
  /**
   * runs the audit procedure; an external Delphi/Lazarus program called intervalle
   * Originates from Ilmenau, extended by Anco and Destatis
   * First a parameter file is written 
   * @param tableSet
   * @throws ArgusException 
   */
    public static void RunAudit (TableSet tableSet) throws ArgusException{
        //Run the intervalle program
        ArrayList<String> commandline = new ArrayList<>();
        int tabNo = tableSet.index; String hs; int result, i;
        Date startDate = new Date();  
        int cn; double lpl,upl,cVal; boolean oke;
        tableSet.undoAudit();
        SystemUtils.writeLogbook("Start of Audit procedure");       
        SaveTable.writeJJ(tableSet, Application.getTempFile("Anneke.JJ"), false, false, 0, false, false);
        //Write Rounding parameter file
        try {
        BufferedWriter out = new BufferedWriter(new FileWriter(Application.getTempFile("Anneke.txt")));
        out.write(Application.getTempFile("Anneke.JJ")); out.newLine();
        out.write ("C:/Lokaler_Datenbereich/xpress/bin/mosel\""); out.newLine();
        out.write (Application.getTempFile("Anneke.Out")); out.newLine();
        out.write (Application.getTempFile("Anneke.txt")); out.newLine();
        out.write ("primsec"); out.newLine();
        out.write ("without"); out.newLine();
        out.write ("2"); out.newLine();
        out.write ("1"); out.newLine();
        out.write ("0"); out.newLine();
        out.write ("0"); out.newLine();
        out.write ("50"); out.newLine();
        out.write ("50"); out.newLine();
        out.write ("0"); out.newLine();
        out.write ("5"); out.newLine();
        if  (Application.solverSelected == Application.SOLVER_NO) {out.write ("GLPK");}
        if  (Application.solverSelected == Application.SOLVER_CPLEX) {out.write ("cplex");}
        if  (Application.solverSelected == Application.SOLVER_XPRESS) {out.write ("xpress");}
        if  (Application.solverSelected == Application.SOLVER_SOPLEX) {out.write ("GLPK");}
        out.newLine();
        
        if  (Application.solverSelected == Application.SOLVER_CPLEX) {
            out.write (TauArgusUtils.GetCplexLicenceFile());}
        else{out.write ("zomaariets");}
        out.newLine();
//        out.write ("C:\\Lokaler Datenbereich\\xpress\\bin\\mosel\""); out.newLine();
        out.close();
        }        
        catch (IOException ex){
          throw new ArgusException("A problem was encountered when writing the parameter file for audit");} 
        
        TauArgusUtils.DeleteFile(Application.getTempFile("Anneke.Out"));

        hs = "";
        try{
          hs = SystemUtils.getApplicationDirectory(OptiSuppress.class).getCanonicalPath();
          hs = hs + "/intervalle.exe";
          if (!TauArgusUtils.ExistFile(hs)){ throw new ArgusException("The audit program could not be found");}
          commandline.add(hs);
          commandline.add(Application.getTempFile("Anneke.txt"));
          commandline.add(Application.getTempFile("Audit.log"));
          TauArgusUtils.writeBatchFileForExec("RunAudit", commandline);
//          hs = StrUtils.quote(hs) + " " + StrUtils.quote(Application.getTempFile("Anneke.txt")) + " "+ 
//                                          StrUtils.quote(Application.getTempFile("audit.log"));
//          BufferedWriter out = new BufferedWriter(new FileWriter(Application.getTempFile("runAudit.bat")));
//          out.write(hs); out.newLine();
//          out.write ("pause"); out.newLine();
//          out.close();
          } 
        catch (IOException ex){}
        //Run the intervalle program
          result = ExecUtils.execCommand(commandline, Application.getTempDir(),false, "Run Audit program");
//        result = ExecUtils.execCommand(hs, Application.getTempDir(),false, "Run Audit program");
        
        if (result !=0){ throw new ArgusException("The audit program was not completed successfully");}
        if (!TauArgusUtils.ExistFile(Application.getTempFile("Anneke.Out"))){
           throw new ArgusException("The audit program was not completed successfully\nNo output file found");} 
        
         try{
          BufferedReader in = new BufferedReader(new FileReader(Application.getTempFile("Anneke.Out")));
          Tokenizer tokenizer = new Tokenizer(in);
          BufferedWriter out = new BufferedWriter(new FileWriter(Application.getTempFile("audit_"+tabNo+ ".html")));
          //KopHTML
          int lineNumber = 0;
          for (i=0;i<4;i++){hs = tokenizer.nextLine();}  //first 4 lines are skipped
          SaveTable.writeKopHtml(tableSet, out, false);
          out.write("<!--XXXXXXXXXXXXXX-->"); out.newLine();
          out.write("<h2>Overview of not-properly protected cells</h2>"); out.newLine();
            while ((tokenizer.nextLine()) != null) {
                hs = tokenizer.nextField(";");
                cn = Integer.parseInt(hs);
//              cn = Integer.parseInt(tokenizer.nextField(";"));
              lpl = Double.parseDouble(tokenizer.nextField(";"));
              upl = Double.parseDouble(tokenizer.nextField(";"));
              cVal = Double.parseDouble(tokenizer.nextField(";"));
              oke = tauArgus.SetRealizedLowerAndUpper(tabNo, cn, upl, lpl);
              if (!oke){throw new ArgusException("An error has occurred when reading the audit results for cell: "+cn);}
               if (tokenizer.getLine().equals("u;1")){
               lineNumber++;
               if(lineNumber==1){
                 out.write("<table>"); out.newLine();
                 out.write("  <tr>"); out.newLine();
                 for (i=0;i<tableSet.expVar.size();i++) {
                   out.write("    <td>"+tableSet.expVar.get(i).name  +"</td>"); out.newLine();
                 }
                 out.write("    <td>Lower Prot.</td>");out.newLine();
                 out.write("    <td>Cell value</td>");out.newLine();
                 out.write("    <td>Upper Prot.</td> ");out.newLine();
                 out.write("    <td>Exact</td>"); out.newLine();
                 out.write("  </tr>"); out.newLine();
               }
               hs = TableService.IndexToCodesString(tableSet, cn);
               hs = hs.replace(",", "</td><td align=\"Right\">");
               hs = "  <tr><td align=\"Right\">" + hs;
               hs = hs + "<td align=\"Right\">"+lpl + 
                         "</td><td align=\"Right\">" + cVal + 
                         "</td><td align=\"Right\">" + upl + "</td><td>";
               if ((cVal == lpl) && (cVal == upl)){ hs = hs + "YES"; tableSet.auditExactDisclosure++;}   
                                             else {hs = hs +"&nbsp;";tableSet.auditPartialDisclosure++;}
               hs = hs + "</td></tr>";
               out.write(hs); out.newLine();
              }
            }
            if(lineNumber>0){out.write("</table>"); out.newLine();}
            out.write("<!--XXXXXXXXXXXXXX-->"); out.newLine();
            SaveTable.writeStaartHTML(out);
            out.close();
            tableSet.hasBeenAudited = true;   
            SystemUtils.writeLogbook("End of Audit procedure\n" + 
                                     tableSet.auditExactDisclosure + " cells could be exactly disclosed\n"+
                                     tableSet.auditPartialDisclosure + " cells could be partially disclosed");              
            Date endDate = new Date();
            long diff = endDate.getTime()-startDate.getTime();
            diff = diff / 1000;
            //tableSet.processingTime = (int) diff;
            tokenizer.close(); 
         }
         catch(IOException ex){throw new ArgusException ("An error has occurred when retrieving the audit results");}
       
    }
    
    public static void RunCTA (TableSet tableSet, boolean doExpert)throws ArgusException, FileNotFoundException, IOException{
        String command = "", tolStr, solFile = "", solver = "", hs = ""; double eps;
        ArrayList<String> commandline = new ArrayList<>();
        boolean oke; int result, tabNo, i;
        int cn; double orgVal, ctaVal;
        int[] nSec; nSec = new int[1];
        String[] solverNames = {"cplex", "xpress", "glpk", "cbc", "sym"};
        tabNo = tableSet.index;
        Date startDate = new Date();  
        SystemUtils.writeLogbook("Start of CTA run");
        SaveTable.writeJJ(tableSet, Application.getTempFile("CTA.IN"), false, false, 0, false, false);
        loadJJParamFromRegistry();
        
        try {
            command = SystemUtils.getApplicationDirectory(OptiSuppress.class).getCanonicalPath();
        } catch (Exception ex) {}

        if (doExpert){ // clean possible output files
          for(i=0;i<solverNames.length;i++){TauArgusUtils.DeleteFile(Application.getTempFile("CTA_"+solverNames[i]+".sol"));}
//            TauArgusUtils.DeleteFileWild ("CTA*.sol",Application.getTempDir());
//            TauArgusUtils.DeleteFileWild ("CTA*.log",Application.getTempDir());
        }
        if (SystemUtils.isWindows()) 
        {
        if (doExpert){command = "\"" + command + "/GUICTA.exe\"";}
        else         {command = "\"" + command + "/mainCTA.exe\"";}
        }
        else // For the moment this is assumed to be Linux
        {
        if (doExpert){command = "\"" + command + "/GUICTA\"";}
        else         {command = "\"" + command + "/main_CTA\"";}
        }   
        
        if (!TauArgusUtils.ExistFile(StrUtils.unQuote(command))){ throw new ArgusException("CTA-program could not be found");}
        commandline.add(StrUtils.unQuote(command));
        command = command + " "+ StrUtils.quote(Application.getTempFile("CTA.IN"));
        commandline.add(Application.getTempFile("CTA.IN"));
        command = command + " \""+Application.getTempDir()+ "\"";
        commandline.add(Application.getTempDir());
        eps = CTATolerance;
        if (!doExpert){
          tolStr = Double.toString(CTATolerance); //   "1.0E-5";
          command = command + " -e " + tolStr;
          commandline.add("-e");
          commandline.add(tolStr);
          
          commandline.add("-s");
          switch (Application.solverSelected){
              case Application.SOLVER_CPLEX:  
                  commandline.add("c");
                  command = command + " -s c"; 
                  solver = "cplex";  break;
              case Application.SOLVER_XPRESS: 
                  commandline.add("x");
                  commandline.add("-M");
                  commandline.add("d");
                  command = command + " -s x -M d"; solver = "xpress"; break; // OEM Xpress has no barrier
              case Application.SOLVER_SOPLEX: 
                  commandline.add("b");
                  command = command + " -s b"; solver = "cbc";   break;//CBC gekozen ipv Soplex
              case Application.SOLVER_NO: throw new ArgusException("No solver has been selected so CTA cannot be applied");            
          }
        }
        BufferedWriter out = new BufferedWriter(new FileWriter(Application.getTempFile("CTA.bat")));
        out.write(command); out.newLine();
        out.write ("pause"); out.newLine();
        out.close();
        
        solFile = Application.getTempDir() + "/CTA_" + solver + ".sol";        
        oke = TauArgusUtils.DeleteFile(solFile);
        
        // In expert mode, no additional progress window needed, i.e. silent = true = doExpert
        result = ExecUtils.execCommand(commandline, Application.getTempDir(),doExpert,"Run CTA program");
//Return values of CTA
//#define CTA_OK 0 // table successfully created, but CTA not yet solved
//#define CTA_OPTIMAL_SOLUTION 1 // optimal solution (within tolerance) found
//#define CTA_TIME_LIMIT_INFEAS 2 // time limit exhausted with no feasible solution
//#define CTA_TIME_LIMIT_FEAS 3 // time limit exhausted with feasible solution
//#define CTA_INFEASIBLE 4 // optimization terminated (not by time limit) with no feasible solution
//#define CTA_FEASIBLE 5 // feasible solution found, likely  not optimal
//#define CTA_FIRST_FEASIBLE 6 //first feasible solution found, likely not optimal
//#define CTA_REPAIRINF_FEAS 7 // repair infeasibility procedure successfully finished
//#define CTA_REPAIRINF_INFEAS 8 // repair infeasibility procedure still reported problem is infeasible
//#define CTA_REPAIRINF_ABNORMAL 9 // repair infeasibility procedure abnormally finished (error, numerical instatibilites...)
//#define CTA_OTHERWISE 10 // other situations from solver with no feasible solution
//1 3 5 6 are reasonable OK return values
        if (!doExpert){
          if ( !((result == 1)||(result == 3)||(result == 5)||(result == 6)) ){
            throw new ArgusException("Incorrect end of CTA-program; Error code: "+ result);           
          }
          if (result == 1) {hs = "CTA_optimal solution (within tolerance) found";}
          if (result == 3) {hs = "CTA_time limit exhausted with feasible solution";}
          if (result == 5) {hs = "CTA_feasible solution found, likely  not optimal";} 
          if (result == 6) {hs = "CTA_first feasible solution found, likely not optimal";} 
          SystemUtils.writeLogbook(hs);
          tableSet.suppressINFO = "Standard CTA solution has been applied<br>" +
                                  "Solver used : " + solver + "<br>" + hs;
        }
        if (doExpert){ // try to find a solutions file;  we take the first non-empty one
          solFile = ""; long lastTime, lt;
          lastTime = 0;
          for(i=0;i<solverNames.length;i++){
            hs = Application.getTempFile("CTA_"+solverNames[i]+".sol");  
            if (TauArgusUtils.ExistFile(hs)){
              hs = Application.getTempFile("CTA_"+solverNames[i]+".sol"); 
              if (TauArgusUtils.FileLength(hs)>0) {
                lt = TauArgusUtils.FileLastModified(hs);
                if (lt > lastTime){
                  lastTime = lt;  
                  solFile = hs;   
                  solver = solverNames[i];
                }
              }
            }  
          }
          if (solFile.equals("")) {throw new ArgusException("No solution file of CTA-program found");}
          tableSet.suppressINFO = "Expert CTA solution has been applied<br>" +
                                  "Solver used : " + solver+ "<br>";
        }
        if (TauArgusUtils.FileLength(solFile)==0){ throw new ArgusException("Solution file of CTA-program is empty");}
        
        //Read rthe results back
        nSec[0]=0;
// anco 1.6 try with resources verwijderd. finally toegevoegd        
//         try (BufferedReader reader = new BufferedReader(new FileReader(solFile));) {
         BufferedReader reader = null;       
         try { reader = new BufferedReader(new FileReader(solFile)); 
           Tokenizer tokenizer = new Tokenizer(reader);
           while ((tokenizer.nextLine()) != null) {
             cn = Integer.parseInt(tokenizer.nextToken());
             orgVal = Double.parseDouble(tokenizer.nextToken());
             ctaVal = Double.parseDouble(tokenizer.nextToken());
             if (Math.abs(orgVal-ctaVal) < eps){ctaVal = orgVal;}
             tauArgus.SetCTAValues(tabNo, cn, orgVal, ctaVal, nSec);
          }
          tokenizer.close();
         }
         finally {reader.close();}
         
        Date endDate = new Date();
        long diff = endDate.getTime()-startDate.getTime();
        diff = diff / 1000;
        tableSet.processingTime = (int) diff;
        
        tableSet.ctaProtect = true; 
        tableSet.suppressed = TableSet.SUP_CTA;;
        i = tableSet.CountSecondaries();
        tableSet.nSecond = tableSet.nSecond+ i;  //eigenijk onzin
     // .Inverseweight = (ChkInverseWeight.Value = vbChecked)
        batch.reportProgress("CTA run successfully completed\n"+
                             "Number of secondaries" + tableSet.suppressed + 
                                 "Processing time:"+ StrUtils.timeToString(tableSet.processingTime));
    } 
   
    private static void verdubbelHitasTxt(){
      /*
       Add an articificial secodn dimension in Hitastab
       Add HitasV2.txt
       Modify NFS.txt
       */  
      File fileKlad = new File(Application.getTempFile("Hitastab.kld"));
      File fileHitas = new File(Application.getTempFile("Hitastab.txt"));
      if (fileKlad.exists()) { fileKlad.delete();}
      fileHitas.renameTo(new File(Application.getTempFile("Hitastab.kld")));  
      String hs, kop, zeroline = "0.0 z 0 0";
      int aant;
      try{
       BufferedWriter out = new BufferedWriter(new FileWriter(Application.getTempFile("Hitastab.txt")));
       BufferedReader in =  new BufferedReader(new FileReader(Application.getTempFile("Hitastab.kld")));
       //while (in.ready()){
       try {
           hs = in.readLine();
       } catch (IOException ex){
           hs = null;
       }
       aant = hs.substring(12).indexOf(' ') + 6;
       while (hs != null){
         kop = hs.substring(0, 11);
         out.write(kop.substring(0, 6) + "    0 " + hs.substring(12)); out.newLine();
         out.write(kop.substring(0, 6) + "    1 " + hs.substring(12)); out.newLine();
         out.write(kop.substring(0, 6) + "    2 " + zeroline.format("%" + aant + "s",zeroline)); out.newLine();
         //out.write(kop.substring(0, 6) + "    2             0.0 z 0 0"); out.newLine();
         try {
           hs = in.readLine();
         } catch (IOException ex){
           hs = null;
         }
       }
      in.close();
      out.close();
      out = new BufferedWriter(new FileWriter(Application.getTempFile("hitasv2.txt")));
      out.write("var_2"); out.newLine();
      out.write(".0");out.newLine();
      out.write(".1");out.newLine();
      out.close();
      out = new BufferedWriter(new FileWriter(Application.getTempFile("NFS.txt")));
      out.write("2");out.newLine();
      out.write (Application.getTempFile("hitasv1.txt")); out.newLine();
      out.write (Application.getTempFile("hitasv2.txt")); out.newLine();
      out.write (Application.getTempFile("Hitastab.txt")); out.newLine();
      out.write (Application.getTempFile("hitassec.txt")); out.newLine();
      out.close();
      }
      catch (Exception ex){}
    }
    
    /**
     * Modular aka HiTas is the popular solution for cell cell suppression.\n
     * Based on the optimal solution provided by JJ Salazar the table is broken down in smaller pieces and protected.\n
     * This is necessary because the optimal solution is often far too hard to compute on a PC.\n
     * First the parameter file NPF and the file with the names of the files (NFS) are written to Temp using PrepareHITAS.\n
     * PrepareHitas also writes the data in a format suitable for Modular.\n 
     * Some extra info is written to NPF\n
     * Then ControleerHITAStabtxt does some final checks.\n
     * In case of a single column table Modular has a problem with its own data structures. It cannot handle a single column table.\n
     * So the table is extended with a column of empty cells using verdubbelHitasTxt.\n
     * The parameters for modular are retrieved from the registry and given to Modular 
     * using loadJJParamFromRegistry and setJJParamIntauHitas.\n
     * After removing some files in TEMP AHitas will protect the table.\n
     * ReadSecondaries will finally give the results back to the engine.
     * 
     * @param tableSet
     * @param VarNo
     * @param propertyChangeListener
     * @return
     * @throws ArgusException
     * @throws FileNotFoundException
     * @throws IOException 
     */
    
    public static boolean rewriteHitasv(int expVarNo, int VarNo ) throws ArgusException {
      int i, n, l; 
      String hs;
      char dot = dots.charAt(0);
      hs = "hitasv" + Integer.toString(VarNo+1);
      File fileKlad = new File(Application.getTempFile(hs+".kld"));
      File fileHitasVtxt = new File(Application.getTempFile(hs+".txt"));
      if (fileKlad.exists()) { fileKlad.delete();}
      fileHitasVtxt.renameTo(new File(Application.getTempFile(hs+".kld")));  

      try{

       BufferedWriter out = new BufferedWriter(new FileWriter(Application.getTempFile(hs+".txt")));
       BufferedReader in =  new BufferedReader(new FileReader(Application.getTempFile(hs+".kld")));
       while (in.ready()){
         hs = in.readLine();
         l = hs.length();
         i=0;
         while ( (i<l) && (hs.charAt(i) == dot)) {i++;}
         hs = hs.substring(i);
         hs = hs.replace (".", "_");
         hs = dots.substring(0, i) + hs;
         out.write(hs + "\n");
         }
         out.close();
         in.close();
         if (fileKlad.exists() && !Application.isAnco()) { fileKlad.delete();}
        }
      catch (IOException ex){
         throw new ArgusException("A problem was encountered when rewriting the file hitasv");}  

     return true;
    }
    
    public static boolean runModular(TableSet tableSet, final PropertyChangeListener propertyChangeListener) throws ArgusException, FileNotFoundException, IOException{
        boolean Oke; int i; String hs; Variable variable; BufferedWriter out ;
        final PropertyChangeSupport pcs = new PropertyChangeSupport(TableService.class);
        pcs.addPropertyChangeListener(propertyChangeListener);
        IProgressListener progressListener = new IProgressListener(){
                    @Override
                    public void UpdateGroups(final int percentage) {
                    pcs.firePropertyChange("progressMain", null, percentage);
                    }
                    @Override
                    public void UpdateTables(final int percentage) {
                    pcs.firePropertyChange("progressDetail", null, percentage);
                    }
        };
        tauHitas.SetProgressListener(progressListener);
        pcs.firePropertyChange("activityMain", null, "Groups");
        pcs.firePropertyChange("progressMain", null, 0);
        pcs.firePropertyChange("activityDetail", null, "Tables");
        pcs.firePropertyChange("progressDetail", null, 0);
//        try{
        Date startDate = new Date();  
        SystemUtils.writeLogbook("Start of the modular protection for table " + TableService.getTableDescription(tableSet));
 //       if (tableSet.expVar.size()> 4 && !Application.isProtectCoverTable()) {
 //           hs = "The table has more than 4 dimensions.\n" + 
 //                "Running Modular can take a lot of time and is error-prone.\n" +
 //                "Please check the results carefully.\n";
 //          int warningResult = ShowWarningMessage(hs);
 //          if (warningResult == 0 ) {
 //             throw new ArgusException(hs); //overlapString);
 //         }
 //       }
        
 // checking for too many dimensions and additivity
        if (!Application.isProtectCoverTable()){
           if (tableSet.expVar.size()== 4) {
              hs = "The table has 4 dimensions.\n" + 
                 "Running Modular can take a lot of time and maybe it is difficult to obtain a correct result.\n" +
                 "Please check the results carefully.\n";
             int warningResult = ShowWarningMessage(hs);
             if (warningResult == 0 ) {
                throw new ArgusException("Modular has not been completed"); //overlapString); 
             }
          }
            
            if (tableSet.expVar.size() > 4 ) {
             if (Application.isAnco()) {
               hs = "The table has more than 4 dimensions.\n" + 
                 "Running Modular can take a lot of time and is error-prone.\n" +
                 "Please check the results carefully.\n";
              int warningResult = ShowWarningMessage(hs);
              if (warningResult == 0 ) {
                throw new ArgusException("Modular has not been completed"); //overlapString);
                }
              }
             else{
               hs = "The table has more than 4 dimensions.\n" + 
                    "Running Modular is not possible.\n";   
               throw new ArgusException(hs); //overlapString);
             }
        
          }
          if (tableSet.additivity == TableSet.ADDITIVITY_NOT_REQUIRED){
               hs = "The table might be not additive.\n" + 
                 "Running Modular successfully Is not guaranteed.\n";
              int warningResult = ShowWarningMessage(hs);
              if (warningResult == 0 ) {
                throw new ArgusException(hs); //overlapString);
              }
              
          }
        }

    
        Oke = tauArgus.PrepareHITAS(tableSet.index, Application.getTempFile("NPF.txt"), Application.getTempFile("NFS.txt"), Application.getTempDir()+"/");
        if (!Oke){throw new ArgusException("An unknown error occurred when preparing the files for Modular");} 
        // TestHitasFiles; check for a bogus at the top level
        // rewrite hitasv?.txt here again without dots in the codes
        // Note that this procedure will faill if the code starts with a dot!!!!
        // Ideally this is coorrected in the TauArgus dll itself
        for(i=0;i<tableSet.expVar.size();i++){
          rewriteHitasv(tableSet.expVar.get(i).index, i); 
        }
        int n= tableSet.respVar.nDecimals;
        try{
         out = new BufferedWriter(new FileWriter(Application.getTempFile("NPF.txt"), true));
      
        out.write("DECIMALS=" + n);       out.newLine();
        // TODO vogelen voor PQ regel
        out.write ("MAXWEIGHT="+ tableSet.maxScaleCost);       out.newLine();       out.newLine();
        out.write ("MINTABVAL="+ tableSet.minTabVal);       out.newLine();
        out.write ("MAXTABVAL="+ tableSet.maxTabVal);       out.newLine();       out.newLine();
        if (tableSet.costFunc == TableSet.COST_DIST){
            out.write("[Distance cost function]");       out.newLine();
            out.write("DISTANCE = 1");       out.newLine();
            for(i=0;i<tableSet.expVar.size();i++){
                variable = tableSet.expVar.get(i);
                hs = "D"+Integer.toString(i+1).trim() + " =";
                out.write(hs);                
                if (variable.hasDistanceFunction ){
                    for (int j=0;j<Variable.MAX_NUMBER_OF_DIST;j++){
                        out.write(" "+variable.distanceFunction[j]);
                    }  
                    }
                else {
                    out.write(" 1 1 1 1 1");
                }
                out.newLine();
                }
             }        
           out.close();
           }
        catch (IOException ex){
            throw new ArgusException("A problem was encountered when writing the file NPF");}  
        if (tableSet.expVar.size() == 1){}
//TODO Dummy variable toevoegen voor HITAS  (SjoemelBestandenVoorHitas1)          
        ControleerHITAStabtxt(tableSet); 
        if (tableSet.expVar.size() == 1) {verdubbelHitasTxt();}
       
        TauArgusUtils.DeleteFile(Application.getTempFile("bt.dat"));
        TauArgusUtils.DeleteFile(Application.getTempFile("jjuit.dat"));
        TauArgusUtils.DeleteFile(Application.getTempFile("infeas.dat"));
        TauArgusUtils.DeleteFile(Application.getTempFile("hitassec.txt"));
        TauArgusUtils.DeleteFile(Application.getTempFile("hitas.log")); //op verzoek van Helen
        TauArgusUtils.DeleteFile(Application.getTempFile("csp.bra"));  //op verzoek van Helen
        TauArgusUtils.DeleteFile(Application.getTempFile("CPWProb.log")); //op verzoek van Helen
        TauArgusUtils.DeleteFile(Application.getTempFile("sdcnet.lp")); //op verzoek van Helen
        TauArgusUtils.DeleteFile(Application.getTempFile("hierinfo.dat")); //op verzoek van Helen
//          
        //Run Hitas
        // "./access.ilm"
    //    hs = Application.getTempDir();
        //hs = tauargus.utils.ExecUtils.getApplicationDirectory(OptiSuppress.class).getCanonicalPath()+"\\access.ilm";  
        //hs = ExecUtils.getRegString("optimal", "cplexlicensefile", ExecUtils.getApplicationDirectory(OptiSuppress.class).getCanonicalPath()+"\\access.ilm");
        hs = "";
        if (Application.solverSelected == Application.SOLVER_CPLEX) hs = TauArgusUtils.GetCplexLicenceFile();
        
        tableSet.maxHitasTime = Application.generalMaxHitasTime;
 
        tauHitas.SetDebugMode(Application.SaveDebugHiTaS);
   
        loadJJParamFromRegistry();
        setJJParamIntauHitas();
      
       i= tauHitas.AHiTaS(Application.getTempFile("NPF.txt"), Application.getTempFile("NFS.txt"), tableSet.maxHitasTime, 
                hs, Application.getTempDir()+"/", solverName[Application.solverSelected], tableSet.singletonSingletonCheck, tableSet.singletonMultipleCheck,
                tableSet.minFreqCheck);
        if (i > 0) {        
          hs = tauHitas.GetErrorString(i);
        //  XPRESS or CPLEX or SCIP
       if (i !=0) {throw new ArgusException ("Error in modular suppression procedure\n" + hs);}
         }
        ReadSecondaries(tableSet);
        tableSet.suppressINFO = ReadHitasINFO("hitas.log");
        if (tableSet.expVar.size() == 1) {tableSet.nSecond = tableSet.nSecond /2;}
        Date endDate = new Date();
        long diff = endDate.getTime()-startDate.getTime();
        diff = diff / 1000;
        //if ( diff == 0){ diff = 1;}
        tableSet.processingTime = (int) diff;
        SystemUtils.writeLogbook("End of modular protection. Time used "+ diff+ " seconds\n" + 
                                 "Number of suppressions: " +tableSet.nSecond); 
        tauHitas.SetProgressListener(null);
        pcs.removePropertyChangeListener(propertyChangeListener);
        return true;
//    } catch (Exception ex)  {
//        return false;
//      }
    }

    public static String ReadHitasINFO (String fn){
       String hs, regel;
       try{ 
       BufferedReader  in  = new BufferedReader(new FileReader(Application.getTempFile(fn))); 
       regel = in.readLine(); hs = "";
       regel = in.readLine();
       while (!regel.contains("Start at")){
         hs = hs + regel + "<br>";  
         regel = in.readLine();
       }
       while  (hs.endsWith("<br>")) {hs = hs.substring(0, hs.length()-4);}
       return hs;
       }
       catch (IOException ex){return "";}
    }

    private static void ReadSecondaries(TableSet tableSet) throws ArgusException{
        int[] nSecondaries = new int[1]; boolean oke;
        File fileSecondaries = new File(Application.getTempFile("hitassec.txt"));
        if (! fileSecondaries.exists() ) {throw new ArgusException("Error in modular;\nNo outputfile found");}
        oke = tauArgus.SetSecondaryHITAS(tableSet.index , nSecondaries);
        tableSet.nSecond = nSecondaries[0];
        tableSet.suppressed = TableSet.SUP_HITAS;
        tableSet.solverUsed = Application.solverSelected;
    }
    
    private static void ControleerHITAStabtxt(TableSet tableSet){
        
    String regelOut,token, status, freq, totalstring; 
    int  i;
    Double respVal, lpl, epsilon, x;
    int[] GTIndex = new int[tableSet.expVar.size()];
    for (i=0;i<tableSet.expVar.size();i++){
        GTIndex[i] = 0;
    }
    
    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setDecimalSeparator('.');
    symbols.setGroupingSeparator(',');
    
    DecimalFormat normalPrecision = new DecimalFormat();
    DecimalFormat extraPrecision = new DecimalFormat();
// first rename Hitastab.txt
    File fileKlad = new File(Application.getTempFile("Hitastab.kld"));
    File fileHitas = new File(Application.getTempFile("Hitastab.txt"));
    if (fileKlad.exists()) { fileKlad.delete();}
    fileHitas.renameTo(new File(Application.getTempFile("Hitastab.kld")));
    
    i = tableSet.respVar.nDecimals;
    normalPrecision.setMinimumFractionDigits(i);
    normalPrecision.setMaximumFractionDigits(i);
    extraPrecision.setMinimumFractionDigits(i+3);
    extraPrecision.setMaximumFractionDigits(i+3);
    extraPrecision.setDecimalFormatSymbols(symbols);
    normalPrecision.setDecimalFormatSymbols(symbols);
    extraPrecision.setGroupingUsed(false);
    normalPrecision.setGroupingUsed(false);
    
    epsilon = 1.0 / ( 10^(i+1));
    try{
       BufferedWriter out = new BufferedWriter(new FileWriter(Application.getTempFile("Hitastab.txt")));
  
    Tokenizer tokenizer;
    tokenizer = null;
    try{
       tokenizer = new Tokenizer(new BufferedReader(new FileReader(Application.getTempFile("Hitastab.kld"))));
       } catch (Exception ex) {};
    
    // Get number of characters of largest value, not necessary the total general
    // This value is only used to write HitasTab a bit more readable   
    double x1, x2;
    x1 = Math.abs(tableSet.maxTabVal);
    x2 = Math.abs(tableSet.minTabVal);
    x1 = Math.max(x1,x2);
    int aant = (int) Math.ceil((Math.log(x1)/Math.log(10)));
    if (x2<0) {aant = aant + 1;}
//    int aant = (int) Math.ceil((Math.log(tableSet.getCell(GTIndex).response)/Math.log(10)));
    if (tableSet.respVar.nDecimals > 0)
        aant = aant + tableSet.respVar.nDecimals + 1;
        
    while (tokenizer.nextLine() != null) {
       regelOut = "";
       for (i=0;i<tableSet.expVar.size();i++){
           token = tokenizer.nextToken();
           regelOut = regelOut +token.format("%6s", token);
       }
       if (tableSet.expVar.size()==1){regelOut=regelOut+regelOut;}
       regelOut=regelOut.substring(1);
       token = tokenizer.nextToken();  //Value
       respVal = Double.parseDouble(token);
       regelOut = regelOut + " " + token.format("%" + aant + "s", token);
       status = tokenizer.nextToken();  //Status
       freq = tokenizer.nextToken();
       if ( (respVal == 0) && status.equals("z") ){
         //if ( tableSet.additivity == TableSet.ADDITIVITY_NOT_REQUIRED || Application.isProtectCoverTable()){
          if ( Application.isProtectCoverTable()){
             if (tokenizer.getLine().equals("0")) {regelOut = regelOut + " n ";}
             else                                   {regelOut = regelOut + " z ";}              
          } else {     regelOut = regelOut + " z ";}
          regelOut = regelOut + freq + " "+ tokenizer.getLine(); 
          tokenizer.clearLine();
       }
       else
       {
           regelOut = regelOut + " "+ status;
           if (status.equals("u")){ //check LPL UPL
               token = freq;
               lpl = Double.parseDouble(token);
               x = respVal - tableSet.minTabVal;
               if (x < 0 ) {x = 0.0;} // should not occur
               if (lpl + epsilon > x) {lpl = x - epsilon;}
               if (lpl < 0 ) {lpl = 0.0;}
               regelOut = regelOut + " "+ extraPrecision.format(lpl);
               regelOut = regelOut + " " + tokenizer.nextToken(); //UPL
               freq = tokenizer.nextToken();
           }
           regelOut = regelOut + " " + freq; //Freq
           token = tokenizer.nextToken();
           if (token.substring(0,1)=="-") {token = token.substring(1);}
//           token = token.replace("-", " ");
           regelOut = regelOut + " " + token; //Cost
       }

       out.write (regelOut);
       out.newLine();
           
       }
    tokenizer.close();
    out.close();
    }
    catch (Exception ex){}
       
    }
    
  public static void runUWE(TableSet tableSet)throws ArgusException, IOException{
      String hs;
      int[] nSec = new int[1];
      ArrayList<String> commandline = new ArrayList<>();
      Date startDate = new Date();  
      SystemUtils.writeLogbook("Start UWE protection for table:"+tableSet.toString());
      SaveTable.writeJJ(tableSet, Application.getTempFile("UWE.jj"), false, true, tableSet.minFreq[0], false, false);
      TauArgusUtils.DeleteFile(Application.getTempFile("s.txt"));
      TauArgusUtils.DeleteFile(Application.getTempFile("s1.txt"));
      hs = SystemUtils.getApplicationDirectory(OptiSuppress.class).getCanonicalPath();
      hs = hs + "/EXP_ExternalUnpickerOnJJ.exe";
      commandline.add(hs);
      if (!TauArgusUtils.ExistFile(hs)) {
          throw new ArgusException("The UWE unpick program could not be found");}
//      hs = StrUtils.quote(hs) + "  " + StrUtils.quote(Application.getTempFile("UWE")) + " Orig";
      commandline.add(Application.getTempFile("UWE"));
      commandline.add("Orig");
      TauArgusUtils.writeBatchFileForExec("UWE1", commandline);
      int result = ExecUtils.execCommand(commandline, Application.getTempDir(),false, "Run UWE program step 1");
      if (result !=0){
          throw new ArgusException("The first step of UWE failed");}
      //step 2
      commandline.clear();
      hs = SystemUtils.getApplicationDirectory(OptiSuppress.class).getCanonicalPath();
      hs = hs + "/COIN_LargeTables.exe";
      if (!TauArgusUtils.ExistFile(hs)) {
          throw new ArgusException("The UWE unpick program could not be found");}
      commandline.add(hs);
//      hs = StrUtils.quote(hs) + " UWE GroupedLP";
      commandline.add("UWE");
      commandline.add("GroupedLP");
      TauArgusUtils.writeBatchFileForExec("UWE2", hs);
      result = ExecUtils.execCommand(commandline, Application.getTempDir(),false, "Run UWE program step 2");
      if (result !=0){
          throw new ArgusException("The second step of UWE failed");}
      if (!TauArgusUtils.ExistFile(Application.getTempFile("s.txt")) ){
          throw new ArgusException("The second step of UWE did not produce a file with secondaries.\n"+
                  "File: "+Application.getTempFile("s.txt")+" could not be found");
      }
      //modify the output for reading the secondaries
      BufferedReader  in  = new BufferedReader(new FileReader(Application.getTempFile("s.txt"))); 
      BufferedWriter out = new BufferedWriter(new FileWriter(Application.getTempFile("s1.txt")));
      out.write("fop"); out.newLine();
      out.write("fop"); out.newLine();
      while ( (hs = in.readLine()) != null){
          hs = hs + " m";
          out.write(hs); out.newLine();
      }
      in.close(); out.close();
      
      result = tauArgus.SetSecondaryJJFORMAT(tableSet.index, Application.getTempFile("s1.txt"), false, nSec);

      tableSet.nSecond = nSec[0];
      Date endDate = new Date();
      long diff = endDate.getTime()-startDate.getTime();
      diff = diff / 1000;
      tableSet.processingTime = (int) diff;
      tableSet.suppressed = TableSet.SUP_UWE;
      SystemUtils.writeLogbook("End of UWE protection. Time used "+ diff+ " seconds\n" + 
                                   "Number of suppressions: " +tableSet.nSecond);     
  }
  
//Now invoke the renameTo() method on the reference, oldFile in this case///  
       
/**
 * Calls the JJ-Rounder for the tableSet.\n
 * First writes a JJ file, using SaveTable.writeJJ\n
 * Checks a bit the JJ file and optionally replaces the weight/cost to unity, using correctRoundJJ\n
 * If required it will partitionate the JJ-file in smaller pieces using splitJJ,  
 * using the first spanning variable as the blocking variable.\n
 * Then the JJ rounder is called and the result is given back to the engine.
 * 
 * @param tableSet
 * @throws ArgusException
 * @throws IOException 
 */
    
//    public static void runRounder(TableSet tableSet) throws ArgusException, IOException{
      public static void runRounder(TableSet tableSet, final PropertyChangeListener propertyChangeListener) throws ArgusException, IOException{
        int i, j, j1, nPart=0; 
        String solutionString, hs, xs, solverName, LicenceFile;
        int solutionType, maxRoundTime;
        double[] upperBound = new double[] { 1.0E40 }; 
        double[] lowerBound = new double[] { 0.0 };
        double[] maxJump = new double[1]; 
        int[] numberJump = new int[1]; 
        int[] errorCode = new int[1]; 
        double[] usedTime = new double[1];
        
        final PropertyChangeSupport pcs = new PropertyChangeSupport(TableService.class);
        pcs.addPropertyChangeListener(propertyChangeListener);
        RProgressListener progressListener = new RProgressListener(){
                    @Override
                    public void UpdateLowerBound(final double val) {
                    pcs.firePropertyChange("lower", null, val);
                    DLB = val;
                    }
                    @Override
                    public void UpdateUpperBound(final double val) {
                    pcs.firePropertyChange("upper", null, val);
                    DUB = val;
                    }
                    @Override
                    public void UpdateNumberOpenSubProb(final int val) {
                    pcs.firePropertyChange("open", null, val);
                    }
                    @Override
                    public void UpdateNumberClosedSubProb(final int val) {
                    pcs.firePropertyChange("closed", null, val);
                    }            
        };
        rounder.SetProgressListener(progressListener);
        rounder.SetCallback(jRCallback);
        
        //TODO test on MaxTableValue
        Date startDate = new Date();
        solverName = Application.getSolverName(Application.solverSelected);
        hs = "Start of the rounding procedure for table: " + tableSet.toString();
        if(tableSet.roundPartitions > 0){hs = hs + " (with partitions)";}
        SystemUtils.writeLogbook(hs);
        try{
          //if (tableSet.maxTabVal > Integer.MAX_VALUE)
             //{ throw new ArgusException ("Max cellvalue ("+ tableSet.maxTabVal  +") is too large for the rounding porcedure");}
          if (tableSet.roundPartitions > 0){TauArgusUtils.DeleteFileWild("JJ*.IN", Application.getTempDir());}  
           SaveTable.writeJJ(tableSet, Application.getTempFile("JJ.IN"), true, false, 0, false, false);
           tableSet.roundedInfo = "";
           tableSet.roundTime = 0;
           tableSet.roundJumps = 0;
           tableSet.roundMaxJump += 0;
           tableSet.roundJumps = 0;
           //maxRoundTime = tableSet.roundMaxTime * 60;
           maxRoundTime = tableSet.roundMaxTime; // Argument of DoRound is in minutes, just like tableSet.roundMaxTime
           for (i=0;i<2;i++){tableSet.roundSolType[i] = 0;}
           correctRoundJJ(Application.getTempFile("JJ.IN"), tableSet.roundBase, (tableSet.roundPartitions==0), tableSet.roundUnitCost);
           if(tableSet.roundPartitions>0){ nPart = splitJJ(tableSet);}

// Files ready to round           
           
/* oud           
            ActivityListener activityListener = new ActivityListener() {
            @Override
            public int TAUmessage(double lb, double ub, double rapid, int nodedone, int nodeleft) {
                System.out.println(lb + " " + ub + " " + rapid + " " + nodedone + " " + nodeleft);
                return 0;
              }
            };
*/
           /*
             #define JJZERO          101
             #define JJINF           102
             #define JJMINVIOLA      103
             #define JJMAXSLACK      104
             #define JJMAXTIME	105
            */
           double jjRoundZero = SystemUtils.getRegDouble("optimal", "jjRoundZero", 0.0000001);
           rounder.SetDoubleConstant(101, jjRoundZero);
           double jjRoundInf = SystemUtils.getRegDouble("optimal", "jjRoundInf", 21400000000000.0);
           rounder.SetDoubleConstant(102, jjRoundInf);
           double jjRoundMinViola = SystemUtils.getRegDouble("optimal", "jjRoundMinViola", 0.0001);
           rounder.SetDoubleConstant(103, jjRoundMinViola);
           double jjRoundMaxSlack = SystemUtils.getRegDouble("optimal", "jjRoundMaxSlack", 0.01);
           rounder.SetDoubleConstant(104, jjRoundMaxSlack);               

           double X = (int) tableSet.roundBase;
           solutionString = "";
           LicenceFile = "";
           if (Application.solverSelected == Application.SOLVER_CPLEX) LicenceFile = TauArgusUtils.GetCplexLicenceFile();
           
           if (tableSet.roundPartitions > 0){ //round all the partitions
             j1 = 1;  
             solutionString = "<h2>Rounding procedure was applied with partitioning";
             if (tableSet.roundPartitions == 2){solutionString = solutionString + "<br>with artificial totals added";}
             if (tableSet.roundPartitions == 3){ j1 = 0;
                solutionString = solutionString + "<br>with " + tableSet.roundNumberofBlocks + " blocks";}
             hs = "</h2>\n<table>\n" ;
             hs = hs +  "<tr><th width=\"10%\" height=\"11\">No</th>\n";
             hs = hs +  "    <th width=\"15%\" height=\"11\">Sol. type</th>\n";
             hs = hs +  "    <th width=\"15%\" height=\"11\">Time</th>\n";
             hs = hs +  "    <th width=\"15%\" height=\"11\">LowerBound</th>\n";
             hs = hs +  "    <th width=\"15%\" height=\"11\">UpperBound</th>\n";
             hs = hs +  "    <th width=\"15%\" height=\"11\">Number of Jumps</th>\n";
             hs = hs +  "    <th width=\"15%\" height=\"11\">Max. Jump</th></tr>\n";
             solutionString = solutionString + hs;
             tableSet.processingTime =0;
             for(j=j1;j<nPart;j++){
               Date startDatePart = new Date();
               xs = Integer.toString(j);
               TauArgusUtils.DeleteFile(Application.getTempFile("JJ"+xs+".OUT"));
               TauArgusUtils.DeleteFile(Application.getTempFile("JJ"+xs+".OUT.RAPID"));
               TauArgusUtils.DeleteFile(Application.getTempFile("JJRound"+xs+".OUT"));
               TauArgusUtils.DeleteFile(Application.getTempFile("JJStat"+xs+".OUT"));
               solutionType = rounder.DoRound(solverName, Application.getTempFile("JJ"+xs+".IN"), X, upperBound, lowerBound, 0,  
                                  Application.getTempFile("JJ"+xs+".OUT"), 
                                  Application.getTempFile("JJstat"+xs+".OUT"),
                                  LicenceFile, 
                                  Application.getTempFile("JJRound"+xs+".log"),
                                  maxRoundTime, 0,
                                  Application.getTempDir()+"/",
                                   maxJump, numberJump , usedTime, errorCode); //, activityListener );
               if (solutionType>2) {throw new ArgusException("Rounding error code = "+tauArgus.GetErrorString(errorCode[0]) + "\noccured in subtable "+j);}             
               tableSet.roundMaxJump = Math.max(tableSet.roundMaxJump, maxJump[0]);
               tableSet.roundJumps = Math.max(tableSet.roundJumps,numberJump[0]);
               if (maxJump[0] > tableSet.roundMaxJump){tableSet.roundMaxJump = maxJump[0];}
               //SOLUTION TYPE IS ZOEK!!!!!!!!!!!!! I neem aan dat de retrun valeu nu de solution type is
               if (solutionType  > 2){
                if (TauArgusUtils.ExistFile(Application.getTempFile("JJRound"+xs+".OUT.RAPID"))){
                 TauArgusUtils.renameFile(Application.getTempFile("JJRound"+xs+".OUT.RAPID"),Application.getTempFile("JJRound"+xs+".OUT"));
                }
               }                
               tableSet.roundSolType[solutionType]++;
               hs =  "<tr><td align=\"Right\">" + xs + "</td><td align=\"Right\">";
               if (solutionType == 2){hs = hs + "Rapid";}
               if (solutionType == 1){hs = hs + "Feasible";}
               if (solutionType == 0){hs = hs + "Optimal";}
               Date endDate = new Date();
               long diff = endDate.getTime()-startDatePart.getTime();
               diff = diff / 1000;
               hs = hs + "</td><td align=\"Right\">" + diff + 
                 "</td><td align=\"Right\">" + upperBound[0] + 
                 "</td><td align=\"Right\">" + lowerBound[0] + 
                 "</td><td align=\"Right\">" + numberJump[0] + 
                 "</td><td align=\"Right\">" + maxJump[0] +
                 "</td></tr>\n\r";
               solutionString = solutionString + hs;
             }
             solutionString = solutionString + "</table>"; 
             joinRounded(tableSet,nPart);
           }
           else{ // round as a single table
              xs = "";
              solutionType = rounder.DoRound(solverName, Application.getTempFile("JJ"+xs+".IN"), X, upperBound, lowerBound, 0,  
                                  Application.getTempFile("JJ"+xs+".OUT"), 
                                  Application.getTempFile("JJstat"+xs+".OUT"),
                                  LicenceFile,
                                  Application.getTempFile("JJRound"+xs+".log"),
                                  maxRoundTime, 0,  //Max time,zero restricted
                                  Application.getTempDir()+"/",   // NamePathExe
                                  maxJump, numberJump , usedTime, errorCode); //, activityListener );
               if (solutionType  == 1){
               if (TauArgusUtils.ExistFile(Application.getTempFile("JJRound.OUT.RAPID"))){
                TauArgusUtils.renameFile(Application.getTempFile("JJRound.OUT.RAPID"),Application.getTempFile("JJRound.OUT"));
               }
             }                
             if (solutionType>2) {throw new ArgusException("Rounding error: "+tauArgus.GetErrorString(errorCode[0]));}  
             tableSet.roundMaxJump = maxJump[0];
             tableSet.roundJumps = numberJump[0];
             tableSet.roundSolType[solutionType]++;             
             solutionString = "Solution Type: <b>";
             if(solutionType ==0){solutionString=solutionString+"Optimal";}
             if(solutionType ==1){solutionString=solutionString+"First feasible";}
             if(solutionType ==2){solutionString=solutionString+"Rapid";}
             solutionString=solutionString+"</b>" + 
                     "; LowerBound:" + StrUtils.formatDouble(lowerBound[0], tableSet.respVar.nDecimals) +
                     ", UpperBound: "+ StrUtils.formatDouble(lowerBound[0], tableSet.respVar.nDecimals) + "<br>";
           }
           Date endDate = new Date();
           long diff = endDate.getTime()-startDate.getTime();
           diff = diff / 1000;
           tableSet.processingTime = (int)diff;  

           tauArgus.SetRoundedResponse(Application.getTempFile("JJ.OUT"), tableSet.index);
           tableSet.roundedInfo = solutionString;
           tableSet.rounded = true;
           tableSet.suppressed = TableSet.SUP_ROUNDING;
           tableSet.solverUsed = Application.solverSelected;
           hs = "";
           if (tableSet.roundUnitCost){ hs = "\nUnit cost function has been used.";}
           SystemUtils.writeLogbook("Table has been rounded.\n" + 
                                    "Rounding base:   "+ tableSet.roundBase + ".\n"+
                                    "Max jump:        " + StrUtils.formatDouble(tableSet.roundMaxJump, tableSet.respVar.nDecimals) + ".\n"+
                                    "Number of steps: " + tableSet.roundJumps + ".");
           SystemUtils.writeLogbook("End of the rounding procedure");
            
                
        } catch(Exception ex){ throw new ArgusException("Error in the rounding procedure\n" + ex.getMessage());
            
        }
 
    } 
         
    
    private static boolean correctRoundJJ(String fn, double base, boolean noPartitions, boolean unitCost) throws ArgusException{
        String regelUit, hs; int i, n, p; double cellValue;
        String[] regel = new String[1];
        TauArgusUtils.renameFile (fn, Application.getTempFile("klad"));
        try{
        BufferedReader in = new BufferedReader(new FileReader(Application.getTempFile("klad")));
        BufferedWriter out = new BufferedWriter(new FileWriter(fn));
        regel[0] = in.readLine(); out.write(regel[0]); out.newLine();
        regel[0] = in.readLine(); out.write(regel[0]); out.newLine();
        n = Integer.parseInt(regel[0]);
        for (i=0;i<n;i++){
           regel[0] = in.readLine(); 
           p = regel[0].indexOf("u");
           if (p > 0 ){ 
             regelUit = ""; // regel[0].substring(0,p+1);
             hs = TauArgusUtils.GetSimpleToken(regel);
             regelUit = regelUit + hs + " ";
             hs = TauArgusUtils.GetSimpleToken(regel);
             regelUit = regelUit + hs + " ";             
             cellValue = Double.parseDouble(hs);
             hs = TauArgusUtils.GetSimpleToken(regel);
             if (unitCost) {hs ="1";}
             regelUit = regelUit + hs + " ";             
             regelUit = regelUit + TauArgusUtils.GetSimpleToken(regel) + "  ";
             regelUit = regelUit + " " + TauArgusUtils.GetSimpleToken(regel);
             regelUit = regelUit + " " + TauArgusUtils.GetSimpleToken(regel);
//somehow the protection interval should be in [0,base], but what about negative values?             
             if (cellValue < base) {
               regelUit = regelUit +  " " + cellValue;
               cellValue = base = cellValue;
               regelUit = regelUit +  " " + cellValue + " 0";
             } else{
               regelUit = regelUit + " "+ regel[0];  
             }
             out.write(regelUit); out.newLine();
           } else{
               if (unitCost) {
                 regelUit = ""; // regel[0].substring(0,p+1);
                 hs = TauArgusUtils.GetSimpleToken(regel);
                 regelUit = regelUit + hs + " ";
                 hs = TauArgusUtils.GetSimpleToken(regel);
                 regelUit = regelUit + hs + " ";             
                 hs = TauArgusUtils.GetSimpleToken(regel);
                 if (unitCost) {hs ="1";}
                 regelUit = regelUit + hs + " ";
                 out.write (regelUit + regel[0]);
               }
               else {out.write(regel[0]); 
               }
               out.newLine();
               }
       } //end loop over the cells
        if (noPartitions){
        regel[0] = in.readLine();
        if (regel[0].trim().equals("1")){  //somehow JJ does not like one restriction only
          out.write("2");  out.newLine(); 
          regel[0] = in.readLine();
          out.write(regel[0]); out.newLine();
          out.write(regel[0]); out.newLine();
        }
        out.write(regel[0]); out.newLine();
        regel[0] = in.readLine();
        while (regel[0] != null){
          out.write(regel[0]); out.newLine();
          regel[0] = in.readLine();            
          }          
        }
        else{ //Partitions but what about the single restiction problem?? 
        regel[0] = in.readLine();
        while (regel[0] != null){
          out.write(regel[0]); out.newLine();
          regel[0] = in.readLine();            
          }    
        }
        in.close();
        out.close();
        return true;
        }
        catch(Exception ex){ throw new ArgusException("Error in checking the JJ file for rounding");
           }
    }
    
   private static void openOut(int i, boolean append){
       String hs;
       hs = Integer.toString(i);
//       if(restrictions){ hs = hs + "R";}
//       else {
       if (!append) {TauArgusUtils.DeleteFile(Application.getTempFile("JJ"+ hs + ".IN"));}
//   }
       try{ out  = new BufferedWriter(new FileWriter(Application.getTempFile("JJ"+ hs + ".IN"), true));}            
       catch (IOException ex){};
   }
   
   private static void closeOut(){
       try{ out.close();}
       catch (IOException ex){};
   }
   
   
   private static int splitJJ(TableSet tableSet) throws ArgusException {
       Variable EV1; String hs;
       int i, j, k, deci, nRest, p, q;
       int nRel, nRelTot;
       int[] nc = new int[1]; int[]nac = new int[1];
        BufferedReader in;
    try{
    in = new BufferedReader(new FileReader(Application.getTempFile("jj.in")));
    EV1 = tableSet.expVar.get(0);
    tauArgus.GetVarNumberOfCodes (EV1.index, nc, nac);
    deci = tableSet.respVar.nDecimals;
    hs = in.readLine();
    hs = in.readLine();
    j = StrUtils.toInteger(hs);
    nRest = j / nac[0];
    //first split the data part.
    if ((tableSet.roundPartitions==1)||(tableSet.roundPartitions==2)){
      if (EV1.hierarchical==Variable.HIER_NONE){
        for(i=0;i<nac[0];i++){
          openOut(i,false);
          out.write("0"); out.newLine();
          out.write(nRest+ " "); out.newLine();
          for(j=0;j<nRest;j++){
            hs = in.readLine().trim();
            p = hs.indexOf(" ");
            hs = j+ " "+ hs.substring(p);
            out.write(hs); out.newLine();
          }
          closeOut();
         } 
      }
      else{throw new ArgusException("Only a non hierarchical first exp var imlemented");}
    }
    else{throw new ArgusException("Only a non hierarchical first exp var imlemented");
    }
    
    
    hs = in.readLine();
    j = StrUtils.toInteger(hs);
    //The restrictions
    String hs1, hs2;
    nRel = 0; nRelTot =0;
    if ((tableSet.roundPartitions==1)||(tableSet.roundPartitions==2)){
      if (EV1.hierarchical==Variable.HIER_NONE){
      for (i=0;i<nRest;i++){hs = in.readLine();}
      j = j - nRest;
      j = j / nac[0];
         BufferedWriter jjRel = new BufferedWriter(new FileWriter(Application.getTempFile("JJREL.IN")));
         BufferedWriter jjRelTot = new BufferedWriter(new FileWriter(Application.getTempFile("JJRELTOT.IN")));
         jjRel.write(j+" "); jjRel.newLine(); 
         for (i=0;i<nRest;i++){
             jjRelTot.write("0 3 :" +i + " (-1)" +(nRest + i) + " (1)" + (2 * nRest + i) + " (1)"); jjRelTot.newLine(); nRelTot++;
        }                              

         i = 0;
        while((hs = in.readLine()) != null) {
            p = hs.indexOf(":");
            q = hs.indexOf("(");
            j = StrUtils.toInteger(hs.substring(p+1,q).trim());
            if(j<nRest){
                jjRel.write(hs); jjRel.newLine();  nRel++;
                jjRelTot.write(hs); jjRelTot.newLine();  nRelTot++;
                i++;
                p = hs.indexOf(":");
                hs1 = hs.substring(0, p+1);
                hs2 = hs1;
                hs = hs.substring(p+1);
                while ((hs != null)  && !hs.equals("")){
                 p = hs.indexOf("(");
                 q = hs.indexOf(")");
                 k = StrUtils.toInteger(hs.substring(0, p));
                 hs1 = hs1 + " " + (k+nRest) + " " + hs.substring(p,q+1);
                 hs2 = hs2 + " " + (k+nRest*2) + " " + hs.substring(p,q+1);
                 hs = hs.substring(q+1).trim();
                }
              jjRelTot.write(hs1); jjRelTot.newLine();      
              jjRelTot.write(hs2); jjRelTot.newLine();
              nRelTot = nRelTot + 2;
            }
        }
        jjRel.close();
        jjRelTot.close();
        //Add the relations to aal the JJ files
        for (i=0;i<nac[0];i++){
            openOut(i,true);
            BufferedReader relIn = new BufferedReader(new FileReader(Application.getTempFile("JJREL.IN")));
            hs = relIn.readLine();
            if (nRel == 1){ //if we have only one relation JJ has some problem. So we double the same relation. Who cares
               out.write(hs); out.newLine();
               hs =  relIn.readLine();
               out.write(hs); out.newLine();
               out.write(hs); out.newLine();
            }else{
              out.write(hs); out.newLine();
              for (j=0;j<nRel;j++){
                hs =  relIn.readLine();
                out.write(hs); out.newLine();
              }  
            }
           out.close();
           relIn.close();             
        }
        in.close();
        return nac[0];
       
      }   
      else{throw new ArgusException("Only a non hierarchical first exp var imlemented");}
    }
    else{throw new ArgusException("Only a non hierarchical first exp var imlemented");
    }
    
    } 

    catch(Exception ex){throw new ArgusException("Error when splitting the JJ file for rounding\n" + ex.getMessage());}

}
 
   
private static void joinRounded(TableSet tableSet, int nPart) {
  // First copy all the subfiles to the master file
  // On the fly compute the aggregate level
  // add the aggegate info at the end  
   int nSubSize, i, n, p1, p2; String hs, regel;
   nSubSize=tableSet.numberOfCells()/nPart;
   double[][] xx = new double[nSubSize][2];
   for (i=0;i<nSubSize;i++){xx[i][0] = 0; xx[i][1] = 0;}
   try{
     BufferedWriter roundResult = new BufferedWriter(new FileWriter(Application.getTempFile("JJ.OUT")));
     n=nSubSize;
     for( i=1;i<nPart;i++){
       hs = Integer.toString(i);     
       BufferedReader roundIn = new BufferedReader(new FileReader(Application.getTempFile("JJ"+hs+".out")));
       for (int j=0;j<nSubSize;j++){
         regel = roundIn.readLine();
         p1 = regel.indexOf("from");
         p2 = regel.indexOf("to");
         hs = regel.substring(p1+4, p2);
         xx[j][0] += StrUtils.toDouble(regel.substring(p1+4, p2));
         hs = regel.substring(p2+2);
         xx[j][1] += StrUtils.toDouble(regel.substring(p2+2));
        roundResult.write(n + " "+regel.substring(p1)); roundResult.newLine();
        n++;        
       }       
       roundIn.close();
     }
    for (int j=0;j<nSubSize;j++){
       roundResult.write("" + j + " from " + xx[j][0] + " to "+ xx[j][1]); roundResult.newLine();
    } 
    roundResult.close();
   } 
    catch (IOException ex){}
    catch (ArgusException ex){}
}

//
//Function JoinRounded(Part As Long)
//Dim i As Long, j As Long, EV1 As Long, NC As Long, NAC As Long, NSstr As String
//Dim NRest As Long, XX() As Double, Hs As String, X As Long, ii As Long, j1 As Long, j2 As Long
//Dim P1 As Long, P2 As Long, M As Long, FF() As Long
//EV1 = TableSetStruct(SelectedTable).ExpVar(1)
//frmMain.ArgOCX.GetVarNumberOfCodes EV1, NC, NAC
//Open TempDir + "\jj.in" For Input As #1
//Line Input #1, Hs
//Line Input #1, Hs
//Close #1
//
//j = Val(Hs)
//NRest = j / NAC
//ReDim XX(1 To NRest, 1 To 2)
//For i = 1 To NRest
// For j = 1 To 2
//  XX(i, j) = 0
// Next j
//Next i
//
//Open TempDir + "\jj.out" For Output As #1
//j1 = 2
//j2 = NAC
//If MetaDataStruct.Varlist(EV1).Hierarchical Then j2 = RoundingsParam.NHierInfo + 1
//
//ii = NRest - 1
//If RoundingsParam.Partitions = 3 Then
// j1 = 1
// j2 = RoundingsParam.NumberOfBlocks
//End If
//For i = j1 To j2
// 'ii = (i - 1) * NRest - 1
// NSstr = Trim(Str(i))
// Open TempDir + "\jj" + NSstr + ".out" For Input As #2
// If i > 2 And Part = 2 Then
//  For j = 1 To NRest
//    Line Input #2, Hs
//  Next j
// End If
// For j = 1 To NRest
//  Line Input #2, Hs
//  P1 = InStr(1, Hs, "from")
//  P2 = InStr(1, Hs, "to")
//  X = Val(Mid(Hs, P1 + 4, P2 - P1 - 4))
//  XX(j, 1) = XX(j, 1) + X
//  X = Val(Mid(Hs, P2 + 2))
//  XX(j, 2) = XX(j, 2) + X
//  If RoundingsParam.Partitions = 1 Or RoundingsParam.Partitions = 2 Then
//   ii = ii + 1
// '  Print #1, ii + j; Mid(Hs, P1 - 1)
//   Print #1, ii; Mid(Hs, P1 - 1)
//  End If
// Next j
// While Not EOF(2)
//  Line Input #2, Hs
//  If Left(Hs, 3) <> "SUM" Then
//   P1 = InStr(1, Hs, "from")
//   ii = ii + 1
//   Print #1, ii; Mid(Hs, P1 - 1)
//  End If
// Wend
// Close #2
// If Not Anco Then DeleteFile TempDir + "\jj" + NSstr + ".out"
//Next i
// M = 0
// For j = 1 To NRest
//  Print #1, j - 1; " from "; XX(j, 1); " to "; XX(j, 2)
//  i = Abs(ROUND(XX(j, 1) - XX(j, 2)))
//  If i > M Then M = i
// Next j
// ReDim FF(0 To M)
// For i = 0 To M
//  FF(i) = 0
// Next i
// For j = 1 To NRest
//  i = Abs(ROUND(XX(j, 1) - XX(j, 2)))
//  FF(i) = FF(i) + 1
// Next j
// Open TempDir + "\rndPart.txt" For Output As #2
// For i = 0 To M
//  Print #2, i, FF(i)
// Next i
// Close #2
// ReDim FF(0 To 1)
// ReDim XX(1 To 1, 1 To 2)
// Close #1
//End Function
//
//Sub PrepareArtTotals(j As Long, XS As String)
//Dim JJData As JJDataLineT, JJMINDATA As JJDataLineT, JJRDATA As JJDataLineT, JJTDATA As JJDataLineT
//Dim i As Long, k As Long, n As Long, p As Long, Hs As String, RRV As Double
//Dim JJ As String, JJMIN As String, JJR As String, JJT As String, RR As String
//Dim MaxCellVal As Double
//frmViewTable.lblRndSubProb.Caption = "Preparing for round:" + Str(j - 1)
//frmViewTable.lblRndSubProb.Refresh
//If j = 2 Then
// XS = ""
// Open TempDir + "\JJ" + Trim(Str(j)) + ".in" For Input As #1
// Open TempDir + "\JJT" + Trim(Str(j)) + ".in" For Output As #2
// Line Input #1, Hs
// Print #2, Hs
// Line Input #1, Hs
// n = Val(Hs)
// Print #2, Hs
// For i = 1 To n
//  Line Input #1, Hs
//  Print #2, Hs
// Next i
// Close #1
// Close #2
//Else
// XS = "EXT"
// Open TempDir + "\jj" + Trim(Str(j)) + ".in" For Input As #1
// Open TempDir + "\jj" + Trim(Str(j - 1)) + ".in" For Input As #2
// If j > 3 Then Open TempDir + "\jjR" + Trim(Str(j - 2)) + ".in" For Input As #3
// Open TempDir + "\jjR" + Trim(Str(j - 1)) + ".in" For Output As #4
// Open TempDir + "\jjT" + Trim(Str(j - 1)) + ".in" For Input As #5
// Open TempDir + "\jjT" + Trim(Str(j)) + ".in" For Output As #6
// Open TempDir + "\jj" + Trim(Str(j - 1)) + ".out" For Input As #7
// Line Input #5, Hs
// If j > 3 Then Line Input #3, Hs
// Line Input #2, Hs
// Line Input #1, Hs
// Print #4, Hs
// Print #6, Hs
// 
// Line Input #5, Hs
// If j > 3 Then Line Input #3, Hs
// Line Input #2, Hs
// Line Input #1, Hs
// n = Val(Hs)
// Print #4, Hs
// Print #6, Hs
// 
// If j > 3 Then
//  For i = 1 To n
//   Line Input #7, RR
//  Next i
// End If
// 
// For i = 1 To n
//  Line Input #1, JJ
//  Line Input #2, JJMIN
//  If j > 3 Then Line Input #3, JJR
//  Line Input #5, JJT
//  Line Input #7, RR
//  JJData = StringToJJ(JJ)
//  If i = 1 Then MaxCellVal = JJData.X(2)
//  JJMINDATA = StringToJJ(JJMIN)
//  If j > 3 Then
//    JJRDATA = StringToJJ(JJR)
//  Else
//   With JJRDATA
//   .n = 0
//   .Value = 0
//   .Weight = 0
//   .Status = "s"
//   For k = 1 To 5
//    .X(k) = 0
//   Next k
//   End With
//  End If
//  JJTDATA = StringToJJ(JJT)
//  p = InStr(1, RR, "to")
//  RRV = Val(Mid(RR, p + 2))
//'  p = InStr(1, RR, "from") 'even de oorspr. waarde terugzetten
//'  RRV = Val(Mid(RR, p + 4))
//
//   
//  JJRDATA.n = i + n - 1
//  JJRDATA.Value = JJRDATA.Value + RRV
//  JJRDATA.Weight = JJRDATA.Weight + JJMINDATA.Weight
//  For k = 1 To 2
//   If JJRDATA.X(k) > JJMINDATA.X(k) Then
//    JJRDATA.X(k) = JJRDATA.X(k)
//   Else
//    JJRDATA.X(k) = JJMINDATA.X(k)
//   End If
//  Next k
//  For k = 3 To 5
//   JJRDATA.X(k) = JJRDATA.X(k) + JJMINDATA.X(k)
//  Next k
//  JJRDATA.X(2) = MaxCellVal
//  
//  JJTDATA.n = i + 2 * n - 1
//  JJTDATA.Value = JJTDATA.Value + JJData.Value
//  JJTDATA.Weight = JJTDATA.Weight + JJData.Weight
//'  If JJData.Status = "u" Or JJTDATA.Status = "u" Then
//'   JJData.Status = "u"
//'  ElseIf JJData.Status = "z" Or JJTDATA.Status = "z" Then
//  If JJData.Status = "z" And JJTDATA.Status = "z" Then
//   JJTDATA.Status = "z"
//   For k = 1 To 5
//    JJTDATA.X(k) = JJData.X(k)
//   Next k
//  Else
//   JJTDATA.Status = "s"
//   For k = 1 To 5
//    JJTDATA.X(k) = JJTDATA.X(k) + JJData.X(k)
//   Next k
//  End If
//  JJTDATA.X(2) = MaxCellVal
//  
//  Hs = JJToString(JJRDATA, 0)
//  Print #4, Hs
//  Hs = JJToString(JJTDATA, 0)
//  Print #6, Hs
// Next i
// Close #1
// Close #2
// Close #3
// Close #4
// Close #5
// Close #6
// Close #7
// 
// 'en nu nog alles bijeenvegen
// Open TempDir + "\jj" + Trim(Str(j)) + ".in" For Input As #1
// Open TempDir + "\jjR" + Trim(Str(j - 1)) + ".in" For Input As #2
// Open TempDir + "\jjT" + Trim(Str(j)) + ".in" For Input As #3
// Open TempDir + "\jjRelTot.in" For Input As #4
// Open TempDir + "\jjEXT" + Trim(Str(j)) + ".in" For Output As #5
// 
// Line Input #3, Hs
// Line Input #2, Hs
// Line Input #1, Hs
// Print #5, Hs
// Line Input #3, Hs
// Line Input #2, Hs
// Line Input #1, Hs
// n = Val(Hs)
// Print #5, 3 * n
// For i = 1 To n
//  Line Input #3, Hs
//  p = InStr(1, Hs, " ")
//  Hs = Str(i - 1) + Mid(Hs, p)
//  Print #5, Hs
// Next i
// For i = 1 To n
//  Line Input #1, Hs
//  p = InStr(1, Hs, " ")
//  Hs = Str(i - 1 + n) + Mid(Hs, p)
//  Print #5, Hs
// Next i
// For i = 1 To n
//  Line Input #2, Hs
//  p = InStr(1, Hs, " ")
//  Hs = Str(i - 1 + 2 * n) + Mid(Hs, p)
//  Print #5, Hs
// Next i
// While Not EOF(4)
//  Line Input #4, Hs
//  Print #5, Hs
// Wend
// Close #1
// Close #2
// Close #3
// Close #4
// Close #5
//End If
//End Sub

    
    
    
//Function RunJJ(Tabnummer As Long, TotUnsafe As Long) As Boolean
//Dim i As Long, Res As Long, ILMString As String, Hs As String, PQQ As Long
//Dim scaling As Boolean, Singleton As Long, MinFreq As Long
//Dim SuppType As Long, NSec As Long, TT As Long, B As Boolean, APB As Double
//Dim B1 As Boolean, B2 As Boolean, B3 As Boolean    
/*        public static void runOptimal(TableSet tableSet) throws ArgusException, IOException{
          int i,result; double apBound = 0.5; String hs;
          int[] nSecondary = new int[1];
          Date startDate = new Date();  

          TauArgusUtils.DeleteFile(Application.getTempFile("JJ.OUT"));//DeleteFile (Temp + "\JJ.OUT")
          TauArgusUtils.DeleteFile(Application.getTempFile("JJ2.OUT"));//DeleteFile (Temp + "\JJ2.OUT") 
              
// frmModularOptions.FreqRule = True
          SaveTable.writeJJ(tableSet, Application.getTempFile("JJ.IN"), false, true, tableSet.minFreq[0], true);
               
// Muis Me, "BEZIG"
// If ChkInverseWeight = vbChecked Then TauFunctions.InverseWeightJJ TempDir + "\jj.in", TempDir       
//  PQQ = TableSetStruct(SelectedTable).PQQ(1)
//  If PQQ = 0 Then PQQ = TableSetStruct(SelectedTable).PQQ(3)
//  TauFunctions.AprioryWeightJJ TempDir + "\jj.in", TempDir,   PQQ, 2
// End If 
// public int FullJJ(String InFileJJ, String OutFile, int MaxTime, String ILMFile, String OutDir, String Solver) {          
          result = 9998;
          hs = tauargus.utils.SystemUtils.getApplicationDirectory(OptiSuppress.class).getCanonicalPath()+"\\access.ilm";                    
          //OptimizeParameters
           result = tauHitas.FullJJ(Application.getTempFile("JJ.IN"), Application.getTempFile("JJ.OUT"), 
                            tableSet.maxTimeOptimal, hs, Application.getTempDir()+"\\", solverName[Application.solverSelected]);
          if (result > 1){
              if (result == 8000 || result == 8001) {throw new ArgusException(tauArgus.GetErrorString(result));}
              throw new ArgusException("No optimal solutionfound/n"+tauArgus.GetErrorString(result)+
                                       "see also file: "+ Application.getTempFile("FullJJ.log"));
          }
          try{ BufferedReader  in  = new BufferedReader(new FileReader(Application.getTempFile("JJ.OUT")));    
               BufferedWriter out = new BufferedWriter(new FileWriter(Application.getTempFile("JJ2.OUT")));
               out.write ("fop"); out.newLine();
               out.write ("fop"); out.newLine();
               while((hs = in.readLine()) != null) { 
                   out.write(hs + " m"); out.newLine();
               } 
               in.close(); out.close();
           } catch (IOException ex){
               throw new ArgusException("An error occured while processing the output of Optimal");
          }
          result = tauArgus.SetSecondaryJJFORMAT(tableSet.index, Application.getTempFile("JJ2.OUT"), false, nSecondary);
          //TestTRivialSolution
          tableSet.nSecond = nSecondary[0];
          Date endDate = new Date();
          long diff = endDate.getTime()-startDate.getTime();
          diff = diff / 1000;
          if ( diff == 0){ diff = 1;}
          tableSet.processingTime = (int) diff;
          tableSet.suppressed = TableSet.SUP_JJ_OPT_XP;
          SystemUtils.writeLogbook("End of Optimal protection. Time used "+ diff+ " seconds\n" + 
                                   "Number of suppressions: " +tableSet.nSecond); 

    }
   */
        public static void runOptimal(TableSet tableSet, final PropertyChangeListener propertyChangeListener, Boolean inverseWeight, Boolean externalJJFile, int maxTime) throws ArgusException, FileNotFoundException, IOException{
            int i,result; double apBound = 0.5; String hs;
            int[] nSecondary = new int[1]; int maxTimeAllowed;
            // First check for the max. dimension of the table. 
            if (tableSet.expVar.size() > 4 ) {
              if (Application.isAnco()) {
                hs = "The table has more than 4 dimensions.\n" + 
                 "Running Optimal can take a lot of time and is error-prone.\n" +
                 "Please check the results carefully.\n";
              int warningResult = ShowWarningMessage(hs);
              if (warningResult == 0 ) {
                throw new ArgusException("Optimal has not been completed"); //overlapString);
                }
              }
             else{
               hs = "The table has more than 4 dimensions.\n" + 
                    "Running Optimal is not possible.\n";   
               throw new ArgusException(hs); 
             }
        
          }
          
            final PropertyChangeSupport pcs = new PropertyChangeSupport(TableService.class);
            pcs.addPropertyChangeListener(propertyChangeListener);
            IProgressListener progressListener = new IProgressListener(){
                    @Override
                    public void UpdateUB(final int percentage) {
                    pcs.firePropertyChange("value1", null, percentage);
                    UB = percentage;
                    }
                    @Override
                    public void UpdateLB(final int percentage) {
                    pcs.firePropertyChange("value2", null, percentage);
                    LB = percentage;
                    }
                    @Override
                    public void UpdateDiscrepancy(final double percentage) {
                    pcs.firePropertyChange("value3", null, 100*percentage); // convert to percentage notation
                    Diff = 100*percentage;
                    }
                    @Override
                    public void UpdateTime(final int seconds) {
                    pcs.firePropertyChange("value4", null, seconds);
                    TimeSoFar = seconds;
                    }
                    @Override
                    public void UpdateNSuppressed(final int value) {
                    nSuppressed = value;    
                    }
            };
            tauHitas.SetProgressListener(progressListener);
            pcs.firePropertyChange("label1", null, "Upper Bound:");
            pcs.firePropertyChange("label2", null, "Lower Bound:");
            pcs.firePropertyChange("label3", null, "Discrepancy:");
            pcs.firePropertyChange("label4", null, "Time used:");
           
            tauHitas.SetCallback(jCallback);
          
            Date startDate = new Date();  
            
          TauArgusUtils.DeleteFile(Application.getTempFile("JJ.OUT"));//DeleteFile (Temp + "\JJ.OUT")
          TauArgusUtils.DeleteFile(Application.getTempFile("JJ2.OUT"));//DeleteFile (Temp + "\JJ2.OUT") 
              
// frmModularOptions.FreqRule = True
          if(!externalJJFile){
            SaveTable.writeJJ(tableSet, Application.getTempFile("JJ.IN"), false, true, tableSet.minFreq[0], false, inverseWeight);
            maxTimeAllowed =  tableSet.maxTimeOptimal;
          }  
          else {
            maxTimeAllowed = maxTime;   
          }
                
//  PQQ = TableSetStruct(SelectedTable).PQQ(1)
//  If PQQ = 0 Then PQQ = TableSetStruct(SelectedTable).PQQ(3)
//  TauFunctions.AprioryWeightJJ TempDir + "\jj.in", TempDir,   PQQ, 2
// End If 
        result = 9998;
//        hs = SystemUtils.getRegString("optimal", "cplexlicensefile", "");
        if (Application.solverSelected == Application.SOLVER_CPLEX) hs = TauArgusUtils.GetCplexLicenceFile();
        else hs ="";
        tauHitas.SetDebugMode(Application.SaveDebugHiTaS);
        
        loadJJParamFromRegistry();
        setJJParamIntauHitas();
        
        result = tauHitas.FullJJ(Application.getTempFile("JJ.IN"), Application.getTempFile("JJ.OUT"), 
                            maxTimeAllowed, hs, Application.getTempDir()+"/", solverName[Application.solverSelected]);
          if (result > 1){
              if (result == 8000 || result == 8001) {throw new ArgusException(tauArgus.GetErrorString(result));}
              throw new ArgusException("No optimal solutionfound/n"+tauArgus.GetErrorString(result)+
                                       "see also file: "+ Application.getTempFile("FullJJ.log"));
          }
          try{ BufferedReader  in  = new BufferedReader(new FileReader(Application.getTempFile("JJ.OUT")));    
               BufferedWriter out = new BufferedWriter(new FileWriter(Application.getTempFile("JJ2.OUT")));
               out.write ("fop"); out.newLine();
               out.write ("fop"); out.newLine();
               while((hs = in.readLine()) != null) { 
                   out.write(hs + " m"); out.newLine();
               } 
               in.close(); out.close();
           } catch (IOException ex){
               throw new ArgusException("An error occured while processing the output of Optimal");
          }
          if (externalJJFile){return;}
          result = tauArgus.SetSecondaryJJFORMAT(tableSet.index, Application.getTempFile("JJ2.OUT"), false, nSecondary);
          tableSet.suppressINFO = ReadHitasINFO("fulljj.log");
          //TestTRivialSolution
          tableSet.nSecond = nSecondary[0];
          Date endDate = new Date();
          long diff = endDate.getTime()-startDate.getTime();
          diff = diff / 1000;
  //        if ( diff == 0){ diff = 1;}
          tableSet.processingTime = (int) diff;
          tableSet.suppressed = TableSet.SUP_JJ_OPT;
          tableSet.solverUsed = Application.solverSelected;
          tableSet.inverseWeight = inverseWeight;
          SystemUtils.writeLogbook("End of Optimal protection. Time used "+ diff+ " seconds\n" + 
                                   "Number of suppressions: " +tableSet.nSecond); 
          tauHitas.SetProgressListener(null);
          pcs.removePropertyChangeListener(propertyChangeListener);
    }
        
    public static void ProtectJJFormat (String JJInputFile) {
         //Application.getTempFile("JJ.IN") 
        
// Run the JJ file        
               final SwingWorker <Integer, Void> worker = new ProgressSwingWorker<Integer, Void>(ProgressSwingWorker.DOUBLE,"Modular approach") {
                    @Override
                    protected Integer doInBackground() throws ArgusException, Exception{
                        super.doInBackground(); 
                       try{
                         OptiSuppress.runOptimal(null, new PropertyChangeListener(){
                                                        @Override
                                                        public void propertyChange(PropertyChangeEvent evt){
                                                        }
                                            }, false, true, 1);
                        }
                        catch (IOException ex) {}                    
                        return null;
                    }

                    @Override
                    protected void done(){
                        super.done();
                        try{
                            get();
                        }
                        catch (InterruptedException ex) {
                            logger.log(Level.SEVERE, null, ex); 
                        } catch (ExecutionException ex) {
                            JOptionPane.showMessageDialog(null, ex.getCause().getMessage());
                        }
                    }
                };
                worker.execute();
                while (!worker.isDone()){
                   try{Thread.sleep(1000);}
                   catch (InterruptedException ex) {}
                }    

    }    
    /**
     * Add the secondaries to a JJ file.
     * Only used for the option to protect a JJ file directly
     * @param JJInputFile
     * @param JJOutputFile 
     */
    public static int addSecondariesToJJFile (String JJInputFile, String JJOutputFile ){
      int i, n, nSec; String hs, regel; int curr;  
      BufferedReader in,sec;  BufferedWriter out;
      try{
      in = new BufferedReader(new FileReader(JJInputFile));
      sec = new BufferedReader(new FileReader(Application.getTempFile(Application.getTempFile("JJ.OUT"))));
      out = new BufferedWriter(new FileWriter(JJOutputFile));
      regel = in.readLine();
      out.write(regel);out.newLine();
      regel = in.readLine();
      out.write(regel);out.newLine();
      n = Integer.parseInt(regel);
      nSec = 0;
      if (sec.ready()){
          hs = sec.readLine().trim(); 
          curr = Integer.parseInt(hs);} 
      else {curr = 999999999;}
      for(i=0;i<n;i++){
        regel = in.readLine();
        if (i==curr){
          if (regel.contains("s")){regel= regel.replace("s", "m"); nSec++;}
          if (sec.ready()){hs = sec.readLine().trim(); curr = Integer.parseInt(hs);} else{curr = 999999999;}
        }
        out.write(regel); out.newLine();
      }
      while (in.ready()){
        regel = in.readLine();
        out.write(regel); out.newLine();
      }
      in.close(); sec.close(); out.close();
      return nSec;
      }
      catch (FileNotFoundException ex){return -1;}
      catch (IOException ex){return -2;}    
    }
   
    static void TestTrivialSolution (TableSet tableSet) throws ArgusException{
        CellStatusStatistics stat = tableSet.getCellStatusStatistics();
        int nSafe = stat.freq[CellStatus.SAFE.getValue()] + stat.freq[CellStatus.SAFE_MANUAL.getValue()];
        if (nSafe == 0) {throw new ArgusException("All cells have been suppressed.\n" + 
                                                  "The problem might be infeasible"); }
    }

    public static boolean RunCellKey (TableSet tableSet, String PTableFile) throws ArgusException, FileNotFoundException, IOException{
        // Assumptions on format of p-table:
        // i runs from 0 to maxNi
        // for each i map of pij for which pij > 0
        
        // Currently only reading ptable from file as given in metadata is possible
        long startTime = new Date().getTime();
        int getmin[]={0}, getmax[]={0};
        int tmp[]={0};
        int result = tauArgus.SetCellKeyValues(tableSet.index, tableSet.cellkeyVar.metadata.getFilePath(PTableFile), getmin, getmax);
        tableSet.minDiff = getmin[0];
        tableSet.maxDiff = getmax[0];
        
        tableSet.CalculateCKMInfo();
        
        if (result == -9){ // error reading ptable file
            JOptionPane.showMessageDialog(null, "Error reading ptable file " + tableSet.cellkeyVar.metadata.getFilePath(PTableFile));
            return false;
        }
        long endTime = new Date().getTime();
        long diff = (endTime - startTime)/1000;
        tableSet.processingTime = (int)diff; 
        
        tableSet.suppressINFO = "Cell Key Method has been applied<br>";
        tableSet.suppressed = TableSet.SUP_CKM;
        tableSet.ckmProtect = true;
        batch.reportProgress("Cell Key Method successfully completed in " + tableSet.processingTime + " seconds\n");
        
        return true;
    }
      
}
