package com.bliss.yang.webpapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.webp.libwebp
import java.io.*
import java.nio.ByteBuffer

class MainActivity : AppCompatActivity() {
    private  val TAG = "MainActivity"

//    companion object{
//        init {
//            System.loadLibrary("webp")
//        }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //        webp   解码速度   编码速度
        var l = System.currentTimeMillis()
        BitmapFactory.decodeResource(resources, R.drawable.splash_bg_webp)
        Log.e(TAG, "解码webp图片耗时:" + (System.currentTimeMillis() - l))

        l = System.currentTimeMillis()
        BitmapFactory.decodeResource(resources, R.drawable.splash_bg_jpeg)
        Log.e(TAG, "解码jpeg图片耗时:" + (System.currentTimeMillis() - l))

        l = System.currentTimeMillis()
        var bitmap = BitmapFactory.decodeResource(resources, R.drawable.splash_bg_png)
        Log.e(TAG, "解码png图片耗时:" + (System.currentTimeMillis() - l))


        //编码  png
        var bitmap1 = BitmapFactory.decodeResource(resources, R.drawable.splash_bg_png)
        l  = System.currentTimeMillis()
        compressBitmap(bitmap1, CompressFormat.PNG, Environment
                .getExternalStorageDirectory().toString() + "/test.png")
        Log.e(TAG, "------->编码png图片耗时:" + (System.currentTimeMillis() - l))

        //编码  jpeg
        bitmap1 = BitmapFactory.decodeResource(resources, R.drawable.splash_bg_jpeg)
        l = System.currentTimeMillis()
        compressBitmap(bitmap1, CompressFormat.JPEG, Environment
                .getExternalStorageDirectory().toString() + "/test.jpeg")
        Log.e(TAG, "------->编码jpeg图片耗时:" + (System.currentTimeMillis() - l))

        //编码  webp
        bitmap1 = BitmapFactory.decodeResource(resources, R.drawable.splash_bg_webp)
        l = System.currentTimeMillis()
        compressBitmap(bitmap1, CompressFormat.WEBP, Environment
                .getExternalStorageDirectory().toString() + "/test.webp")
        Log.e(TAG, "------->编码webp图片耗时:" + (System.currentTimeMillis() - l))

//        编译的解码器
//        l = System.currentTimeMillis()
//        decodeWebp()
//        Log.e(TAG, "libwebp解码图片耗时:" + (System.currentTimeMillis() - l))
//        l = System.currentTimeMillis()
//        encodeWebp(bitmap)
//        Log.e(TAG, "libwebp编码图片耗时:" + (System.currentTimeMillis() - l))
    }

    private fun encodeWebp(bitmap: Bitmap) {
        //获取bitmap 宽高
        val width = bitmap.width
        val height = bitmap.height
        //获得bitmap中的 ARGB 数据 nio
        val buffer: ByteBuffer = ByteBuffer.allocate(bitmap.byteCount)
        bitmap.copyPixelsToBuffer(buffer)
        //编码 获得 webp格式文件数据  4 *width
        val bytes: ByteArray = libwebp.WebPEncodeRGBA(buffer.array(), width, height, width * 4, 75F)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(Environment
                    .getExternalStorageDirectory().toString() + "/libwebp.webp")
            fos.write(bytes)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (null != fos) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun decodeWebp(): Bitmap? {
        @SuppressLint("ResourceType") val `is`: InputStream = resources.openRawResource(R.drawable.splash_bg_webp)
        val bytes = stream2Bytes(`is`)
        //将webp格式的数据转成 argb
        val width = IntArray(1)
        val height = IntArray(1)
        try {
            `is`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val argb: ByteArray = libwebp.WebPDecodeARGB(bytes, bytes.size.toLong(), width, height)
        //将argb byte数组转成 int数组
        val pixels = IntArray(argb.size / 4)
        ByteBuffer.wrap(argb).asIntBuffer().get(pixels)
        //获得bitmap
        return Bitmap.createBitmap(pixels, width[0], height[0], Bitmap.Config.ARGB_8888)
    }

    fun stream2Bytes(`is`: InputStream): ByteArray {
        val bos = ByteArrayOutputStream()
        val buffer = ByteArray(2048)
        var len: Int
        try {
            while (`is`.read(buffer).also { len = it } != -1) {
                bos.write(buffer, 0, len)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bos.toByteArray()
    }

    private fun compressBitmap(bitmap: Bitmap, format: CompressFormat, file: String) {
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        bitmap.compress(format, 75, fos)
        if (null != fos) {
            try {
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}