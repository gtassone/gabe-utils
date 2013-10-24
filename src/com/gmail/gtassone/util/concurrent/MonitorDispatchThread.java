package com.gmail.gtassone.util.concurrent;

import java.util.Collection;
import java.util.Iterator;

/**
 * This class performs an action on a Collection of objects in sequence on a
 * Thread, allowing a set time interval for each execution. Multiple hooks are
 * provided to enable logging, metrics data collection, or customized behavior
 * at each step of execution. The default timeout is 10 seconds.
 * <p>
 * Client should implement a MonitoredTask subclass which performs the desired
 * action for the object collection. Also extend MonitorDispatchThread or
 * {@link MonitorDispatchThread$Adapter} to implement the desired abstract
 * methods. The execution may be started via Thread.start() or via Executor.
 * 
 * @param <T>
 *            The type of objects to perform action on.
 * @author gabriel
 */
public abstract class MonitorDispatchThread<T> extends Thread {

	/**
	 * Utility adapter class for MonitorDispatchThread implementations which
	 * don't wish to implement every method.
	 * 
	 * @param <T>
	 *            The type of objects to perform action on.
	 * @author gabriel
	 */
	public static class Adapter<T> extends MonitorDispatchThread<T> {

		/**
		 * Adapter Class so concrete subclasses needn't define unused abstract
		 * methods.
		 * 
		 * @param coll
		 *            Collection of objects over which to iterate and perform
		 *            action.
		 * @param monitoredTask
		 *            Action to be performed to each object in the collection.
		 */
		public Adapter(Collection<T> coll, MonitoredAction<T> monitoredTask) {
			super(coll, monitoredTask);
		}

		@Override
		public void handleTimeout() {
		}

		@Override
		public void handleInterruptedTask(InterruptedException e) {
		}

		@Override
		public void handleTaskCompleted() {
		}

		@Override
		public void handleMonitorException(Exception e) {
		}

		@Override
		public void finish() {
		}

		@Override
		public void handleNullObject() {
		}

		@Override
		public void handleStartTask(T nextObj) {
		}

		@Override
		public void handleDispatchException(Exception e) {
		}

	}

	/**
	 * Human-readable execution state. The shared state is the basis for
	 * locking.
	 * 
	 * @author <a href=mailto:gtassone@cougaarsoftware.com>gtassone</a>
	 * @version $Revision$
	 */
	public static enum ExecutionState {
		/**
		 * No task is currently being carried out.
		 */
		NO_TASK,
		/**
		 * The next task has been assigned but has not been started yet.
		 */
		NEXT_TASK_ASSIGNED,
		/**
		 * A task is currently being executed.
		 */
		EXECUTING_TASK,
		/**
		 * The current task has finished execution.
		 */
		FINISHED_TASK,
		/**
		 * The current task was interrupted.
		 */
		INTERRUPTED_TASK,
		/**
		 * Execution timed out.
		 */
		TIMED_OUT;
	}

	/**
	 * The default value to be used for thread timeouts.
	 */
	public static final int DEFAULT_TIMEOUT = 10000;

	/**
	 * Client-defined execution block.
	 * 
	 * @author <a href=mailto:gtassone@cougaarsoftware.com>gtassone</a>
	 * @version $Revision$
	 */
	public static interface MonitoredAction<T> {

		/**
		 * Execute action on given object.
		 * 
		 * @param nextObj
		 *            Object to execute action on.
		 * @throws InterruptedException
		 *             if action is interrupted.
		 */
		void execute(T nextObj) throws Exception;

	}

	/**
	 * Various actions can be performed on a timeout, including interrupting the
	 * entire Thread, interrupting the long-running class, terminating after the
	 * current long-running task, or custom behavior.
	 */
	public abstract void handleTimeout();

	/**
	 * This hook is called when the current Task is interrupted by
	 * {@link #interruptMonitoredTask}.
	 * 
	 * @param e
	 *            Exception that is the product of the task interruption.
	 */
	public abstract void handleInterruptedTask(InterruptedException e);

