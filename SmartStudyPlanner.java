import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║            SMART STUDY PLANNER — Java Console Edition           ║
 * ║                    Single File — Run Directly in VS Code        ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  DSA Implemented:                                               ║
 * ║  ✅ Doubly Linked List      (Task storage & traversal)          ║
 * ║  ✅ Singly Linked Stack     (Session history, expression eval)  ║
 * ║  ✅ Circular Queue          (Session scheduling)                ║
 * ║  ✅ Linked Queue            (FIFO utility)                      ║
 * ║  ✅ Deque                   (Double-ended queue)                ║
 * ║  ✅ Binary Max/Min Heap     (Priority Queue for tasks/remind.)  ║
 * ║  ✅ Hash Table              (Separate Chaining + Rehashing)     ║
 * ║  ✅ Bubble Sort             O(n²)                               ║
 * ║  ✅ Selection Sort          O(n²)                               ║
 * ║  ✅ Insertion Sort          O(n²)                               ║
 * ║  ✅ Merge Sort              O(n log n)                          ║
 * ║  ✅ Quick Sort              O(n log n)                          ║
 * ║  ✅ Linear Search           O(n)                                ║
 * ║  ✅ Binary Search           O(log n)                            ║
 * ║  ✅ Infix → Postfix         (Stack application)                 ║
 * ║  ✅ Postfix Evaluation      (Stack application)                 ║
 * ║  ✅ Balanced Symbols        (Stack application)                 ║
 * ║  ✅ Polynomial ADT          (Linked List application)           ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * HOW TO RUN IN VS CODE:
 *   1. Open this file in VS Code
 *   2. Make sure Java Extension Pack is installed
 *   3. Click  ▶ Run  button at the top right  OR
 *      Right-click → Run Java  OR
 *      Terminal: javac SmartStudyPlanner.java && java SmartStudyPlanner
 */
public class SmartStudyPlanner {

    // ═══════════════════════════════════════════════════════════════════
    //  SECTION 1 — MODELS
    // ═══════════════════════════════════════════════════════════════════

    static class Task implements Comparable<Task> {
        private static int idCounter = 1;
        private final int id;
        private String subject, description, priority;
        private LocalDate dueDate;
        private boolean completed;
        private int priorityValue;

        Task(String subject, String description, String priority, LocalDate dueDate) {
            this.id = idCounter++;
            this.subject = subject;
            this.description = description;
            this.priority = priority;
            this.dueDate = dueDate;
            this.completed = false;
            this.priorityValue = priority.equalsIgnoreCase("High") ? 3
                               : priority.equalsIgnoreCase("Medium") ? 2 : 1;
        }

        @Override
        public int compareTo(Task other) {
            if (this.priorityValue != other.priorityValue)
                return Integer.compare(other.priorityValue, this.priorityValue);
            return this.dueDate.compareTo(other.dueDate);
        }

        int getId()             { return id; }
        String getSubject()     { return subject; }
        String getPriority()    { return priority; }
        LocalDate getDueDate()  { return dueDate; }
        boolean isCompleted()   { return completed; }
        void setCompleted(boolean c) { completed = c; }
        int getPriorityValue()  { return priorityValue; }

