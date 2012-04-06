package nl.astraeus.template;

/**
 * User: rnentjes
 * Date: 4/4/12
 * Time: 5:11 PM
 */
public class TemplateToken {

    private TokenType type;
    private String value;
    private int line;

    public TemplateToken(TokenType type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line = line;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }
}
