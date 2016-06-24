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

package tauargus.extern.taurounder;

public class RounderCtrl {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected RounderCtrl(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(RounderCtrl obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        taurounderJNI.delete_RounderCtrl(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public RounderCtrl() {
    this(taurounderJNI.new_RounderCtrl(), true);
  }

  public void SetProgressListener(RProgressListener jProgressListener) {
    taurounderJNI.RounderCtrl_SetProgressListener(swigCPtr, this, RProgressListener.getCPtr(jProgressListener), jProgressListener);
  }

  public void SetCallback(RCallback jCallback) {
    taurounderJNI.RounderCtrl_SetCallback(swigCPtr, this, RCallback.getCPtr(jCallback), jCallback);
  }

  public void SetDoubleConstant(int variable, double value) {
    taurounderJNI.RounderCtrl_SetDoubleConstant(swigCPtr, this, variable, value);
  }

  public int DoRound(String Solver, String InFileName, double Base, double[] UpperBound, double[] LowerBound, int Auditing, String SolutionFile, String StatisticsFile, String LicenseFile, String LogFile, int MaxTime, int ZeroRestricted, String NamePathExe, double[] MaxJump, int[] NumberJump, double[] UsedTime, int[] ErrorCode) {
    return taurounderJNI.RounderCtrl_DoRound(swigCPtr, this, Solver, InFileName, Base, UpperBound, LowerBound, Auditing, SolutionFile, StatisticsFile, LicenseFile, LogFile, MaxTime, ZeroRestricted, NamePathExe, MaxJump, NumberJump, UsedTime, ErrorCode);
  }

}
