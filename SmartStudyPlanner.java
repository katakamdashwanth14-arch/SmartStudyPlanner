package dsaproject;

import java.util.Scanner;
import java.util.Stack;

public class SmartStudyPlanner {

    private static String[] userTopics = new String[0];

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("==================================================");
        System.out.println("          WELCOME TO SMART STUDY PLANNER          ");
        System.out.println("==================================================");
        System.out.print("Enter your Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your Password: ");
        String password = scanner.nextLine();
        
        System.out.println("\nLogin Successful! Welcome, " + username + ".");
        
        boolean exit = false;

        while (!exit) {
            System.out.println("\n===== MAIN DASHBOARD =====");
            System.out.println("1. Study Topic Management");
            System.out.println("2. Course Syllabus & Goals Tracker");
            System.out.println("3. Task Managers & Calculators");
            System.out.println("4. Urgent Deadline Management");
            System.out.println("5. Subject Index & Dictionary");
            System.out.println("0. Logout and Exit");
            System.out.print("Choose a category (0-5): ");

            int choice = -1;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } else {
                scanner.next();
                System.out.println("Invalid input!");
                continue;
            }

            switch (choice) {
                case 1:
                    runStudyTopicMenu(scanner);
                    break;
                case 2:
                    runSyllabusMenu(scanner);
                    break;
                case 3:
                    runTasksCalculatorsMenu(scanner);
                    break;
                case 4:
                    runDeadlinesMenu(scanner);
                    break;
                case 5:
                    runSubjectIndexMenu(scanner);
                    break;
                case 0:
                    exit = true;
                    System.out.println("Logging out... Good luck with your studies, " + username + "!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
        scanner.close();
    }

    // ==========================================
    // MODULE 1: TOPIC MANAGEMENT (Backend: Search & Sort)
    // ==========================================
    private static void runStudyTopicMenu(Scanner sc) {
        boolean back = false;
        while (!back) {
            System.out.println("\n-- Study Topic Management --");
            System.out.println("1. Input Study Topics");
            System.out.println("2. Display My Topics");
            System.out.println("3. Basic Topic Search");
            System.out.println("4. Quick Topic Search");
            System.out.println("5. Organize Topics Method 1");
            System.out.println("6. Organize Topics Method 2");
            System.out.println("7. Organize Topics Method 3");
            System.out.println("8. Organize Topics Method 4");
            System.out.println("9. Organize Topics Method 5");
            System.out.println("0. Back to Dashboard");
            System.out.print("Select action: ");
            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    System.out.print("How many topics do you want to add? ");
                    int count = sc.nextInt();
                    sc.nextLine();
                    userTopics = new String[count];
                    for (int i = 0; i < count; i++) {
                        System.out.print("Enter topic #" + (i + 1) + ": ");
                        userTopics[i] = sc.nextLine();
                    }
                    System.out.println("Topics saved successfully.");
                    break;
                case 2:
                    if (userTopics.length == 0) System.out.println("No topics available. Add some first.");
                    else printArray(userTopics);
                    break;
                case 3:
                    if (userTopics.length == 0) { System.out.println("No topics available."); break; }
                    System.out.print("Enter topic name to find: ");
                    String target = sc.nextLine();
                    // Backend: Linear Search
                    int linearIdx = SearchSortHelper.linearSearch(userTopics, target);
                    System.out.println(linearIdx >= 0 ? "Topic found at position " + (linearIdx + 1) : "Topic not found.");
                    break;
                case 4:
                    if (userTopics.length == 0) { System.out.println("No topics available."); break; }
                    System.out.print("Enter topic name to find (list must be organized first): ");
                    String binTarget = sc.nextLine();
                    // Backend: Binary Search
                    int binIdx = SearchSortHelper.binarySearch(userTopics, binTarget);
                    System.out.println(binIdx >= 0 ? "Topic found at position " + (binIdx + 1) : "Topic not found.");
                    break;
                case 5:
                    if (userTopics.length == 0) { System.out.println("No list to organize."); break; }
                    // Backend: Bubble Sort
                    SearchSortHelper.bubbleSort(userTopics);
                    System.out.println("List organized successfully (Method 1).");
                    printArray(userTopics);
                    break;
                case 6:
                    if (userTopics.length == 0) { System.out.println("No list to organize."); break; }
                    // Backend: Insertion Sort
                    SearchSortHelper.insertionSort(userTopics);
                    System.out.println("List organized successfully (Method 2).");
                    printArray(userTopics);
                    break;
                case 7:
                    if (userTopics.length == 0) { System.out.println("No list to organize."); break; }
                    // Backend: Selection Sort
                    SearchSortHelper.selectionSort(userTopics);
                    System.out.println("List organized successfully (Method 3).");
                    printArray(userTopics);
                    break;
                case 8:
                    if (userTopics.length == 0) { System.out.println("No list to organize."); break; }
                    // Backend: Merge Sort
                    SearchSortHelper.mergeSort(userTopics, 0, userTopics.length - 1);
                    System.out.println("List organized successfully (Method 4).");
                    printArray(userTopics);
                    break;
                case 9:
                    if (userTopics.length == 0) { System.out.println("No list to organize."); break; }
                    // Backend: Quick Sort
                    SearchSortHelper.quickSort(userTopics, 0, userTopics.length - 1);
                    System.out.println("List organized successfully (Method 5).");
                    printArray(userTopics);
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid.");
            }
        }
    }

