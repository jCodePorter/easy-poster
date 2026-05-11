package com.bytefuture.easy.poster.func.text;

import com.bytefuture.easy.poster.element.v2.text.layout.PunctuationClassifier;
import com.bytefuture.easy.poster.model.PunctuationType;
import org.junit.Assert;
import org.junit.Test;

public class PunctuationClassifierTest {

    private final PunctuationClassifier classifier = new PunctuationClassifier();

    @Test
    public void shouldClassifyAvoidHeadPunctuation() {
        Assert.assertEquals(PunctuationType.AVOID_HEAD, classifier.classify('。'));
        Assert.assertEquals(PunctuationType.AVOID_HEAD, classifier.classify('！'));
        Assert.assertEquals(PunctuationType.AVOID_HEAD, classifier.classify('？'));
        Assert.assertEquals(PunctuationType.AVOID_HEAD, classifier.classify('；'));
        Assert.assertEquals(PunctuationType.AVOID_HEAD, classifier.classify('：'));
        Assert.assertEquals(PunctuationType.AVOID_HEAD, classifier.classify('…'));
    }

    @Test
    public void shouldClassifyAvoidTailPunctuation() {
        Assert.assertEquals(PunctuationType.AVOID_TAIL, classifier.classify('，'));
        Assert.assertEquals(PunctuationType.AVOID_TAIL, classifier.classify('、'));
    }

    @Test
    public void shouldClassifyOpenBracket() {
        Assert.assertEquals(PunctuationType.OPEN_BRACKET, classifier.classify('（'));
        Assert.assertEquals(PunctuationType.OPEN_BRACKET, classifier.classify('《'));
        Assert.assertEquals(PunctuationType.OPEN_BRACKET, classifier.classify('「'));
        Assert.assertEquals(PunctuationType.OPEN_BRACKET, classifier.classify('【'));
    }

    @Test
    public void shouldClassifyCloseBracket() {
        Assert.assertEquals(PunctuationType.CLOSE_BRACKET, classifier.classify('）'));
        Assert.assertEquals(PunctuationType.CLOSE_BRACKET, classifier.classify('》'));
        Assert.assertEquals(PunctuationType.CLOSE_BRACKET, classifier.classify('」'));
        Assert.assertEquals(PunctuationType.CLOSE_BRACKET, classifier.classify('】'));
    }

    @Test
    public void shouldReturnNoneForNonPunctuation() {
        Assert.assertEquals(PunctuationType.NONE, classifier.classify('春'));
        Assert.assertEquals(PunctuationType.NONE, classifier.classify('A'));
        Assert.assertEquals(PunctuationType.NONE, classifier.classify('1'));
        Assert.assertEquals(PunctuationType.NONE, classifier.classify(' '));
    }

    @Test
    public void shouldClassifyByCodePoint() {
        Assert.assertEquals(PunctuationType.AVOID_HEAD, classifier.classifyByCodePoint(0x3002));  // 。
        Assert.assertEquals(PunctuationType.OPEN_BRACKET, classifier.classifyByCodePoint(0xFF08)); // （
        Assert.assertEquals(PunctuationType.NONE, classifier.classifyByCodePoint(0x6625));         // 春
    }
}