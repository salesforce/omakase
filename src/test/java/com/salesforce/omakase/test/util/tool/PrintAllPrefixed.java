package com.salesforce.omakase.test.util.tool;

import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.plugin.basic.Prefixer;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Prints everything supported by {@link Prefixer}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc", "rawtypes"})
public class PrintAllPrefixed {
    private static final Yaml yaml = new Yaml();

    public static void main(String[] args) throws IOException {
        new PrintAllPrefixed().run();
    }

    @SuppressWarnings("unchecked")
    public void run() throws IOException {

        Map types = (Map)yaml.load(Tools.readFile("/data/prefixible.yaml"));
        String header = "%-28s%n";

        // props
        System.out.printf(header, "Property");
        System.out.printf(header, dash(28));
        Map<String, List<String>> properties = (Map)types.get("properties");
        for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
            for (String p : entry.getValue()) {
                Property prop = Property.lookup(p);
                System.out.println(String.format("%-28s", prop));
            }
        }
        System.out.println();

        // functions
        System.out.printf(header, "Function");
        System.out.printf(header, dash(28));
        Map<String, List<String>> functions = (Map)types.get("functions");
        for (Map.Entry<String, List<String>> entry : functions.entrySet()) {
            for (String f : entry.getValue()) {
                System.out.println(String.format("%-28s", f));
            }
        }
        System.out.println();

        // at-rules
        System.out.printf(header, "At Rule");
        System.out.printf(header, dash(28));
        Map<String, List<String>> atRules = (Map)types.get("at-rules");
        for (Map.Entry<String, List<String>> entry : atRules.entrySet()) {
            for (String ar : entry.getValue()) {
                System.out.println(String.format("%-28s", ar));
            }
        }
        System.out.println();

        // selectors
        System.out.printf(header, "Selector");
        System.out.printf(header, dash(28));

        Map<String, List<String>> selectors = (Map)types.get("selectors");
        for (Map.Entry<String, List<String>> entry : selectors.entrySet()) {
            for (String s : entry.getValue()) {
                System.out.println(String.format("%-28s", s));
            }
        }

        System.out.println();
    }

    private String dash(int number) {
        return repeat("-", number);
    }

    private String repeat(String string, int number) {
        return new String(new char[number]).replace("\0", string);
    }
}
