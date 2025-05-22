package com.two.stikcy.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import com.two.stikcy.R
import com.two.stikcy.bean.SchoolBean
import com.two.stikcy.databinding.ItemSchoolBinding

/**
 * Author:Khaos116
 * Date:2025/5/21
 * Time:16:39
 */
class SchoolDelegate(
  private val onItemClick: ((SchoolBean) -> Unit)? = null,
) : ItemViewDelegate<SchoolBean, SchoolDelegate.ViewHolder>() {
  override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
    return ViewHolder(ItemSchoolBinding.inflate(LayoutInflater.from(context), parent, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, item: SchoolBean) {
    holder.vb.tvSchoolName.text = item.name
    holder.vb.ivExpand.setImageResource(if (item.isCollapse()) R.drawable.ic2025_more_v1 else R.drawable.ic2025_more_v2)
    holder.vb.root.setOnClickListener { onItemClick?.invoke(item) }
  }

  class ViewHolder(viewBinding: ItemSchoolBinding) : RecyclerView.ViewHolder(viewBinding.root) {
    val vb = viewBinding
  }
}