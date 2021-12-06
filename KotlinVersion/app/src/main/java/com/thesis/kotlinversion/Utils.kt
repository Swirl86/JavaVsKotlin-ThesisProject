package com.thesis.kotlinversion

import android.text.TextUtils
import android.graphics.drawable.BitmapDrawable
import android.util.Patterns
import android.webkit.URLUtil
import android.widget.ImageView
import java.lang.Exception
import java.lang.NumberFormatException
import java.net.URL

object Utils {
    @JvmStatic
    fun isNumeric(strNum: String?): Boolean {
        if (strNum == null) {
            return false
        }
        try {
            val d = strNum.toDouble()
        } catch (nfe: NumberFormatException) {
            return false
        }
        return true
    }

    /* https://stackoverflow.com/a/23381504 */
    @JvmStatic
    fun isValidUrl(input: CharSequence): Boolean {
        if (TextUtils.isEmpty(input)) {
            return false
        }
        val URL_PATTERN = Patterns.WEB_URL
        var isURL = URL_PATTERN.matcher(input).matches()
        if (!isURL) {
            val urlString = input.toString() + ""
            if (URLUtil.isNetworkUrl(urlString)) {
                try {
                    URL(urlString)
                    isURL = true
                } catch (ignored: Exception) {
                }
            }
        }
        return isURL
    }

    /* https://stackoverflow.com/a/32066539 */
    fun hasImage(view: ImageView): Boolean {
        val drawable = view.drawable
        var hasImage = drawable != null
        if (hasImage && drawable is BitmapDrawable) {
            hasImage = drawable.bitmap != null
        }
        return hasImage
    }
}