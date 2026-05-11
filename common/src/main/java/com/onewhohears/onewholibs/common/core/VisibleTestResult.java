package com.onewhohears.onewholibs.common.core;

public enum VisibleTestResult {
    NONE(false, false, false),
    VISION_PASSED(true, false, true),
    VISION_OBSTRUCTED_BLOCK(true, false, false),
    VISION_OBSTRUCTED_HEIGHT_MAP(true, false, false),
    FAILED_EXPIRED(true, true, false),
    FAILED_INVALID_LEVEL_ID(true, true, false),
    FAILED_ENTITY_1_NOT_FOUND(true, true, false),
    FAILED_ENTITY_2_NOT_FOUND(true, true, false);
    public final boolean computeComplete, failed, passed;
    VisibleTestResult(boolean computeComplete, boolean failed, boolean passed) {
        this.computeComplete = computeComplete;
        this.failed = failed;
        this.passed = passed;
    }
}
