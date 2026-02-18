package com.rpg;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Inventory {
    private final Map<String, Integer> items = new LinkedHashMap<>();
    private static final int MAX_STACK = 999;

    public void addItem(String item, int quantity) {
        if (quantity <= 0) return;
        items.merge(item, quantity, (old, add) -> Math.min(old + add, MAX_STACK));
    }

    public boolean hasItem(String item, int quantity) {
        return items.getOrDefault(item, 0) >= quantity;
    }

    public boolean removeItem(String item, int quantity) {
        if (!hasItem(item, quantity)) return false;
        int current = items.get(item);
        int remaining = current - quantity;
        if (remaining <= 0) {
            items.remove(item);
        } else {
            items.put(item, remaining);
        }
        return true;
    }

    public int getCount(String item) {
        return items.getOrDefault(item, 0);
    }

    public Map<String, Integer> getAllItems() {
        return Collections.unmodifiableMap(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int uniqueItemCount() {
        return items.size();
    }
}
