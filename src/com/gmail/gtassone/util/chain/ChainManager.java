package com.gmail.gtassone.util.chain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.gmail.gtassone.util.concurrent.MonitorDispatchThread;
import com.gmail.gtassone.util.concurrent.MonitorDispatchThread.MonitoredAction;
import com.gmail.gtassone.util.chain.ChainHandler.ChainHandlerResult;

/**
 * Base class for Chain of Responsibility pattern. This implementation improves
 * on the canonical C-of-R pattern by using a ChainManager to invoke each
 * successive ChainHandler, rather than each Handler invoking its successor
 * directly. The ChainManager is meant to manage a single long-lived Chain of
 * ChainHandlers.
 * <p>
 * Each ChainMessage submitted to the ChainManager gets a dedicated thread, on
 * which each of the ChainHandlers are invoked in succession. Each ChainHandler
 * returns {@link ChainHandler$ChainHandlerResult.HANDLED} if the message was
 * 'handled', indicating that no further handling should be performed.
 * {@link ChainHandler$ChainHandlerResult.PASS} indicates that the ChainManager
 * should pass control on to the next ChainHandler in the chain.
 * <p>
 * ChainManager implementations can provide a submitResult(ChainMessage) method.
 * This allows data results to be provided by ChainHandlers and then managed by
 * the ChainManager implementation. Different approaches might be to implement
 * Request/Response, callbacks, or flow control via this mechanism. For
 * instance, an implementation might have all Handlers return false from
 * execute(), and let the ChainManager implementation decide when to terminate
 * the chain thread based on result() data.
 * 
 * @param <M>
 *        message type
 * @param <C>
 *        controller type.
 * @author <a href=mailto:gtassone@gmail.com>gtassone</a>
 * @version $Revision$
 */
public abstract class ChainManager<M extends ChainMessage, C extends ChainController<M>> {

  /**
   * Timeout for ChainThreads used by the manager.
   */

  private final Executor executor;

  private final List<ChainHandler<M>> handlerList;

  private final ChainController<M> controller;

  private final boolean defaultController;

  private final Map<ChainMessage, ChainThread> messageThreadMap = new ConcurrentHashMap<ChainMessage, ChainThread>();

  /**
   * Constructor sets the list of ChainHandlers. Uses default
   * {@link ChainController}.
   * 
   * @param handlers
   *        The handlers in this chain. Cannot have duplicate entries.
   */
  public ChainManager(List<? extends ChainHandler<M>> handlers) {
    if (handlerListHasDuplicates(handlers)) {
      throw new IllegalArgumentException(
          "ChainManager cannot be initialized with a list of ChainHandlers which contains duplicates.");
    }

    defaultController = true;
    controller = new ChainController.Adapter<M>(this) {
    };
    handlerList = new ArrayList<ChainHandler<M>>(handlers);
    executor = Executors.newCachedThreadPool(new ChainThreadFactory());
  }

  /**
   * Constructor sets the list of ChainHandlers and the ChainController.
   * 
   * @param handlers
   *        The handlers in this chain. Cannot have duplicate entries.
   * @param chainController
   *        The ChainController for this chain. If null, the default controller
   *        will be used.
   */
  public ChainManager(List<? extends ChainHandler<M>> handlers,
      C chainController) {
    if (handlerListHasDuplicates(handlers)) {
      throw new IllegalArgumentException(
          "ChainManager cannot be initialized with a list of ChainHandlers which contains duplicates.");
    }

    if (chainController != null) {
      defaultController = false;
      controller = chainController;
    } else {
      defaultController = true;
      controller = new ChainController.Adapter<M>(this) {
      };
    }

    handlerList = new ArrayList<ChainHandler<M>>(handlers);
    executor = Executors.newCachedThreadPool(new ChainThreadFactory());
  }

  /**
   * Constructor sets the list of ChainHandlers. Uses default
   * {@link ChainController}.
   * 
   * @param handlers
   *        The handlers in this chain. Cannot have duplicate entries.
   * @param exec
   *        A custom executor that will be used for message processing.
   */
  public ChainManager(List<? extends ChainHandler<M>> handlers, Executor exec) {
    if (handlerListHasDuplicates(handlers)) {
      throw new IllegalArgumentException(
          "ChainManager cannot be initialized with a list of ChainHandlers which contains duplicates.");
    }

    defaultController = true;
    controller = new ChainController.Adapter<M>(this) {
    };
    handlerList = new ArrayList<ChainHandler<M>>(handlers);

    if (exec != null) {
      executor = exec;
    } else {
      executor = Executors.newCachedThreadPool(new ChainThreadFactory());
    }
  }

