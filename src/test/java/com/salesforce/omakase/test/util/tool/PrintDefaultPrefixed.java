package com.salesforce.omakase.test.util.tool;

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.data.Property;
import com.salesforce.omakase.plugin.basic.Prefixer;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Prints stuff that's prefixed by {@link Prefixer#defaultBrowserSupport()}.
 *
 * @author nmcwilliams
 */
@SuppressWarnings({"JavaDoc", "rawtypes"})
public class PrintDefaultPrefixed {
    private static final Yaml yaml = new Yaml();

    public static void main(String[] args) throws IOException {
        new PrintDefaultPrefixed().run();
    }

    @SuppressWarnings("unchecked")
    public void run() throws IOException {
        System.out.println("Items automatically prefixed with Prefixer.defaultBrowserSupport:\n");

        SupportMatrix support = Prefixer.defaultBrowserSupport().support();

        Map types = (Map)yaml.load(Tools.readFile("/data/prefixible.yaml"));
        String header = "%-28s   %s%n";

        // props
        System.out.printf(header, "Property", "Prefix");
        System.out.printf(header, dash(28), dash(15));
        Map<String, List<String>> properties = (Map)types.get("properties");
        for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
            for (String p : entry.getValue()) {
                Property prop = Property.lookup(p);

                for (Prefix prefix : Prefix.values()) {
                    if (support.requiresPrefixForProperty(prefix, prop)) {
                        System.out.println(String.format("%-28s   %s", prop, prefix.name().toLowerCase()));
                    }
                }
            }
        }
        System.out.println();

        // functions
        System.out.printf(header, "Function", "Prefix");
        System.out.printf(header, dash(28), dash(15));
        Map<String, List<String>> functions = (Map)types.get("functions");
        for (Map.Entry<String, List<String>> entry : functions.entrySet()) {
            for (String f : entry.getValue()) {

                for (Prefix prefix : Prefix.values()) {
                    if (support.requiresPrefixForFunction(prefix, f)) {
                        System.out.println(String.format("%-28s   %s", f, prefix.name().toLowerCase()));
                    }
                }
            }
        }
        System.out.println();

        // at-rules
        System.out.printf(header, "At Rule", "Prefix");
        System.out.printf(header, dash(28), dash(15));
        Map<String, List<String>> atRules = (Map)types.get("at-rules");
        for (Map.Entry<String, List<String>> entry : atRules.entrySet()) {
            for (String ar : entry.getValue()) {

                for (Prefix prefix : Prefix.values()) {
                    if (support.requiresPrefixForAtRule(prefix, ar)) {
                        System.out.println(String.format("%-28s   %s", ar, prefix.name().toLowerCase()));
                    }
                }
            }
        }
        System.out.println();

        // selectors
        System.out.printf(header, "Selector", "Prefix");
        System.out.printf(header, dash(28), dash(15));
        Map<String, List<String>> selectors = (Map)types.get("selectors");
        for (Map.Entry<String, List<String>> entry : selectors.entrySet()) {
            for (String s : entry.getValue()) {

                for (Prefix prefix : Prefix.values()) {
                    if (support.requiresPrefixForSelector(prefix, s)) {
                        System.out.println(String.format("%-28s   %s", s, prefix.name().toLowerCase()));
                    }
                }
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
