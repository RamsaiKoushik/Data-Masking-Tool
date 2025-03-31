package datamaskingtool.CustomClasses;

import java.util.List;

import java.util.*;

public class CustomFloatList implements List<Float> {
    private List<Float> internalList = new ArrayList<>();

    @Override
    public int size() {
        return internalList.size();
    }

    @Override
    public boolean isEmpty() {
        return internalList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return internalList.contains(o);
    }

    @Override
    public Iterator<Float> iterator() {
        return internalList.iterator();
    }

    @Override
    public Object[] toArray() {
        return internalList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return internalList.toArray(a);
    }

    @Override
    public boolean add(Float s) {
        return internalList.add(s);
    }

    @Override
    public boolean remove(Object o) {
        return internalList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return internalList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Float> c) {
        return internalList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Float> c) {
        return internalList.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return internalList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return internalList.retainAll(c);
    }

    @Override
    public void clear() {
        internalList.clear();
    }

    @Override
    public Float get(int index) {
        return internalList.get(index);
    }

    @Override
    public Float set(int index, Float element) {
        return internalList.set(index, element);
    }

    @Override
    public void add(int index, Float element) {
        internalList.add(index, element);
    }

    @Override
    public Float remove(int index) {
        return internalList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return internalList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return internalList.lastIndexOf(o);
    }

    @Override
    public ListIterator<Float> listIterator() {
        return internalList.listIterator();
    }

    @Override
    public ListIterator<Float> listIterator(int index) {
        return internalList.listIterator(index);
    }

    @Override
    public List<Float> subList(int fromIndex, int toIndex) {
        return internalList.subList(fromIndex, toIndex);
    }
}
