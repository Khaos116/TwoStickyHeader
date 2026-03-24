@file:Suppress("DEPRECATION")

package com.two.stikcy.utils

import android.content.Context
import android.graphics.PixelFormat
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.two.stikcy.databinding.LayoutTopToastBinding
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

object TopToastUtils {
  // 1. 使用线程安全的列表存储弱引用，支持展示多个提示并防止内存泄露
  private val activeViews = CopyOnWriteArrayList<WeakReference<View>>()

  /**
   * 显示顶部提示
   */
  fun show(context: Context, message: CharSequence) {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    // 2. 加载布局
    val vbToast = LayoutTopToastBinding.inflate(LayoutInflater.from(context))
    vbToast.tvMessage.text = message
    val root = vbToast.root

    // 将当前 View 加入管理列表
    val weakRoot = WeakReference<View>(root)
    activeViews.add(weakRoot)

    // 3. 配置参数
    val params = WindowManager.LayoutParams().apply {
      width = WindowManager.LayoutParams.MATCH_PARENT
      height = WindowManager.LayoutParams.WRAP_CONTENT
      gravity = Gravity.TOP
      format = PixelFormat.TRANSLUCENT
      type = WindowManager.LayoutParams.TYPE_APPLICATION
      flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
      windowAnimations = 0
    }

    // 4. 绑定生命周期：Activity 销毁时自动移除该 View，防止泄露
    (context as? FragmentActivity)?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
      override fun onDestroy(owner: LifecycleOwner) {
        removeViewImmediate(root, windowManager)
        activeViews.remove(weakRoot)
      }
    })

    // 5. 初始状态并添加到窗口
    root.translationY = -400f
    try {
      windowManager.addView(root, params)
    } catch (_: Exception) {
      return
    }

    // 6. 动画逻辑
    root.animate()
      .translationY(0f)
      .setDuration(400)
      .setInterpolator(DecelerateInterpolator())
      .withEndAction {
        root.postDelayed({
          // 只有当 View 还在列表中时（没被手动 cancelAll）才执行退出动画
          if (activeViews.contains(weakRoot)) {
            performExitAnimation(root, windowManager, weakRoot)
          }
        }, 2500)
      }
      .start()
  }

  /**
   * 执行退出动画并移除
   */
  private fun performExitAnimation(view: View, wm: WindowManager, ref: WeakReference<View>) {
    view.animate()
      .translationY(-view.height.toFloat() - 100f)
      .setDuration(300)
      .setInterpolator(AccelerateInterpolator())
      .withEndAction {
        removeViewImmediate(view, wm)
        activeViews.remove(ref)
      }
      .start()
  }

  /**
   * 核心优化：取消全部提示
   */
  fun cancelAll() {
    activeViews.forEach { ref ->
      ref.get()?.let { view ->
        try {
          view.animate().cancel()
          val wm = view.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
          removeViewImmediate(view, wm)
        } catch (_: Exception) {
        }
      }
    }
    activeViews.clear()
  }

  private fun removeViewImmediate(view: View, wm: WindowManager) {
    try {
      if (view.isAttachedToWindow) {
        wm.removeViewImmediate(view)
      }
    } catch (_: Exception) {
    }
  }
}