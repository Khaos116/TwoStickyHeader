package com.two.stikcy.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import com.two.stikcy.bean.StudentBean
import com.two.stikcy.databinding.ItemStudentBinding
import com.two.stikcy.databinding.ItemStudentParentBinding
import com.two.stikcy.utils.ToastUtils

/**
 * Author:Khaos116
 * Date:2025/5/21
 * Time:16:39
 */
class StudentDelegate : ItemViewDelegate<StudentBean, StudentDelegate.ViewHolder>() {
  override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
    return ViewHolder(ItemStudentParentBinding.inflate(LayoutInflater.from(context), parent, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, item: StudentBean) {
    holder.vb.root.removeAllViews()
    if (!item.isCollapse()) {
      val vb = ItemStudentBinding.inflate(LayoutInflater.from(holder.vb.root.context), holder.vb.root, true)
      vb.tvStudentName.text = item.name
      vb.tvStudentName.mDrawableClickListener = {
        ToastUtils.showCenterToast(vb.root.context, "点到图标了")
      }
      vb.tvStudentName.setOnClickListener {
        ToastUtils.showCenterToast(vb.root.context, "点到名字了")
      }
    }
  }

  class ViewHolder(viewBinding: ItemStudentParentBinding) : RecyclerView.ViewHolder(viewBinding.root) {
    val vb = viewBinding
  }
}