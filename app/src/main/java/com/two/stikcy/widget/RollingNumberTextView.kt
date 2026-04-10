package com.two.stikcy.widget

import android.animation.*
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import java.text.DecimalFormat
import java.util.LinkedList
import java.util.Queue

/**
 * 每次设置数字会自动变化的文本展示效果
 */
class RollingNumberTextView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

  var duration: Long = 4000
  var pauseDuration: Long = 2000

  private var currentDisplayNumber = 0.0
  private var isAnimating = false
  private val numberQueue: Queue<Double> = LinkedList()
  private var valueAnimator: ValueAnimator? = null

  // 格式化工具：#,###.## 表示千分位，最多保留两位小数，# 表示如果没有则不显示（剔除无效0）
  //private val formatter = DecimalFormat("#,###.##").apply {
  private val formatter = DecimalFormat("#,###.00").apply {
    // 关键点：强制禁用科学计数法
    maximumFractionDigits = 2
    isGroupingUsed = true
  }

  init {
    // 确保使用的是支持该特性的系统字体
    this.typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
    // 强制开启等宽数字和对齐数字
    this.fontFeatureSettings = "\"tnum\", \"lnum\""
  }

  /**
   * 接收 Double 类型数字
   */
  fun setRollingNumber(newNumber: Double) {
    numberQueue.offer(newNumber)
    checkQueue()
  }

  private fun checkQueue() {
    if (isAnimating || numberQueue.isEmpty()) return
    val nextNumber = numberQueue.poll() ?: return
    startRollingAnimation(nextNumber)
  }

  private fun startRollingAnimation(targetNumber: Double) {
    isAnimating = true
    cancelAnimation()

    // 优化点：直接使用 Double 的求值器，避免 Float 精度转换带来的科学计数法风险
    valueAnimator = ValueAnimator.ofObject(TypeEvaluator<Double> { fraction, startValue, endValue ->
      startValue + fraction * (endValue - startValue)
    }, currentDisplayNumber, targetNumber).apply {

      duration = this@RollingNumberTextView.duration

      addUpdateListener { animation ->
        val animatedValue = animation.animatedValue as Double
        // 使用格式化工具转换，DecimalFormat 会处理掉科学计数法
        text = formatter.format(animatedValue)
        currentDisplayNumber = animatedValue
      }

      addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
          postDelayed({
            isAnimating = false
            checkQueue()
          }, pauseDuration)
        }
      })
      start()
    }
  }

  private fun cancelAnimation() {
    valueAnimator?.apply {
      removeAllUpdateListeners()
      removeAllListeners()
      cancel()
    }
    valueAnimator = null
  }

  override fun onDetachedFromWindow() {
    cancelAnimation()
    removeCallbacks(null)
    numberQueue.clear()
    isAnimating = false
    super.onDetachedFromWindow()
  }
}