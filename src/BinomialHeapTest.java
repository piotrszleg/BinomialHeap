import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class BinomialHeapTest {

    Integer[] testArray=new Integer[]{1,5,6};

    @org.junit.jupiter.api.Test
    void insertContains() {
        BinomialHeap<Integer> heap=new BinomialHeap<>();
        for(Integer i : testArray) {
            heap.insert(i);
        }
        assertTrue(testHeap.isCorrect());
        for(Integer i : testArray) {
            assertTrue(heap.contains(i));
        }
    }

    @org.junit.jupiter.api.Test
    void arrayConstructor() {
        BinomialHeap<Integer> heap=new BinomialHeap<>(testArray);
        assertTrue(testHeap.isCorrect());
        for(Integer i : testArray) {
            assertTrue(heap.contains(i));
        }
    }

    @org.junit.jupiter.api.Test
    void union() {
        Integer[] array1=new Integer[]{1,5,6};
        Integer[] array2=new Integer[]{4,8,6};
        BinomialHeap<Integer> heap1=new BinomialHeap<>(array1);
        assertTrue(heap1.isCorrect());
        BinomialHeap<Integer> heap2=new BinomialHeap<>(array2);
        assertTrue(heap2.isCorrect());
        BinomialHeap<Integer> union=BinomialHeap.union(heap1, heap2);
        // union arguments need to be cleared
        assertNull(heap1.head);
        assertNull(heap2.head);
        for(Integer i : array1){
            assertTrue(union.contains(i));
        }
        for(Integer i : array2){
            assertTrue(union.contains(i));
        }
    }

    BinomialHeap<Integer> testHeap;

    @BeforeEach
    void setUp() {
        testHeap=new BinomialHeap<>(new Integer[]{5, 8, 10, 12, 1, 5, 20, 6, 7, 17});
    }

    @org.junit.jupiter.api.Test
    void minimum() {
        assertEquals(testHeap.minimum().key, 1);
    }

    @org.junit.jupiter.api.Test
    void extractMin() {
        assertEquals(testHeap.extractMin().key, 1);
        assertTrue(testHeap.isCorrect());
        assertFalse(testHeap.contains(1));
    }

    @org.junit.jupiter.api.Test
    void decreaseKey() {
        BinomialHeap.Node<Integer> node=testHeap.insert(7);
        testHeap.decreaseKey(node, 3);
        assertTrue(testHeap.isCorrect());
    }

    @org.junit.jupiter.api.Test
    void decreaseKeyException() {
        BinomialHeap.Node<Integer> node=testHeap.insert(7);
        assertThrows(IllegalArgumentException.class,
                ()->testHeap.decreaseKey(node, 10));
        assertTrue(testHeap.isCorrect());
    }

    @org.junit.jupiter.api.Test
    void delete() {
        BinomialHeap.Node<Integer> node=testHeap.insert(7);
        testHeap.delete(node, Integer.MIN_VALUE);
        assertTrue(testHeap.contains(7));
    }
}