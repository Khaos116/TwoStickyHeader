package com.two.stikcy.bean

import androidx.recyclerview.widget.DiffUtil

/**
 * Author:Khaos116
 * Date:2026/5/9
 * Time:10:36
 */
data class CountdownItem(
  val id: String? = null, //
  val title: String? = null, //
  val expireTime: Long = 0, //
)

class CountdownDiffCallback : DiffUtil.ItemCallback<CountdownItem>() {
  companion object {
    const val COUNT_DOWN_DIFF_TAG = "PAYLOAD_TIME"
  }

  // 1. 判断是否是同一个对象（通过唯一 ID）
  override fun areItemsTheSame(oldItem: CountdownItem, newItem: CountdownItem): Boolean {
    return oldItem.id == newItem.id
  }

  // 2. 判断内容是否完全一致
  override fun areContentsTheSame(oldItem: CountdownItem, newItem: CountdownItem): Boolean {
    return oldItem == newItem
  }

  // 3. 关键：如果 contents 不同，返回一个标识，告诉 Adapter 只刷时间
  // 在倒计时场景中，通常数据没变，只是我们需要强制触发局部刷新
  override fun getChangePayload(oldItem: CountdownItem, newItem: CountdownItem): Any? {
    return COUNT_DOWN_DIFF_TAG
  }
}
