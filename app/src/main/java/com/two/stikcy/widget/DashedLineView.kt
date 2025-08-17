package com.two.stikcy.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.toColorInt
import com.two.stikcy.R

class DashedLineView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
  //<editor-fold defaultstate="collapsed" desc="变量">
  private val mPaint: Paint = Paint()
  private var mOrientation: Orientation = Orientation.HORIZONTAL
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    context.withStyledAttributes(attrs, R.styleable.DashedLineView) {
      val lineColor = getColor(R.styleable.DashedLineView_lineColor, "#bfbfbf".toColorInt())
      val lineWidth = getDimension(R.styleable.DashedLineView_lineWidth, 1.5f * resources.displayMetrics.density)
      val dashLength = getDimension(R.styleable.DashedLineView_lineDashLength, 3f * resources.displayMetrics.density)
      val dashGap = getDimension(R.styleable.DashedLineView_lineDashGap, 3f * resources.displayMetrics.density)
      mOrientation = Orientation.entries.toTypedArray()[getInt(R.styleable.DashedLineView_lineOrientation, 0)]
      mPaint.apply {
        color = lineColor
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
        pathEffect = DashPathEffect(floatArrayOf(dashLength, dashGap), 0f)
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="绘制虚线">
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    if (mOrientation == Orientation.HORIZONTAL) {
      canvas.drawLine(0f, height / 2f, width.toFloat(), height / 2f, mPaint)
    } else {
      canvas.drawLine(width / 2f - mPaint.strokeWidth / 2f, 0f, width / 2f - mPaint.strokeWidth / 2f, height.toFloat(), mPaint)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="圆角位置">
  enum class Orientation {
    //如果不满足，再添加即可
    HORIZONTAL, VERTICAL
  }
  //</editor-fold>
}
