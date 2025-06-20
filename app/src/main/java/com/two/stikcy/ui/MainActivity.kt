package com.two.stikcy.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.drakeet.multitype.MultiTypeAdapter
import com.two.stikcy.R
import com.two.stikcy.bean.*
import com.two.stikcy.databinding.AcMainBinding
import com.two.stikcy.decoration.DoubleStickyHeaderDecoration
import com.two.stikcy.item.*
import com.two.stikcy.span.MyTouchLinkSpan
import com.two.stikcy.utils.CollapseUtils

class MainActivity : AppCompatActivity() {
  @SuppressLint("NotifyDataSetChanged")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val vb = AcMainBinding.inflate(layoutInflater)
    setContentView(vb.root)
    val sb = SpannableStringBuilder()
    sb.append("点我")
      .append("点我")
      .append("点我")
      .append("点我")
    val clickSpan = SpannableString("点我")
    clickSpan.setSpan(object : MyTouchLinkSpan(
      colorNormal = Color.BLUE,
      colorPress = Color.RED,
      showUnderline = true,
    ) {
      override fun onSpanClick(widget: View) {
        Toast.makeText(this@MainActivity, "点击了", Toast.LENGTH_SHORT).also { it.setGravity(Gravity.CENTER, 0, 0) }.show()
      }
    }, 0, clickSpan.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    sb.append(clickSpan)
    vb.tvClickSpan.text = sb
    vb.tvClickSpan.setOnClickListener {
      Toast.makeText(this@MainActivity, "点错了", Toast.LENGTH_SHORT).also { it.setGravity(Gravity.CENTER, 0, 0) }.show()
    }
    val adapter = MultiTypeAdapter()
    adapter.register(SchoolDelegate { b ->
      if (b.isCollapse()) { //全部收起的情况
        CollapseUtils.mCollapseClass.removeAll(b.allTags())
      } else {
        val toAdd = b.allTags().filterNot { f -> f in CollapseUtils.mCollapseClass }
        CollapseUtils.mCollapseClass.addAll(toAdd)
      }
      adapter.notifyDataSetChanged()
    })
    adapter.register(ClassDelegate { b ->
      if (b.isCollapse()) {
        CollapseUtils.mCollapseClass.remove(b.collapseTag())
      } else {
        CollapseUtils.mCollapseClass.add(b.collapseTag())
      }
      adapter.notifyDataSetChanged()
    })
    adapter.register(StudentDelegate())
    val items = mutableListOf<Any>()
    val hasSchool = Math.random() > 0.5
    if (hasSchool) {
      for (i in 1..5) {
        val n1 = "学校${i}"
        val schoolBean = SchoolBean(name = n1)
        items.add(schoolBean)
        for (j in 1..5) {
          val n2 = "班级${i}${j}"
          schoolBean.allClassNames.add(n2)
          items.add(ClassBean(schoolName = n1, name = n2))
          for (k in 1..5) {
            val n3 = "学生${i}${j}${k}"
            items.add(StudentBean(schoolName = n1, className = n2, name = n3))
          }
        }
      }
    } else {
      for (j in 1..19) {
        val n2 = "班级${j}"
        items.add(ClassBean(schoolName = "xxx", name = n2))
        for (k in 1..5) {
          val n3 = "学生${j}${k}"
          items.add(StudentBean(schoolName = "xxx", className = n2, name = n3))
        }
      }
    }
    adapter.items = items
    val decoration = DoubleStickyHeaderDecoration(
      mClassT = SchoolBean::class,
      mClassU = ClassBean::class,
      mFloatView1 = vb.floatView1.root,
      mFloatView2 = vb.floatView2.root,
      mMultiTypeAdapter = adapter,
      mCallFillFloatFirst = { b, show, offset ->
        if (show && b != null) {
          vb.floatView1.root.visibility = View.VISIBLE
          vb.floatView1.root.translationY = offset
          vb.floatView1.tvSchoolName.text = b.name
          vb.floatView1.ivExpand.setImageResource(if (b.isCollapse()) R.drawable.ic2025_more_v1 else R.drawable.ic2025_more_v2)
          vb.floatView1.root.setOnClickListener {
            if (b.isCollapse()) { //全部收起的情况
              CollapseUtils.mCollapseClass.removeAll(b.allTags())
            } else {
              val toAdd = b.allTags().filterNot { f -> f in CollapseUtils.mCollapseClass }
              CollapseUtils.mCollapseClass.addAll(toAdd)
            }
            adapter.notifyDataSetChanged()
          }
        } else {
          vb.floatView1.root.visibility = View.GONE
        }
      },
      mCallFillFloatSecond = { b, show, offset ->
        if (show && b != null) {
          vb.floatView2.root.visibility = View.VISIBLE
          vb.floatView2.root.translationY = offset
          vb.floatView2.tvClassName.text = b.name
          vb.floatView2.ivExpand.setImageResource(if (b.isCollapse()) R.drawable.ic2025_more_v1 else R.drawable.ic2025_more_v2)
          vb.floatView2.root.setOnClickListener {
            if (b.isCollapse()) {
              CollapseUtils.mCollapseClass.remove(b.collapseTag())
            } else {
              CollapseUtils.mCollapseClass.add(b.collapseTag())
            }
            adapter.notifyDataSetChanged()
          }
        } else {
          vb.floatView2.root.visibility = View.GONE
        }
      }
    )
    vb.recyclerView.addItemDecoration(decoration)
    vb.recyclerView.adapter = adapter
  }
}