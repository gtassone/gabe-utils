package com.gmail.gtassone.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Abstract base class for asynchronous computations. The major difference from
 * a normal FutureValue is that AsynchronousFuture expects an external entity to
 * set its final value via setResponseValue(Object value). Once the response
 * value is set, the waiting computation thread terminates. AsynchronousFuture
 * also has a built-in time-out mechanism. This class assumes a timed maximum
 * delay to wait for a response to an asynchronous call. Set the delay with
 * setDelay(int millis). Rather than providing a Runnable or Callable
 * implementation as for a FutureValue, the computation behavior for
 * AsynchronousFuture is fixed. It initiates the asynchronous call, then waits
 * for its final value to be set. Behavior can be customized via the abstract
 * methods onFailure, onInterrupt, onCancellation. For instance, onFailure is
 * triggered when the call times out; implementations might cancel the
 * computation, or send a status query, or start another wait cycle.
 * 
 * @param <T>
 *        return type parameter for the Future.
 * @author <a href=mailto:gtassone@gmail.com>GTassone</a>
 * @version $Revision: 4272 $
 */
public abstract class AsynchronousFuture<T> implements Future<T>, Runnable {

  // should be easy to add a Callback mechanism whereby the setResponseValue()
  // initiates Callbacks.
  // implement a method to invoke cancellation or failure with an external
  // exception.
  // ensure Exceptions and cancel, failure work as intended.
  
  /**
   * Called when the computation thread begins. Clearly implementations should
   * initiate an asynchronous call of some kind.
   */
  public abstract void initiateAsynchronousCall();

  /**
   * Triggered on timeout; or a null value. Implementations can use this method
   * to cancel the call, trigger a wait cycle, or do other arbitrary
   * book-keeping like sending a status update request.
   */
  public abstract void onFailure();

  /**
   * triggered on interrupt.
   * 
   * @param e
   *        the exception.
   */
  public abstract void onInterrupt(InterruptedException e);

  /**
   * triggered on cancellation.
   * 
   * @param e
   *        the exception.
   */
  public abstract void onCancellation(CancellationException e);

  private String label;

  private boolean isDebug;

  /**
   * sets the label for this object, used in logging.
   * 
   * @param l
   *        the label String.
   */
  public final void setLabel(String l) {
    label = l;
  }

  /**
   * Gets the label.
   * 
   * @return the label for this object.
   */
  public final String getLabel() {
    return label;
  }

  /**
   * writes internal debug statements.
   * 
   * @param debug
   *        the log string.
   */
  public final void debug(String debug) {
    if (isDebug) {
      System.err.println(label + " : " + debug);
    }
  }

  /**
   * sets the debug flag.
   * 
   * @param debug
   *        is debugging turned on.
   */
  public final void setDebug(boolean debug) {
    this.isDebug = debug;
  }

  private FutureTask<T> future;

  private AsynchronousCallable<T> callable;

  /**
   * default Constructor. Provides an AsynchronousFuture with default settings -
   * no timeout, label is 'unlabeled'.
   */
  public AsynchronousFuture() {

    isDebug = false;
    label = "unlabeled";
    callable = new AsynchronousCallable<T>(false);
    future = new FutureTask<T>(callable);
    callable.setParentFuture(this);
  }

  /**
   * Constructor for a timed AsynchronousFuture.
   * 
   * @param delayMillis
   *        the timeout delay, in milliseconds.
   */
  public AsynchronousFuture(int delayMillis) {
    this();
    callable.setDelayMillis(delayMillis);
  }

  /**
   * sets the timeout delay for a wait cycle.
   * 
   * @param millis
   *        the timeout delay, in milliseconds.
   */
  public final void setDelay(int millis) {
    callable.setDelayMillis(millis);

  }

  /**
   * Allows the AsynchronousFuture to be submitted to an Executor, like
   * FutureValue. This method starts the computation thread, and triggers the
   * onCancellation method if the computation is canceled.
   */
  public final void run() {
    try {
      future.run();
    } catch (CancellationException e) {
      onCancellation(e);
    }
  }

  /**
   * sets the final response value, which causes the computation thread to
   * terminate.
   * 
   * @param val
   *        the return value for the Future. (The Future is now.)
   * @return false if the value is already set, true otherwise.
   */
  public final boolean setResponseValue(T val) {
    return callable.setResponseValue(val);
  }