	/**
	 * This hook is called when the next object is ready to be processed.
	 * 
	 * @param nextObj
	 *            The next object to be processed.
	 */
	public abstract void handleStartTask(T nextObj);

	/**
	 * This hook is called when an object is successfully processed by the
	 * MonitoredTask.
	 */
	public abstract void handleTaskCompleted();

	/**
	 * This hook is called when the MonitorThread is interrupted, or another
	 * exception occurs, for instance during a hook execution.
	 * 
	 * @param e
	 *            Exception that is the cause of MonitorThread's interruption.
	 */
	public abstract void handleMonitorException(Exception e);

	/**
	 * This hook is called when the Task Dispatch Thread throws an exception,
	 * for instance when the MonitoredTask implementation throws an exception.
	 * 
	 * @param e
	 *            Exception thrown by Task Dispatch Thread.
	 */
	public abstract void handleDispatchException(Exception e);

	/**
	 * This hook is called right before exit from the MonitorThread.
	 */
	public abstract void finish();

	/**
	 * This hook is called when the MonitorThread tries to process a null value
	 * in the client Collection.
	 */
	public abstract void handleNullObject();

	private MonitoredAction<T> monitoredTask;

	private DispatchThread dispatcher;

	private Collection<T> managedObjects;

	private int timeout = DEFAULT_TIMEOUT;

	private boolean shouldJoinDispatch = false;

	private SharedState<T> sharedState;

	/**
	 * Creates a thread that will perform a given task on each element of the
	 * given collection in iterator order.
	 * 
	 * @param coll
	 *            The collection to perform task on.
	 * @param monitoredTask
	 *            The task to perform on the collection.
	 */
	public MonitorDispatchThread(Collection<T> coll,
			MonitoredAction<T> monitoredTask) {

		this.managedObjects = coll;
		this.monitoredTask = monitoredTask;
		sharedState = new SharedState<T>();
		dispatcher = new DispatchThread();
	}

	/**
	 * Flag for the Monitor thread to join on the dispatch thread when finished
	 * pushing task objects. probably doesn't matter because it will still
	 * perform timing on the last task. But if it doesn't interrupt the last
	 * task, it would matter.
	 * 
	 * @param should
	 *            yes or no
	 */
	public void shouldJoinDispatch(boolean should) {
		shouldJoinDispatch = should;
	}

	/**
	 * Set the timeout for each MonitoredTask execution.
	 * 
	 * @param timeout
	 *            milliseconds.
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * This interrupts the currently processing MonitoredTask execution.
	 */
	public void interruptMonitoredTask() {
		dispatcher.interrupt();
	}

	/**
	 * This sets the internal terminate flag. The current MonitoredTask
	 * execution will continue. This method provides a way to finish the
	 * remainder of the Collection in a different thread, without cancelling the
	 * long-running request.
	 */
	public void terminateAfterThisTask() {
		sharedState.requestTerminate();
	}

	/**
	 * This method returns the currently set object. It is most useful to
	 * determine which object was being processed when the thread was cancelled.
	 * 
	 * @return current object.
	 */
	public T getCurrentObject() {
		return sharedState.getCurrentObject();
	}

	/**
	 * the action being performed.
	 * 
	 * @return the action being performed.
	 */
	public MonitoredAction<T> getMonitoredAction() {
		return monitoredTask;
	}

	/**
	 * the set of objects being processed.
	 * 
	 * @return the set of objects being processed.
	 */
	public Collection<T> getManagedObjects() {
		return managedObjects;
	}

	/**
	 * The current ExecutionState.
	 * 
	 * @return current ExecutionState.
	 */
	public ExecutionState getExecutionState() {
		return sharedState.getExecutionState();
	}

	/**
	 * Functions as a mutex with additional synchronized state information for
	 * the two threads. The two threads share state and data information through
	 * this object, and synchronize on its monitor.
	 * 
	 * @author <a href=mailto:gtassone@cougaarsoftware.com>gtassone</a>
	 * @version $Revision$
	 */
	private static class SharedState<T> {

