package datamaskingtool.CustomClasses;

import java.util.List;

import java.util.*;

public class CustomStringList implements List<String> {
    private List<String> internalList = new ArrayList<>();

    public CustomStringList(List<String> list){
        this.internalList = list;
    }

    public CustomStringList(){}

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
    public Iterator<String> iterator() {
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
    public boolean add(String s) {
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
    public boolean addAll(Collection<? extends String> c) {
        return internalList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends String> c) {
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
    public String get(int index) {
        return internalList.get(index);
    }

    @Override
    public String set(int index, String element) {
        return internalList.set(index, element);
    }

    @Override
    public void add(int index, String element) {
        internalList.add(index, element);
    }

    @Override
    public String remove(int index) {
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
    public ListIterator<String> listIterator() {
        return internalList.listIterator();
    }

    @Override
    public ListIterator<String> listIterator(int index) {
        return internalList.listIterator(index);
    }

    @Override
    public List<String> subList(int fromIndex, int toIndex) {
        return internalList.subList(fromIndex, toIndex);
    }
}
