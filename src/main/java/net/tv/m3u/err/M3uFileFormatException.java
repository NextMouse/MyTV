package net.tv.m3u.err;

public class M3uFileFormatException extends Exception {

    private int lineNum;

    private String line;

    private String msg;

    public M3uFileFormatException(String msg) {
        this.msg = msg;
    }

    public M3uFileFormatException(int lineNum, String line, String msg) {
        this.lineNum = lineNum;
        this.line = line;
        this.msg = msg;
    }

    public M3uFileFormatException(String message, int lineNum, String line, String msg) {
        super(message);
        this.lineNum = lineNum;
        this.line = line;
        this.msg = msg;
    }

    public M3uFileFormatException(String message, Throwable cause, int lineNum, String line, String msg) {
        super(message, cause);
        this.lineNum = lineNum;
        this.line = line;
        this.msg = msg;
    }

    public M3uFileFormatException(Throwable cause, int lineNum, String line, String msg) {
        super(cause);
        this.lineNum = lineNum;
        this.line = line;
        this.msg = msg;
    }

    public M3uFileFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int lineNum, String line, String msg) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.lineNum = lineNum;
        this.line = line;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return ": lineNum=" + lineNum +
                ", line='" + line + '\'' +
                ", msg='" + msg;
    }
}
