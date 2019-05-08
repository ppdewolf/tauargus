/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package tauargus.extern.tauhitas;

public class ICallback {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected ICallback(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ICallback obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        tauhitasJNI.delete_ICallback(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  protected void swigDirectorDisconnect() {
    swigCMemOwn = false;
    delete();
  }

  public void swigReleaseOwnership() {
    swigCMemOwn = false;
    tauhitasJNI.ICallback_change_ownership(this, swigCPtr, false);
  }

  public void swigTakeOwnership() {
    swigCMemOwn = true;
    tauhitasJNI.ICallback_change_ownership(this, swigCPtr, true);
  }

  public int SetStopTime() {
    return tauhitasJNI.ICallback_SetStopTime(swigCPtr, this);
  }

  public ICallback() {
    this(tauhitasJNI.new_ICallback(), true);
    tauhitasJNI.ICallback_director_connect(this, swigCPtr, swigCMemOwn, true);
  }

}
