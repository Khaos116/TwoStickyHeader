package com.two.stikcy.bean

import com.two.stikcy.utils.CollapseUtils

/**
 * Author:Khaos116
 * Date:2025/5/21
 * Time:16:37
 */
data class SchoolBean(val name: String) {
  //所有班级名称
  val allClassNames = mutableListOf<String>()

  fun allTags(): List<String> {
    return allClassNames.map { a -> "${name}_${a}" }
  }

  //是否是收起状态
  fun isCollapse(): Boolean {
    val allTag = allClassNames.map { a -> "${name}_${a}" }
    return CollapseUtils.mCollapseClass.containsAll(allTag) //全部收起的情况
  }
}