        @Override
        public String toString() {
            String status = completed ? "[DONE]" : "[PEND]";
            return String.format("ID:%-3d %s %-20s | %-32s | Priority:%-6s | Due:%s",
                    id, status, subject, description, priority,
                    dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
    }

    // ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ──

    static class Note {
        private static int idCounter = 1;
        private final int id;
        private String title, content;

        Note(String title, String content) {
            this.id = idCounter++;
            this.title = title;
            this.content = content;
        }

        int getId()          { return id; }
        String getTitle()    { return title; }
        String getContent()  { return content; }

        @Override
        public String toString() {
            return String.format("ID:%-3d [%-25s] %s", id, title, content);
        }
    }

    // ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ──

    static class Reminder implements Comparable<Reminder> {
        private static int idCounter = 1;
        private final int id;
        private final String title;
        private final LocalDateTime dateTime;
        private boolean triggered;

        Reminder(String title, LocalDateTime dateTime) {
            this.id = idCounter++;
            this.title = title;
            this.dateTime = dateTime;
            this.triggered = false;
        }

        @Override
        public int compareTo(Reminder other) {
            return this.dateTime.compareTo(other.dateTime);
        }

        int getId()               { return id; }
        String getTitle()         { return title; }
        LocalDateTime getDateTime() { return dateTime; }
        boolean isTriggered()     { return triggered; }
        void setTriggered(boolean t) { triggered = t; }

        @Override
        public String toString() {
            String status = triggered ? "[FIRED]" : "[ACTIVE]";
            return String.format("ID:%-3d %s %-30s | When: %s",
                    id, status, title,
                    dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
    }


    // ═══════════════════════════════════════════════════════════════════
    //  SECTION 2 — DATA STRUCTURES
    // ═══════════════════════════════════════════════════════════════════

    // ── 2a. Doubly Linked List ──────────────────────────────────────────
    /**
     * Generic Doubly Linked List — List ADT.
     * O(1) insert at head/tail. O(n) remove by value.
     * Used: Task storage and traversal.
     */
    static class DoublyLinkedList<T> implements Iterable<T> {

        static class Node<T> {
            T data;
            Node<T> prev, next;
            Node(T data) { this.data = data; }
        }

        private Node<T> head, tail;
        private int size;

        void addLast(T data) {
            Node<T> n = new Node<>(data);
            if (tail == null) { head = tail = n; }
            else { n.prev = tail; tail.next = n; tail = n; }
            size++;
        }

        void addFirst(T data) {
            Node<T> n = new Node<>(data);
            if (head == null) { head = tail = n; }
            else { n.next = head; head.prev = n; head = n; }
            size++;
        }

        boolean remove(T data) {
            for (Node<T> cur = head; cur != null; cur = cur.next) {
                if (cur.data.equals(data)) { unlink(cur); return true; }
            }
            return false;
        }

        T removeFirst() {
            if (head == null) throw new NoSuchElementException("List is empty");
            T data = head.data;
            unlink(head);
            return data;
        }

        private void unlink(Node<T> node) {
            if (node.prev != null) node.prev.next = node.next; else head = node.next;
            if (node.next != null) node.next.prev = node.prev; else tail = node.prev;
            size--;
        }

        T getFirst() { if (head == null) throw new NoSuchElementException(); return head.data; }
        T getLast()  { if (tail == null) throw new NoSuchElementException(); return tail.data; }
        int size()        { return size; }
        boolean isEmpty() { return size == 0; }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<>() {
                Node<T> cur = head;
                public boolean hasNext() { return cur != null; }
                public T next() {
                    if (!hasNext()) throw new NoSuchElementException();
                    T d = cur.data; cur = cur.next; return d;
                }
            };
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            for (Node<T> cur = head; cur != null; cur = cur.next) {
                sb.append(cur.data);
                if (cur.next != null) sb.append(" <-> ");
            }
            return sb.append("]").toString();
        }
    }


    // ── 2b. Linked Stack ────────────────────────────────────────────────
    /**
     * Stack ADT — Singly Linked List.
     * LIFO. O(1) push/pop/peek.
     * Used: Session history (Pomodoro), Expression evaluator, Symbol balancer.
     */
    static class LinkedStack<T> {
        private static class Node<T> {
            T data; Node<T> next;
            Node(T d) { data = d; }
        }
        private Node<T> top;
        private int size;

        void push(T item) {
            Node<T> n = new Node<>(item);
            n.next = top; top = n; size++;
        }

        T pop() {
            if (isEmpty()) throw new EmptyStackException();
            T d = top.data; top = top.next; size--; return d;
        }

        T peek() {
            if (isEmpty()) throw new EmptyStackException();
            return top.data;
        }

        boolean isEmpty() { return top == null; }
        int size()        { return size; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("TOP -> ");
            for (Node<T> cur = top; cur != null; cur = cur.next) {
                sb.append("[").append(cur.data).append("]");
                if (cur.next != null) sb.append(" -> ");
            }
            return sb.toString();
        }
    }


    // ── 2c. Circular Queue ──────────────────────────────────────────────
    /**
     * Array-based Circular Queue — Queue ADT.
     * O(1) enqueue/dequeue. Fixed capacity, wraps around.
     * Used: Session round-robin scheduling.
     */
    static class CircularQueue<T> {
        private final Object[] data;
        private int front, rear, size;
        private final int capacity;

        CircularQueue(int capacity) {
            this.capacity = capacity;
            data = new Object[capacity];
            front = 0; rear = -1; size = 0;
        }

        void enqueue(T item) {
            if (isFull()) throw new IllegalStateException("Queue is full");
            rear = (rear + 1) % capacity;
            data[rear] = item;
            size++;
        }

        @SuppressWarnings("unchecked")
        T dequeue() {
            if (isEmpty()) throw new NoSuchElementException("Queue is empty");
            T item = (T) data[front];
            front = (front + 1) % capacity;
            size--;
            return item;
        }

        @SuppressWarnings("unchecked")
        T peek() {
            if (isEmpty()) throw new NoSuchElementException();
            return (T) data[front];
        }

        boolean isEmpty() { return size == 0; }
        boolean isFull()  { return size == capacity; }
        int size()        { return size; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("CircularQueue[");
            for (int i = 0; i < size; i++) {
                sb.append(data[(front + i) % capacity]);
                if (i < size - 1) sb.append(", ");
            }
            return sb.append("]").toString();
        }
    }


    // ── 2d. Linked Queue ────────────────────────────────────────────────
    /**
     * Queue ADT via Singly Linked List. O(1) enqueue/dequeue.
     */
    static class LinkedQueue<T> {
        private static class Node<T> {
            T data; Node<T> next;
            Node(T d) { data = d; }
        }
        private Node<T> front, rear;
        private int size;

        void enqueue(T item) {
            Node<T> n = new Node<>(item);
            if (rear != null) rear.next = n;
            rear = n;
            if (front == null) front = n;
            size++;
        }

        T dequeue() {
            if (isEmpty()) throw new NoSuchElementException();
            T d = front.data;
            front = front.next;
            if (front == null) rear = null;
            size--;
            return d;
        }

        T peek()          { if (isEmpty()) throw new NoSuchElementException(); return front.data; }
        boolean isEmpty() { return front == null; }
        int size()        { return size; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("FRONT -> ");
            for (Node<T> cur = front; cur != null; cur = cur.next) {
                sb.append("[").append(cur.data).append("]");
                if (cur.next != null) sb.append(" -> ");
            }
            return sb.append(" <- REAR").toString();
        }
    }


    // ── 2e. Deque ───────────────────────────────────────────────────────
    /**
     * Double-Ended Queue — built on DoublyLinkedList.
     * O(1) add/remove at both ends.
     */
    static class Deque<T> {
        private final DoublyLinkedList<T> list = new DoublyLinkedList<>();
        void addFront(T item) { list.addFirst(item); }
        void addRear(T item)  { list.addLast(item); }
        T removeFront()       { return list.removeFirst(); }
        T getFirst()          { return list.getFirst(); }
        boolean isEmpty()     { return list.isEmpty(); }
        int size()            { return list.size(); }
        @Override public String toString() { return list.toString(); }
    }


    // ── 2f. Binary Heap (Priority Queue) ───────────────────────────────
    /**
     * Binary Max/Min Heap — Priority Queue ADT.
     * O(log n) insert/remove. O(1) peek. O(n) buildHeap.
     * Used: Tasks (max-heap by priority), Reminders (min-heap by datetime).
     */
    static class BinaryHeap<T extends Comparable<T>> {
        private final List<T> heap = new ArrayList<>();
        private final boolean isMaxHeap;

        BinaryHeap(boolean isMaxHeap) { this.isMaxHeap = isMaxHeap; }

        void insert(T item) {
            heap.add(item);
            bubbleUp(heap.size() - 1);
        }

        T peek() {
            if (isEmpty()) throw new NoSuchElementException("Heap is empty");
            return heap.get(0);
        }

        T remove() {
            if (isEmpty()) throw new NoSuchElementException("Heap is empty");
            T root = heap.get(0);
            T last = heap.remove(heap.size() - 1);
            if (!heap.isEmpty()) { heap.set(0, last); siftDown(0); }
            return root;
        }

        void buildHeap(List<T> items) {
            heap.clear(); heap.addAll(items);
            for (int i = heap.size() / 2 - 1; i >= 0; i--) siftDown(i);
        }

        List<T> heapSort(List<T> items) {
            BinaryHeap<T> tmp = new BinaryHeap<>(isMaxHeap);
            tmp.buildHeap(items);
            List<T> sorted = new ArrayList<>();
            while (!tmp.isEmpty()) sorted.add(tmp.remove());
            return sorted;
        }

        int size()         { return heap.size(); }
        boolean isEmpty()  { return heap.isEmpty(); }
        List<T> getAll()   { return new ArrayList<>(heap); }

        private int cmp(T a, T b) { return isMaxHeap ? a.compareTo(b) : b.compareTo(a); }

        private void bubbleUp(int i) {
            while (i > 0) {
                int p = (i - 1) / 2;
                if (cmp(heap.get(i), heap.get(p)) > 0) { swap(i, p); i = p; }
                else break;
            }
        }

        private void siftDown(int i) {
            int n = heap.size();
            while (true) {
                int l = 2*i+1, r = 2*i+2, t = i;
                if (l < n && cmp(heap.get(l), heap.get(t)) > 0) t = l;
                if (r < n && cmp(heap.get(r), heap.get(t)) > 0) t = r;
                if (t != i) { swap(i, t); i = t; } else break;
            }
        }

        private void swap(int i, int j) {
            T tmp = heap.get(i); heap.set(i, heap.get(j)); heap.set(j, tmp);
        }

        @Override public String toString() { return "Heap" + heap; }
    }


    // ── 2g. Hash Table (Separate Chaining + Rehashing) ─────────────────
    /**
     * Hash Table — Separate Chaining.
     * O(1) avg put/get/remove. Auto-rehashes at load > 0.75.
     * Used: Notes repository (key = title, value = Note).
     */
    static class HashTable<K, V> {
        private static class Entry<K, V> {
            K key; V value;
            Entry(K k, V v) { key = k; value = v; }
            @Override public String toString() { return key + "=" + value; }
        }

        private List<LinkedList<Entry<K, V>>> buckets;
        private int size, capacity;
        private static final double LF = 0.75;

        HashTable()           { this(16); }
        HashTable(int cap)    { capacity = cap; size = 0; init(); }

        private void init() {
            buckets = new ArrayList<>(capacity);
            for (int i = 0; i < capacity; i++) buckets.add(new LinkedList<>());
        }

        private int hash(K key) {
            int h = key.hashCode();
            h ^= (h >>> 16);
            return Math.abs(h % capacity);
        }

        void put(K key, V value) {
            int idx = hash(key);
            for (Entry<K, V> e : buckets.get(idx)) {
                if (e.key.equals(key)) { e.value = value; return; }
            }
            buckets.get(idx).add(new Entry<>(key, value));
            size++;
            if ((double) size / capacity > LF) rehash();
        }

        V get(K key) {
            for (Entry<K, V> e : buckets.get(hash(key)))
                if (e.key.equals(key)) return e.value;
            return null;
        }

        boolean remove(K key) {
            LinkedList<Entry<K, V>> chain = buckets.get(hash(key));
            for (Entry<K, V> e : chain) {
                if (e.key.equals(key)) { chain.remove(e); size--; return true; }
            }
            return false;
        }

        boolean containsKey(K key) { return get(key) != null; }
        int size()                  { return size; }
        boolean isEmpty()           { return size == 0; }

        private void rehash() {
            int oldCap = capacity;
            capacity *= 2;
            List<LinkedList<Entry<K, V>>> old = buckets;
            init(); size = 0;
            System.out.println("  [HashTable] Rehashing: " + oldCap + " -> " + capacity + " buckets");
            for (LinkedList<Entry<K, V>> chain : old)
                for (Entry<K, V> e : chain) put(e.key, e.value);
        }

        List<V> searchByValueKeyword(String keyword) {
            List<V> results = new ArrayList<>();
            String kw = keyword.toLowerCase();
            for (LinkedList<Entry<K, V>> chain : buckets)
                for (Entry<K, V> e : chain)
                    if (e.value.toString().toLowerCase().contains(kw) ||
                        e.key.toString().toLowerCase().contains(kw))
                        results.add(e.value);
            return results;
        }

        List<V> values() {
            List<V> result = new ArrayList<>();
            for (LinkedList<Entry<K, V>> chain : buckets)
                for (Entry<K, V> e : chain) result.add(e.value);
            return result;
        }

        void printStats() {
            int max = buckets.stream().mapToInt(LinkedList::size).max().orElse(0);
            System.out.printf("  HashTable: capacity=%d, size=%d, load=%.2f%n",
                    capacity, size, (double) size / capacity);
            System.out.println("  Max chain length: " + max);
        }
    }


    // ═══════════════════════════════════════════════════════════════════
    //  SECTION 3 — ALGORITHMS
    // ═══════════════════════════════════════════════════════════════════

    // ── 3a. Sorting Algorithms ──────────────────────────────────────────

    static class SortResult<T> {
        List<T> sorted;
        long comparisons, swaps, timeNs;
        String algorithm;

        @Override public String toString() {
            return String.format("[%-15s] Comparisons: %-6d | Swaps: %-6d | Time: %.4f ms",
                    algorithm, comparisons, swaps, timeNs / 1_000_000.0);
        }
    }

    // Bubble Sort  O(n²) — optimised with early-exit flag
    static <T extends Comparable<T>> SortResult<T> bubbleSort(List<T> input) {
        List<T> a = new ArrayList<>(input);
        int n = a.size(); long c = 0, sw = 0; long t = System.nanoTime();
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                c++;
                if (a.get(j).compareTo(a.get(j+1)) > 0) {
                    T tmp = a.get(j); a.set(j, a.get(j+1)); a.set(j+1, tmp);
                    sw++; swapped = true;
                }
            }
            if (!swapped) break;
        }
        return sr("Bubble Sort", a, c, sw, t);
    }