  /**
   * This method triggers the internal computation thread to start another wait
   * cycle. This method should only be called in onFailure or onInterrupt,
   * because these methods are triggered within the computation thread. This
   * method should not be called outside the computation thread.
   */
  public final void awaitResponse() {
    callable.awaitResponse();
  }

  private void setComputationState(ComputationState state) {
    callable.setComputationState(state);
  }

  /**
   * gets the computation state.
   * 
   * @return the ComputationState representing the current state of the Future.
   */
  public final ComputationState getComputationState() {
    return callable.getComputationState();
  }

  /**
   * cancels the Future execution.
   * 
   * @param mayInterruptIfRunning
   *        flag passed to future.cancel.
   * @return success or failure.
   */
  public final boolean cancel(boolean mayInterruptIfRunning) {
    return future.cancel(mayInterruptIfRunning);
  }

  /**
   * blocks until future value is set, then returns the value.
   * 
   * @return T the final result value of the Future.
   * @exception InterruptedException
   *            if interrupted.
   * @exception ExecutionException
   *            if error.
   */
  public final T get() throws InterruptedException, ExecutionException {
    return future.get();
  }

  /**
   * like get, but times out if timeout measured in unit is expired.
   * 
   * @param timeout
   *        the timeout interval.
   * @param unit
   *        the timeout interval measurement unit.
   * @return T the final result value of the Future.
   * @exception InterruptedException
   *            if interrupted.
   * @exception ExecutionException
   *            if error.
   * @exception TimeoutException
   *            if timed out.
   */
  public final T get(long timeout, TimeUnit unit) throws TimeoutException,
      InterruptedException, ExecutionException {
    return future.get(timeout, unit);
  }

  /**
   * cancelled predicate.
   * 
   * @return true if cancelled.
   */
  public final boolean isCancelled() {
    return future.isCancelled();
  }

  /**
   * done predicate.
   * 
   * @return true if done.
   */
  public final boolean isDone() {
    return future.isDone();
  }

  /**
   * Enumeration of possible computation states. The AsynchronousFuture manages
   * its state internally, and client code can access it via
   * AsynchronousFuture.getComputationState
   * 
   * @author GTassone
   */
  public static enum ComputationState {

    /**
     * state before AsynchronousFuture is executed.
     */
    NOT_STARTED("NOT_STARTED"),

    /**
     * state when AsynchronousFuture has started executing.
     */
    STARTED("STARTED"),

    /**
     * state when AsynchronousFuture has finished executing
     * initiateAsynchronousCall() and is in a wait loop.
     */
    WAITING("WAITING"),

    /**
     * state when AsynchronousFuture has successfully set a return value.
     */
    SUCCESS("SUCCESS"),

    /**
     * state set when wait cycle has completed and status is not SUCCESS.
     */
    FAILURE("FAILURE"),

    /**
     * state set when Future is cancelled.
     */
    CANCELLED("CANCELLED");

    ComputationState(final String n) {
      this.statename = n;
    }

    private final String statename;

    /**
     * returns String name of state.
     * 
     * @return the name of the state.
     */
    public final String getStateName() {
      return statename;
    }

    /**
     * returns String representation of state.
     * 
     * @return the String representation of the state.
     */
    public String toString() {
      return statename;
    }
  }

  /**
   * Internal Callable implementation which executes the 'computation' and
   * response wait cycle.
   * 
   * @author GTassone
   */
  protected static class AsynchronousCallable<T> implements Callable<T> {

    private AsynchronousFuture<T> parent = null;

    private Object responseLock;

    private T response;

    private int delayMillis = 0;

    private boolean isTimed;

    private ComputationState state;

    /**
     * sets up default callable.
     */
    public AsynchronousCallable() {
      responseLock = new Object();
      response = null;
      isTimed = false;
      state = ComputationState.NOT_STARTED;
    }

    /**
     * sets the timed flag.
     * 
     * @param timed
     *        flag.
     */
    public AsynchronousCallable(boolean timed) {
      this();
      isTimed = timed;
    }

