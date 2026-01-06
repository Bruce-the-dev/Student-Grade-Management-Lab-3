import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class CollectionTests {

    @Test
    void testHashMapBasicOperations() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("Alice", 90);
        map.put("Bob", 85);

        assertEquals(2, map.size());
        assertTrue(map.containsKey("Alice"));
        assertEquals(85, map.get("Bob"));

        map.remove("Alice");
        assertFalse(map.containsKey("Alice"));
        assertEquals(1, map.size());
    }

    @Test
    void testTreeMapSorting() {
        TreeMap<String, Integer> treeMap = new TreeMap<>();
        treeMap.put("Charlie", 70);
        treeMap.put("Alice", 90);
        treeMap.put("Bob", 85);

        List<String> keys = new ArrayList<>(treeMap.keySet());
        assertEquals(Arrays.asList("Alice", "Bob", "Charlie"), keys);
    }

    @Test
    void testHashSetUniqueness() {
        HashSet<String> set = new HashSet<>();
        set.add("Alice");
        set.add("Bob");
        set.add("Alice"); // duplicate

        assertEquals(2, set.size());
        assertTrue(set.contains("Bob"));
    }

    @Test
    void testHashSetIterationOrderIrrelevant() {
        HashSet<String> set = new HashSet<>();
        set.add("X");
        set.add("Y");
        set.add("Z");

        // order may not be predictable
        assertTrue(set.contains("X"));
        assertTrue(set.contains("Y"));
        assertTrue(set.contains("Z"));
    }
}
