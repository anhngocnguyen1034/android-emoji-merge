package com.anhnn.emoji_merge.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

/** Tải bytes ảnh từ URL (Emoji Kitchen). */
private suspend fun downloadBytes(url: String): ByteArray = withContext(Dispatchers.IO) {
    URL(url).openStream().use { it.readBytes() }
}

/**
 * Lưu ảnh ghép vào thư viện ảnh.
 * Android Q+ dùng MediaStore (không cần quyền); dưới Q lưu vào Pictures qua MediaStore legacy.
 */
suspend fun saveMergeToGallery(context: Context, url: String, displayName: String): Boolean =
    withContext(Dispatchers.IO) {
        try {
            val bytes = downloadBytes(url)
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/EmojiMerge")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: return@withContext false
            resolver.openOutputStream(uri)?.use { it.write(bytes) }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

/** Tải ảnh về cache rồi mở trình chia sẻ. */
suspend fun shareMerge(context: Context, url: String) {
    val file = withContext(Dispatchers.IO) {
        val dir = File(context.filesDir, "images").apply { mkdirs() }
        val out = File(dir, "shared_image.png")
        out.writeBytes(downloadBytes(url))
        out
    }
    val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Chia sẻ emoji"))
}
