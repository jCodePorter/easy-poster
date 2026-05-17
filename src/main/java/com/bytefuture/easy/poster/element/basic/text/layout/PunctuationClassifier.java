package com.bytefuture.easy.poster.element.basic.text.layout;

import com.bytefuture.easy.poster.model.PunctuationType;

import java.util.HashMap;
import java.util.Map;

/**
 * 标点分类器
 * 根据 Unicode 码点返回 PunctuationType
 *
 * @author biaoy
 * @since 2026/05/11
 */
public class PunctuationClassifier {

    private static final Map<Integer, PunctuationType> PUNCTUATION_MAP = new HashMap<>();

    static {
        // 避头字符：句号、感叹号、问号、分号、冒号
        PUNCTUATION_MAP.put(0x3002, PunctuationType.AVOID_HEAD); // 。
        PUNCTUATION_MAP.put(0xFF01, PunctuationType.AVOID_HEAD); // ！
        PUNCTUATION_MAP.put(0xFF1F, PunctuationType.AVOID_HEAD); // ？
        PUNCTUATION_MAP.put(0xFF1B, PunctuationType.AVOID_HEAD); // ；
        PUNCTUATION_MAP.put(0xFF1A, PunctuationType.AVOID_HEAD); // ：
        PUNCTUATION_MAP.put(0x2026, PunctuationType.AVOID_HEAD); // …（省略号单字符）

        // 避尾字符：逗号、顿号
        PUNCTUATION_MAP.put(0xFF0C, PunctuationType.AVOID_TAIL); // ，
        PUNCTUATION_MAP.put(0x3001, PunctuationType.AVOID_TAIL); // 、

        // 左括号类
        PUNCTUATION_MAP.put(0xFF08, PunctuationType.OPEN_BRACKET); // （
        PUNCTUATION_MAP.put(0x300A, PunctuationType.OPEN_BRACKET); // 《
        PUNCTUATION_MAP.put(0x300C, PunctuationType.OPEN_BRACKET); // 「
        PUNCTUATION_MAP.put(0x3010, PunctuationType.OPEN_BRACKET); // 【

        // 右括号类
        PUNCTUATION_MAP.put(0xFF09, PunctuationType.CLOSE_BRACKET); // ）
        PUNCTUATION_MAP.put(0x300B, PunctuationType.CLOSE_BRACKET); // 》
        PUNCTUATION_MAP.put(0x300D, PunctuationType.CLOSE_BRACKET); // 」
        PUNCTUATION_MAP.put(0x3011, PunctuationType.CLOSE_BRACKET); // 】
    }

    /**
     * 根据 Unicode 字符返回 PunctuationType
     *
     * @param ch Unicode 字符
     * @return 标点类型
     */
    public PunctuationType classify(char ch) {
        return classifyByCodePoint(ch);
    }

    /**
     * 根据 Unicode 码点返回 PunctuationType
     *
     * @param codePoint Unicode 码点
     * @return 标点类型
     */
    public PunctuationType classifyByCodePoint(int codePoint) {
        PunctuationType type = PUNCTUATION_MAP.get(codePoint);
        return type != null ? type : PunctuationType.NONE;
    }

    /**
     * 获取指定码点的配对括号码点
     *
     * @param codePoint 左括号或右括号的码点
     * @return 配对括号的码点，无配对时返回 -1
     */
    public int getMatchingBracket(int codePoint) {
        switch (codePoint) {
            case 0xFF08: return 0xFF09; // （ → ）
            case 0xFF09: return 0xFF08; // ） → （
            case 0x300A: return 0x300B; // 《 → 》
            case 0x300B: return 0x300A; // 》 → 《
            case 0x300C: return 0x300D; // 「 → 」
            case 0x300D: return 0x300C; // 」 → 「
            case 0x3010: return 0x3011; // 【 → 】
            case 0x3011: return 0x3010; // 】 → 【
            default: return -1;
        }
    }
}