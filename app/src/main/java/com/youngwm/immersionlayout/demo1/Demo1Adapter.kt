package com.youngwm.immersionlayout.demo1

import android.widget.TextView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.youngwm.immersionlayout.R
import com.youngwm.immersionlayout.base.MyBaseAdapter

/**
 * 说明：
 * @author  youngwm
 * @date  2022/10/1 10:59
 **/
class Demo1Adapter: MyBaseAdapter<Demo1Item, BaseViewHolder>(R.layout.item_demo1) {

    override fun convert(holder: BaseViewHolder, item: Demo1Item) {
        val name = holder.getView<TextView>(R.id.text)

        name.apply {
            text = item.name
            setOnClickListener { viewClick?.let { it(this, holder.layoutPosition, item) } }
        }
    }
}
