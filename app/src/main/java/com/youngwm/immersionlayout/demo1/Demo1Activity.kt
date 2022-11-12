package com.youngwm.immersionlayout.demo1

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.youngwm.immersion.ImmersionScrollRefreshLayout
import com.youngwm.immersion.data.ScrollShowMode
import com.youngwm.immersion.data.ToolbarChildScrollStyle
import com.youngwm.immersion.util.StatusBarUtil
import com.youngwm.immersionlayout.R

class Demo1Activity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo1)
        initData()
    }

    private fun initData() {
        val immersionLayout = findViewById<ImmersionScrollRefreshLayout>(R.id.immersionLayout)
        val rv = findViewById<RecyclerView>(R.id.rvDemo1)
        val title = findViewById<TextView>(R.id.title)
        val back = findViewById<TextView>(R.id.back)
        val share = findViewById<ImageView>(R.id.shareBtn)
        val appBarLayout = findViewById<Toolbar>(R.id.appBarLayout)

        immersionLayout.apply {
            enableImmersion(this@Demo1Activity)
            addScrollStyle(
                ToolbarChildScrollStyle(
                    title,
                    showModeIn = ScrollShowMode.GRADIENT_ALPHA_0_TO_1,
                    showModeAfter = ScrollShowMode.SHOW
                ),
                ToolbarChildScrollStyle(
                    back,
                    textColorIn = context.resources.getColor(R.color.white),
                    textColorAfter = Color.BLACK,
                    showModeIn = ScrollShowMode.GRADIENT_HALF_SWITCH
                ),
                ToolbarChildScrollStyle(
                    share,
                    bgIn = R.drawable.ic_baseline_share_white,
                    bgAfter = R.drawable.ic_baseline_share_black,
                    showModeIn = ScrollShowMode.GRADIENT_HALF_SWITCH
                ),
            )
            addScrollListener { lessCriticalDistance, scrollY ->
                if (scrollY > 30){
                    StatusBarUtil.setLightMode(this@Demo1Activity)
                }else{
                    StatusBarUtil.setDarkMode(this@Demo1Activity)
                }
                if (!lessCriticalDistance){
                    appBarLayout.elevation = 7f
                }else{
                    appBarLayout.elevation = 0f
                }
            }
        }

        val mAdapter = Demo1Adapter()
        rv.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@Demo1Activity).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }

        val data = mutableListOf<Demo1Item>().apply {
            for (i in 0..30){
                add(Demo1Item("item$i"))
            }
        }
        mAdapter.apply {
            setNewInstance(data)
            viewClick = { v, position, item ->
                Toast.makeText(this@Demo1Activity, "点击了${item.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
