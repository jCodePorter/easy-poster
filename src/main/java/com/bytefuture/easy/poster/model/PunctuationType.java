package com.bytefuture.easy.poster.model;

/**
 * 标点避头尾类型枚举
 * 用于竖排文本的标点排版规则
 *
 * @author biaoy
 * @since 2026/05/11
 */
public enum PunctuationType {
    /** 非标点，不参与避头尾 */
    NONE,
    /** 不能出现在列首（如句号、感叹号、右括号） */
    AVOID_HEAD,
    /** 不能出现在列尾（如逗号、左括号） */
    AVOID_TAIL,
    /** 既避头又避尾 */
    AVOID_BOTH,
    /** 左括号类，避尾 + 配对标记 */
    OPEN_BRACKET,
    /** 右括号类，避头 + 配对标记 */
    CLOSE_BRACKET
}