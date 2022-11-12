package com.youngwm.immersionlayout

import android.view.View
import android.widget.BaseAdapter
import android.widget.Button
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.youngwm.immersionlayout.base.MyBaseAdapter
import com.youngwm.immersionlayout.data.ClickItem

/**
 * 说明：
 * @author  youngwm
 * @date  2022/10/1 09:45
 **/
open class MainAdapter: MyBaseAdapter<ClickItem, BaseViewHolder>(R.layout.item_main) {

    override fun convert(holder: BaseViewHolder, item: ClickItem) {
        val position = holder.layoutPosition
        val btn = holder.getView<Button>(R.id.btn)
        btn.apply {
            text = item.name
            setOnClickListener {
                viewClick?.let { it(this, position, item) }
            }
        }
    }

}
