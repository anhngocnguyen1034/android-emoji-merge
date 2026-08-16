package com.anhnn.emoji_merge.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Nguồn dữ liệu cho tab ghép ảnh RIÊNG — hoàn toàn độc lập với Emoji Kitchen
 * của Google. Dữ liệu bundle sẵn trong assets:
 *  - `local_merge_sources.json`: danh sách 15 ảnh nguồn (id, label, drawable).
 *  - `local_merges.json`      : map cặp (idA__idB) -> drawable ảnh kết quả.
 *
 * Ảnh (nguồn và kết quả) đều là drawable resource trong res/drawable, nên khi
 * đổi PNG sang WEBP chỉ cần thấy tên file — tên resource giữ nguyên.
 */
object LocalMergeRepository {

    private const val TAG = "LocalMergeRepository"
    private const val ASSET_SOURCES = "local_merge_sources.json"
    private const val ASSET_MERGES = "local_merges.json"

    private val gson = Gson()

    @Volatile
    var sources: List<LocalMergeSource> = emptyList()
        private set

    /** idA__idB -> tên drawable ảnh kết quả. */
    @Volatile
    var mergeMap: LocalMergeMap = emptyMap()
        private set

    @Volatile
    private var loaded = false

    val isLoaded: Boolean get() = loaded

    /** Nạp nguồn + map ghép từ assets (an toàn gọi nhiều lần, chạy ở IO). */
    suspend fun load(context: Context) = withContext(Dispatchers.IO) {
        if (loaded) return@withContext
        sources = loadSources(context)
        mergeMap = loadMerges(context)
        loaded = true
        Log.d(TAG, "Nạp xong: ${sources.size} nguồn, ${mergeMap.size} cặp ghép")
    }

    /** Tìm ảnh nguồn theo id. */
    fun sourceById(id: String): LocalMergeSource? =
        sources.firstOrNull { it.id == id }

    /**
     * Giải tên drawable resource sang int res id. Nếu không thấy trả về 0.
     * Package đặt "res:///" để tránh trùng với tên trong package hệ thống.
     */
    fun drawableId(context: Context, drawableName: String): Int =
        if (drawableName.isBlank()) 0
        else context.resources.getIdentifier(
            drawableName, "drawable", context.packageName
        )

    /**
     * Hỏi 2 nguồn đã chọn -> kết quả ghép (nếu cặp này có ảnh kết quả).
     * Key không phân biệt thứ tự (A+B == B+A).
     */
    fun resolve(a: LocalMergeSource, b: LocalMergeSource): LocalMergeResult? {
        val key = LocalMergeResult.pairKey(a.id, b.id)
        val resultDrawable = mergeMap[key] ?: return null
        return LocalMergeResult(sourceA = a, sourceB = b, resultDrawable = resultDrawable)
    }

    private fun loadSources(context: Context): List<LocalMergeSource> {
        val json = context.assetText(ASSET_SOURCES) ?: return emptyList()
        return try {
            gson.fromJson<List<LocalMergeSource>>(
                json, object : TypeToken<List<LocalMergeSource>>() {}.type
            ) ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Không parse được $ASSET_SOURCES", e)
            emptyList()
        }
    }

    private fun loadMerges(context: Context): LocalMergeMap {
        val json = context.assetText(ASSET_MERGES) ?: return emptyMap()
        return try {
            gson.fromJson<Map<String, String>>(
                json, object : TypeToken<Map<String, String>>() {}.type
            )?.let { map -> map.filterKeys { !it.startsWith("_") } } ?: emptyMap()
        } catch (e: Exception) {
            Log.e(TAG, "Không parse được $ASSET_MERGES", e)
            emptyMap()
        }
    }

    private fun Context.assetText(name: String): String? =
        try {
            assets.open(name).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            Log.e(TAG, "Không đọc được asset $name", e)
            null
        }
}
