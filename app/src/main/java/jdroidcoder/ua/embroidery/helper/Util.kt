package jdroidcoder.ua.embroidery.helper

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory


object Util {

    fun bitmapToString(bitmap: Bitmap): String? {
        return try {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val b = baos.toByteArray()
            Base64.encodeToString(b, Base64.DEFAULT)
        } catch (e: Exception) {
            e.message
            null
        }

    }

    fun stringToBitMap(encodedString: String): Bitmap? {
        return try {
            val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            e.message
            null
        }
    }
}