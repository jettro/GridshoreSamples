package nl.gridshore.eswp.wordpress;

/**
 * Exception used to indicate problems while importing data from wordpress.
 */
public class BlogImportException extends RuntimeException {
    public BlogImportException(String message) {
        super(message);
    }

    public BlogImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
