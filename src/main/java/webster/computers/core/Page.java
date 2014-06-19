package webster.computers.core;

import java.util.List;

public class Page<T> {

    private final List<T> items;
    private final int maxItemsPerPage;
    private final int totalItems;
    private final int index;

    public Page(List<T> items, int maxItemsPerPage, int totalItems, int index) {
        this.items = items;
        this.maxItemsPerPage = maxItemsPerPage;
        this.totalItems = totalItems;
        this.index = index;
    }

    private int firstItemIndex() {
        return (index * maxItemsPerPage) + 1;
    }

    private int lastItemIndex() {
        return firstItemIndex() + items.size() - 1;
    }

    public String displayXtoYofZ(String to, String of) {
        return firstItemIndex() + to + lastItemIndex() + of + totalItems;
    }

    public List<T> items() {
        return items;
    }

    public int totalItems() {
        return totalItems;
    }

    public boolean hasPrevious() {
        return index > 0;
    }

    public boolean hasNext() {
        return lastItemIndex() < totalItems;
    }

    public int index() {
        return index;
    }
}
