package exceptions;

/**
 * Mostly acts as a wrapper around {@code org.json.JSONException}.
 * <p>
 * This was a Checked Exception, however, due to how often this was thrown I decided to make it a Runtime Exception instead
 * so the user doesn't have to keep using try/catch blocks.
 */
public class ParsingException extends RuntimeException  {

    public ParsingException(){
        super();
    }

    public ParsingException(String message) {
        super(message);
    }

    public ParsingException(Throwable cause) {
        super(cause);
    }

    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }

}
