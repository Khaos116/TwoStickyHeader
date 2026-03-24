package com.two.stikcy.utils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.two.stikcy.databinding.DialogMyActionBinding

class MyActionDialog : DialogFragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val vb = DialogMyActionBinding.inflate(inflater, container, false)
    vb.btnTestSnack.setOnClickListener {
      TopToastUtils.show(requireActivity(), "顶部Toast测试${"${System.currentTimeMillis()}".takeLast(6)}")
    }
    vb.btnClose.setOnClickListener {
      dismissAllowingStateLoss()
      TopToastUtils.cancelAll()
    }
    return vb.root
  }

  override fun onStart() {
    super.onStart()
    // 设置非全屏样式，让 Dialog 居中
    dialog?.window?.apply {
      // 设置背景透明，否则会有系统默认的边框
      setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val params = attributes
      params.width = (resources.displayMetrics.widthPixels * 1.0).toInt()
      params.height = (resources.displayMetrics.heightPixels * 1.0).toInt()
      attributes = params
    }
  }
}