    // Selection Sort  O(n²)
    static <T extends Comparable<T>> SortResult<T> selectionSort(List<T> input) {
        List<T> a = new ArrayList<>(input);
        int n = a.size(); long c = 0, sw = 0; long t = System.nanoTime();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i+1; j < n; j++) { c++; if (a.get(j).compareTo(a.get(minIdx)) < 0) minIdx = j; }
            if (minIdx != i) { T tmp = a.get(i); a.set(i, a.get(minIdx)); a.set(minIdx, tmp); sw++; }
        }
        return sr("Selection Sort", a, c, sw, t);
    }

    // Insertion Sort  O(n²)
    static <T extends Comparable<T>> SortResult<T> insertionSort(List<T> input) {
        List<T> a = new ArrayList<>(input);
        int n = a.size(); long c = 0, sw = 0; long t = System.nanoTime();
        for (int i = 1; i < n; i++) {
            T key = a.get(i); int j = i - 1;
            while (j >= 0) {
                c++;
                if (a.get(j).compareTo(key) > 0) { a.set(j+1, a.get(j)); sw++; j--; }
                else break;
            }
            a.set(j+1, key);
        }
        return sr("Insertion Sort", a, c, sw, t);
    }

    // Merge Sort  O(n log n)
    private static long _mc = 0;
    static <T extends Comparable<T>> SortResult<T> mergeSort(List<T> input) {
        List<T> a = new ArrayList<>(input); _mc = 0; long t = System.nanoTime();
        mergeSortRec(a, 0, a.size() - 1);
        return sr("Merge Sort", a, _mc, 0, t);
    }
    private static <T extends Comparable<T>> void mergeSortRec(List<T> a, int l, int r) {
        if (l < r) { int m = (l + r) / 2; mergeSortRec(a, l, m); mergeSortRec(a, m+1, r); merge(a, l, m, r); }
    }
    private static <T extends Comparable<T>> void merge(List<T> a, int l, int m, int r) {
        List<T> L = new ArrayList<>(a.subList(l, m+1));
        List<T> R = new ArrayList<>(a.subList(m+1, r+1));
        int i = 0, j = 0, k = l;
        while (i < L.size() && j < R.size()) {
            _mc++;
            if (L.get(i).compareTo(R.get(j)) <= 0) a.set(k++, L.get(i++));
            else a.set(k++, R.get(j++));
        }
        while (i < L.size()) a.set(k++, L.get(i++));
        while (j < R.size()) a.set(k++, R.get(j++));
    }

    // Quick Sort  O(n log n) avg
    private static long _qc = 0, _qs = 0;
    static <T extends Comparable<T>> SortResult<T> quickSort(List<T> input) {
        List<T> a = new ArrayList<>(input); _qc = 0; _qs = 0; long t = System.nanoTime();
        quickSortRec(a, 0, a.size() - 1);
        return sr("Quick Sort", a, _qc, _qs, t);
    }
    private static <T extends Comparable<T>> void quickSortRec(List<T> a, int lo, int hi) {
        if (lo < hi) { int p = partition(a, lo, hi); quickSortRec(a, lo, p-1); quickSortRec(a, p+1, hi); }
    }
    private static <T extends Comparable<T>> int partition(List<T> a, int lo, int hi) {
        T pivot = a.get(hi); int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            _qc++;
            if (a.get(j).compareTo(pivot) <= 0) { i++; T tmp = a.get(i); a.set(i, a.get(j)); a.set(j, tmp); _qs++; }
        }
        T tmp = a.get(i+1); a.set(i+1, a.get(hi)); a.set(hi, tmp); _qs++;
        return i + 1;
    }

    private static <T> SortResult<T> sr(String name, List<T> sorted, long c, long sw, long startNs) {
        SortResult<T> r = new SortResult<>();
        r.algorithm = name; r.sorted = sorted; r.comparisons = c;
        r.swaps = sw; r.timeNs = System.nanoTime() - startNs;
        return r;
    }


    // ── 3b. Search Algorithms ───────────────────────────────────────────

    static class SearchResult<T> {
        List<T> found;
        int index;
        long comparisons, timeNs;
        String algorithm;

        @Override public String toString() {
            return String.format("[%s] Comparisons: %d | Time: %.4f ms | Found: %d item(s)",
                    algorithm, comparisons, timeNs / 1_000_000.0,
                    found == null ? (index >= 0 ? 1 : 0) : found.size());
        }
    }

    // Linear Search  O(n)
    static <T> SearchResult<T> linearSearch(List<T> list, String keyword) {
        long t = System.nanoTime(); long c = 0;
        List<T> results = new ArrayList<>();
        String kw = keyword.toLowerCase();
        for (T item : list) { c++; if (item.toString().toLowerCase().contains(kw)) results.add(item); }
        SearchResult<T> r = new SearchResult<>();
        r.found = results; r.comparisons = c; r.timeNs = System.nanoTime() - t; r.algorithm = "Linear Search";
        return r;
    }

    // Binary Search  O(log n)
    static <T extends Comparable<T>> SearchResult<T> binarySearch(List<T> sorted, T target) {
        long t = System.nanoTime(); long c = 0;
        int lo = 0, hi = sorted.size() - 1, found = -1;
        while (lo <= hi) {
            int mid = (lo + hi) / 2; c++;
            int cmp = sorted.get(mid).compareTo(target);
            if (cmp == 0) { found = mid; break; }
            else if (cmp < 0) lo = mid + 1;
            else hi = mid - 1;
        }
        SearchResult<T> r = new SearchResult<>();
        r.found = found >= 0 ? List.of(sorted.get(found)) : new ArrayList<>();
        r.index = found; r.comparisons = c; r.timeNs = System.nanoTime() - t; r.algorithm = "Binary Search";
        return r;
    }

    // Binary Search by ID
    static <T> int binarySearchById(List<T> sorted, int targetId, Function<T, Integer> idFn) {
        int lo = 0, hi = sorted.size() - 1;
        while (lo <= hi) {
            int mid = (lo + hi) / 2, id = idFn.apply(sorted.get(mid));
            if (id == targetId) return mid;
            else if (id < targetId) lo = mid + 1;
            else hi = mid - 1;
        }
        return -1;
    }

    static void printComplexityAnalysis(int n) {
        System.out.println("\n  ┌─ Algorithm Complexity Analysis (n=" + n + ") ─────────────────────┐");
        System.out.printf("  │ Linear Search  — O(n)       : ~%-7d comparisons (worst)    │%n", n);
        System.out.printf("  │ Binary Search  — O(log n)   : ~%-7d comparisons (worst)    │%n", (int)(Math.log(n)/Math.log(2))+1);
        System.out.printf("  │ Bubble Sort    — O(n²)      : ~%-7d comparisons (worst)    │%n", n*n);
        System.out.printf("  │ Selection Sort — O(n²)      : ~%-7d comparisons (worst)    │%n", n*n);
        System.out.printf("  │ Insertion Sort — O(n²)      : ~%-7d comparisons (worst)    │%n", n*n);
        System.out.printf("  │ Merge Sort     — O(n log n) : ~%-7d comparisons (avg)      │%n", (int)(n*(Math.log(n)/Math.log(2))));
        System.out.printf("  │ Quick Sort     — O(n log n) : ~%-7d comparisons (avg)      │%n", (int)(n*(Math.log(n)/Math.log(2))));
        System.out.println("  └────────────────────────────────────────────────────────────────┘");
    }


    // ── 3c. Stack Applications ──────────────────────────────────────────

    // Balanced Symbols checker
    static boolean isBalanced(String expr) {
        LinkedStack<Character> stack = new LinkedStack<>();
        for (char c : expr.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') stack.push(c);
            else if (c == ')' || c == ']' || c == '}') {
                if (stack.isEmpty()) return false;
                char top = stack.pop();
                if ((c == ')' && top != '(') || (c == ']' && top != '[') || (c == '}' && top != '{'))
                    return false;
            }
        }
        return stack.isEmpty();
    }

    // Infix → Postfix (Shunting-Yard Algorithm)
    static String infixToPostfix(String infix) {
        LinkedStack<Character> ops = new LinkedStack<>();
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);
            if (c == ' ') continue;
            if (Character.isDigit(c) || c == '.') {
                while (i < infix.length() && (Character.isDigit(infix.charAt(i)) || infix.charAt(i) == '.'))
                    out.append(infix.charAt(i++));
                out.append(' '); i--;
            } else if (c == '(') {
                ops.push(c);
            } else if (c == ')') {
                while (!ops.isEmpty() && ops.peek() != '(') out.append(ops.pop()).append(' ');
                if (!ops.isEmpty()) ops.pop();
            } else if (isOp(c)) {
                while (!ops.isEmpty() && ops.peek() != '(' && prec(ops.peek()) >= prec(c))
                    out.append(ops.pop()).append(' ');
                ops.push(c);
            }
        }
        while (!ops.isEmpty()) out.append(ops.pop()).append(' ');
        return out.toString().trim();
    }

    // Postfix Evaluation
    static double evaluatePostfix(String postfix) {
        LinkedStack<Double> stack = new LinkedStack<>();
        for (String tok : postfix.split("\\s+")) {
            if (tok.isEmpty()) continue;
            try { stack.push(Double.parseDouble(tok)); }
            catch (NumberFormatException e) {
                double b = stack.pop(), a = stack.pop();
                stack.push(applyOp(tok.charAt(0), a, b));
            }
        }
        return stack.pop();
    }

    private static boolean isOp(char c) { return "+-*/^".indexOf(c) >= 0; }
    private static int prec(char op) {
        return switch (op) { case '+', '-' -> 1; case '*', '/' -> 2; case '^' -> 3; default -> 0; };
    }
    private static double applyOp(char op, double a, double b) {
        return switch (op) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> { if (b == 0) throw new ArithmeticException("Division by zero"); yield a / b; }
            case '^' -> Math.pow(a, b);
            default  -> throw new IllegalArgumentException("Unknown op: " + op);
        };
    }


    // ── 3d. Polynomial ADT ──────────────────────────────────────────────
    /**
     * Polynomial via Singly Linked List.
     * Each node = (coefficient, exponent). Ordered by descending exponent.
     */
    static class Polynomial {
        private static class Term {
            double coef; int exp; Term next;
            Term(double c, int e) { coef = c; exp = e; }
        }
        private Term head;

        void addTerm(double coef, int exp) {
            if (coef == 0) return;
            Term nt = new Term(coef, exp);
            if (head == null || exp > head.exp) { nt.next = head; head = nt; return; }
            Term cur = head;
            while (cur != null) {
                if (cur.exp == exp) { cur.coef += coef; if (cur.coef == 0) removeTerm(exp); return; }
                if (cur.next == null || exp > cur.next.exp) { nt.next = cur.next; cur.next = nt; return; }
                cur = cur.next;
            }
        }
        private void removeTerm(int exp) {
            if (head == null) return;
            if (head.exp == exp) { head = head.next; return; }
            for (Term cur = head; cur.next != null; cur = cur.next)
                if (cur.next.exp == exp) { cur.next = cur.next.next; return; }
        }
        double evaluate(double x) {
            double r = 0; for (Term t = head; t != null; t = t.next) r += t.coef * Math.pow(x, t.exp); return r;
        }
        static Polynomial add(Polynomial p1, Polynomial p2) {
            Polynomial res = new Polynomial();
            Term t1 = p1.head, t2 = p2.head;
            while (t1 != null && t2 != null) {
                if (t1.exp > t2.exp) { res.addTerm(t1.coef, t1.exp); t1 = t1.next; }
                else if (t1.exp < t2.exp) { res.addTerm(t2.coef, t2.exp); t2 = t2.next; }
                else { res.addTerm(t1.coef + t2.coef, t1.exp); t1 = t1.next; t2 = t2.next; }
            }
            while (t1 != null) { res.addTerm(t1.coef, t1.exp); t1 = t1.next; }
            while (t2 != null) { res.addTerm(t2.coef, t2.exp); t2 = t2.next; }
            return res;
        }
        @Override public String toString() {
            if (head == null) return "0";
            StringBuilder sb = new StringBuilder();
            for (Term cur = head; cur != null; cur = cur.next) {
                double c = cur.coef; int e = cur.exp;
                if (sb.length() > 0) sb.append(c > 0 ? " + " : " - "); else if (c < 0) sb.append("-");
                double abs = Math.abs(c);
                if (abs != 1 || e == 0) sb.append(abs % 1 == 0 ? (long) abs : abs);
                if (e > 0) { sb.append("x"); if (e > 1) sb.append("^").append(e); }
            }
            return sb.toString();
        }
    }


    // ═══════════════════════════════════════════════════════════════════
    //  SECTION 4 — APP MODULES
    // ═══════════════════════════════════════════════════════════════════

    // ── 4a. Task Manager ────────────────────────────────────────────────
    static class TaskManager {
        private final DoublyLinkedList<Task> taskList = new DoublyLinkedList<>();

        void menu(Scanner sc) {
            boolean back = false;
            while (!back) {
                System.out.println("\n  ╔══════════════════════════════════════╗");
                System.out.println("  ║       📋 STUDY PLANNER (Tasks)       ║");
                System.out.println("  ╠══════════════════════════════════════╣");
                System.out.println("  ║  1. Add Task                         ║");
                System.out.println("  ║  2. View All Tasks                   ║");
                System.out.println("  ║  3. Mark Task Complete               ║");
                System.out.println("  ║  4. Delete Task                      ║");
                System.out.println("  ║  5. Search Tasks  (Linear O(n))      ║");
                System.out.println("  ║  6. Sort & View Tasks                ║");
                System.out.println("  ║  7. Complexity Analysis              ║");
                System.out.println("  ║  0. Back                             ║");
                System.out.println("  ╚══════════════════════════════════════╝");
                System.out.print("  Choice: ");
                String ch = sc.nextLine().trim();
                switch (ch) {
                    case "1" -> addTask(sc);
                    case "2" -> viewTasks();
                    case "3" -> markComplete(sc);
                    case "4" -> deleteTask(sc);
                    case "5" -> searchTasks(sc);
                    case "6" -> sortAndView(sc);
                    case "7" -> printComplexityAnalysis(Math.max(taskList.size(), 16));
                    case "0" -> back = true;
                    default  -> System.out.println("  ❌ Invalid choice.");
                }
            }
        }

        private void addTask(Scanner sc) {
            System.out.print("  Subject     : "); String subj = sc.nextLine().trim();
            System.out.print("  Description : "); String desc = sc.nextLine().trim();
            System.out.print("  Priority (High/Medium/Low): "); String pri = sc.nextLine().trim();
            if (!pri.equalsIgnoreCase("High") && !pri.equalsIgnoreCase("Medium") && !pri.equalsIgnoreCase("Low"))
                pri = "Medium";
            System.out.print("  Due Date (yyyy-MM-dd): ");
            LocalDate date;
            try { date = LocalDate.parse(sc.nextLine().trim()); }
            catch (Exception e) { date = LocalDate.now().plusDays(7); System.out.println("  (Defaulted to +7 days)"); }
            Task t = new Task(subj, desc, pri, date);
            taskList.addLast(t);
            System.out.println("  ✅ Task #" + t.getId() + " added to Doubly Linked List.");
        }

        private void viewTasks() {
            if (taskList.isEmpty()) { System.out.println("  (No tasks yet)"); return; }
            System.out.println("\n  ── Tasks (Doubly Linked List traversal) ──");
            int i = 1;
            for (Task t : taskList) System.out.println("  " + i++ + ". " + t);
        }

        private void markComplete(Scanner sc) {
            viewTasks();
            System.out.print("  Enter Task ID to mark complete: ");
            try {
                int id = Integer.parseInt(sc.nextLine().trim());
                for (Task t : taskList) if (t.getId() == id) { t.setCompleted(true); System.out.println("  ✅ Marked complete."); return; }
                System.out.println("  ❌ Not found.");
            } catch (NumberFormatException e) { System.out.println("  ❌ Invalid ID."); }
        }

        private void deleteTask(Scanner sc) {
            viewTasks();
            System.out.print("  Enter Task ID to delete: ");
            try {
                int id = Integer.parseInt(sc.nextLine().trim());
                for (Task t : taskList) if (t.getId() == id) { taskList.remove(t); System.out.println("  🗑️  Task removed."); return; }
                System.out.println("  ❌ Not found.");
            } catch (NumberFormatException e) { System.out.println("  ❌ Invalid ID."); }
        }

        private void searchTasks(Scanner sc) {
            System.out.print("  Search keyword: ");
            String kw = sc.nextLine().trim();
            List<Task> all = new ArrayList<>(); for (Task t : taskList) all.add(t);
            var result = linearSearch(all, kw);
            System.out.println("  " + result);
            result.found.forEach(t -> System.out.println("    → " + t));
            if (result.found.isEmpty()) System.out.println("  No matches found.");
        }

        private void sortAndView(Scanner sc) {
            System.out.println("\n  Sort algorithm:");
            System.out.println("  1. Bubble Sort   2. Selection Sort   3. Insertion Sort");
            System.out.println("  4. Merge Sort    5. Quick Sort");
            System.out.print("  Choice: ");
            String alg = sc.nextLine().trim();
            List<Task> tasks = new ArrayList<>(); for (Task t : taskList) tasks.add(t);
            if (tasks.isEmpty()) { System.out.println("  (No tasks)"); return; }
            SortResult<Task> result = switch (alg) {
                case "1" -> bubbleSort(tasks);
                case "2" -> selectionSort(tasks);
                case "3" -> insertionSort(tasks);
                case "4" -> mergeSort(tasks);
                case "5" -> quickSort(tasks);
                default  -> mergeSort(tasks);
            };
            System.out.println("\n  " + result);
            System.out.println("  ── Sorted Tasks ──");
            int i = 1; for (Task t : result.sorted) System.out.println("  " + i++ + ". " + t);
        }
    }


    // ── 4b. Notes Manager ───────────────────────────────────────────────
    static class NotesManager {
        private final HashTable<String, Note> table = new HashTable<>();

        void menu(Scanner sc) {
            boolean back = false;
            while (!back) {
                System.out.println("\n  ╔══════════════════════════════════════╗");
                System.out.println("  ║     📝 KNOWLEDGE NOTES REPOSITORY    ║");
                System.out.println("  ╠══════════════════════════════════════╣");
                System.out.println("  ║  1. Add Note                         ║");
                System.out.println("  ║  2. View All Notes                   ║");
                System.out.println("  ║  3. Search by Title  (Hash O(1))     ║");
                System.out.println("  ║  4. Full-text Search (Linear O(n))   ║");
                System.out.println("  ║  5. Delete Note                      ║");
                System.out.println("  ║  6. Hash Table Stats                 ║");
                System.out.println("  ║  0. Back                             ║");
                System.out.println("  ╚══════════════════════════════════════╝");
                System.out.print("  Choice: ");
                String ch = sc.nextLine().trim();
                switch (ch) {
                    case "1" -> addNote(sc);
                    case "2" -> viewAll();
                    case "3" -> searchTitle(sc);
                    case "4" -> fullText(sc);
                    case "5" -> deleteNote(sc);
                    case "6" -> table.printStats();
                    case "0" -> back = true;
                    default  -> System.out.println("  ❌ Invalid choice.");
                }
            }
        }

        private void addNote(Scanner sc) {
            System.out.print("  Note Title  : "); String title = sc.nextLine().trim();
            System.out.print("  Note Content: "); String content = sc.nextLine().trim();
            table.put(title.toLowerCase(), new Note(title, content));
            System.out.println("  ✅ Note inserted into Hash Table (key=\"" + title.toLowerCase() + "\")");
        }

        private void viewAll() {
            List<Note> all = table.values();
            if (all.isEmpty()) { System.out.println("  (No notes yet)"); return; }
            System.out.println("\n  ── All Notes ──");
            int i = 1; for (Note n : all) System.out.println("  " + i++ + ". " + n);
        }

        private void searchTitle(Scanner sc) {
            System.out.print("  Enter exact title: "); String title = sc.nextLine().trim();
            long t = System.nanoTime();
            Note note = table.get(title.toLowerCase());
            long elapsed = System.nanoTime() - t;
            if (note != null) { System.out.printf("  ✅ Found in %.4f ms (O(1) hash)%n", elapsed / 1_000_000.0); System.out.println("  → " + note); }
            else System.out.println("  ❌ Not found.");
        }

        private void fullText(Scanner sc) {
            System.out.print("  Search keyword: "); String kw = sc.nextLine().trim();
            long t = System.nanoTime();
            List<Note> results = table.searchByValueKeyword(kw);
            System.out.printf("  Found %d result(s) in %.4f ms (O(n) linear scan)%n", results.size(), (System.nanoTime()-t)/1_000_000.0);
            results.forEach(n -> System.out.println("  → " + n));
        }

        private void deleteNote(Scanner sc) {
            System.out.print("  Title to delete: "); String title = sc.nextLine().trim();
            System.out.println(table.remove(title.toLowerCase()) ? "  🗑️  Removed." : "  ❌ Not found.");
        }
    }


    // ── 4c. Reminders Manager ───────────────────────────────────────────
    static class RemindersManager {
        private final BinaryHeap<Reminder> heap = new BinaryHeap<>(false); // min-heap

        void menu(Scanner sc) {
            checkDue();
            boolean back = false;
            while (!back) {
                System.out.println("\n  ╔══════════════════════════════════════╗");
                System.out.println("  ║      ⏰ PUSH REMINDERS HUB           ║");
                System.out.println("  ║    (Min-Heap / Priority Queue)       ║");
                System.out.println("  ╠══════════════════════════════════════╣");
                System.out.println("  ║  1. Add Reminder                     ║");
                System.out.println("  ║  2. Peek Next  (Heap root O(1))      ║");
                System.out.println("  ║  3. View All Reminders               ║");
                System.out.println("  ║  4. Dismiss Next  (Heap remove)      ║");
                System.out.println("  ║  0. Back                             ║");
                System.out.println("  ╚══════════════════════════════════════╝");
                System.out.print("  Choice: ");
                String ch = sc.nextLine().trim();
                switch (ch) {
                    case "1" -> addReminder(sc);
                    case "2" -> { if (heap.isEmpty()) System.out.println("  (No reminders)"); else System.out.println("  ⏰ Next: " + heap.peek()); }
                    case "3" -> { if (heap.isEmpty()) System.out.println("  (No reminders)"); else { List<Reminder> all = heap.getAll(); for (int i = 0; i < all.size(); i++) System.out.println("  [" + i + "] " + all.get(i)); } }
                    case "4" -> { if (heap.isEmpty()) System.out.println("  (No reminders)"); else System.out.println("  ✅ Dismissed: " + heap.remove()); }
                    case "0" -> back = true;
                    default  -> System.out.println("  ❌ Invalid choice.");
                }
            }
        }

        private void addReminder(Scanner sc) {
            System.out.print("  Reminder Title: "); String title = sc.nextLine().trim();
            System.out.print("  Date (yyyy-MM-dd): ");
            LocalDate date; try { date = LocalDate.parse(sc.nextLine().trim()); } catch (Exception e) { date = LocalDate.now(); System.out.println("  (Defaulted to today)"); }
            System.out.print("  Time (HH:mm): ");
            LocalTime time; try { time = LocalTime.parse(sc.nextLine().trim()); } catch (Exception e) { time = LocalTime.of(8, 0); System.out.println("  (Defaulted to 08:00)"); }
            heap.insert(new Reminder(title, LocalDateTime.of(date, time)));
            System.out.println("  ✅ Reminder inserted into Min-Heap. Size: " + heap.size());
        }

        private void checkDue() {
            LocalDateTime now = LocalDateTime.now();
            while (!heap.isEmpty() && !heap.peek().getDateTime().isAfter(now)) {
                Reminder r = heap.remove();
                if (!r.isTriggered()) { System.out.println("\n  🔔 REMINDER DUE: " + r.getTitle()); r.setTriggered(true); }
            }
        }
    }


    // ── 4d. Pomodoro Module ─────────────────────────────────────────────
    static class PomodoroModule {
        private final LinkedStack<String> history = new LinkedStack<>();
        private int focus = 25, brk = 5, total = 0;

        void menu(Scanner sc) {
            boolean back = false;
            while (!back) {
                System.out.println("\n  ╔══════════════════════════════════════╗");
                System.out.println("  ║     🍅 POMODORO FOCUS SESSIONS       ║");
                System.out.println("  ╠══════════════════════════════════════╣");
                System.out.println("  ║  1. Start Focus Session              ║");
                System.out.println("  ║  2. Start Break Session              ║");
                System.out.println("  ║  3. View Session History  (Stack)    ║");
                System.out.println("  ║  4. Undo Last Session  (Stack Pop)   ║");
                System.out.println("  ║  5. Set Durations                    ║");
                System.out.println("  ║  6. View Stats                       ║");
                System.out.println("  ║  0. Back                             ║");
                System.out.println("  ╚══════════════════════════════════════╝");
                System.out.print("  Choice: ");
                String ch = sc.nextLine().trim();
                switch (ch) {
                    case "1" -> session(sc, true);
                    case "2" -> session(sc, false);
                    case "3" -> { System.out.println("\n  ── Session History (Stack) ──"); System.out.println("  " + history); if (history.isEmpty()) System.out.println("  (No sessions yet)"); }
                    case "4" -> { if (history.isEmpty()) System.out.println("  (Nothing to undo)"); else { System.out.println("  ↩️  Removed: " + history.pop()); total--; } }
                    case "5" -> setDur(sc);
                    case "6" -> System.out.println("\n  Sessions in stack: " + history.size() + " | Total: " + total + " | Focus: " + focus + "m | Break: " + brk + "m");
                    case "0" -> back = true;
                    default  -> System.out.println("  ❌ Invalid choice.");
                }
            }
        }

        private void session(Scanner sc, boolean isFocus) {
            String type = isFocus ? "FOCUS" : "BREAK"; int dur = isFocus ? focus : brk;
            System.out.print("  Topic: "); String topic = sc.nextLine().trim();
            if (topic.isEmpty()) topic = isFocus ? "General Study" : "Rest";
            System.out.println("  ⏳ Starting " + dur + "min " + type + ": " + topic);
            System.out.println("  [Press ENTER to complete session]");
            sc.nextLine();
            history.push(String.format("[Session #%d] %s | %s | %dmin", ++total, type, topic, dur));
            System.out.println("  ✅ Session pushed to history stack.");
        }

        private void setDur(Scanner sc) {
            System.out.print("  Focus minutes [" + focus + "]: ");
            try { int v = Integer.parseInt(sc.nextLine().trim()); if (v > 0) focus = v; } catch (Exception ignored) {}
            System.out.print("  Break minutes [" + brk + "]: ");
            try { int v = Integer.parseInt(sc.nextLine().trim()); if (v > 0) brk = v; } catch (Exception ignored) {}
            System.out.println("  ✅ Updated.");
        }
    }


    // ── 4e. DSA Showcase ────────────────────────────────────────────────
    static class DSAShowcase {
        void menu(Scanner sc) {
            boolean back = false;
            while (!back) {
                System.out.println("\n  ╔══════════════════════════════════════════╗");
                System.out.println("  ║        🧮 DSA ALGORITHMS SHOWCASE        ║");
                System.out.println("  ╠══════════════════════════════════════════╣");
                System.out.println("  ║  1.  Sorting Algorithms Benchmark        ║");
                System.out.println("  ║  2.  Linear Search Demo                  ║");
                System.out.println("  ║  3.  Binary Search Demo                  ║");
                System.out.println("  ║  4.  Doubly Linked List Demo             ║");
                System.out.println("  ║  5.  Stack Demo (push/pop)               ║");
                System.out.println("  ║  6.  Circular Queue Demo                 ║");
                System.out.println("  ║  7.  Deque Demo                          ║");
                System.out.println("  ║  8.  Expression Evaluator (Stack App)    ║");
                System.out.println("  ║  9.  Symbol Balancer (Stack App)         ║");
                System.out.println("  ║  10. Binary Heap (Priority Queue) Demo   ║");
                System.out.println("  ║  11. Hash Table Demo                     ║");
                System.out.println("  ║  12. Polynomial ADT Demo                 ║");
                System.out.println("  ║  13. Complexity Analysis Table           ║");
                System.out.println("  ║  0.  Back                                ║");
                System.out.println("  ╚══════════════════════════════════════════╝");
                System.out.print("  Choice: ");
                String ch = sc.nextLine().trim();
                switch (ch) {
                    case "1"  -> sortBench(sc);
                    case "2"  -> linearDemo(sc);
                    case "3"  -> binaryDemo(sc);
                    case "4"  -> dllDemo();
                    case "5"  -> stackDemo();
                    case "6"  -> cqDemo();
                    case "7"  -> dequeDemo();
                    case "8"  -> exprDemo(sc);
                    case "9"  -> balanceDemo(sc);
                    case "10" -> heapDemo();
                    case "11" -> hashDemo();
                    case "12" -> polyDemo();
                    case "13" -> printComplexityAnalysis(1000);
                    case "0"  -> back = true;
                    default   -> System.out.println("  ❌ Invalid choice.");
                }
            }
        }

        private void sortBench(Scanner sc) {
            System.out.print("  Enter space-separated integers (e.g. 5 3 8 1 9 2): ");
            List<Integer> nums = new ArrayList<>();
            for (String p : sc.nextLine().trim().split("\\s+")) { try { nums.add(Integer.parseInt(p)); } catch (Exception ignored) {} }
            if (nums.isEmpty()) nums = Arrays.asList(64, 34, 25, 12, 22, 11, 90);
            System.out.println("\n  Input: " + nums + "\n");
            for (SortResult<Integer> r : new SortResult[]{ bubbleSort(nums), selectionSort(nums), insertionSort(nums), mergeSort(nums), quickSort(nums) }) {
                System.out.println("  " + r); System.out.println("  Sorted: " + r.sorted + "\n");
            }
        }

        private void linearDemo(Scanner sc) {
            List<String> data = Arrays.asList("Math", "Physics", "Chemistry", "Biology", "English", "History");
            System.out.println("  Dataset: " + data);
            System.out.print("  Search keyword: "); String kw = sc.nextLine().trim();
            var r = linearSearch(data, kw);
            System.out.println("  " + r); System.out.println("  Matches: " + r.found);
        }

        private void binaryDemo(Scanner sc) {
            List<Integer> sorted = Arrays.asList(2, 5, 8, 12, 16, 23, 38, 56, 72, 91);
            System.out.println("  Sorted list: " + sorted);
            System.out.print("  Target integer: ");
            try {
                int target = Integer.parseInt(sc.nextLine().trim());
                var r = binarySearch(sorted, target);
                System.out.println("  " + r);
                if (r.index >= 0) System.out.println("  ✅ Found at index " + r.index);
                else System.out.println("  ❌ Not found.");
            } catch (Exception e) { System.out.println("  ❌ Invalid number."); }
        }

        private void dllDemo() {
            DoublyLinkedList<String> dll = new DoublyLinkedList<>();
            System.out.println("\n  Building Doubly Linked List...");
            for (String s : new String[]{"Math", "Physics", "Chemistry"}) { dll.addLast(s); System.out.println("  addLast(\"" + s + "\") → " + dll); }
            dll.addFirst("Biology"); System.out.println("  addFirst(\"Biology\") → " + dll);
            System.out.println("  removeFirst() → " + dll.removeFirst());
            System.out.println("  After: " + dll + " | Size: " + dll.size());
        }

        private void stackDemo() {
            LinkedStack<String> stack = new LinkedStack<>();
            System.out.println("\n  Stack (Linked List — LIFO):");
            for (String v : new String[]{"A","B","C"}) { stack.push(v); System.out.println("  push(" + v + ") → " + stack); }
            System.out.println("  pop() → " + stack.pop() + " | " + stack);
            System.out.println("  peek() → " + stack.peek());
            System.out.println("  pop() → " + stack.pop() + " | " + stack);
        }

        private void cqDemo() {
            CircularQueue<String> cq = new CircularQueue<>(4);
            System.out.println("\n  Circular Queue (capacity=4):");
            cq.enqueue("Session1"); System.out.println("  enqueue(Session1) → " + cq);
            cq.enqueue("Session2"); System.out.println("  enqueue(Session2) → " + cq);
            cq.enqueue("Session3"); System.out.println("  enqueue(Session3) → " + cq);
            System.out.println("  dequeue() → " + cq.dequeue() + " | " + cq);
            cq.enqueue("Session4"); System.out.println("  enqueue(Session4) → " + cq);
            System.out.println("  isFull: " + cq.isFull());
        }

        private void dequeDemo() {
            Deque<String> dq = new Deque<>();
            System.out.println("\n  Deque (Double-Ended Queue):");
            dq.addRear("C");  System.out.println("  addRear(C)  → " + dq);
            dq.addFront("B"); System.out.println("  addFront(B) → " + dq);
            dq.addFront("A"); System.out.println("  addFront(A) → " + dq);
            System.out.println("  removeFront() → " + dq.removeFront() + " | " + dq);
        }

        private void exprDemo(Scanner sc) {
            System.out.print("  Enter infix expression (e.g. 3+5*2): "); String expr = sc.nextLine().trim();
            try {
                String postfix = infixToPostfix(expr);
                double result  = evaluatePostfix(postfix);
                System.out.println("  Infix   : " + expr);
                System.out.println("  Postfix : " + postfix);
                System.out.printf("  Result  : %.4f%n", result);
            } catch (Exception e) { System.out.println("  ❌ Error: " + e.getMessage()); }
        }

        private void balanceDemo(Scanner sc) {
            System.out.print("  Enter expression to check (e.g. {[()]}): "); String expr = sc.nextLine().trim();
            System.out.println("  " + expr + " → " + (isBalanced(expr) ? "✅ BALANCED" : "❌ NOT BALANCED"));
        }

        private void heapDemo() {
            int[] vals = {15, 3, 28, 7, 42, 1, 19};
            System.out.println("\n  Max-Heap input: " + Arrays.toString(vals));
            BinaryHeap<Integer> mh = new BinaryHeap<>(true);
            for (int v : vals) mh.insert(v);
            System.out.println("  Heap array: " + mh);
            StringBuilder sb = new StringBuilder("  Extracted (desc): ");
            while (!mh.isEmpty()) sb.append(mh.remove()).append(" ");
            System.out.println(sb);

            System.out.println("\n  Min-Heap input: " + Arrays.toString(vals));
            BinaryHeap<Integer> nh = new BinaryHeap<>(false);
            for (int v : vals) nh.insert(v);
            sb = new StringBuilder("  Extracted (asc): ");
            while (!nh.isEmpty()) sb.append(nh.remove()).append(" ");
            System.out.println(sb);
        }

        private void hashDemo() {
            System.out.println("\n  Hash Table (Separate Chaining + Rehashing) demo:");
            HashTable<String, String> ht = new HashTable<>(4);
            String[][] data = {{"math","Algebra notes"},{"physics","Kinematics"},{"cs","DSA"},{"bio","Cell theory"},{"chem","Periodic table"}};
            for (String[] kv : data) { System.out.print("  put(\"" + kv[0] + "\") → "); ht.put(kv[0], kv[1]); System.out.println("done"); }
            ht.printStats();
            System.out.println("  get(\"cs\") → " + ht.get("cs"));
            ht.remove("math"); System.out.println("  Removed \"math\". Size: " + ht.size());
        }

        private void polyDemo() {
            System.out.println("\n  Polynomial ADT (Linked List):");
            Polynomial p1 = new Polynomial(); p1.addTerm(3,4); p1.addTerm(-2,2); p1.addTerm(5,0);
            Polynomial p2 = new Polynomial(); p2.addTerm(1,3); p2.addTerm(2,2); p2.addTerm(-4,0);
            System.out.println("  P1    = " + p1);
            System.out.println("  P2    = " + p2);
            System.out.println("  P1+P2 = " + Polynomial.add(p1, p2));
            System.out.printf("  P1 at x=2 → %.2f%n", p1.evaluate(2));
        }
    }


    // ═══════════════════════════════════════════════════════════════════
    //  SECTION 5 — MAIN APPLICATION
    // ═══════════════════════════════════════════════════════════════════

    private static String loggedInUser = null;
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        printBanner();
        authFlow();
        dashboardLoop();
        System.out.println("\n  👋 Goodbye, " + loggedInUser + "! Happy studying!");
        sc.close();
    }

    private static void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════════╗");
        System.out.println("  ║  ███████╗███╗  ███╗ █████╗ ██████╗ ████████╗        ║");
        System.out.println("  ║  ██╔════╝████╗████║██╔══██╗██╔══██╗╚══██╔══╝        ║");
        System.out.println("  ║  ███████╗██╔████╔██║███████║██████╔╝   ██║           ║");
        System.out.println("  ║  ╚════██║██║╚██╔╝██║██╔══██║██╔══██╗   ██║           ║");
        System.out.println("  ║  ███████║██║ ╚═╝ ██║██║  ██║██║  ██║   ██║           ║");
        System.out.println("  ║  ╚══════╝╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝           ║");
        System.out.println("  ║          SMART STUDY PLANNER  v1.0  (DSA Edition)   ║");
        System.out.println("  ╚══════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void authFlow() {
        System.out.println("  ┌─── Authentication ───────────────────────────────────┐");
        System.out.println("  │  1. Login    2. Sign Up                              │");
        System.out.println("  └──────────────────────────────────────────────────────┘");
        System.out.print("  Choice: "); sc.nextLine();

        while (true) {
            System.out.print("  Your name     (min 4 chars) : "); String name  = sc.nextLine().trim();
            System.out.print("  Your email    (must have @) : "); String email = sc.nextLine().trim();
            System.out.print("  Your password (min 6 chars) : "); String pass  = sc.nextLine().trim();

            if (name.length() < 4 || pass.length() < 6 || !email.contains("@")) {
                System.out.println("\n  ❌ Validation failed:");
                if (name.length() < 4)    System.out.println("     • Name must be at least 4 characters");
                if (!email.contains("@")) System.out.println("     • Email must contain @");
                if (pass.length() < 6)    System.out.println("     • Password must be at least 6 characters");
                System.out.println("  Please try again.\n");
            } else {
                loggedInUser = name;
                System.out.println("\n  ✅ Welcome, " + name + "! Logged in successfully.");
                break;
            }
        }
    }

    private static void dashboardLoop() {
        TaskManager     tm = new TaskManager();
        NotesManager    nm = new NotesManager();
        RemindersManager rm = new RemindersManager();
        PomodoroModule  pm = new PomodoroModule();
        DSAShowcase     ds = new DSAShowcase();

        boolean running = true;
        while (running) {
            System.out.println("\n  ╔══════════════════════════════════════════╗");
            System.out.printf( "  ║  👤 %-38s║%n", loggedInUser);
            System.out.println("  ║            📊 DASHBOARD                  ║");
            System.out.println("  ╠══════════════════════════════════════════╣");
            System.out.println("  ║  1.  📋 Study Planner (Tasks)            ║");
            System.out.println("  ║  2.  🍅 Pomodoro Timer                   ║");
            System.out.println("  ║  3.  📝 Knowledge Notes                  ║");
            System.out.println("  ║  4.  ⏰ Reminders                        ║");
            System.out.println("  ║  5.  🧮 DSA Algorithms Showcase          ║");
            System.out.println("  ║  6.  ℹ️  About & Course Outcomes          ║");
            System.out.println("  ║  0.  🚪 Logout                           ║");
            System.out.println("  ╚══════════════════════════════════════════╝");
            System.out.print("  Choice: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1" -> tm.menu(sc);
                case "2" -> pm.menu(sc);
                case "3" -> nm.menu(sc);
                case "4" -> rm.menu(sc);
                case "5" -> ds.menu(sc);
                case "6" -> showAbout();
                case "0" -> running = false;
                default  -> System.out.println("  ❌ Invalid choice.");
            }
        }
    }

    private static void showAbout() {
        System.out.println("\n  ╔══════════════════════════════════════════════════════════╗");
        System.out.println("  ║                COURSE OUTCOMES MAPPING                   ║");
        System.out.println("  ╠══════════════════════════════════════════════════════════╣");
        System.out.println("  ║ CO1: Internet Fundamentals, HTML & CSS Styling           ║");
        System.out.println("  ║ CO2: HTML Forms, Semantic Tags & CSS Layouts             ║");
        System.out.println("  ║ CO3: JavaScript Programming Essentials                   ║");
        System.out.println("  ║ CO4: JavaScript DOM, Storage, Async                      ║");
        System.out.println("  ║ CO5: Advanced Web Dev & Deployment                       ║");
        System.out.println("  ╠══════════════════════════════════════════════════════════╣");
        System.out.println("  ║ DSA TOPICS IMPLEMENTED:                                  ║");
        System.out.println("  ║  ✓ Linear Search O(n) & Binary Search O(log n)           ║");
        System.out.println("  ║  ✓ Bubble, Selection, Insertion Sort  O(n²)              ║");
        System.out.println("  ║  ✓ Merge Sort & Quick Sort  O(n log n)                   ║");
        System.out.println("  ║  ✓ List ADT — Doubly Linked List                         ║");
        System.out.println("  ║  ✓ Stack ADT — Infix→Postfix, Eval, Balancer             ║");
        System.out.println("  ║  ✓ Queue ADT — Circular Queue, Linked Queue, Deque       ║");
        System.out.println("  ║  ✓ Binary Max/Min Heap — Priority Queue                  ║");
        System.out.println("  ║  ✓ Hash Table — Separate Chaining + Rehashing            ║");
        System.out.println("  ║  ✓ Polynomial ADT via Linked List                        ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════╝");
    }
}