    private static void printArray(String[] arr) {
        System.out.print("Current Topics: [ ");
        for (String s : arr) System.out.print("'" + s + "' ");
        System.out.println("]");
    }

    // ==========================================
    // MODULE 2: SYLLABUS & GOALS (Backend: Lists & Poly ADT)
    // ==========================================
    private static void runSyllabusMenu(Scanner sc) {
        SinglyLinkedList sll = new SinglyLinkedList();
        DoublyLinkedList dll = new DoublyLinkedList();
        CircularLinkedList cll = new CircularLinkedList();

        boolean back = false;
        while (!back) {
            System.out.println("\n-- Course Syllabus & Goals Tracker --");
            System.out.println("1. Add a Main Study Goal");
            System.out.println("2. Add a Timeline Event");
            System.out.println("3. Add a Repeating Study Habit");
            System.out.println("4. Calculate Combined Course Workloads");
            System.out.println("0. Back to Dashboard");
            System.out.print("Choose action: ");
            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    System.out.print("Enter Goal description: ");
                    sll.add(sc.nextLine());
                    System.out.print("Current Goals: "); sll.printList();
                    break;
                case 2:
                    System.out.print("Enter Timeline Event: ");
                    dll.add(sc.nextLine());
                    System.out.print("Current Timeline: "); dll.printList();
                    break;
                case 3:
                    System.out.print("Enter Habit: ");
                    cll.add(sc.nextLine());
                    System.out.print("Habit Loop: "); cll.printList();
                    break;
                case 4:
                    System.out.println("Simulating combining two different workloads / weights...");
                    Polynomial p1 = new Polynomial();
                    p1.addTerm(3, 2); p1.addTerm(5, 1);
                    Polynomial p2 = new Polynomial();
                    p2.addTerm(4, 2); p2.addTerm(-1, 1); p2.addTerm(2, 0);
                    System.out.print("Course 1 Weights: "); p1.printP();
                    System.out.print("Course 2 Weights: "); p2.printP();
                    Polynomial p3 = Polynomial.add(p1, p2);
                    System.out.print("Total Workload: "); p3.printP();
                    break;
                case 0:
                    back = true;
                    break;
            }
        }
    }

    // ==========================================
    // MODULE 3: TASKS & CALCULATORS (Backend: Stacks & Queues)
    // ==========================================
    private static void runTasksCalculatorsMenu(Scanner sc) {
        boolean back = false;
        while (!back) {
            System.out.println("\n-- Task Managers & Calculators --");
            System.out.println("1. Verify Math Expression Brackets");
            System.out.println("2. Format Math Expression for Machine");
            System.out.println("3. Calculate Machine-formatted Expression");
            System.out.println("4. Cycle Study Breaks");
            System.out.println("5. Manage Two-Way Task List");
            System.out.println("6. Note & Job Tracker Simulation");
            System.out.println("0. Back to Dashboard");
            System.out.print("Choose action: ");
            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    System.out.print("Enter expression to check brackets (e.g. {[(x)]}): ");
                    System.out.println("Result: " + (StackApplications.isBalanced(sc.nextLine()) ? "Syntax OK" : "Syntax Error - Mismatched Brackets"));
                    break;
                case 2:
                    System.out.print("Enter standard math expression (e.g. A+B): ");
                    System.out.println("Machine Format: " + StackApplications.infixToPostfix(sc.nextLine()));
                    break;
                case 3:
                    System.out.print("Enter machine format numbers (e.g. 52+): ");
                    System.out.println("Result Value: " + StackApplications.evaluatePostfix(sc.nextLine()));
                    break;
                case 4:
                    System.out.println("Managing Break Queue...");
                    CircularQueue cq = new CircularQueue(3);
                    cq.enqueue("Break 1 (10m)"); cq.enqueue("Break 2 (15m)"); cq.enqueue("Break 3 (5m)");
                    cq.display();
                    System.out.println("Taking Break: " + cq.dequeue());
                    System.out.println("Adding new break...");
                    cq.enqueue("Break 4 (20m)");
                    cq.display();
                    break;
                case 5:
                    System.out.println("Double Ended Task Tracker...");
                    DequeArray deq = new DequeArray(5);
                    deq.insertRear("Read Chapter 1"); deq.insertFront("Urgent: Reply Email"); deq.insertRear("Write Code");
                    System.out.println("Finished Front Task: " + deq.deleteFront());
                    System.out.println("Finished Rear Task: " + deq.deleteRear());
                    break;
                case 6:
                    LinkedStack ls = new LinkedStack();
                    ls.push("Draft Notes"); ls.push("Final Notes");
                    System.out.println("Taking latest Note off stack: " + ls.pop());
                    LinkedQueue lq = new LinkedQueue();
                    lq.enqueue("Download File 1"); lq.enqueue("Download File 2");
                    System.out.println("Processing first queued Job: " + lq.dequeue());
                    break;
                case 0:
                    back = true;
                    break;
            }
        }
    }

    // ==========================================
    // MODULE 4: DEADLINES (Backend: Priority Queues/Heaps)
    // ==========================================
    private static void runDeadlinesMenu(Scanner sc) {
        BinaryMinHeap heap = new BinaryMinHeap(10);
        boolean back = false;
        while (!back) {
            System.out.println("\n-- Urgent Deadline Management --");
            System.out.println("1. Add a Task with Urgency Level (Lower number = higher urgency)");
            System.out.println("2. Address Most Urgent Task");
            System.out.println("3. View Overall Urgency Statuses");
            System.out.println("0. Back to Dashboard");
            System.out.print("Choose action: ");
            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    System.out.print("Enter urgency level for task (1 for most urgent, higher for less): ");
                    heap.insert(sc.nextInt());
                    sc.nextLine();
                    System.out.println("Task registered.");
                    break;
                case 2:
                    try {
                        System.out.println("You should now work on task with Urgency Level: " + heap.extractMin());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 3:
                    heap.display();
                    break;
                case 0:
                    back = true;
                    break;
            }
        }
    }

    // ==========================================
    // MODULE 5: SUBJECT INDEX (Backend: Hashing)
    // ==========================================
    private static void runSubjectIndexMenu(Scanner sc) {
        SeparateChaining scHash = new SeparateChaining(5);
        OpenAddressing oaHash = new OpenAddressing(7);
        boolean back = false;
        while (!back) {
            System.out.println("\n-- Subject Index & Dictionary --");
            System.out.println("1. Add Subject Code (Storage Method 1)");
            System.out.println("2. Add Subject Code (Storage Method 2)");
            System.out.println("3. Verify if Subject Code Exists");
            System.out.println("0. Back to Dashboard");
            System.out.print("Choose action: ");
            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    System.out.print("Enter subject/course code (numeric) for System A: ");
                    scHash.insert(sc.nextInt());
                    System.out.println("Current Index map:");
                    scHash.display();
                    break;
                case 2:
                    System.out.print("Enter subject/course code (numeric) for System B: ");
                    oaHash.insert(sc.nextInt());
                    System.out.println("Current Index map:");
                    oaHash.display();
                    break;
                case 3:
                    System.out.print("Enter subject/course code to verify: ");
                    int k = sc.nextInt();
                    System.out.println("Record exists in System B? " + oaHash.search(k));
                    break;
                case 0:
                    back = true;
                    break;
            }
        }
    }
}

