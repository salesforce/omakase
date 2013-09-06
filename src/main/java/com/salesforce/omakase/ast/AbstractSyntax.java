/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import static com.google.common.base.Preconditions.checkNotNull;

import com.salesforce.omakase.As;
import com.salesforce.omakase.broadcaster.Broadcaster;

/**
 * TESTME Base class for {@link Syntax} units.
 * 
 * @author nmcwilliams
 */
public abstract class AbstractSyntax implements Syntax {
    private final int line;
    private final int column;
    private Broadcaster broadcaster;

    private Status status = Status.UNBROADCASTED;

    /**
     * 
     */
    public AbstractSyntax() {
        this(-1, -1);
    }

    /**
     * Creates a new instance with the given line and column numbers.
     * 
     * @param line
     *            The line number.
     * @param column
     *            The column number.
     */
    public AbstractSyntax(int line, int column) {
        this(line, column, null);
    }

    /**
     * TODO
     * 
     * @param line
     *            TODO
     * @param column
     *            TODO
     * @param broadcaster
     *            TODO
     */
    public AbstractSyntax(int line, int column, Broadcaster broadcaster) {
        this.line = line;
        this.column = column;
        this.broadcaster = broadcaster;
    }

    @Override
    public int line() {
        return line;
    }

    @Override
    public int column() {
        return column;
    }

    @Override
    public boolean hasSourcePosition() {
        return line != -1 && column != -1;
    }

    @Override
    public Syntax status(Status status) {
        this.status = checkNotNull(status, "status cannot be null");
        return this;
    }

    @Override
    public Status status() {
        return status;
    }

    @Override
    public Syntax broadcaster(Broadcaster broadcaster) {
        this.broadcaster = broadcaster;
        return this;
    }

    @Override
    public Broadcaster broadcaster() {
        return broadcaster;
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        // broadcast ourselves if we haven't been broadcasted yet
        if (this.status() == Status.UNBROADCASTED) {
            broadcaster.broadcast(this);
        }
    }

    @Override
    public String toString() {
        return As.stringNamed("")
            .add("line", line)
            .add("column", column)
            .toString();
    }
}
