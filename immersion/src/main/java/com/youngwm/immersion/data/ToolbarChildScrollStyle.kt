package com.youngwm.immersion.data

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat

/**
 * 说明：toolbar中子控件滚动式的样式参数集合
 * @author  youngwm
 * @date  2022/10/2 21:45
 **/
data class ToolbarChildScrollStyle(
    var child: View,
    @ColorInt
    var textColorIn: Int = if (child is TextView) child.currentTextColor else Color.BLACK,
    @ColorInt
    var textColorBefore: Int = textColorIn,
    @ColorInt
    var textColorAfter: Int = if (child is TextView) child.currentTextColor else Color.BLACK,
    @DrawableRes
    var bgIn: Int=-1,
    @DrawableRes
    var bgBefore: Int=bgIn,
    @DrawableRes
    var bgAfter: Int=-1,
    var showModeBefore: Int=ScrollShowMode.SHOW,
    var showModeIn: Int=ScrollShowMode.GRADIENT_HALF_SWITCH,
    var showModeAfter: Int=ScrollShowMode.SHOW,
){

    private var bgDrawableDefault: Drawable? = child.background


    fun getTextColor(scrollArea: ScrollArea, lessHalfDistance: Boolean, showMode: Int): Int{

        return when(scrollArea){
            ScrollArea.BEFORE_GRADIENT_AREA -> { textColorBefore }
            ScrollArea.IN_GRADIENT_AREA -> {
                if (showMode == ScrollShowMode.GRADIENT_HALF_SWITCH){
                    if (lessHalfDistance) textColorIn else textColorAfter
                }else{
                    textColorIn
                }
            }
            ScrollArea.AFTER_GRADIENT_AREA -> { textColorAfter }
        }
    }


    fun getBackground(scrollArea: ScrollArea, lessHalfDistance: Boolean, showMode: Int): Drawable?{

        fun getDrawable(@DrawableRes resId: Int): Drawable?{
            return if (resId == -1){
                bgDrawableDefault
            }else{
                ResourcesCompat.getDrawable(child.context.resources, resId, null) ?: bgDrawableDefault
            }
        }
        
        return when(scrollArea){
            ScrollArea.BEFORE_GRADIENT_AREA -> getDrawable(bgBefore)
            ScrollArea.IN_GRADIENT_AREA -> {
                if (showMode == ScrollShowMode.GRADIENT_HALF_SWITCH){
                    val bg = if (lessHalfDistance) bgIn else bgAfter
                    getDrawable(bg)
                }else{
                    getDrawable(bgIn)
                }
            }
            ScrollArea.AFTER_GRADIENT_AREA -> getDrawable(bgAfter)
        }
    }



    fun getShowMode(lessDistance: Boolean): Int{
        return if (lessDistance) showModeIn else showModeAfter
    }



}
