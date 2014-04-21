package tv.xiaocong.sdk;

/**
 * Communication errors between clients and XIAOCONG server.
 * 
 * @author yaoyuan
 * 
 */
public class RemoteServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RemoteServiceException() {
        super();
    }

    public RemoteServiceException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public RemoteServiceException(String detailMessage) {
        super(detailMessage);
    }

    public RemoteServiceException(Throwable throwable) {
        super(throwable);
    }

}
