package com.youngwm.immersionlayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.youngwm.immersionlayout.data.ClickItem
import com.youngwm.immersionlayout.demo1.Demo1Activity
import com.youngwm.immersionlayout.demo2.Demo2Activity

class MainActivity : AppCompatActivity() {

    private val mainAdapter by lazy { MainAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
    }

    private fun initData() {
        val rv = findViewById<RecyclerView>(R.id.rvMain)
        rv.apply {
            adapter = mainAdapter
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }

        val data = mutableListOf<ClickItem>().apply {
            add(ClickItem(0,  "demo1:沉浸式布局Activity"))
            add(ClickItem(1,  "demo2:仿朋友圈沉浸式布局"))
            add(ClickItem(2,  "demo3:"))
            add(ClickItem(3,  "demo4:"))
        }

        mainAdapter.apply {
            setNewInstance(data.toMutableList())
            viewClick = { v, position, itemClick->
                when(itemClick.id){
                    0 ->{
                        startActivity(Intent(this@MainActivity, Demo1Activity::class.java))
                    }
                    1 ->{
                        startActivity(Intent(this@MainActivity, Demo2Activity::class.java))
                    }
                }
            }
        }

    }


}
