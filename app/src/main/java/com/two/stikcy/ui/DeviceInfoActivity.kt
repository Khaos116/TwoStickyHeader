package com.two.stikcy.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import android.view.Display
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.two.stikcy.databinding.AcDeviceInfoBinding
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

/**
 * Author:XX
 * Date:2026/9/2
 * Time:16:29
 */
class DeviceInfoActivity : AppCompatActivity() {
  companion object {
    fun startActivity(context: Context) {
      context.startActivity(Intent(context, DeviceInfoActivity::class.java))
    }
  }

  private lateinit var vb: AcDeviceInfoBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    vb = AcDeviceInfoBinding.inflate(layoutInflater)
    setContentView(vb.root)
    vb.ivBack.setOnClickListener { finish() }
    renderFields(safeAreaBucket = null)
    // safeAreaBucket依赖窗口insets,需等View attach后才能取到
    vb.root.post {
      renderFields(safeAreaBucket = resolveSafeAreaBucket())
    }
  }

  private fun renderFields(safeAreaBucket: String?) {
    val dm = resources.displayMetrics
    val density = dm.density
    val config = resources.configuration
    val locale = Locale.getDefault()
    val timeZone = TimeZone.getDefault()

    val fields = listOf(
      "platform" to "ANDROID",
      "device_class" to resolveDeviceClass(dm),
      "os_major" to Build.VERSION.RELEASE.substringBefore('.'),
      "os_minor" to (Build.VERSION.RELEASE.substringAfter('.', "").ifEmpty { "0" }),
      "screen.logical_width" to (dm.widthPixels / density).roundToInt().toString(),
      "screen.logical_height" to (dm.heightPixels / density).roundToInt().toString(),
      "screen.pixel_ratio" to density.toString(),
      "screen.orientation" to if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) "landscape" else "portrait",
      "screen.color_gamut" to resolveColorGamut(),
      "screen.safe_area_bucket" to (safeAreaBucket ?: "计算中..."),
      "locale.primary_language" to locale.language,
      "locale.preferred_languages" to resolvePreferredLanguages(config),
      "locale.locale_identifier" to locale.toString(),
      "locale.region_code" to locale.country,
      "locale.timezone_id" to timeZone.id,
      "locale.utc_offset_minutes" to (timeZone.getOffset(System.currentTimeMillis()) / 60000).toString(),
      "locale.hour_cycle" to if (DateFormat.is24HourFormat(this)) "h23" else "h12",
      "locale.calendar" to android.icu.util.Calendar.getInstance(locale).type,
      "appearance.color_scheme" to resolveColorScheme(config),
      "appearance.reduce_motion" to resolveReduceMotion().toString(),
      "appearance.high_contrast" to "unknown",
    )

    vb.tvContent.text = fields.joinToString("\n\n") { (path, value) -> "【$path】\n$value" }
  }

  private fun resolveDeviceClass(dm: android.util.DisplayMetrics): String {
    val widthDp = dm.widthPixels / dm.density
    val heightDp = dm.heightPixels / dm.density
    val smallestWidthDp = minOf(widthDp, heightDp)
    return if (smallestWidthDp >= 600) "TABLET" else "PHONE"
  }

  private fun resolveColorGamut(): String {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return "unknown"
    val display: Display? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) display else windowManager.defaultDisplay
    return if (display?.isWideColorGamut == true) "p3" else "srgb"
  }

  private fun resolveSafeAreaBucket(): String {
    val insets = ViewCompat.getRootWindowInsets(vb.root) ?: return "unknown"
    val cutout = insets.displayCutout
    val topInsetDp = (cutout?.safeInsetTop ?: 0) / resources.displayMetrics.density
    return when {
      cutout == null && topInsetDp == 0f -> "none"
      topInsetDp <= 32f -> "normal"
      else -> "large"
    }
  }

  private fun resolvePreferredLanguages(config: Configuration): String {
    val locales = config.locales
    return (0 until locales.size()).joinToString(",") { locales[it].toLanguageTag() }
  }

  private fun resolveColorScheme(config: Configuration): String {
    return when (config.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
      Configuration.UI_MODE_NIGHT_YES -> "dark"
      Configuration.UI_MODE_NIGHT_NO -> "light"
      else -> "system"
    }
  }

  private fun resolveReduceMotion(): Boolean {
    val scale = Settings.Global.getFloat(contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
    return scale == 0f
  }
}