// -------------------------------------------------------------
// CLASSES FOR MODULE 1: SEARCHING AND SORTING
// -------------------------------------------------------------
class SearchSortHelper {
    public static int linearSearch(String[] arr, String target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equalsIgnoreCase(target)) return i;
        }
        return -1;
    }

    public static int binarySearch(String[] arr, String target) {
        int left = 0, right = arr.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int cmp = arr[mid].compareToIgnoreCase(target);
            if (cmp == 0) return mid;
            if (cmp < 0) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }

    public static void bubbleSort(String[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j].compareToIgnoreCase(arr[j + 1]) > 0) {
                    swap(arr, j, j + 1);
                }
            }
        }
    }

    public static void insertionSort(String[] arr) {
        for (int i = 1; i < arr.length; ++i) {
            String key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j].compareToIgnoreCase(key) > 0) {
                arr[j + 1] = arr[j];
                j = j - 1;
            }
            arr[j + 1] = key;
        }
    }

    public static void selectionSort(String[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j].compareToIgnoreCase(arr[minIdx]) < 0) {
                    minIdx = j;
                }
            }
            swap(arr, minIdx, i);
        }
    }

    public static void mergeSort(String[] arr, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    private static void merge(String[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        String[] L = new String[n1];
        String[] R = new String[n2];
        for (int i = 0; i < n1; ++i) L[i] = arr[left + i];
        for (int j = 0; j < n2; ++j) R[j] = arr[mid + 1 + j];

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (L[i].compareToIgnoreCase(R[j]) <= 0) {
                arr[k++] = L[i++];
            } else {
                arr[k++] = R[j++];
            }
        }
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    public static void quickSort(String[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private static int partition(String[] arr, int low, int high) {
        String pivot = arr[high];
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (arr[j].compareToIgnoreCase(pivot) < 0) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private static void swap(String[] arr, int i, int j) {
        String temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}

// -------------------------------------------------------------
// CLASSES FOR MODULE 2: LISTS AND POLYNOMIAL ADT
// -------------------------------------------------------------
class Node {
    String data;
    Node next;
    Node(String d) { data = d; next = null; }
}

class SinglyLinkedList {
    Node head;
    public void add(String d) {
        Node newNode = new Node(d);
        if (head == null) { head = newNode; } 
        else {
            Node temp = head;
            while(temp.next != null) temp = temp.next;
            temp.next = newNode;
        }
    }
    public void printList() {
        Node temp = head;
        while(temp != null) { System.out.print(temp.data + " -> "); temp = temp.next; }
        System.out.println("null");
    }
}

class DNode {
    String data; DNode prev, next;
    DNode(String d) { data = d; }
}

class DoublyLinkedList {
    DNode head;
    public void add(String d) {
        DNode newNode = new DNode(d);
        if (head == null) { head = newNode; }
        else {
            DNode temp = head;
            while(temp.next != null) temp = temp.next;
            temp.next = newNode;
            newNode.prev = temp;
        }
    }
    public void printList() {
        DNode temp = head;
        while(temp != null) { System.out.print(temp.data + " <-> "); temp = temp.next; }
        System.out.println("null");
    }
}

class CircularLinkedList {
    Node head, tail;
    public void add(String d) {
        Node newNode = new Node(d);
        if (head == null) {
            head = newNode; tail = newNode; newNode.next = head;
        } else {
            tail.next = newNode;
            tail = newNode;
            tail.next = head;
        }
    }
    public void printList() {
        if (head == null) return;
        Node temp = head;
        do {
            System.out.print(temp.data + " -> ");
            temp = temp.next;
        } while(temp != head);
        System.out.println("(head)");
    }
}

class PolyNode {
    int coeff, exp;
    PolyNode next;
    PolyNode(int c, int e) { coeff = c; exp = e; }
}

class Polynomial {
    PolyNode head;
    public void addTerm(int c, int e) {
        PolyNode nn = new PolyNode(c, e);
        if (head == null) head = nn;
        else {
            PolyNode temp = head;
            while(temp.next != null) temp = temp.next;
            temp.next = nn;
        }
    }
    public static Polynomial add(Polynomial p1, Polynomial p2) {
        Polynomial res = new Polynomial();
        PolyNode t1 = p1.head, t2 = p2.head;
        while(t1 != null && t2 != null) {
            if (t1.exp > t2.exp) { res.addTerm(t1.coeff, t1.exp); t1 = t1.next; }
            else if (t1.exp < t2.exp) { res.addTerm(t2.coeff, t2.exp); t2 = t2.next; }
            else { res.addTerm(t1.coeff + t2.coeff, t1.exp); t1 = t1.next; t2 = t2.next; }
        }
        while(t1 != null) { res.addTerm(t1.coeff, t1.exp); t1 = t1.next; }
        while(t2 != null) { res.addTerm(t2.coeff, t2.exp); t2 = t2.next; }
        return res;
    }
    public void printP() {
        PolyNode t = head;
        while(t != null) {
            System.out.print(t.coeff + "x^" + t.exp + (t.next != null ? " + " : ""));
            t = t.next;
        }
        System.out.println();
    }
}

// -------------------------------------------------------------
// CLASSES FOR MODULE 3: STACKS AND QUEUES
// -------------------------------------------------------------
class StackApplications {
    public static boolean isBalanced(String expr) {
        Stack<Character> stack = new Stack<>();
        for (char ch : expr.toCharArray()) {
            if (ch == '(' || ch == '{' || ch == '[') stack.push(ch);
            else if (ch == ')' || ch == '}' || ch == ']') {
                if (stack.isEmpty()) return false;
                char top = stack.pop();
                if ((ch == ')' && top != '(') || (ch == '}' && top != '{') || (ch == ']' && top != '[')) return false;
            }
        }
        return stack.isEmpty();
    }

    public static int precedence(char ch) {
        switch (ch) {
            case '+': case '-': return 1;
            case '*': case '/': return 2;
            case '^': return 3;
        }
        return -1;
    }

    public static String infixToPostfix(String exp) {
        StringBuilder result = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);
            if (Character.isLetterOrDigit(c)) result.append(c);
            else if (c == '(') stack.push(c);
            else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') result.append(stack.pop());
                stack.pop();
            } else {
                while (!stack.isEmpty() && precedence(c) <= precedence(stack.peek())) {
                    result.append(stack.pop());
                }
                stack.push(c);
            }
        }
        while (!stack.isEmpty()) {
            if (stack.peek() == '(') return "Invalid Expression";
            result.append(stack.pop());
        }
        return result.toString();
    }

    public static int evaluatePostfix(String exp) {
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);
            if (Character.isDigit(c)) stack.push(c - '0');
            else {
                int val1 = stack.pop(), val2 = stack.pop();
                switch (c) {
                    case '+': stack.push(val2 + val1); break;
                    case '-': stack.push(val2 - val1); break;
                    case '*': stack.push(val2 * val1); break;
                    case '/': stack.push(val2 / val1); break;
                }
            }
        }
        return stack.pop();
    }
}

