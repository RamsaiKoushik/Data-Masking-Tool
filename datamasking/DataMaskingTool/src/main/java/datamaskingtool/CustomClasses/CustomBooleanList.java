package datamaskingtool.CustomClasses;

import java.util.*;

public class CustomBooleanList implements List<Boolean>, CustomList{
    private List<Boolean> internalList = new ArrayList<>();

    public CustomBooleanList(List<Boolean> list){
        this.internalList = list;
    }

    public CustomBooleanList(){}

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
    public Iterator<Boolean> iterator() {
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
    public boolean add(Boolean b) {
        return internalList.add(b);
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
    public boolean addAll(Collection<? extends Boolean> c) {
        return internalList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Boolean> c) {
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
    public Boolean get(int index) {
        return internalList.get(index);
    }

    @Override
    public Boolean set(int index, Boolean element) {
        return internalList.set(index, element);
    }

    @Override
    public void add(int index, Boolean element) {
        internalList.add(index, element);
    }

    @Override
    public Boolean remove(int index) {
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
    public ListIterator<Boolean> listIterator() {
        return internalList.listIterator();
    }

    @Override
    public ListIterator<Boolean> listIterator(int index) {
        return internalList.listIterator(index);
    }

    @Override
    public List<Boolean> subList(int fromIndex, int toIndex) {
        return internalList.subList(fromIndex, toIndex);
    }

    @Override
    public List<Boolean> getInternalList(){
        return internalList;
    }
}
