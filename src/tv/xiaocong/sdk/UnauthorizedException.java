package tv.xiaocong.sdk;

public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int code;

    public UnauthorizedException(int code) {
        super();
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
