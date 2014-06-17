/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package jp.lunchat.core.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author jun.ozeki
 */
public class VirtualList<T> implements List<T> {
    /**
     * 
     * @author jun.ozeki
     * @param <T>
     */
    public static interface VirtualListLoader<T> {
        List<T> load();
    }

    private List<T> source;
    private VirtualListLoader<T> loader;
    
    /**
     * @param loader
     */
    public VirtualList(VirtualListLoader<T> loader) {
        this.loader = loader;
    }

    public List<T> getSource() {
        // TODO synchronized ?
        if (source == null)
            source = loader.load();
        return source;
    }

    /**
     * @see java.util.List#size()
     */
    @Override
    public int size() {
        return getSource().size();
    }

    /**
     * @see java.util.List#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return getSource().isEmpty();
    }

    /**
     * @see java.util.List#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object o) {
        return getSource().contains(o);
    }

    /**
     * @see java.util.List#iterator()
     */
    @Override
    public Iterator<T> iterator() {
        return getSource().iterator();
    }

    /**
     * @see java.util.List#toArray()
     */
    @Override
    public Object[] toArray() {
        return getSource().toArray();
    }

    /**
     * @see java.util.List#toArray(java.lang.Object[])
     */
    @Override
    public <E> E[] toArray(E[] a) {
        return getSource().toArray(a);
    }

    /**
     * @see java.util.List#add(java.lang.Object)
     */
    @Override
    public boolean add(T e) {
        return getSource().add(e);
    }

    /**
     * @see java.util.List#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object o) {
        return getSource().remove(o);
    }

    /**
     * @see java.util.List#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return getSource().containsAll(c);
    }

    /**
     * @see java.util.List#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        return getSource().addAll(c);
    }

    /**
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return getSource().addAll(index, c);
    }

    /**
     * @see java.util.List#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        return getSource().removeAll(c);
    }

    /**
     * @see java.util.List#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        return getSource().retainAll(c);
    }

    /**
     * @see java.util.List#clear()
     */
    @Override
    public void clear() {
       getSource().clear(); 
    }

    /**
     * @see java.util.List#get(int)
     */
    @Override
    public T get(int index) {
        return getSource().get(index);
    }

    /**
     * @see java.util.List#set(int, java.lang.Object)
     */
    @Override
    public T set(int index, T element) {
        return getSource().set(index, element);
    }

    /**
     * @see java.util.List#add(int, java.lang.Object)
     */
    @Override
    public void add(int index, T element) {
        getSource().add(index, element);
    }

    /**
     * @see java.util.List#remove(int)
     */
    @Override
    public T remove(int index) {
        return getSource().remove(index);
    }

    /**
     * @see java.util.List#indexOf(java.lang.Object)
     */
    @Override
    public int indexOf(Object o) {
        return getSource().indexOf(o);
    }

    /**
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    @Override
    public int lastIndexOf(Object o) {
        return getSource().lastIndexOf(o);
    }

    /**
     * @see java.util.List#listIterator()
     */
    @Override
    public ListIterator<T> listIterator() {
        return getSource().listIterator();
    }

    /**
     * @see java.util.List#listIterator(int)
     */
    @Override
    public ListIterator<T> listIterator(int index) {
        return getSource().listIterator(index);
    }

    /**
     * @see java.util.List#subList(int, int)
     */
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return getSource().subList(fromIndex, toIndex);
    }
    
}