/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast.collection;

import java.util.Iterator;
import java.util.LinkedList;

import com.google.common.collect.Lists;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
public class BaseSyntaxCollection<T> implements SyntaxCollection<T> {
    private final LinkedList<T> list = Lists.newLinkedList();

    @Override
    public Iterator<T> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean contains(T unit) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public SyntaxCollection<T> prepend(T unit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SyntaxCollection<T> prependAll(Iterable<T> units) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SyntaxCollection<T> prependBefore(T existingunit, T newunit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SyntaxCollection<T> append(T unit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SyntaxCollection<T> appendAll(Iterable<T> units) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SyntaxCollection<T> appendAfter(T existingunit, T newunit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SyntaxCollection<T> replaceExistingWith(Iterable<T> units) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SyntaxCollection<T> detach(T unit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SyntaxCollection<T> clear() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public static <T> SyntaxCollection<T> create() {
        return new BaseSyntaxCollection<>();
    }
}
