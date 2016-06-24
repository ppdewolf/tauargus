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

public class taurounderJNI {
  public final static native void delete_RProgressListener(long jarg1);
  public final static native void RProgressListener_UpdateLowerBound(long jarg1, RProgressListener jarg1_, double jarg2);
  public final static native void RProgressListener_UpdateUpperBound(long jarg1, RProgressListener jarg1_, double jarg2);
  public final static native void RProgressListener_UpdateNumberClosedSubProb(long jarg1, RProgressListener jarg1_, int jarg2);
  public final static native void RProgressListener_UpdateNumberOpenSubProb(long jarg1, RProgressListener jarg1_, int jarg2);
  public final static native long new_RProgressListener();
  public final static native void RProgressListener_director_connect(RProgressListener obj, long cptr, boolean mem_own, boolean weak_global);
  public final static native void RProgressListener_change_ownership(RProgressListener obj, long cptr, boolean take_or_release);
  public final static native void delete_RCallback(long jarg1);
  public final static native int RCallback_SetExtraTime(long jarg1, RCallback jarg1_);
  public final static native long new_RCallback();
  public final static native void RCallback_director_connect(RCallback obj, long cptr, boolean mem_own, boolean weak_global);
  public final static native void RCallback_change_ownership(RCallback obj, long cptr, boolean take_or_release);
  public final static native long new_RounderCtrl();
  public final static native void delete_RounderCtrl(long jarg1);
  public final static native void RounderCtrl_SetProgressListener(long jarg1, RounderCtrl jarg1_, long jarg2, RProgressListener jarg2_);
  public final static native void RounderCtrl_SetCallback(long jarg1, RounderCtrl jarg1_, long jarg2, RCallback jarg2_);
  public final static native void RounderCtrl_SetDoubleConstant(long jarg1, RounderCtrl jarg1_, int jarg2, double jarg3);
  public final static native int RounderCtrl_DoRound(long jarg1, RounderCtrl jarg1_, String jarg2, String jarg3, double jarg4, double[] jarg5, double[] jarg6, int jarg7, String jarg8, String jarg9, String jarg10, String jarg11, int jarg12, int jarg13, String jarg14, double[] jarg15, int[] jarg16, double[] jarg17, int[] jarg18);

  public static void SwigDirector_RProgressListener_UpdateLowerBound(RProgressListener self, double LowerBound) {
    self.UpdateLowerBound(LowerBound);
  }
  public static void SwigDirector_RProgressListener_UpdateUpperBound(RProgressListener self, double UpperBound) {
    self.UpdateUpperBound(UpperBound);
  }
  public static void SwigDirector_RProgressListener_UpdateNumberClosedSubProb(RProgressListener self, int ClosedSubProb) {
    self.UpdateNumberClosedSubProb(ClosedSubProb);
  }
  public static void SwigDirector_RProgressListener_UpdateNumberOpenSubProb(RProgressListener self, int OpenSubProb) {
    self.UpdateNumberOpenSubProb(OpenSubProb);
  }
  public static int SwigDirector_RCallback_SetExtraTime(RCallback self) {
    return self.SetExtraTime();
  }

  private final static native void swig_module_init();
  static {
    swig_module_init();
  }
}
