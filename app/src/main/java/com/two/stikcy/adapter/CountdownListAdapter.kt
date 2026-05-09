package com.two.stikcy.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.two.stikcy.bean.CountdownDiffCallback
import com.two.stikcy.bean.CountdownItem
import com.two.stikcy.databinding.ItCountdownTimeBinding

/**
 * Author:Khaos116
 * Date:2026/5/9
 * Time:10:40
 */
class CountdownListAdapter : ListAdapter<CountdownItem, CountdownListAdapter.CountdownViewHolder>(CountdownDiffCallback()) {

  private var currentTime: Long = System.currentTimeMillis()

  fun updateCurrentTime(newTime: Long) {
    this.currentTime = newTime
    // 核心：只刷新可见区域的时间文本
    notifyItemRangeChanged(0, itemCount, CountdownDiffCallback.COUNT_DOWN_DIFF_TAG)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountdownViewHolder {
    val vb = ItCountdownTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return CountdownViewHolder(vb)
  }

  override fun onBindViewHolder(holder: CountdownViewHolder, position: Int, payloads: MutableList<Any>) {
    if (payloads.contains(CountdownDiffCallback.COUNT_DOWN_DIFF_TAG)) {
      holder.bindTime(getItem(position).expireTime, currentTime)
    } else {
      super.onBindViewHolder(holder, position, payloads)
    }
  }

  override fun onBindViewHolder(holder: CountdownViewHolder, position: Int) {
    holder.bindFull(getItem(position), currentTime)
  }

  class CountdownViewHolder(private val vbItem: ItCountdownTimeBinding) : RecyclerView.ViewHolder(vbItem.root) {
    fun bindFull(item: CountdownItem, currentTime: Long) {
      vbItem.tvTitle.text = item.title
      bindTime(item.expireTime, currentTime)
    }

    fun bindTime(expireTime: Long, currentTime: Long) {
      val remaining = expireTime - currentTime
      if (remaining <= 0) {
        vbItem.tvTime.text = "活动已结束"
        vbItem.tvTime.setTextColor(Color.GRAY)
      } else {
        vbItem.tvTime.text = formatTime(remaining)
        vbItem.tvTime.setTextColor(Color.RED)
      }
    }

    private fun formatTime(ms: Long): String {
      val s = ms / 1000
      return String.format("%02d:%02d:%02d", s / 3600, (s % 3600) / 60, s % 60)
    }
  }
}