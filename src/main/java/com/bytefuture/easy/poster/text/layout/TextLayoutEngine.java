package com.bytefuture.easy.poster.text.layout;

import com.bytefuture.easy.poster.model.PosterContext;

/**
 * 文本布局入口。
 * 负责把元素配置解析为渲染规格，并基于元素实例缓存布局结果。
 */
public final class TextLayoutEngine {

    /** 按元素实例缓存最近一次布局结果，避免重复测量。 */
    private final LayoutCache cache = new LayoutCache();

    public TextLayoutResult layout(com.bytefuture.easy.poster.element.basic.EnhanceTextElement element, PosterContext context,
                                   int posterWidth, int posterHeight) {
        TextRenderSpec spec = TextRenderSpecFactory.from(element, context.getConfig());
        String cacheKey = spec.cacheKey() + "|" + posterWidth + "|" + posterHeight;
        synchronized (this.cache.getStore()) {
            LayoutCache.CacheEntry cacheEntry = this.cache.get(element);
            // 规格和画布尺寸均未变化时，直接复用缓存结果。
            if (cacheEntry != null && cacheEntry.key.equals(cacheKey)) {
                return cacheEntry.result;
            }
            // 布局测量委托给元素自身完成，这样元素可决定纯文本或富文本布局策略。
            TextLayoutResult result = element.measureLayoutInternal(spec, context, posterWidth, posterHeight);
            this.cache.put(element, new LayoutCache.CacheEntry(cacheKey, result));
            return result;
        }
    }
}
