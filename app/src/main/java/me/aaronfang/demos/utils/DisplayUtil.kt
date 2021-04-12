package me.aaronfang.demos.utils

import android.content.Context

object DisplayUtil {
    fun dp2px(context: Context, dpValue: Float): Float {
        val scale: Float = context.resources.displayMetrics.density;
        return dpValue * scale
    }

    fun px2dp(context: Context, pxValue: Float): Float {
        val scale: Float = context.resources.displayMetrics.density;
        return pxValue / scale
    }
}