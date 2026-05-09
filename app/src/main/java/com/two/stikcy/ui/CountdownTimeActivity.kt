package com.two.stikcy.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.two.stikcy.adapter.CountdownListAdapter
import com.two.stikcy.databinding.AcCountdownTimeBinding
import com.two.stikcy.viewmodel.CountdownViewModel
import kotlinx.coroutines.launch

/**
 * Author:Khaos116
 * Date:2026/5/9
 * Time:10:13
 */
class CountdownTimeActivity : AppCompatActivity() {
  companion object {
    fun startActivity(context: Context) {
      context.startActivity(Intent(context, CountdownTimeActivity::class.java))
    }
  }

  private val viewModel by lazy { CountdownViewModel() }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val vb = AcCountdownTimeBinding.inflate(layoutInflater)
    setContentView(vb.root)
    val adapter = CountdownListAdapter()
    vb.ivBack.setOnClickListener { finish() }
    vb.tvRefresh.setOnClickListener { viewModel.fetchList() }
    vb.recyclerView.adapter = adapter
    // 1. 监听列表数据
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.items.collect { list ->
          adapter.submitList(list)
        }
      }
    }

    // 2. 监听时间脉搏刷新 UI
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.tickerFlow.collect { timestamp ->
          adapter.updateCurrentTime(timestamp)
        }
      }
    }
    // 开始获取数据
    viewModel.fetchList()
  }
}