package com.two.stikcy.bean

import com.two.stikcy.utils.CollapseUtils

/**
 * Author:Khaos116
 * Date:2025/5/21
 * Time:16:37
 */
data class ClassBean(
  val schoolName: String,
  val name: String,
) {
  //收起的表示
  fun collapseTag(): String = "${schoolName}_${name}"

  //是否是收起状态
  fun isCollapse(): Boolean = collapseTag() in CollapseUtils.mCollapseClass
}
