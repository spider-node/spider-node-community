package cn.spider.framework.linker.server.loadbalancer.utils;

import com.google.common.annotations.VisibleForTesting;

import java.util.*;

/**
 * HashIndexedPriorityQueue implemented both interfaces of Queue<> and Set<>
 * Comparing with {@link java.util.PriorityQueue}, HashIndexedPriorityQueue
 * - reduced contains(obj)/remove(obj) complexity from O(n) to O(logN)
 * - offer(obj)/add(obj) ensures uniqueness
 * - offer(obj)/add(obj) adjust order in-place with O(logn) complexity, if obj exist.
 *
 * HashIndexedPriorityQueue is preferred than {@link java.util.PriorityQueue}
 * when element order needs frequent update. within {@link java.util.PriorityQueue}, to update
 * order of element, remove(obj) + offer(obj) needs to be called sequentially to make sure queue
 * structure is maintained correctly.
 * With {@link HashIndexedPriorityQueue}, offer(obj) allows the queue to adjust order in-place with O(logN)
 * Complexity
 *
 * Fairness guarantee
 * If there are multiple minimal value items in HashIndexedPriorityQueue, they shall have equal probability being polled
 *
 * Example: Suppose class Entity.value is Integer
 * <pre>
 * class Entity {
 *     int value;
 *     Entity(int v) {
 *         this.value = v;
 *      }
 * }
 * Comparator<Entity> comparator = Comparator.comparingInt(o -> o.value);
 * HashIndexedPriorityQueue<Entity> queue = new HashIndexedPriorityQueue<>(comparator);
 * //add entities into queue
 * entity1.value+=1; //change order of entity
 * queue.offer(entity1) //if entity1 is not in the queue, it will be added. otherwise order of entity1 will be in-place adjusted
 * </pre>
 *
 * @param <E> the type parameter
 */
public class HashIndexedPriorityQueue<E> extends AbstractQueue<E> implements Set<E> {
    private static final int INDEX_NOT_FOUND = -1;
    private final HashMap<E, Integer> entityToIndex;
    private final List<E> entityList;
    private final Comparator<E> comparator;
    private final RandomBooleanGenerator booleanGenerator = new RandomBooleanGenerator();

    /**
     * Instantiates a new Hash indexed priority queue.
     *
     * @param comparator the comparator
     */
    public HashIndexedPriorityQueue(Comparator<E> comparator) {
        this.entityToIndex = new HashMap<>();
        this.entityList = new ArrayList<>();
        if (comparator == null) {
            this.comparator = (o1, o2) -> {
                Comparable<E> c1 = (Comparable)o1;
                return c1.compareTo(o2);
            };
        } else {
            this.comparator = comparator;
        }
    }

    /**
     * Instantiates a new Hash indexed priority queue.
     * use (e1, e2) -> e1.compareTo(e2) as default comparator
     */
    public HashIndexedPriorityQueue() {
        this(null);
    }

    @Override
    public Iterator<E> iterator() {
        return new IteratorImpl();
    }

    @Override
    public int size() {
        return entityList.size();
    }

    /**
     * 1. add entity if obj not exist
     * 2. adjust order in-place with O(logn) complexity, if obj exist.
     *
     * @param e entity to add or update order
     * @return
     */
    @Override
    public boolean offer(E e) {
        int index = indexOf(e);
        if (index != -1) {
            siftUp(index, e); // sift up if smaller than parent
            siftDown(index, e); // sift down if bigger than child
        } else {
            index = entityList.size();
            entityList.add(e);
            entityToIndex.put(e, index);
            siftUp(index, e);
        }
        return true;
    }

    @Override
    public E poll() {
        return removeByIndex(0);
    }

    @Override
    public E peek() {
        if (entityList.isEmpty()) {
            return null;
        }
        return entityList.get(0);
    }

    @Override
    public boolean contains(Object o) {
        return entityToIndex.containsKey(o);
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index == INDEX_NOT_FOUND) {
            return false;
        }
        E result = removeByIndex(index);
        Objects.requireNonNull(result);
        return true;
    }

    /**
     * Validate entityToIndex and entityList
     */
    @VisibleForTesting
    protected void validate() {
        for (Map.Entry<E,Integer> entry: entityToIndex.entrySet()) {
            int index = entry.getValue();
            if (entityList.get(index) != entry.getKey()) {
                throw new IllegalStateException("entity " + entry + " is not at expected index " + index);
            }
        }
    }

    /**
     * exchange index with parent if given entity is smaller
     */
    private void siftUp(int index, E e) {
        int oIndex = index;
        while (index > 0) {
            int pIndex = (index - 1) >>> 1;
            E parent = entityList.get(pIndex);
            if (comparator.compare(e, parent) >= 0) {
                break;
            }
            entityList.set(index, parent);
            entityToIndex.put(parent, index);
            index = pIndex;
        }

        if (oIndex == index)
            return;
        entityList.set(index, e);
        entityToIndex.put(e, index);
    }

   /**
    * exchange index with smaller child if given entity is bigger
    * when two children are equal in value, choose one child in equal possibility
    */
    private void siftDown(int index, E e) {
        int oIndex = index;
        int size = entityList.size();
        int mid = size >>> 1;
        while (index < mid) {
            int cIndex = (index << 1) + 1;
            int rcIndex = cIndex + 1;
            E c = entityList.get(cIndex);

            if (rcIndex < size) {
                E rc = entityList.get(rcIndex);
                int cmp = comparator.compare(c, rc);
                if (cmp > 0 || (cmp == 0 && booleanGenerator.next())) {
                    c = rc;
                    cIndex = rcIndex;
                }
            }
            if (comparator.compare(c, e) > 0) {
                break;
            }
            entityList.set(index, c);
            entityToIndex.put(c, index);
            index = cIndex;
        }
        if (oIndex == index)
            return;
        entityList.set(index, e);
        entityToIndex.put(e, index);
    }

    /**
     * if given index is last in queue, remove the element
     * other wise, relocate last element to given index and sift down
     */
    private E removeByIndex(int index) {
        int size = entityList.size();
        if (index >= size) {
            return null;
        }
        E entity = entityList.get(index);
        entityToIndex.remove(entity);
        if (index == size - 1) {
            entityList.remove(index);
        } else {
            E replacement = entityList.remove(size - 1);
            entityList.set(index, replacement);
            entityToIndex.put(replacement, index);
            siftDown(index, replacement);
        }

        return entity;
    }

    /**
     * return -1 if not found
     */
    private int indexOf(Object e) {
        Integer index = entityToIndex.get(e);
        if (index == null) {
            return INDEX_NOT_FOUND;
        }
        return index;
    }

    /**
     * iterator remove() is not supported yet
     */
    private class IteratorImpl implements Iterator<E> {
        private Iterator<E> impl;

        /**
         * Instantiates a new Iterator.
         */
        IteratorImpl() {
            impl = entityList.iterator();
        }

        @Override
        public boolean hasNext() {
            return impl.hasNext();
        }

        @Override
        public E next() {
            return impl.next();
        }
    }
}