		private ExecutionState state = ExecutionState.NO_TASK;

		private boolean done = false;

		private T currentObject;

		public synchronized void pushTask(T t) {
			currentObject = t;
			state = ExecutionState.NEXT_TASK_ASSIGNED;
			notify();
		}

		public synchronized void finishTask() {
			state = ExecutionState.FINISHED_TASK;
			notify();
		}

		public synchronized void interruptedTask() {
			state = ExecutionState.INTERRUPTED_TASK;
			notify();
		}

		public synchronized void timedOut() {
			state = ExecutionState.TIMED_OUT;
			notify();
		}

		public synchronized boolean isDone() {
			return done;
		}

		public synchronized T getNextObject() {
			state = ExecutionState.EXECUTING_TASK;
			return currentObject;
		}

		public synchronized T getCurrentObject() {
			return currentObject;
		}

		public synchronized ExecutionState getExecutionState() {
			return state;
		}

		public synchronized void requestTerminate() {
			done = true;
			notify();
		}

	}

	@Override
	public final void run() {

		try {
			dispatcher.start();
			Iterator<T> objIter = managedObjects.iterator();

			while (objIter.hasNext() && !sharedState.isDone()) {

				// when the second thread is handling an interrupt we should be
				// blocking.
				synchronized (sharedState) {
					while (ExecutionState.INTERRUPTED_TASK == sharedState
							.getExecutionState()
							|| ExecutionState.TIMED_OUT == sharedState
									.getExecutionState()) {
						sharedState.wait();
					}
				}

				T nextObject = objIter.next();

				sharedState.pushTask(nextObject);

				handleStartTask(nextObject);

				synchronized (sharedState) {
					long startTime = System.currentTimeMillis();
					while (ExecutionState.FINISHED_TASK != sharedState
							.getExecutionState()) {
						long elapsed = System.currentTimeMillis() - startTime;
						if (elapsed < timeout) {
							long remaining = timeout - elapsed;

							sharedState.wait(remaining);

							if (ExecutionState.INTERRUPTED_TASK == sharedState
									.getExecutionState()) {
								break;
							}
						} else {
							sharedState.timedOut();
							handleTimeout();
							break;
						}
					} // end while !ExecutionState.FINISHED_TASK
				} // end synchronized (sharedState)
			} // end while

			sharedState.requestTerminate();
			if (shouldJoinDispatch) {
				dispatcher.join();
			}

		} catch (Exception e) {
			// hook for logging or custom behavior.
			sharedState.requestTerminate();
			dispatcher.interrupt();
			handleMonitorException(e);

		} finally {
			finish();
		}
	}

	/**
	 * Execution thread which waits for the next Object to process, then
	 * processes it with the client MonitoredTask.
	 * 
	 * @author gabriel
	 */
	public class DispatchThread extends Thread {

		@Override
		public final void run() {

			while (true) {

				T nextObj = null;

				synchronized (sharedState) {
					try {

						while (ExecutionState.NEXT_TASK_ASSIGNED != sharedState
								.getExecutionState() && !sharedState.isDone()) {
							sharedState.wait();
						}

						if (sharedState.isDone()) {
							break;
						}

						nextObj = sharedState.getNextObject();

					} catch (InterruptedException e) {
						// this shouldn't happen
						e.printStackTrace(System.out);
					} finally {
						sharedState.notify();
					}
				}

				if (nextObj != null) {
					try {
						monitoredTask.execute(nextObj);
						handleTaskCompleted();

					} catch (InterruptedException e) {
						sharedState.interruptedTask();
						handleInterruptedTask(e);

					} catch (Exception e) {
						handleDispatchException(e);

					} finally {

						sharedState.finishTask();
					}
				} else {
					handleNullObject();
					sharedState.finishTask();
				}
			}
		}
	}

}