class CircularQueue {
    String[] arr; int front, rear, size, capacity;
    CircularQueue(int cap) {
        capacity = cap; arr = new String[capacity]; front = size = 0; rear = capacity - 1;
    }
    public void enqueue(String item) {
        if (size == capacity) { System.out.println("Queue is full"); return; }
        rear = (rear + 1) % capacity; arr[rear] = item; size++;
    }
    public String dequeue() {
        if (size == 0) return null;
        String item = arr[front]; front = (front + 1) % capacity; size--; return item;
    }
    public void display() {
        if (size == 0) { System.out.println("Empty"); return; }
        System.out.print("Current Queue Schedule: ");
        for(int i = 0; i < size; i++) System.out.print("[" + arr[(front + i) % capacity] + "] ");
        System.out.println();
    }
}

class DequeArray {
    int arr[], front, rear, size, capacity;
    DequeArray(int cap) {
        capacity = cap; arr = new int[capacity]; front = -1; rear = 0; size = 0;
    }
    boolean isFull() { return size == capacity; }
    boolean isEmpty() { return size == 0; }
    void insertFront(int key) {
        if (isFull()) return;
        if (front == -1) { front = 0; rear = 0; }
        else if (front == 0) front = capacity - 1;
        else front = front - 1;
        arr[front] = key; size++;
    }
    void insertFront(String key) { insertFront(key.hashCode()); }
    void insertRear(String key) { insertRear(key.hashCode()); }
    void insertRear(int key) {
        if (isFull()) return;
        if (front == -1) { front = 0; rear = 0; }
        else if (rear == capacity - 1) rear = 0;
        else rear = rear + 1;
        arr[rear] = key; size++;
    }
    int deleteFront() {
        if (isEmpty()) return -1;
        int ret = arr[front];
        if (front == rear) { front = -1; rear = -1; }
        else if (front == capacity - 1) front = 0;
        else front = front + 1;
        size--; return ret;
    }
    int deleteRear() {
        if (isEmpty()) return -1;
        int ret = arr[rear];
        if (front == rear) { front = -1; rear = -1; }
        else if (rear == 0) rear = capacity - 1;
        else rear = rear - 1;
        size--; return ret;
    }
}

