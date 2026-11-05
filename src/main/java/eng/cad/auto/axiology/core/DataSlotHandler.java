package eng.cad.auto.axiology.core;

import net.minecraft.world.inventory.ContainerData;

import java.util.LinkedHashMap;
import java.util.Map;

public class DataSlotHandler implements ContainerData {
    private final Map<String, Integer> indexByKey = new LinkedHashMap<>();
    private final int[] values;

    public DataSlotHandler(String... keys) {
        for (String key : keys) {
            indexByKey.computeIfAbsent(key, k -> indexByKey.size());
        }
        values = new int[indexByKey.size()];
    }

    public int indexOf(String key) {
        Integer index = indexByKey.get(key);
        if (index == null) {
            throw new IllegalArgumentException("Unknown data key: " + key);
        }
        return index;
    }

    public int get(String key) {
        return values[indexOf(key)];
    }

    public void set(String key, int value) {
        values[indexOf(key)] = value;
    }

    @Override
    public int get(int index) {
        return values[index];
    }

    @Override
    public void set(int index, int value) {
        values[index] = value;
    }

    @Override
    public int getCount() {
        return values.length;
    }
}