package com.youngwm.immersionlayout.base

import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.youngwm.immersionlayout.data.ClickItem

/**
 * 说明：
 * @author  youngwm
 * @date  2022/10/1 10:23
 **/
abstract class MyBaseAdapter<T, VH: BaseViewHolder>
@JvmOverloads constructor(@LayoutRes private val layoutResId: Int, data: MutableList<T>? = null)
    : BaseQuickAdapter<T, VH>(layoutResId, data) {

    var viewClick: ((view: View, position:Int, item: T)->Unit) ?= null


}
