package com.two.stikcy.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.text.*
import android.util.AttributeSet
import android.view.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.withTranslation
import com.allen.library.helper.AttributeSetHelper
import com.allen.library.helper.ShapeBuilder
import com.two.stikcy.R
import kotlin.math.hypot

/**
 * 文本和右边图标一起居中的TextView
 */
@SuppressLint("RtlHardcoded", "ResourceType")
class CenteredTextView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
  //<editor-fold defaultstate="collapsed" desc="变量">
  var mDrawableWith = 0
  var mDrawableHeight = 0
  var mDrawableClickListener: (() -> Unit)? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    includeFontPadding = true
    ShapeBuilder().init(this, AttributeSetHelper().loadFromAttributeSet(context, attrs))
    context.obtainStyledAttributes(attrs, R.styleable.CenteredTextView, defStyleAttr, 0).use { ta ->
      mDrawableWith = ta.getDimensionPixelSize(R.styleable.CenteredTextView_drawableWidth, 0)
      mDrawableHeight = ta.getDimensionPixelSize(R.styleable.CenteredTextView_drawableHeight, 0)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="计算并绘制文字和图标">
  override fun onDraw(canvas: Canvas) {
    if (gravity != (Gravity.START or Gravity.CENTER_VERTICAL)) gravity = (Gravity.START or Gravity.CENTER_VERTICAL)
    if (paddingStart != 0 || paddingEnd != 0 || paddingTop != 0 || paddingBottom != 0) this.setPadding(0, 0, 0, 0)
    if (lineSpacingExtra != 0f || lineSpacingMultiplier != 1f) setLineSpacing(0f, 1f)
    val ds1 = compoundDrawablesRelative //左上右下
    val ds2 = compoundDrawables //左上右下
    val dRight = ds1[2] ?: ds2[2]
    if (dRight == null) { //只处理一个方向有图的
      super.onDraw(canvas)
      return
    }
    dRight.alpha = if (isPressedRightDrawable) 128 else 255
    val space = compoundDrawablePadding
    var dW = mDrawableWith
    var dH = mDrawableHeight
    if (dW <= 0) dW = dRight.intrinsicWidth
    if (dH <= 0) dH = dRight.intrinsicHeight
    val size = getTextSize(text, paint, width - space - dW)
    val textW = size.first
    if (textW + space + dW > width) {
      super.onDraw(canvas)
      return
    }
    val endX = (width - textW - dW - space) / 2f
    val dStartX = (width - endX - dW).toInt()
    val dStartY = (height - dH) / 2
    canvas.withTranslation(endX, 0f) { super.onDraw(this) }
    dRight.setBounds(dStartX, dStartY, dStartX + dW, dStartY + dH)
    dRight.draw(canvas)
    // 记录 Drawable 的点击范围
    mRightDrawableBounds = dRight.bounds
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="获取文本宽高">
  private fun getTextSize(text: CharSequence, paint: TextPaint, maxWidth: Int): Pair<Float, Int> {
    val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, paint, maxWidth)
      .setAlignment(Layout.Alignment.ALIGN_NORMAL)
      .setIncludePad(false)
      .setLineSpacing(0f, 1f)
      .build()
    // 宽度 = 所有行里最宽的那一行
    val width = (0 until staticLayout.lineCount).maxOf { staticLayout.getLineWidth(it) }
    // 高度 = StaticLayout 计算的高度
    val height = staticLayout.height
    return width to height
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="右边图标点击事件">
  // 辅助字段
  private var mRightDrawableBounds: Rect? = null
  private var isPressedRightDrawable = false
  private var downTime: Long = 0
  private var downX = 0f
  private var downY = 0f

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    when (event.action) {
      MotionEvent.ACTION_DOWN -> {
        mRightDrawableBounds?.let { bounds ->
          if (bounds.contains(event.x.toInt(), event.y.toInt())) {
            // 记录按下
            isPressedRightDrawable = true
            invalidate()
            downTime = System.currentTimeMillis()
            downX = event.x
            downY = event.y
            return true // 先拦截，等UP确认
          }
        }
      }

      MotionEvent.ACTION_UP -> {
        if (isPressedRightDrawable) {
          val upTime = System.currentTimeMillis()
          val duration = upTime - downTime
          val dx = event.x - downX
          val dy = event.y - downY
          val distance = hypot(dx.toDouble(), dy.toDouble())

          if (mRightDrawableBounds?.contains(event.x.toInt(), event.y.toInt()) == true
            && duration <= 200 //ViewConfiguration.getTapTimeout()
            && distance <= ViewConfiguration.get(context).scaledTouchSlop
          ) {
            mDrawableClickListener?.invoke()
          }
          isPressedRightDrawable = false
          invalidate()
          return true
        }
      }

      MotionEvent.ACTION_CANCEL -> {
        isPressedRightDrawable = false
        invalidate()
      }
    }
    return super.onTouchEvent(event)
  }
  //</editor-fold>
}
