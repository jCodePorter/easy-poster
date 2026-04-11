package com.bytefuture.easy.poster.text.layout;

import com.bytefuture.easy.poster.model.PosterContext;

public final class TextLayoutEngine {

    private final LayoutCache cache = new LayoutCache();

    public TextLayoutResult layout(com.bytefuture.easy.poster.element.basic.EnhanceTextElement element, PosterContext context,
                                   int posterWidth, int posterHeight) {
        TextRenderSpec spec = TextRenderSpecFactory.from(element, context.getConfig());
        String cacheKey = spec.cacheKey() + "|" + posterWidth + "|" + posterHeight;
        synchronized (this.cache.getStore()) {
            LayoutCache.CacheEntry cacheEntry = this.cache.get(element);
            if (cacheEntry != null && cacheEntry.key.equals(cacheKey)) {
                return cacheEntry.result;
            }
            TextLayoutResult result = element.measureLayoutInternal(spec, context, posterWidth, posterHeight);
            this.cache.put(element, new LayoutCache.CacheEntry(cacheKey, result));
            return result;
        }
    }
}
