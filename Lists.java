package dsaproject;

class Node {
    Task data;
    Node next, prev;
    public Node(Task data) { this.data = data; this.next = null; this.prev = null; }
}

public class Lists {
    static class SinglyLinkedList {
        Node head;
        public void add(Task data) {
            Node newNode = new Node(data);
            if (head == null) { head = newNode; return; }
            Node temp = head;
            while (temp.next != null) temp = temp.next;
            temp.next = newNode;
        }
        public void display() {
            if (head == null) { System.out.println("List is empty."); return; }
            Node temp = head;
            while(temp != null) { System.out.println(temp.data); temp = temp.next; }
        }
    }

    static class DoublyLinkedList {
        Node head, tail;
        public void add(Task data) {
            Node newNode = new Node(data);
            if (head == null) { head = tail = newNode; return; }
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        public void displayForward() {
            if (head == null) { System.out.println("List is empty."); return; }
            Node temp = head;
            while(temp != null) { System.out.println(temp.data); temp = temp.next; }
        }
        public void displayBackward() {
            if (tail == null) { System.out.println("List is empty."); return; }
            Node temp = tail;
            while(temp != null) { System.out.println(temp.data); temp = temp.prev; }
        }
    }

    static class CircularLinkedList {
        Node head, tail;
        public void add(Task data) {
            Node newNode = new Node(data);
            if (head == null) { head = tail = newNode; head.next = head; return; }
            tail.next = newNode;
            newNode.next = head;
            tail = newNode;
        }
        public void display() {
            if (head == null) { System.out.println("List is empty."); return; }
            Node temp = head;
            do { System.out.println(temp.data); temp = temp.next; } while (temp != head);
        }
    }

    // Polynomial ADT Helper Classes
    static class PolyNode {
        int coeff, power;
        PolyNode next;
        public PolyNode(int c, int p) { coeff = c; power = p; next = null; }
    }

    static class Polynomial {
        PolyNode head;
        public void addTerm(int c, int p) {
            PolyNode newNode = new PolyNode(c, p);
            if (head == null) { head = newNode; return; }
            PolyNode temp = head;
            while (temp.next != null) temp = temp.next;
            temp.next = newNode;
        }
        public void display() {
            if (head == null) { System.out.println("Empty polynomial."); return; }
            PolyNode temp = head;
            while(temp != null) {
                System.out.print(temp.coeff + "x^" + temp.power);
                if(temp.next != null) System.out.print(" + ");
                temp = temp.next;
            }
            System.out.println();
        }
    }
}
