package com.nze.nzexchange.tools

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/11
 */
class MD5Tool {
    companion object {
        private val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

        private fun bytesToHex(bytes: ByteArray): String {
            val sb = StringBuffer()
            var t: Int
            for (i in 0..15) {
                t = bytes[i].toInt()
                if (t < 0)
                    t += 256
                sb.append(hexDigits[t.ushr(4)])
                sb.append(hexDigits[t % 16])
            }
            return sb.toString()
        }

        @Throws(Exception::class)
        fun getMd5_16(input: String, bit: Int): String {
            try {
                val md = MessageDigest.getInstance(System.getProperty("MD5.algorithm", "MD5"))
                return if (bit == 16) bytesToHex(md.digest(input.toByteArray(charset("utf-8")))).substring(8, 24) else bytesToHex(md.digest(input.toByteArray(charset("utf-8"))))
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
                throw Exception("Could not found MD5 algorithm.", e)
            }

        }

        fun getMd5_32(plainText: String): String {
            var re_md5 = String()
            try {
                val md = MessageDigest.getInstance("MD5")
                md.update(plainText.toByteArray())
                val b = md.digest()
                var i: Int
                val buf = StringBuffer("")
                for (offset in b.indices) {
                    i = b[offset].toInt()
                    if (i < 0)
                        i += 256
                    if (i < 16)
                        buf.append("0")
                    buf.append(Integer.toHexString(i))
                }
                re_md5 = buf.toString()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }

            return re_md5
        }

        fun getMd5_32_UP(plainText: String): String {
            val res = getMd5_32(plainText)
            return res.toUpperCase()
        }
    }

}