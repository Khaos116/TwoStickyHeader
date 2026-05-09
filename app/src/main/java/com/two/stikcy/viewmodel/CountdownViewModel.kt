package com.two.stikcy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.two.stikcy.bean.CountdownItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

/**
 * Author:Khaos116
 * Date:2026/5/9
 * Time:10:50
 */
class CountdownViewModel : ViewModel() {

  // 每秒发出一次当前系统时间
  val tickerFlow: Flow<Long> = flow {
    while (currentCoroutineContext().isActive) {
      emit(System.currentTimeMillis())
      delay(1000)
    }
  }.flowOn(Dispatchers.Default) // 在后台线程计时

  // 模拟接口数据
  private val _items = MutableStateFlow<List<CountdownItem>>(emptyList())
  val items: StateFlow<List<CountdownItem>> = _items

  // 用于生成唯一 ID 的计数器
  private var idCounter = 0

  fun fetchList() {
    viewModelScope.launch {
      // 模拟加载效果
      delay(300)
      val now = System.currentTimeMillis()
      val currentList = _items.value.toMutableList()
      // 1. 如果是第一次刷新，初始化 10 条数据
      if (currentList.isEmpty()) {
        repeat(10) {
          currentList.add(createRandomItem(now))
        }
      } else {
        // 2. 否则，在原有基础上新增 1 条数据
        currentList.add(createRandomItem(now))
      }
      // 3. 按照过期时间从小到大排序 (升序)
      val sortedList = currentList.sortedBy { it.expireTime }
      // 更新数据源
      _items.value = sortedList
    }
  }

  // 提取生成随机数据的辅助函数
  private fun createRandomItem(now: Long): CountdownItem {
    idCounter++
    val prefix = listOf("特价", "秒杀", "打折", "预售")
    val suffix = listOf("电脑", "手机", "相机", "手表")
    // 随机 10秒 到 1小时 之间
    val randomDuration = Random.nextLong(10000, 3600000)
    return CountdownItem(
      id = "id_$idCounter", // 唯一的 ID
      title = "${prefix.random()}${suffix.random()} ($idCounter)",
      expireTime = now + randomDuration
    )
  }
}