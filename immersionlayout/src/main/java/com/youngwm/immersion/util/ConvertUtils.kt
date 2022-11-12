package com.youngwm.immersion.util

import android.content.res.Resources

object ConvertUtils {

    fun dp2px(dpValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

}
