package com.youngwm.immersion.data

/**
 * 说明：滚动区域枚举值，根据显示效果分为3类：渐变前区域；渐变区域内；渐变后区域；
 * @author  youngwm
 * @date  2022/10/4 09:34
 **/
enum class ScrollArea(description: String) {
    BEFORE_GRADIENT_AREA("滚动位置在toolbar开始渐变前的区域内"),
    IN_GRADIENT_AREA("滚动位置在toolbar渐变区域内"),
    AFTER_GRADIENT_AREA("滚动位置超过toolbar渐变的区域"),
}
