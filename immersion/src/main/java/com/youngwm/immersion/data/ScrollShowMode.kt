package com.youngwm.immersion.data

/**
 * 说明：toolbar中子控件在滚动过程中的显示样式
 * @author  youngwm
 * @date  2022/10/2 21:46
 **/
object ScrollShowMode{

    /**滚动时，一直显示*/
    const val SHOW = 0x01

    /**滚动时，一直隐藏*/
    const val HIDE = 0x02

    /**在渐变距离(≤gradientDistance）内滚动时，控件渐变显示，
    * 从全透明（alpha=0）到 不透明（alpha=1）
    * */
    const val GRADIENT_ALPHA_0_TO_1 = 0x04

    /** 
     * 在渐变距离(≤gradientDistance）内滚动时，控件渐变显示，
    * 距离小于一半渐变距离时(≤gradientDistance/2），从不透明（alpha=1） 到 透明（alpha=0）；
    * 距离大于一半渐变距离(＞gradientDistance/2）且 小于渐变距离时(≤gradientDistance），
    *   从透明（alpha=0） 到 不透明（alpha=1）；
     *   这种切换效果最顺滑，不会有闪屏的视觉差。
    */
    const val GRADIENT_HALF_SWITCH = 0x08
}