    /**
     * sets timed flag and delay interval in milliseconds.
     * 
     * @param delayMillis
     *        delay interval in milliseconds.
     */
    public AsynchronousCallable(int delayMillis) {
      this();
      setDelayMillis(delayMillis);
    }

    /**
     * attaches parent AsynchronousFuture.
     * 
     * @param p
     *        the parent AsynchronousFuture containing this callable.
     * @return this.
     */
    protected final AsynchronousCallable<T> setParentFuture(
        AsynchronousFuture<T> p) {
      parent = p;
      return this;
    }

    /**
     * sets the timed flag and delay interval in milliseconds.
     * 
     * @param millis
     *        delay interval in milliseconds.
     */
    public final void setDelayMillis(int millis) {
      isTimed = true;
      delayMillis = millis;
    }

    /**
     * safely alters the ComputationState.
     * 
     * @param newState
     *        the current ComputationState.
     */
    public final void setComputationState(ComputationState newState) {

      parent.debug("AsyncFuture in setComputationState");

      synchronized (responseLock) {

        parent.debug("AsyncFuture in setComputationState sync block");
        parent.debug("AsyncFuture setting ComputationState to " + newState);

        this.state = newState;
      }
    }

    /**
     * gets the current ComputationState.
     * 
     * @return the current ComputationState.
     */
    public final ComputationState getComputationState() {

      synchronized (responseLock) {
        return state;
      }
    }

    /**
     * Sets the final value of the AsynchronousCallable, which should cause it
     * to return, which should end the computation cycle of the enclosing
     * FutureValue.
     * 
     * @param val
     *        the final result.
     * @return true on success.
     */
    public final boolean setResponseValue(T val) {

      synchronized (responseLock) {

        parent
            .debug("in setResponseValue sync block -- this should not occur without first entering awaitResponse()");
        // since we can't do anything until call() releases the response
        // lock, this is a good place to check the ComputationState.

        if (getComputationState().equals(ComputationState.STARTED)
            || getComputationState().equals(ComputationState.WAITING)) {
          response = val;
          setComputationState(ComputationState.SUCCESS);

          responseLock.notifyAll();
          return true;
        } else {
          return false;
        }
      }
    }

    /**
     * Wait loop of the execution thread. This wait loop constitutes the main
     * behavior of the computation thread; actual computation or retrieval of
     * the response value is expected to be carried out externally.
     */
    public final void awaitResponse() {

      parent.debug("AsyncFuture in awaitResponse");

      synchronized (responseLock) {

        parent.debug("AsyncFuture in awaitResponse sync block");

        if (getComputationState().equals(ComputationState.STARTED)) {

          parent.setComputationState(ComputationState.WAITING);

          try {
            if (isTimed) {
              responseLock.wait(delayMillis);
            } else {
              responseLock.wait();
            }
          } catch (InterruptedException e) {
            parent.onInterrupt(e);
          }

        }
      }
    }

    /**
     * The method called on the main computation thread. This method obtains the
     * response lock; initiates the external computation via
     * initiateAsynchronousCall; and then releases (waits on) the response lock
     * so the response can be set. If the response is not set the computation
     * thread regains the lock and executes AsynchronousFuture.onFailure()...
     * 
     * @return T the final result value set when
     *         AsynchronousFuture.setResponseValue() is called
     * @exception Exception
     *            if something goes wrong.
     */
    public final T call() throws Exception {

      if (parent == null) {
        throw new Exception(
            "unset Future; AsynchronousCallable must be wrapped by AsynchronousFuture");
      }
      synchronized (responseLock) {

        parent.debug("AsyncFuture called");

        setComputationState(ComputationState.STARTED);

        parent.debug("AsyncFuture set to started");

        (new Thread() {

          public void run() {
            parent.initiateAsynchronousCall();
          }
        }).run();

        awaitResponse();

        parent.debug("AsyncFuture finished awaiting Response");

        if (!getComputationState().equals(ComputationState.SUCCESS)) {
          setComputationState(ComputationState.FAILURE);
          parent.onFailure();
        }
        // new behavior is to allow null response value with no complaint.
        // if (response == null) {
        // use onFailure to customize behavior; for example to
        // trigger another
        // awaitResponse cycle
        // parent.onFailure();
        // }

      }
      return response;
    }

  }
}