  /**
   * Constructor sets the list of ChainHandlers and the ChainController.
   * 
   * @param handlers
   *        The handlers in this chain. Cannot have duplicate entries.
   * @param chainController
   *        The ChainController for this chain. If null, the default controller
   *        will be used.
   * @param exec
   *        A custom executor that will be used for message processing.
   */
  public ChainManager(List<? extends ChainHandler<M>> handlers,
      C chainController, Executor exec) {
    if (handlerListHasDuplicates(handlers)) {
      throw new IllegalArgumentException(
          "ChainManager cannot be initialized with a list of ChainHandlers which contains duplicates.");
    }

    if (chainController != null) {
      defaultController = false;
      controller = chainController;
    } else {
      defaultController = true;
      controller = new ChainController.Adapter<M>(this) {
      };
    }

    handlerList = new ArrayList<ChainHandler<M>>(handlers);

    if (exec != null) {
      executor = exec;
    } else {
      executor = Executors.newCachedThreadPool(new ChainThreadFactory());
    }
  }

  /**
   * @return The time alloted for each handler to process a message.
   */
  public abstract int getHandlerTimeout();

  /**
   * @param toCheck
   *        The list of chain handlers to check.
   * @return true if the given list contains duplicates.
   */
  private boolean handlerListHasDuplicates(
      List<? extends ChainHandler<M>> toCheck) {
    Set<ChainHandler<M>> set = new HashSet<ChainHandler<M>>(toCheck);
    return toCheck.size() != set.size();
  }

  /**
   * @return the controller
   */
  @SuppressWarnings("unchecked")
  public final C getController() {
    if (defaultController) {
      return null;
    }

    return (C) this.controller;
  }

  /**
   * The main execution method to call for each ChainMessage. This method starts
   * a new MessageChainThread to handle the submitted message.
   * 
   * @param msg
   *        the message to handle.
   */
  public final void handleChainMessage(M msg) {
    InvokeHandlerTask invokeHandlerTask = new InvokeHandlerTask(msg);
    ChainThread msgThread = new ChainThread(invokeHandlerTask);
    messageThreadMap.put(msg, msgThread);
    controller.start(msg);
    executor.execute(msgThread);
  }

  /**
   * Ends the processing of the given message.
   * 
   * @param msg
   *        The message.
   */
  public final void terminateMessageHandling(M msg) {
    ChainThread thread = messageThreadMap.get(msg);
    if (thread == null) {
      throw new NullPointerException(
          "Could not find thread responsible for handling given message.");
    }

    thread.terminateAfterThisTask();
  }

  /**
   * Interrupts the processing of the given message.
   * 
   * @see java.lang.Thread#interrupt()
   * @param msg
   *        The message.
   */
  public final void interruptMessageHandling(M msg) {
    ChainThread thread = messageThreadMap.get(msg);
    if (thread == null) {
      throw new NullPointerException(
          "Could not find thread responsible for handling given message.");
    }

    thread.interruptMonitoredTask();
  }

  /**
   * Sets the given message's handled flag to the given value.
   * 
   * @param msg
   *        The message.
   * @param isHandled
   *        How the set the message's handled flag.
   */
  public final void setMessageHandled(M msg, boolean isHandled) {
    ChainThread thread = messageThreadMap.get(msg);
    if (thread == null) {
      throw new NullPointerException(
          "Could not find thread responsible for handling given message.");
    }

    thread.setMessageHandled(isHandled);
  }

  /**
   * This method hook provides Handlers a direct means of indicating feedback
   * data for a submitted message. Since this architecture is not meant to
   * represent explicit Request/Response paradigm, a ChainManager subclass could
   * implement Request/Response directly by wrapping
   * {@link #handleChainMessage(ChainMessage)} with a custom Chain invocation
   * method, therein blocking or maintaining a Callback to the client.
   * ChainHandlers with a handle to the ChainManager could then provide
   * Responses via ChainManager.result(). The result() implementation could also
   * potentially cancel the chain thread, although the handler should cancel the
   * thread by returning true.
   * 
   * @param msg
   *        The result message. This may be the original message, updated in
   *        some way, or a different message, generated by a Handler.
   */
  public final void submitResult(M msg) {
    controller.submitResult(msg, messageThreadMap.get(msg).getCurrentObject());
  }

  /**
   * Thread Factory for executor that acts similar to
   * {@link Executors#defaultThreadFactory()} but uses more meaningful names.
   * 
   * @author <a href=mailto:vkoriabine@cougaarsoftware.com>vkoriabine</a>
   * @version $Revision$
   */
  private static class ChainThreadFactory implements ThreadFactory {

    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

    private final ThreadGroup group;

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final String namePrefix;

    ChainThreadFactory() {
      SecurityManager localSecurityManager = System.getSecurityManager();
      this.group = ((localSecurityManager != null) ? localSecurityManager
          .getThreadGroup() : Thread.currentThread().getThreadGroup());
      this.namePrefix = "pool-" + POOL_NUMBER.getAndIncrement()
          + "-ChainThread-";
    }

    public Thread newThread(Runnable paramRunnable) {
      Thread localThread = new Thread(this.group, paramRunnable,
          this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
      if (localThread.isDaemon()) {
        localThread.setDaemon(false);
      }
      if (localThread.getPriority() != Thread.NORM_PRIORITY) {
        localThread.setPriority(Thread.NORM_PRIORITY);
      }
      return localThread;
    }

  }