class LinkedStack {
    Node top;
    void push(String d) { Node nn = new Node(d); nn.next = top; top = nn; }
    String pop() {
        if (top == null) return null;
        String v = top.data; top = top.next; return v;
    }
}

class LinkedQueue {
    Node front, rear;
    void enqueue(String d) {
        Node nn = new Node(d);
        if (rear == null) { front = rear = nn; return; }
        rear.next = nn; rear = nn;
    }
    String dequeue() {
        if (front == null) return null;
        String v = front.data; front = front.next;
        if (front == null) rear = null;
        return v;
    }
}


// -------------------------------------------------------------
// CLASSES FOR MODULE 4: HEAPS
// -------------------------------------------------------------
class BinaryMinHeap {
    private int[] heap;
    private int size;
    private int capacity;

    public BinaryMinHeap(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.heap = new int[capacity];
    }

    private int parent(int i) { return (i - 1) / 2; }
    private int left(int i) { return 2 * i + 1; }
    private int right(int i) { return 2 * i + 2; }
    private void swap(int i, int j) { int t = heap[i]; heap[i] = heap[j]; heap[j] = t; }

    public void insert(int val) {
        if (size == capacity) { System.out.println("Status: Capacity Full"); return; }
        int i = size++;
        heap[i] = val;
        while (i != 0 && heap[parent(i)] > heap[i]) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    public int extractMin() throws Exception {
        if (size <= 0) throw new Exception("Status: No records available");
        if (size == 1) return heap[--size];
        int root = heap[0];
        heap[0] = heap[--size];
        minHeapify(0);
        return root;
    }

    private void minHeapify(int i) {
        int l = left(i), r = right(i), smallest = i;
        if (l < size && heap[l] < heap[smallest]) smallest = l;
        if (r < size && heap[r] < heap[smallest]) smallest = r;
        if (smallest != i) {
            swap(i, smallest);
            minHeapify(smallest);
        }
    }

    public void display() {
        System.out.print("System Status Tracker: ");
        for (int i=0; i<size; i++) System.out.print(heap[i] + " ");
        System.out.println();
    }
}


// -------------------------------------------------------------
// CLASSES FOR MODULE 5: HASHING
// -------------------------------------------------------------
class HashNode {
    int key; HashNode next;
    HashNode(int k) { key = k; }
}

class SeparateChaining {
    HashNode[] table;
    int size;
    SeparateChaining(int size) {
        this.size = size;
        table = new HashNode[size];
    }
    void insert(int key) {
        int idx = key % size;
        HashNode nn = new HashNode(key);
        if (table[idx] == null) table[idx] = nn;
        else {
            HashNode temp = table[idx];
            while(temp.next != null) temp = temp.next;
            temp.next = nn;
        }
    }
    void display() {
        for(int i=0; i<size; i++) {
            System.out.print("Storage Bin " + i + ": ");
            HashNode temp = table[i];
            while(temp != null) { System.out.print(temp.key + " -> "); temp = temp.next; }
            System.out.println("End");
        }
    }
}

class OpenAddressing {
    Integer[] table;
    int size;
    int count = 0;
    OpenAddressing(int size) {
        this.size = size; table = new Integer[size];
    }
    void insert(int key) {
        if (count == size) { System.out.println("Capacity Reached"); return; }
        int idx = key % size;
        while (table[idx] != null) {
            idx = (idx + 1) % size; // Linear probing
        }
        table[idx] = key; count++;
    }
    boolean search(int key) {
        int idx = key % size;
        int start = idx;
        while(table[idx] != null) {
            if (table[idx] == key) return true;
            idx = (idx + 1) % size;
            if (idx == start) break;
        }
        return false;
    }
    void display() {
        for(int i=0; i<size; i++) {
            System.out.println("Record Location " + i + ": " + (table[i] == null ? "Empty" : table[i]));
        }
    }
}
