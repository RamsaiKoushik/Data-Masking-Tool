package datamaskingtool.CustomClasses;

import java.util.List;

import java.util.*;

public class CustomIntegerList implements List<Integer>, CustomList {
    private List<Integer> internalList = new ArrayList<>();

    public CustomIntegerList(List<Integer> list){
        this.internalList = list;
    }

    public CustomIntegerList(){}

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
    public Iterator<Integer> iterator() {
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
    public boolean add(Integer s) {
        return internalList.add(s);
    }

    @Override
    public boolean remove(Object o) {
        return internalList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return new HashSet<>(internalList).containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Integer> c) {
        return internalList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Integer> c) {
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
    public Integer get(int index) {
        return internalList.get(index);
    }

    @Override
    public Integer set(int index, Integer element) {
        return internalList.set(index, element);
    }

    @Override
    public void add(int index, Integer element) {
        internalList.add(index, element);
    }

    @Override
    public Integer remove(int index) {
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
    public ListIterator<Integer> listIterator() {
        return internalList.listIterator();
    }

    @Override
    public ListIterator<Integer> listIterator(int index) {
        return internalList.listIterator(index);
    }

    @Override
    public List<Integer> subList(int fromIndex, int toIndex) {
        return internalList.subList(fromIndex, toIndex);
    }

    @Override
    public List<Integer> getInternalList(){
        return internalList;
    }
}
