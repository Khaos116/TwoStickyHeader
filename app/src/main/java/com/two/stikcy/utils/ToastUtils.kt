package com.two.stikcy.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.Gravity
import android.widget.Toast
import androidx.core.graphics.toColorInt
import com.allen.library.shape.ShapeTextView
import java.lang.ref.WeakReference

/**
 * Author:Khaos116
 * Date:2025/8/18
 * Time:3:14
 */
object ToastUtils {
  private val paddingH = dp2px(15f).toInt()
  private val paddingV = dp2px(10f).toInt()
  private val radius = dp2px(8f)
  private var toast: WeakReference<Toast>? = null
  fun showCenterToast(context: Context, msg: String?) {
    if (msg.isNullOrBlank()) return
    toast?.get()?.cancel()
    Toast(context).also { t ->
      t.view = ShapeTextView(context).apply {
        text = msg
        textSize = 16f
        gravity = Gravity.CENTER
        setTextColor(Color.WHITE)
        setPadding(paddingH, paddingV, paddingH, paddingV)
        shapeBuilder?.setShapeSolidColor("#CC000000".toColorInt())
          ?.setShapeCornersRadius(radius)
          ?.into(this)
      }
      t.duration = Toast.LENGTH_SHORT
      t.setGravity(Gravity.CENTER, 0, 0)
      toast = WeakReference(t)
    }.show()
  }

  private fun dp2px(dpValue: Float): Float {
    val scale: Float = Resources.getSystem().displayMetrics.density
    return dpValue * scale + 0.5f
  }
}