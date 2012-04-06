package nl.astraeus.template;

/**
 * User: rnentjes
 * Date: 4/4/12
 * Time: 4:12 PM
 */
public class ParseException extends RuntimeException {

    private String message;
    private int line;

    public ParseException(String message, int line) {
        super();
        this.message = message;
        this.line = line;
    }

    @Override
    public String getMessage() {
        return "ParseException '" + message + "' in line: "+line;
    }

}
