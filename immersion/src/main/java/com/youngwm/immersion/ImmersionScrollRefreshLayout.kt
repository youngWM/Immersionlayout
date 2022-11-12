package com.youngwm.immersion

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.youngwm.immersion.data.ScrollArea
import com.youngwm.immersion.data.ScrollShowMode
import com.youngwm.immersion.data.ToolbarChildScrollStyle
import com.youngwm.immersion.util.ConvertUtils
import com.youngwm.immersion.util.StatusBarUtil
import kotlin.math.abs


/**
 * 说明：实现沉浸式布局 + 可滚动布局 + 下拉刷新/上拉加载的 基类，
 * 默认情况下，界面显示沉浸式布局，开启下拉刷新，禁用上拉加载
 * @author  youngwm
 * @date  2022/8/21 10:01
 **/
class ImmersionScrollRefreshLayout: RelativeLayout {

    lateinit var refreshLayout: SmartRefreshLayout
    lateinit var scrollView: NestedScrollView

    /**
     * 沉浸式布局显示的正文布局视图
     */
    private var contentView: View ?= null

    /**
     * 沉浸式布局显示的toolbar布局视图
     */
    private var toolbarView: View ?= null

    /**
     *  toolbar显示为不透明（alpha=1）时的背景色，默认值 Color.WHITE
     */
    private var toolbarBgColor = Color.WHITE

    /**
     *  toolbar 开始渐变时的 Y 轴位置
     */
    private var startGradientY = 0

    /**
     * toolbar显示为不透明（alpha=1）时的滚动距离（scrollY）称为临界距离，这里设置临界距离默认值，单位Dp
     */
    private val CRITICAL_DISTANCE_DEFAULT = 70f

    /**
     *  toolbar 结束渐变时的 Y 轴位置
     */
    private var endGradientY = ConvertUtils.dp2px(CRITICAL_DISTANCE_DEFAULT)

    /**
     *  toolbar子控件在滚动时的显示样式列表
     */
    private var toolbarChildScrollStyleList = mutableListOf<ToolbarChildScrollStyle>()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.layout_immersimion_view, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        refreshLayout = getChildAt(0) as SmartRefreshLayout
        contentView = getChildAt(1)
        toolbarView = getChildAt(2)

        // 配置刷新布局
        with(refreshLayout){
            setRefreshHeader(MaterialHeader(context))
            setRefreshFooter(ClassicsFooter(context))
            setEnableRefresh(true)
            setEnableLoadMore(true)
            setHeaderInsetStart(StatusBarUtil.getStatusBarHeight(context).toFloat())
        }
        // 刷新布局只能包含一个子控件，这里使用RelativeLayout，
        // RelativeLayout 将会包含2个子布局：滚动布局 + toolbar
        val refreshChildLayout = refreshLayout.getChildAt(0) as RelativeLayout
        scrollView = refreshChildLayout.getChildAt(0) as NestedScrollView

        // 将显示内容移动到滚动布局下
        contentView?.let{
            removeView(it)
            scrollView.addView(it)
        }

