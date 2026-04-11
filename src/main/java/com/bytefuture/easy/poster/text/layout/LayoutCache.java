package com.bytefuture.easy.poster.text.layout;

import java.util.Map;
import java.util.WeakHashMap;

final class LayoutCache {

    private final Map<com.bytefuture.easy.poster.element.basic.EnhanceTextElement, CacheEntry> store =
            new WeakHashMap<com.bytefuture.easy.poster.element.basic.EnhanceTextElement, CacheEntry>();

    Map<com.bytefuture.easy.poster.element.basic.EnhanceTextElement, CacheEntry> getStore() {
        return this.store;
    }

    CacheEntry get(com.bytefuture.easy.poster.element.basic.EnhanceTextElement element) {
        return this.store.get(element);
    }

    void put(com.bytefuture.easy.poster.element.basic.EnhanceTextElement element, CacheEntry entry) {
        this.store.put(element, entry);
    }

    static final class CacheEntry {
        final String key;
        final TextLayoutResult result;

        CacheEntry(String key, TextLayoutResult result) {
            this.key = key;
            this.result = result;
        }
    }
}
