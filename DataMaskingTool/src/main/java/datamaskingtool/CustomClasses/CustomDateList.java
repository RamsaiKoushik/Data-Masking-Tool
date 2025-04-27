package datamaskingtool.CustomClasses;

import java.util.*;
import java.sql.Date;

public class CustomDateList implements List<Date>, CustomList{
    private List<Date> internalList = new ArrayList<>();

    public CustomDateList(List<Date> list){
        this.internalList = list;
    }

    public CustomDateList(){}

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
    public Iterator<Date> iterator() {
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
    public boolean add(Date date) {
        return internalList.add(date);
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
    public boolean addAll(Collection<? extends Date> c) {
        return internalList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Date> c) {
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
    public Date get(int index) {
        return internalList.get(index);
    }

    @Override
    public Date set(int index, Date element) {
        return internalList.set(index, element);
    }

    @Override
    public void add(int index, Date element) {
        internalList.add(index, element);
    }

    @Override
    public Date remove(int index) {
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
    public ListIterator<Date> listIterator() {
        return internalList.listIterator();
    }

    @Override
    public ListIterator<Date> listIterator(int index) {
        return internalList.listIterator(index);
    }

    @Override
    public List<Date> subList(int fromIndex, int toIndex) {
        return internalList.subList(fromIndex, toIndex);
    }

    @Override
    public List<Date> getInternalList(){
        return internalList;
    }
}
