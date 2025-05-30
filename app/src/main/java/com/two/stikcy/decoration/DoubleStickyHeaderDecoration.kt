package com.two.stikcy.decoration

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import kotlin.reflect.KClass

/**
 * RecyclerView ItemDecoration 实现双层 Sticky Header
 * 第一层：学校 Header
 * 第二层：班级 Header
 *
 * 适配 MultiTypeAdapter，数据结构示例：
 * items: List<Any> 包含 T, U, StudentBean
 */
@Suppress("UNCHECKED_CAST")
class DoubleStickyHeaderDecoration<T : Any, U : Any>(
  private val mClassT: KClass<T>, //一级悬浮数据类型
  private val mClassU: KClass<U>, //二级悬浮数据类型
  private val mFloatView1: View, //一级悬浮，主要测量高度
  private val mFloatView2: View, //二级悬浮，主要测量高度
  private val mMultiTypeAdapter: MultiTypeAdapter, //适配器
  private val mCallFillFloatFirst: (t: T?, show: Boolean, offset: Float) -> Unit, //回调数据和偏移量
  private val mCallFillFloatSecond: (u: U?, show: Boolean, offset: Float) -> Unit, //回调数据和偏移量
) : RecyclerView.ItemDecoration() {

  override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    val layoutManager = parent.layoutManager as? LinearLayoutManager ?: return
    val topChild = parent.getChildAt(0) ?: return
    val topPosition = parent.getChildAdapterPosition(topChild)
    if (topPosition == RecyclerView.NO_POSITION) return
    var mFloatSchoolHeader: View? = null
    var mFloatClassHeader: View? = null
    //需查找的数据
    var mCurrentSchool: T? = null //顶部的第一个学校
    var mCurrentClass: U? = null //顶部的第一个班级
    var mNextClass: U? = null //顶部的第一个班级的下一个班级
    var mNextSchoolView: View? = null //顶部的下一个学校View
    var mNextClassView: View? = null //顶部的下一个班级View
    //学校的高度
    var mSchoolHeight = 0
    //班级的高度
    var mClassHeight = 0
    //找到当前第一个显示的数据
    val firstData = mMultiTypeAdapter.items[topPosition]
    if (mClassT.isInstance(firstData)) { //第一个就是学校
      mCurrentSchool = firstData as T
    }
    if (mCurrentSchool == null) { //如果第一个不是学校，就往前，找到第一个数据对应的学校
      mCurrentSchool = findCurrentSchool(topPosition)
    }
    mCurrentSchool?.let {
      val v = fixLayoutSize(parent, mFloatView1) //找到学校，创建学校悬浮UI
      mSchoolHeight = v.height
      mFloatSchoolHeader = v
      //找到下个学校
      val nextSchoolIndex = findNextSchoolPosition(findFirstItemBelowHeightIndex(parent, 0))
      if (nextSchoolIndex != -1) {
        mNextSchoolView = layoutManager.findViewByPosition(nextSchoolIndex)
      }
    }

    //查找当前存在的班级
    val belowIndex = findFirstItemBelowHeightIndex(parent, mSchoolHeight)
    if (belowIndex >= 0 && belowIndex < mMultiTypeAdapter.items.size) { //存在数据
      val belowData = mMultiTypeAdapter.items[belowIndex]
      if (mClassU.isInstance(belowData)) { //下一个数据是班级
        mCurrentClass = belowData as U
      }
      if (mCurrentClass == null && !mClassT.isInstance(belowData)) { //如果是学校挨着学校就不找班级了，否则：下一个数据不是班级，就往前，找到第一个数据对应的班级
        mCurrentClass = findCurrentClass(belowIndex)
      }
    }

    mCurrentClass?.let {
      mFloatClassHeader = fixLayoutSize(parent, mFloatView2) //找到班级，创建班级悬浮UI
      mClassHeight = mFloatClassHeader?.height ?: 0
      //找到下个班级
      val nextClassIndex = findNextClassPosition(findFirstItemBelowHeightIndex(parent, mSchoolHeight))
      if (nextClassIndex != -1) {
        mNextClass = mMultiTypeAdapter.items[nextClassIndex] as? U
        mNextClassView = layoutManager.findViewByPosition(nextClassIndex)
      }
    }
    // 班级头被下一个班级顶出时
    mNextClassView?.let { v ->
      if (v.top <= mSchoolHeight) { //当下一个班级已经和悬浮的学校挨着时，更新班级悬浮为下一个班级
        mCurrentClass = mNextClass
        mFloatClassHeader = fixLayoutSize(parent, mFloatView2) //找到班级，创建班级悬浮UI
        mClassHeight = mFloatClassHeader?.height ?: 0
        mNextClass = null
      }
    }
    //悬浮学校的偏移量
    var mOffSetSchool = 0
    //悬浮班级的偏移量
    var mOffSetClass = mSchoolHeight
    //班级的偏移量计算
    mNextClassView?.let { v ->
      if (v.top <= mSchoolHeight) {
        mOffSetClass = mSchoolHeight //下一个班级把上一个班级完全替代后，就把班级悬浮显示到学校下面
      } else if (v.top <= mSchoolHeight + mClassHeight) {
        mOffSetClass = v.top - mClassHeight //下一个班级正在推出上一个班级
      }
    }
    //学校的偏移量计算
    mNextSchoolView?.let { v ->
      if (v.top <= mSchoolHeight) {
        mOffSetSchool = v.top - mSchoolHeight //下一个学校正在推出上一个学校,这时候就不需要显示悬浮班级了
        mFloatClassHeader = null
      } else {
        mFloatClassHeader?.let { //有班级悬浮的时候
          if (v.top <= mSchoolHeight + mClassHeight) {
            mOffSetClass = v.top - mClassHeight //下一个学校正在推出上一个班级
          }
        }
      }
    }
    mCallFillFloatSecond.invoke(mCurrentClass, mFloatClassHeader != null, mOffSetClass.toFloat())
    mCallFillFloatFirst.invoke(mCurrentSchool, mFloatSchoolHeader != null, mOffSetSchool.toFloat())
  }

  /** 测量并布局 Header View，确保其宽高正确 */
  private fun fixLayoutSize(parent: RecyclerView, view: View): View {
    val layoutParams = view.layoutParams ?: RecyclerView.LayoutParams(
      RecyclerView.LayoutParams.MATCH_PARENT,
      RecyclerView.LayoutParams.WRAP_CONTENT
    )
    val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)

    val heightSpec = if (layoutParams.height > 0) {
      View.MeasureSpec.makeMeasureSpec(layoutParams.height, View.MeasureSpec.EXACTLY)
    } else {
      View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    }

    view.measure(widthSpec, heightSpec)
    view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    return view
  }

  /** 绘制 Header 到 Canvas 上 */
  private fun drawStickyHeader(canvas: Canvas, view: View, y: Int) {
    canvas.save()
    canvas.translate(0f, y.toFloat())
    view.draw(canvas)
    canvas.restore()
  }

  /** 找当前位置向上遍历，找到最近的 T */
  private fun findCurrentSchool(position: Int): T? {
    for (i in position downTo 0) {
      val item = mMultiTypeAdapter.items[i]
      if (mClassT.isInstance(item)) return item as T
    }
    return null
  }

  /** 找当前位置向上遍历，找到最近的 U */
  private fun findCurrentClass(position: Int): U? {
    for (i in position downTo 0) {
      val item = mMultiTypeAdapter.items[i]
      if (mClassU.isInstance(item)) return item as U
    }
    return null
  }

  /** 找当前位置向下遍历，找到下一个 T 的位置 */
  private fun findNextSchoolPosition(position: Int): Int {
    for (i in position + 1 until mMultiTypeAdapter.items.size) {
      if (mClassT.isInstance(mMultiTypeAdapter.items[i])) return i
    }
    return -1
  }

  /** 找当前位置向下遍历，找到下一个 U 的位置 */
  private fun findNextClassPosition(position: Int): Int {
    for (i in position + 1 until mMultiTypeAdapter.items.size) {
      if (mClassU.isInstance(mMultiTypeAdapter.items[i])) return i
    }
    return -1
  }

  /** 找到屏幕中固定高度底部下面的第一个可见Item对应位置 */
  private fun findFirstItemBelowHeightIndex(parent: RecyclerView, height: Int): Int {
    val child = parent.findChildViewUnder(parent.width / 2f, height * 1f)
    if (child != null) {
      return parent.getChildAdapterPosition(child)
    }
    return RecyclerView.NO_POSITION
  }
}