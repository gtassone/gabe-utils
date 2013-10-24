package com.gmail.gtassone.util.chain;

/**
 * A ChainHandler is a worker in a Chain of Responsibility implementation,
 * managed by a {@link ChainManager}.
 * 
 * @param <M>
 *        the type of ChainMessage handled by this Handler.
 * @author <a href=mailto:gtassone@gmail.com>gtassone</a>
 * @version $Revision$
 */
public interface ChainHandler<M extends ChainMessage> {

  /**
   * Enum of possible results for {@link #execute} method.
   * 
   * @author <a href=mailto:gtassone@gmail.com>gtassone</a>
   * @version $Revision$
   */
  public enum ChainHandlerResult {

    /**
     * indicates the ChainMessage should be passed along the chain to the next
     * ChainHandler.
     */
    PASS,

    /**
     * indicates the ChainHandler has handled the message, and further
     * processing should not occur. This should be used sparingly.
     */
    HANDLED,

    /**
     * indicates that something went wrong when processing the message, custom
     * ChainControllers may decide what to do in this situation in the
     * {@link ChainController#handleReportedFailure(ChainMessage)} hook. The
     * default implementation stops the Chain from further processing.
     */
    FAILED,

    /**
     * indicates that the request depends on another request in order to
     * continue processing. the current chain thread is terminated, but the
     * request remains active and will be reactivated on response of the
     * sub-request.
     */
    WAIT;
  }

  /**
   * @param message
   *        the message to handle.
   * @return the result of the handling.
   */
  ChainHandlerResult execute(M message) throws Exception;

  /**
   * ChainHandlers often need to invoke the ChainManager in various ways, to
   * trigger new requests, or to submit results, or other stuff.
   * 
   * @param manager
   *        the manager
   */
  void setChainManager(ChainManager<M, ? extends ChainController<M>> manager);

  /**
   * convenience base implementation.
   * 
   * @author <a href=mailto:gtassone@cougaarsoftware.com>gtassone</a>
   * @version $Revision$
   */
  public abstract class Adapter<M extends ChainMessage> implements
      ChainHandler<M> {

    private ChainManager<M, ? extends ChainController<M>> manager;

    /**
     * @param mgr
     *        the manager
     */
    public final void setChainManager(
        ChainManager<M, ? extends ChainController<M>> mgr) {
      this.manager = mgr;
    }

    /**
     * @return the manager.
     */
    public final ChainManager<M, ? extends ChainController<M>> getChainManager() {
      return this.manager;
    }
  }
}