        // 将 toolbar 移动到 RelativeLayout 中
        toolbarView?.let{
            removeView(it)
            refreshChildLayout.addView(it)
            // toolbar 向下移动 statusBar 距离
            StatusBarUtil.setPaddingSmart(it)
            // toolbar渐变背景色在滚动时显示，初始时透明度为0，
            // 向上滚动时增加透明度，滚动距离 >= 渐变距离 后透明度 100%
            refreshToolbarStyleWhenScroll(scrollView, it)
        }

    }


    /**
     * 设置toolbar滚动时的渐变区域
     * @param gradientDistance 渐变的距离，单位Dp
     * @param startY 开始渐变时的 Y 轴坐标位置，单位Dp
     * */
    fun setGradientArea(gradientDistance: Float, startY:Float = 0f){
        startGradientY = ConvertUtils.dp2px(startY)
        endGradientY = ConvertUtils.dp2px(startY + gradientDistance)
    }

    /**
    * 开启activity中沉浸式布局
    * */
    fun enableImmersion(activity: Activity){
        StatusBarUtil.setTranslucentForImageView(activity, 0, null)
    }

    /**
     * 开启Fragment中沉浸式布局
     * */
    fun enableImmersionInFragment(activity: Activity){
        StatusBarUtil.setTranslucentForImageView(activity, 0, null)
    }

    /**
     * 添加滚动时toolbar子控件的样式
     * */
    fun addScrollStyle(vararg pairs: ToolbarChildScrollStyle){
        toolbarChildScrollStyleList.addAll(pairs)
        refreshToolbarStyle()
    }

    /**
     * 根据当前滚动视图的Y轴坐标位置，刷新toolbar的显示样式。在使用ViewPager的界面，
     * 你很有可能需要在onResume()中调用本函数。
     * */
    fun refreshToolbarStyle(scrollY:Int = scrollView.scrollY){

        val scrollArea = when(true){
            scrollY in 0 until  startGradientY -> ScrollArea.BEFORE_GRADIENT_AREA
            scrollY in startGradientY until endGradientY -> ScrollArea.IN_GRADIENT_AREA
            else -> ScrollArea.AFTER_GRADIENT_AREA
        }
        // 实际使用场景中，toolbar 渐变前的区域，绝大多数情况下，显示效果和 Y 轴位置为 startGradientY 的相同，
        // 因此，将渐变前区域Y坐标 视为 startGradientY。
        // 这里，为了简化计算，使用相对位置，开始渐变的Y坐标 startGradientY 设置为 0 。

        // Y坐标相对值
        var releaseY = scrollY - startGradientY
        if (releaseY<0) releaseY = 0

        // toolbar渐变的距离
        val gradientDistance = endGradientY - startGradientY
        val halfDistance = gradientDistance / 2

        val lessDistance = releaseY < gradientDistance
        val lessHalfDistance = releaseY < halfDistance

        var viewAlpha0To1 = 1f
        var viewAlphaHalfSwitch = 1f

        if (lessDistance) {
            viewAlpha0To1 = 1f * releaseY.coerceAtMost(gradientDistance) / gradientDistance
            viewAlphaHalfSwitch = 1f * abs(releaseY-halfDistance) / halfDistance
        }
        scrollListener?.let { it(lessDistance, releaseY) }
        toolbarChildScrollStyleList.forEach {
            val child = it.child
            val showStyle = it

            val showMode = showStyle.getShowMode(lessDistance)
            val textColor = showStyle.getTextColor(scrollArea, lessHalfDistance, showMode)
            val bgDrawable = showStyle.getBackground(scrollArea, lessHalfDistance, showMode)

            // 字体颜色
            if (child is TextView){
                child.setTextColor(textColor)
            }
            // 背景图
            child.background = bgDrawable
            // 控件透明度
            child.alpha  = when(showMode){
                ScrollShowMode.SHOW -> 1f
                ScrollShowMode.HIDE -> 0f
                ScrollShowMode.GRADIENT_ALPHA_0_TO_1 -> viewAlpha0To1
                ScrollShowMode.GRADIENT_HALF_SWITCH -> viewAlphaHalfSwitch
                else-> 1f
            }
        }
        // toolbar 背景色
        toolbarView?.setBackgroundColor(
            // toolbar的 透明度 or 色值
            255 * releaseY.coerceAtMost(gradientDistance) / gradientDistance shl 24
            or
            (toolbarBgColor and 0x00ffffff)
        )
    }

    private var scrollListener:((lessCriticalDistance:Boolean, scrollY: Int)->Unit) ?= null

    fun addScrollListener(onChange:(lessCriticalDistance:Boolean, scrollY: Int)->Unit){
        scrollListener = onChange
    }

    /**
     * 监听NestedScrollView滚动事件，刷新工具栏样式。
     * toolbar背景为颜色色值时，使用该色值作为背景色，否则默认白色作为背景色；
     * */
    private fun refreshToolbarStyleWhenScroll(scrollView: NestedScrollView, toolbar: View){
        val drawable = toolbar.background
        toolbarBgColor = if (drawable is ColorDrawable) drawable.color else toolbarBgColor

        scrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                refreshToolbarStyle(scrollY)
            }
        )
        refreshToolbarStyle()
    }

}
