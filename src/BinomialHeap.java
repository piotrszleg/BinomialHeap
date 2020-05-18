public class BinomialHeap<T extends Comparable<T>> {
    public static class Node<T> {
        private T key;
        private Node<T> parent;
        private Node<T> sibling;
        private Node<T> child;
        private int degree;

        void addChild(Node<T> child) {
            child.setParent(this);
            child.setSibling(getChild());
            setChild(child);
            setDegree(getDegree() + 1);
        }

        public T getKey() {
            return key;
        }

        public void setKey(T key) {
            this.key = key;
        }

        public Node<T> getParent() {
            return parent;
        }

        public void setParent(Node<T> parent) {
            this.parent = parent;
        }

        public Node<T> getSibling() {
            return sibling;
        }

        public void setSibling(Node<T> sibling) {
            this.sibling = sibling;
        }

        public Node<T> getChild() {
            return child;
        }

        public void setChild(Node<T> child) {
            this.child = child;
        }

        public int getDegree() {
            return degree;
        }

        public void setDegree(int degree) {
            this.degree = degree;
        }
    }
    Node<T> head;

    public BinomialHeap(){}

    private boolean isCorrectRecursive(Node<T> node){
        if(node==null) {
            return true;
        } if(node.getParent() !=null && node.getParent().getKey().compareTo(node.getKey())>0){
            // key in parent needs to be smaller than key in children
            return false;
        } else {
            return isCorrectRecursive(node.getSibling()) && isCorrectRecursive(node.getChild());
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

    /* This method is slower than the one below,
       but it won't generate duplicate nodes when we insert one element two times */
    public Node<T> insert(T key){
        Node<T> contained=find(key);
        if(contained==null){
            return insertNotContained(key);
        } else {
            return contained;
        }
    }

    public Node<T> insertNotContained(T key){
        BinomialHeap<T> newH=new BinomialHeap<>();
        Node<T> x= new Node<>();
        x.setParent(null);
        x.setChild(null);
        x.setSibling(null);
        x.setDegree(0);
        x.setKey(key);
        newH.head=x;
        head=union(this, newH).head;
        return x;
    }

    private Node<T> findRecursive(Node<T> node, T key){
        if(node==null){
            return null;
        } else if(node.getKey().compareTo(key)==0){
            return node;
        } else {
            Node<T> fromChild=null;
            // it only makes sense to check children if searched key is bigger than node.key
            if(node.getKey().compareTo(key)<0){
                fromChild=findRecursive(node.getChild(), key);
            }
            if(fromChild!=null) {
                return fromChild;
            } else {
                return findRecursive(node.getSibling(), key);
            }
        }
    }

    public Node<T> find(T key) {
        return findRecursive(head, key);
    }

    public boolean contains(T key){
        return find(key)!=null;
    }

    public Node<T> minimum(){
        Node<T> y=null;
        Node<T> x=head;
        T min=null;
        while(x!=null){
            if(min==null || x.getKey().compareTo(min)<0){
                min= x.getKey();
                y=x;
            }
            x= x.getSibling();
        }
        return y;
    }

    private Node<T> extractMinRoot(){
        Node<T> x = null;
        Node<T> previous = null;
        Node<T> x_previous = null;
        for (Node<T> current = head; current != null; previous = current, current = current.getSibling()) {
            if (x == null || current.getKey().compareTo(x.getKey()) < 0) {
                x = current;
                x_previous = previous;
            }
        }
        if (x_previous != null) {
            x_previous.setSibling(x.getSibling());
        } else if(x!=null) {
            // x was head
            head = x.getSibling();
        } else {
            head=null;
        }
        return x;
    }

    private Node<T> reverseChildren(Node<T> x){

        Node<T> previous = null;
        Node<T> current = x.getChild();
        while(current!=null) {
            Node<T> next = current.getSibling();
            current.setSibling(previous);
            previous=current;
            current=next;
        }
        return previous;
    }

    public Node<T> extractMin(){
        // Znajdź korzeń x z minimalnym kluczem na liście korzeni H
        // i usuń x z listy korzeni H
        Node<T> x = extractMinRoot();
        BinomialHeap<T> newH=new BinomialHeap<>();
        // Odwróć kolejność elementów na liście synów węzła x i zapamiętaj
        // wskaźnik do głowy wynikowej listy w zmiennej head[H‘]
        newH.head=reverseChildren(x);
        head=union(this,newH).head;
        return x;
    }

    private static <T extends Comparable<T>> Node<T> merge(BinomialHeap<T> H1, BinomialHeap<T> H2){
        Node<T> left=H1.head;
        Node<T> right=H2.head;

        Node<T> result=null;
        Node<T> current=null;
        while(left!=null || right!=null) {
            Node<T> toInsert;
            if (right == null) {
                toInsert = left;
                left= left.getSibling();
            } else if (left == null) {
                toInsert = right;
                right= right.getSibling();
            } else if (left.getDegree() <= right.getDegree()) {
                toInsert = left;
                left= left.getSibling();
            } else {
                toInsert = right;
                right= right.getSibling();
            }
            if(result==null){
                result=toInsert;
            } else {
                current.setSibling(toInsert);
            }
            current=toInsert;
        }
        return result;
    }

    // H1 and H2 are destroyed in process
    public static <T extends Comparable<T>> BinomialHeap<T> union(BinomialHeap<T> H1, BinomialHeap<T> H2){
        BinomialHeap<T> H = new BinomialHeap<>();
        H.head= merge(H1, H2);
        // Zwolnij obiekty H1 and H2, ale nie listy, na które wskazują
        if (H.head==null) {
            return H;
        }
        Node<T> prev_x=null;
        Node<T> x=H.head;
        Node<T> next_x= x.getSibling();
        while (next_x != null) {
            if ((x.getDegree() != next_x.getDegree()) || (next_x.getSibling() != null &&
                    next_x.getSibling().getDegree() == x.getDegree())) {
                prev_x = x; // przypadek 1 i 2
                x = next_x; // przypadek 1 i 2
            } else {
                if (x.getKey().compareTo(next_x.getKey())<=0) {
                    x.setSibling(next_x.getSibling()); // przypadek 3
                    x.addChild(next_x); // przypadek 3
                } else {
                    if (prev_x == null) { // przypadek 4
                        H.head = next_x; // przypadek 4
                    } else { // przypadek 4
                        prev_x.setSibling(next_x); // przypadek 4
                    }
                    next_x.addChild(x); // przypadek 4
                    x = next_x; // przypadek 4
                }
            }
            next_x = x.getSibling();
        }
        // H1 and H2 are destroyed in process
        H1.head=null;
        H2.head=null;
        return H;
    }
    void decreaseKey(Node<T> x, T k){
        if(k.compareTo(x.getKey())>0) {
            throw new IllegalArgumentException("New key is greater than current key");
        }
        x.setKey(k);
        Node<T> y=x;
        Node<T> z= y.getParent();
        while(z!=null && y.getKey().compareTo(z.getKey())<0) {// key[y]<key[z]
            // exchange key[y]and key[z]
            T temp= y.getKey();
            y.setKey(z.getKey());
            z.setKey(temp);
            // if y and z have satellite fields, exchange them, too
            y=z;
            z= y.getParent();
        }
    }
    void delete(Node<T> x, T leastT){
        decreaseKey(x, leastT);
        extractMin();
    }

    void delete(T key, T leastT){
        Node<T> node=find(key);
        if(node!=null) {
            delete(node, leastT);
        }
    }
}
