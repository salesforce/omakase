/**
 * ADD LICENSE
 */
package com.salesforce.omakase.writer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.ast.Writeable;
import com.salesforce.omakase.plugin.DependentPlugin;
import com.salesforce.omakase.plugin.basic.SyntaxTree;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class StyleWriter implements DependentPlugin {
    private Map<Class<? extends Writeable>, CustomWriter<?>> overrides = new HashMap<>();
    private SyntaxTree tree;
    private WriterMode mode;

    /**
     * TODO
     */
    public StyleWriter() {
        this(WriterMode.INLINE);
    }

    /**
     * TODO
     * 
     * @param mode
     *            TODO
     */
    public StyleWriter(WriterMode mode) {
        this.mode = mode;
    }

    @Override
    public void dependencies(PluginRegistry registry) {
        tree = registry.require(SyntaxTree.class);
    }

    /**
     * TODO Description
     * 
     * @param mode
     *            TODO
     * @return TODO
     */
    public StyleWriter mode(WriterMode mode) {
        this.mode = checkNotNull(mode, "mode cannot be null");
        return this;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public boolean verbose() {
        return mode == WriterMode.VERBOSE;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public boolean inline() {
        return mode == WriterMode.INLINE;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public boolean compressed() {
        return mode == WriterMode.COMPRESSED;
    }

    /**
     * TODO Description
     * 
     * @param <T>
     *            TODO
     * @param writeable
     *            TODO
     * @param writer
     *            TODO
     * @return TODO
     */
    public <T extends Writeable> StyleWriter override(Class<T> writeable, CustomWriter<T> writer) {
        overrides.put(writeable, writer);
        return this;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     * @throws IOException
     *             TODO
     */
    public String write() throws IOException {
        checkState(tree != null, "syntax tree not set (did you include this writer in the request?)");

        StyleAppendable appendable = new StyleAppendable();
        write(appendable);
        return appendable.toString();
    }

    /**
     * TODO Description
     * 
     * @param appendable
     *            TODO
     * @throws IOException
     *             TODO
     */
    public void write(Appendable appendable) throws IOException {
        write(new StyleAppendable(appendable));
    }

    /**
     * TODO Description
     * 
     * @param out
     *            TODO
     * @throws IOException
     *             TODO
     */
    public void write(StyleAppendable out) throws IOException {
        checkNotNull(out, "out cannot be null");
        checkState(tree != null, "syntax tree not set (did you include this writer in the request?)");

        write(tree.stylesheet(), out);
    }

    /**
     * TODO Description
     * 
     * @param <T>
     *            TODO
     * @param writeable
     *            TODO
     * @param builder
     *            TODO
     * @throws IOException
     *             TODO
     */
    @SuppressWarnings("unchecked")
    public <T extends Writeable> void write(T writeable, StyleAppendable builder) throws IOException {
        Class<? extends Writeable> klass = writeable.getClass();

        if (overrides.containsKey(klass)) {
            // cast is safe as long as the map is guarded by #override
            ((CustomWriter<T>)overrides.get(klass)).write(writeable, builder);
        } else {
            writeable.write(this, builder);
        }
    }
}