  /**
   * MonitoredDispatchThread MonitoredAction implementation which operates on
   * the ChainHandler list by invoking each one in turn.
   * 
   * @author <a href=mailto:gtassone@cougaarsoftware.com>gtassone</a>
   * @version $Revision$
   */
  private class InvokeHandlerTask implements MonitoredAction<ChainHandler<M>> {

    private M message;

    private ChainThread thread;

    InvokeHandlerTask(M message) {
      this.message = message;
    }

    private void setThread(ChainThread thread) {
      this.thread = thread;
    }

    @Override
    public void execute(ChainHandler<M> nextHandler)
        throws Exception {

      ChainHandlerResult result = nextHandler.execute(message);
      switch (result) {
        case HANDLED:
          if (!controller.handleHandledMessage(message)) {
            thread.isMessageHandled = true;
            thread.terminateAfterThisTask();
          }
          break;
        case FAILED:
          if (!controller.handleReportedFailure(message, nextHandler)) {
            thread.terminateAfterThisTask();
          }
          break;
        case WAIT:
          thread.isMessageWaiting = true;
          thread.terminateAfterThisTask();
          break;
        case PASS:
          // handler returned PASS
          // pass control to the next Handler with no complaints
          break;
        default:
          // handler didn't behave. complain and then treat like PASS
          System.err
              .println("ChainHandler did not respond to execute in a valid format! Treating response like PASS.");

      }
    }
  }

  /**
   * MonitoredDispatchThread implementation which calls the chain handlers in
   * order, and monitors their execution.
   * 
   * @author <a href=mailto:gtassone@cougaarsoftware.com>gtassone</a>
   * @version $Revision$
   */
  public class ChainThread extends
      MonitorDispatchThread.Adapter<ChainHandler<M>> {

    private boolean isMessageHandled = false;

    private boolean isMessageWaiting = false;

    private InvokeHandlerTask task;

    /**
     * True if a ChainHandler has returned ChainHandlerResult.HANDLED.
     * 
     * @return True if a ChainHandler has returned true for this Thread's
     *         ChainMessage.
     */
    public final boolean isMessageHandled() {
      return isMessageHandled;
    }

    /**
     * @param isHandled
     *        is it or not.
     */
    public final void setMessageHandled(boolean isHandled) {
      this.isMessageHandled = isHandled;
    }

    /**
     * True if a ChainHandler has returned ChainHandlerResult.WAIT.
     * 
     * @return True if a ChainHandler has returned true for this Thread's
     *         ChainMessage.
     */
    public final boolean isMessageWaiting() {
      return isMessageWaiting;
    }

    /**
     * @param isWaiting
     *        is it or not.
     */
    public final void setMessageWaiting(boolean isWaiting) {
      this.isMessageWaiting = isWaiting;
    }

    /**
     * Constructor creates a new MessageChainThread for a ChainMessage and
     * InvokeHandlerTask. The InvokeHandlerTask marks the thread
     * {@link #isMessageHandled} field to true when a Handler.execute method
     * returns true.
     * 
     * @param t
     *        the handler task.
     */
    public ChainThread(InvokeHandlerTask t) {
      super(handlerList, t);
      task = t;
      t.setThread(this);
      this.setTimeout(getHandlerTimeout());
      this.shouldJoinDispatch(true);
    }

    /**
     * Alternative Constructor with a provided list of ChainHandlers, useful
     * when invoking a new ChainMessage that should be handled by a sublist of
     * the full Handler list.
     * 
     * @param list
     *        custom handler list
     * @param t
     *        the handler task
     */
    public ChainThread(List<ChainHandler<M>> list, InvokeHandlerTask t) {
      super(list, t);
      task = t;
      t.setThread(this);
      this.setTimeout(getHandlerTimeout());
      this.shouldJoinDispatch(true);
    }

    @Override
    public final void handleTimeout() {
      controller.handleTimeout(task.message, getCurrentObject());
    }

    @Override
    public final void handleInterruptedTask(InterruptedException e) {
      controller.handleInterruptedTask(e, task.message, getCurrentObject());
    }

    @Override
    public final void handleTaskCompleted() {
      controller.handleTaskCompleted(task.message, getCurrentObject());
    }

    @Override
    public final void handleMonitorException(Exception e) {
      controller.handleMonitorException(e, task.message, getCurrentObject());
    }

    @Override
    public final void finish() {
      controller.finish(task.message, getCurrentObject());
      if (!isMessageHandled && !isMessageWaiting) {
        controller.handleUnhandledMessage(task.message);
      }

      messageThreadMap.remove(task.message);
    }

    @Override
    public final void handleNullObject() {
      controller.handleNullObject(task.message);
    }

    @Override
    public final void handleStartTask(ChainHandler<M> nextObj) {
      controller.handleStartTask(task.message, getCurrentObject());
    }

    @Override
    public final void handleDispatchException(Exception e) {
      controller.handleDispatchException(e, task.message, getCurrentObject());
    }
  }
}
