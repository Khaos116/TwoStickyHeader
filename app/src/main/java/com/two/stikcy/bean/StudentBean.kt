package com.two.stikcy.bean

import com.two.stikcy.utils.CollapseUtils

/**
 * Author:Khaos116
 * Date:2025/5/21
 * Time:16:37
 */
data class StudentBean(
  val schoolName: String,
  val className: String,
  val name: String,
) {
  //是否是收起状态
  fun isCollapse(): Boolean = "${schoolName}_${className}" in CollapseUtils.mCollapseClass
}
