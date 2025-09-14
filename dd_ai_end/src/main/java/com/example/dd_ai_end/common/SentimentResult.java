package com.example.dd_ai_end.common;

public class SentimentResult {
    private int label;
    private double confidence;

    // 构造函数
    public SentimentResult(int label, double confidence) {
        this.label = label;
        this.confidence = confidence;
    }

    // Getter方法
    public int getLabel() { return label; }
    public double getConfidence() { return confidence; }
}
