package com.youngwm.immersion

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.util.SmartUtil
import com.youngwm.immersion.util.StatusBarUtil
import com.youngwm.immersionlayout.R


/**
 * 说明：实现沉浸式布局 + 可滚动布局 + 下拉刷新/上拉加载的 基类，
 * 默认情况下，界面显示沉浸式布局，开启下拉刷新，禁用上拉加载
 * @author  youngwm
 * @date  2022/8/21 10:01
 **/
class ImmersionScrollRefreshLayout: RelativeLayout {

    private var mScrollHeight = 70f
    lateinit var refreshLayout: SmartRefreshLayout
    lateinit var scrollView: NestedScrollView

    private var toolbarChildShowMode = mutableMapOf<View, ImmersionShowParam>()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.layout_immersimion_view, this)

        val typedArray = context.theme
            .obtainStyledAttributes(attrs, R.styleable.ImmersionRefreshScrollLayout, defStyleAttr, 0 )
        mScrollHeight = typedArray.getFloat(R.styleable.ImmersionRefreshScrollLayout_opacityScrollDistance, mScrollHeight)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return ImmersionLayoutParam(context, attrs)
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        if (child != null && params != null){
            if (params is ImmersionLayoutParam && params.immersionShowParam.haveSetParam()){
                toolbarChildShowMode[child] = params.immersionShowParam
            }
        }
        super.addView(child, params)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        refreshLayout = rootView.findViewById(R.id.refreshLayout)
        val refreshChildLayout = rootView.findViewById<RelativeLayout>(R.id.refreshChildLayout)
        scrollView = refreshChildLayout.findViewById(R.id.scrollView)

        val contentView = getChildAt(1)
        val toolbarView = getChildAt(2)

        if (contentView != null){
            removeView(contentView)
            scrollView.addView(contentView)
        }
        if (toolbarView != null){
            removeView(toolbarView)
            refreshChildLayout.addView(toolbarView)
            initStatusBar(scrollView, mScrollHeight, toolbarView)
        }

        initRefreshLayout(refreshLayout)

    }

    private fun initRefreshLayout(refreshLayout: SmartRefreshLayout) {
        with(refreshLayout){
            setEnableRefresh(true)
            setEnableLoadMore(false)
            setHeaderInsetStart(StatusBarUtil.getStatusBarHeight(refreshLayout.context).toFloat())
        }

    }

    private fun initStatusBar(scrollView:NestedScrollView, scrollHeight: Float, toolbar: View?) {
        if (toolbar == null) return
        // toolbar 向下移动 statusbar 距离
        StatusBarUtil.setPaddingSmart(toolbar)
        // toolbar渐变背景色在滚动时显示，初始时透明度为0，
        // 向上滚动时增加透明度，滚动距离 >= 70dp 后透明度 100%
        StatusBarUtil.toolbarGradientWhenScroll(scrollView, scrollHeight, toolbar, toolbarChildShowMode)

    }

    fun setGradientScrollView(@ColorRes toolbarBgColorRes: Int, toolbar: Toolbar, vararg views: View){
        StatusBarUtil.toolbarGradientWhenScroll(scrollView, mScrollHeight, toolbarBgColorRes, toolbar, *views)
    }

    /**
    * 开启activity中沉浸式布局
    * */
    fun openActivityImmersionMode(activity: Activity){
        StatusBarUtil.setTranslucentForImageView(activity, 0, null)
    }

    /**
     * 开启Fragment中沉浸式布局
     * */
    fun openFragmentImmersionMode(activity: Activity){
        StatusBarUtil.setTranslucentForImageView(activity, 0, null)
    }

    /**
    * 是否开启下拉刷新功能
    * */
    fun enableRefresh(enable: Boolean) {
        refreshLayout.setEnableRefresh(enable)
    }


    fun toolbarViewScrollListener(scrollView: NestedScrollView,
                                  scrollHeight: Float,
                                  @ColorInt toolbarBgColor: Int,
                                  toolbar: View,
                                  toolbarChildShowMode: MutableMap<View, ImmersionShowParam>){
        toolbar.setBackgroundColor(0)
        scrollView.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {

            private var mScrollY = 0
            private var mLastScrollY = 0
            private val opacityDistance: Int = SmartUtil.dp2px(scrollHeight)
            private val mColor = toolbarBgColor and 0x00ffffff

            override fun onScrollChange(v: NestedScrollView, scrollX: Int, scrollY: Int,
                                        oldScrollX: Int, oldScrollY: Int) {

                var scrollY = scrollY
                val lessDistance = mLastScrollY <= this.opacityDistance
                var viewAlpha = 1f
                if (lessDistance) {
                    scrollY = Math.min(this.opacityDistance, scrollY)
                    mScrollY = Math.min(scrollY, this.opacityDistance)
                    viewAlpha = 1f * mScrollY / opacityDistance
                }
                toolbarChildShowMode.forEach {

                    val view = it.key
                    val immersionShowParam = it.value

                    val textColor = immersionShowParam.getTextColor(lessDistance)
                    val bgDrawable = immersionShowParam.getBackground(lessDistance)
                    val showMode = immersionShowParam.getShowMode(lessDistance)

                    // 字体颜色
                    if (view is TextView && textColor != -1){
                        view.setTextColor(ResourcesCompat.getColor(context.resources,textColor, null))
                    }
                    // 背景图
                    if ( bgDrawable != -1){
                        view.background = ResourcesCompat
                            .getDrawable(context.resources, bgDrawable, null)
                    }
                    // 控件透明度
                    viewAlpha = when(showMode){
                        ImmersionShowParam.ImmersionShowMode.SHOW-> 1f
                        ImmersionShowParam.ImmersionShowMode.HIDE-> 0f
                        ImmersionShowParam.ImmersionShowMode.GRADIENT-> viewAlpha
                        else-> 1f
                    }
                    view.alpha = viewAlpha
                }
                toolbar.setBackgroundColor(255 * mScrollY / opacityDistance shl 24 or mColor)
                mLastScrollY = scrollY
            }

        })



    }


    data class ImmersionShowParam(
        var textColorBefore: Int=-1,
        var textColorAfter: Int=-1,
        var bgBefore: Int=-1,
        var bgAfter: Int=-1,
        var showModeBefore: Int=ImmersionShowMode.SHOW,
        var showModeAfter: Int=ImmersionShowMode.SHOW,
    ){
        object ImmersionShowMode{
            val SHOW = 0x01
            val HIDE = 0x02
            val GRADIENT = 0x04
        }

        fun haveSetParam():Boolean{
            return textColorBefore != -1
                    && textColorAfter != -1
                    && bgAfter != null
                    && bgBefore != null
                    && showModeBefore != ImmersionShowMode.SHOW
                    && showModeAfter != ImmersionShowMode.SHOW
        }

        fun getTextColor(lessDistance: Boolean): Int{
            return if (lessDistance) textColorBefore else textColorAfter
        }

        fun getBackground(lessDistance: Boolean): Int{
            return if (lessDistance) bgBefore else bgAfter
        }

        fun getShowMode(lessDistance: Boolean): Int{
            return if (lessDistance) showModeBefore else showModeAfter
        }

    }

    private class ImmersionLayoutParam
    constructor(context: Context, attrs: AttributeSet?)
        : RelativeLayout.LayoutParams(context, attrs) {

        val immersionShowParam = ImmersionShowParam()

        init {
            val typeArrays = context.obtainStyledAttributes(attrs, R.styleable.ImmersionRefreshScrollLayout)
            typeArrays.apply {
                immersionShowParam.textColorBefore = getColor(R.styleable.ImmersionRefreshScrollLayout_textColorBeforeOpacity, -1)
                immersionShowParam.textColorAfter = getColor(R.styleable.ImmersionRefreshScrollLayout_textColorAfterOpacity, -1)
                immersionShowParam.bgBefore = getInt(R.styleable.ImmersionRefreshScrollLayout_backgroundBeforeOpacity, -1)
                immersionShowParam.bgAfter = getInt(R.styleable.ImmersionRefreshScrollLayout_backgroundAfterOpacity, -1)
                immersionShowParam.showModeBefore = getInt(R.styleable.ImmersionRefreshScrollLayout_showModeBeforeDistance, ImmersionShowParam.ImmersionShowMode.SHOW)
                immersionShowParam.showModeAfter = getInt(R.styleable.ImmersionRefreshScrollLayout_showModeAfterDistance, ImmersionShowParam.ImmersionShowMode.SHOW)
            }
            typeArrays.recycle()
        }

    }

}
