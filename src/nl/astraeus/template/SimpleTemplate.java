package nl.astraeus.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * User: rnentjes
 * Date: 4/4/12
 * Time: 3:52 PM
 */
public class SimpleTemplate {

    private char separatorChar;
    private List<TemplatePart> parts = new ArrayList<TemplatePart>();

    public SimpleTemplate(String template) {
        this('#', template);
    }

    public SimpleTemplate(char separator, String template) {
        this.separatorChar = separator;

        parseTemplate(template);
    }

    public SimpleTemplate(InputStream input) throws IOException {
        this('#', input);
    }

    public SimpleTemplate(char separator, InputStream input) throws IOException {
        this.separatorChar = separator;

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder buffer = new StringBuilder();

        while(reader.ready()) {
            buffer.append(reader.readLine());
            buffer.append("\n");
        }

        parseTemplate(buffer.toString());
    }

    public String render(Map<String, Object> model) {
        StringBuilder result = new StringBuilder();

        for (TemplatePart part : parts) {
            result.append(part.render(model));
        }

        return result.toString();
    }

    public List<TemplatePart> getParts() {
        return parts;
    }

    private void parseTemplate(String template) {
        TemplateTokenizer tokenizer = new TemplateTokenizer(separatorChar, template);

        List<TemplateToken> tokens = tokenizer.getTokens();
        Stack<List<TemplatePart>> stack = new Stack<List<TemplatePart>>();
        stack.push(new ArrayList<TemplatePart>());
        Stack<IfPart> currentIfPart = new Stack<IfPart>();
        Stack<ForEachPart> currentForEach = new Stack<ForEachPart>();

        for (TemplateToken token : tokens) {
            switch(token.getType()) {
                case STRING:
                    stack.peek().add(new StringPart(token.getValue()));
                    break;
                case VALUE:
                    stack.peek().add(new ValuePart(token.getValue()));
                    break;
                case PLAINVALUE:
                    stack.peek().add(new PlainValuePart(token.getValue()));
                    break;
                case IF:
                    stack.push(new ArrayList<TemplatePart>());
                    currentIfPart.push(new IfPart(getParameterFromCommand(token.getValue())));
                    break;
                case IFNOT:
                    stack.push(new ArrayList<TemplatePart>());
                    currentIfPart.push(new IfNotPart(getParameterFromCommand(token.getValue())));
                    break;
                case ELSE:
                    currentIfPart.peek().setIfParts(stack.pop());
                    currentIfPart.peek().setHasElse(true);
                    stack.push(new ArrayList<TemplatePart>());
                    break;
                case ENDIF:
                    if (currentIfPart.peek().isHasElse()) {
                        currentIfPart.peek().setElseParts(stack.pop());
                    } else {
                        currentIfPart.peek().setIfParts(stack.pop());
                    }
                    stack.peek().add(currentIfPart.pop());
                    break;
                case EACH:
                    stack.push(new ArrayList<TemplatePart>());
                    String [] parts = getParameterFromCommand(token.getValue()).split(" as ");
                    currentForEach.push(new ForEachPart(parts[0], parts[1]));
                    break;
                case EACHALT:
                    currentForEach.peek().setHasAlt(true);
                    currentForEach.peek().setParts(stack.pop());
                    stack.push(new ArrayList<TemplatePart>());
                    break;
                case EACHLAST:
                    currentForEach.peek().setHasLast(true);
                    if (currentForEach.peek().isHasAlt()) {
                        currentForEach.peek().setAltParts(stack.pop());
                    } else {
                        currentForEach.peek().setParts(stack.pop());
                    }
                    stack.push(new ArrayList<TemplatePart>());
                    break;
                case EACHEND:
                    if (currentForEach.peek().isHasLast()) {
                        currentForEach.peek().setLastParts(stack.pop());
                    } else if (currentForEach.peek().isHasAlt()) {
                        currentForEach.peek().setAltParts(stack.pop());
                    } else {
                        currentForEach.peek().setParts(stack.pop());
                    }
                    stack.peek().add(currentForEach.pop());
                    break;
            }
        }

        parts = stack.pop();

        assert parts.size() == 0;
    }

    private String getParameterFromCommand(String command) {
        String [] parts = command.split("\\(");

        assert parts.length > 1;

        String [] parts2 = parts[1].split("\\)");

        assert parts2.length > 0;

        return parts2[0];
    }

    public static void main(String [] args) throws IOException {
        InputStream in = SimpleTemplate.class.getResourceAsStream("testtemplate.html");

        SimpleTemplate template = new SimpleTemplate(in);

        in.close();

        Map<String, Object> model = new HashMap<String, Object>();
        List<String> list = new ArrayList<String>();
        list.add("test1");
        list.add("test2");
        list.add("test3");
        list.add("test4");
        list.add("test5");
        list.add("test6");
        list.add("test7");

        model.put("pipo", "Mamaloe");
        model.put("test", template);
        model.put("list", list);

        System.out.println(template.render(model));
    }

}
