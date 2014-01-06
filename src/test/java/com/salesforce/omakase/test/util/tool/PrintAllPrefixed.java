package com.salesforce.omakase.test.util.tool;

import com.google.common.collect.Sets;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.plugin.basic.Prefixer;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        Set<String> props = Sets.newTreeSet();
        for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
            props.addAll(entry.getValue());
        }
        for (String p : props) {
            Property prop = Property.lookup(p);
            System.out.println(String.format("%-28s", prop));
        }
        System.out.println();

        // functions
        System.out.printf(header, "Function");
        System.out.printf(header, dash(28));
        Map<String, List<String>> functions = (Map)types.get("functions");
        Set<String> funcs = Sets.newTreeSet();
        for (Map.Entry<String, List<String>> entry : functions.entrySet()) {
            funcs.addAll(entry.getValue());
        }
        for (String f : funcs) {
            System.out.println(String.format("%-28s", f));
        }
        System.out.println();

        // at-rules
        System.out.printf(header, "At Rule");
        System.out.printf(header, dash(28));
        Map<String, List<String>> atRules = (Map)types.get("at-rules");
        Set<String> ars = Sets.newTreeSet();
        for (Map.Entry<String, List<String>> entry : atRules.entrySet()) {
            ars.addAll(entry.getValue());
        }
        for (String ar : ars) {
            System.out.println(String.format("%-28s", ar));
        }

        System.out.println();

        // selectors
        System.out.printf(header, "Selector");
        System.out.printf(header, dash(28));
        Map<String, List<String>> selectors = (Map)types.get("selectors");
        Set<String> sels = Sets.newTreeSet();
        for (Map.Entry<String, List<String>> entry : selectors.entrySet()) {
            sels.addAll(entry.getValue());
        }
        for (String s : sels) {
            System.out.println(String.format("%-28s", s));
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
