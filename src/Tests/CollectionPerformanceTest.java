import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollectionPerformanceTest {
    private static final int DATA_SIZE = 100_000;
    private static final String TARGET_KEY = "KEY_99999";

    private Map<String, Integer> buildHashMap() {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < DATA_SIZE; i++) {
            map.put("KEY_" + i, i);
        }
        return map;
    }

    private List<String> buildArrayList() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < DATA_SIZE; i++) {
            list.add("KEY_" + i);
        }
        return list;
    }

    @Test
    void testHashMapLookupSpeed() {
        Map<String, Integer> map = buildHashMap();

        long start = System.nanoTime();

        for (int i = 0; i < 10_000; i++) {
            map.get(TARGET_KEY);
        }

        long duration = System.nanoTime() - start;

        System.out.println("HashMap lookup time: " + duration + " ns");

        assertTrue(duration > 0);
    }
    @Test
    void testArrayListSearchSpeed() {
        List<String> list = buildArrayList();

        long start = System.nanoTime();

        for (int i = 0; i < 10_000; i++) {
            list.contains(TARGET_KEY);
        }

        long duration = System.nanoTime() - start;

        System.out.println("ArrayList search time: " + duration + " ns");

        assertTrue(duration > 0);
    }
    @Test
    void compareHashMapVsArrayList() {
        Map<String, Integer> map = buildHashMap();
        List<String> list = buildArrayList();

        long startMap = System.nanoTime();
        for (int i = 0; i < 10_000; i++) {
            map.get(TARGET_KEY);
        }
        long mapTime = System.nanoTime() - startMap;

        long startList = System.nanoTime();
        for (int i = 0; i < 10_000; i++) {
            list.contains(TARGET_KEY);
        }
        long listTime = System.nanoTime() - startList;

        System.out.println("HashMap time: " + mapTime + " ns");
        System.out.println("ArrayList time: " + listTime + " ns");

        assertTrue(mapTime < listTime,
                "HashMap should be faster than ArrayList for lookups");
    }


}
