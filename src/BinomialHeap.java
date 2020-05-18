public class BinomialHeap<T extends Comparable<T>> {
    public static class Node<T> {
        T key;
        Node<T> p;
        Node<T> sibling;
        Node<T> child;
        int degree;
    }
    Node<T> head;

    public BinomialHeap(){}

    private boolean isCorrectRecursive(Node<T> node){
        if(node==null) {
            return true;
        } if(node.p!=null && node.p.key.compareTo(node.key)>0){
            // key in parent needs to be smaller than key in children
            return false;
        } else {
            return isCorrectRecursive(node.sibling) && isCorrectRecursive(node.child);
        }
    }

    // used for testing
    boolean isCorrect(){
        return isCorrectRecursive(head);
    }

    public BinomialHeap(T[] array){
        for(T key : array){
            insert(key);
        }
    }

    public Node<T> insert(T key){
        BinomialHeap<T> newH=new BinomialHeap<>();
        Node<T> x= new Node<>();
        x.p=null;
        x.child=null;
        x.sibling=null;
        x.degree=0;
        x.key=key;
        newH.head=x;
        head=union(this, newH).head;
        return x;
    }

    Node<T> findRecursive(Node<T> node, T key){
        if(node==null){
            return null;
        } else if(node.key==key){
            return node;
        } else {
            Node<T> fromChild=findRecursive(node.child, key);
            if(fromChild!=null) {
                return fromChild;
            } else {
                return findRecursive(node.sibling, key);
            }
        }
    }

    Node<T> find(T key) {
        return findRecursive(head, key);
    }

    boolean contains(T key){
        return find(key)!=null;
    }

    Node<T> minimum(){
        Node<T> y=null;
        Node<T> x=head;
        T min=null;
        while(x!=null){
            if(min==null || x.key.compareTo(min)<0){
                min=x.key;
                y=x;
            }
            x=x.sibling;
        }
        return y;
    }

    private Node<T> extractMinRoot(){
        // Znajdź korzeń x z minimalnym kluczem na liście korzeni H
        // i usuń x z listy korzeni H
        Node<T> x = null;
        Node<T> previous = null;
        Node<T> x_previous = null;
        for (Node<T> current = head; current != null; previous = current, current = current.sibling) {
            if (x == null || current.key.compareTo(x.key) < 0) {
                x = current;
                x_previous = previous;
            }
        }
        if (x_previous != null) {
            x_previous.sibling = x.sibling;
        } else if(x!=null) {
            // x was head
            head = x.sibling;
        } else {
            head=null;
        }
        return x;
    }

    private Node<T> reverseChildren(Node<T> x){
        // Odwróć kolejność elementów na liście synów węzła x i zapamiętaj
        // wskaźnik do głowy wynikowej listy w zmiennej head[H‘]
        Node<T> previous = null;
        Node<T> current = x.child;
        while(current!=null) {
            Node<T> next = current.sibling;
            current.sibling = previous;
            previous=current;
            current=next;
        }
        return previous;
    }

    public Node<T> extractMin(){
        Node<T> x = extractMinRoot();
        BinomialHeap<T> newH=new BinomialHeap<>();
        newH.head=reverseChildren(x);
        head=union(this,newH).head;
        return x;
    }

    static <T> void BinomialLink(Node<T> y, Node<T> parent) {
        y.p = parent;
        y.sibling=parent.child;
        parent.child=y;
        parent.degree=parent.degree + 1;
    }

    private static <T extends Comparable<T>> Node<T> BinomialHeapMerge(BinomialHeap<T> H1, BinomialHeap<T> H2){
        Node<T> left=H1.head;
        Node<T> right=H2.head;

        Node<T> result=null;
        Node<T> current=null;
        while(left!=null || right!=null) {
            Node<T> toInsert;
            if (right == null) {
                toInsert = left;
                left=left.sibling;
            } else if (left == null) {
                toInsert = right;
                right=right.sibling;
            } else if (left.degree <= right.degree) {
                toInsert = left;
                left=left.sibling;
            } else {
                toInsert = right;
                right=right.sibling;
            }
            if(result==null){
                result=toInsert;
            } else {
                current.sibling=toInsert;
            }
            current=toInsert;
        }
        return result;
    }

    // H1 and H2 are destroyed in process
    public static <T extends Comparable<T>> BinomialHeap<T> union(BinomialHeap<T> H1, BinomialHeap<T> H2){
        BinomialHeap<T> H = new BinomialHeap<>();
        H.head=BinomialHeapMerge(H1, H2);
        // Zwolnij obiekty H1 and H2, ale nie listy, na które wskazują
        if (H.head==null) {
            return H;
        }
        Node<T> prev_x=null;
        Node<T> x=H.head;
        Node<T> next_x=x.sibling;
        while (next_x != null) {
            if ((x.degree != next_x.degree) || (next_x.sibling != null &&
                    next_x.sibling.degree == x.degree)) {
                prev_x = x; // przypadek 1 i 2
                x = next_x; // przypadek 1 i 2
            } else {
                if (x.key.compareTo(next_x.key)<=0) {
                    x.sibling = next_x.sibling; // przypadek 3
                    BinomialLink(next_x, x); // przypadek 3
                } else {
                    if (prev_x == null) { // przypadek 4
                        H.head = next_x; // przypadek 4
                    } else { // przypadek 4
                        prev_x.sibling = next_x; // przypadek 4
                    }
                    BinomialLink(x, next_x); // przypadek 4
                    x = next_x; // przypadek 4
                }
            }
            next_x = x.sibling;
        }
        // H1 and H2 are destroyed in process
        H1.head=null;
        H2.head=null;
        return H;
    }
    void decreaseKey(Node<T> x, T k){
        if(k.compareTo(x.key)>0) {
            throw new IllegalArgumentException("new key is greater than current key");
        }
        x.key=k;
        Node<T> y=x;
        Node<T> z=y.p;
        while(z!=null && y.key.compareTo(z.key)<0) {// key[y]<key[z]
            // exchange key[y]and key[z]
            T temp=y.key;
            y.key=z.key;
            z.key=temp;
            // if y and z have satellite fields, exchange them, too
            y=z;
            z=y.p;
        }
    }
    void delete(Node<T> x, T leastT){
        decreaseKey(x, leastT);
        extractMin();
    }
}
