package com.anhnn.emoji_merge.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.util.zip.ZipInputStream

/**
 * Nguồn dữ liệu emoji dùng chung (thay cho `AppData` + các extension trong `duonglh/utils`).
 *
 * Dữ liệu được **bundle sẵn trong assets** nên không phụ thuộc remote config / mạng:
 *  - `emoji_tabs.json`  : danh mục emoji (char + hex + tab).
 *  - `emoji_kitchen.zip`: map ghép Emoji Kitchen (giải nén 1 lần vào cacheDir).
 *
 * Khác bản gốc `com.taymay.emoji.merge`: bản đó tải `emoji_kitchen.zip` / `EmojiTab.json`
 * từ host trong data config rồi cache lại. Ở đây bỏ hẳn nhánh remote — muốn cập nhật bộ
 * emoji thì thay asset và ra bản mới.
 */
object EmojiRepository {

    private const val TAG = "EmojiRepository"

    private const val KITCHEN_CACHE = "emoji_kitchen.json"
    private const val ASSET_KITCHEN_ZIP = "emoji_kitchen.zip"
    private const val ASSET_TABS = "emoji_tabs.json"
    private const val ASSET_WORDS = "word_pictogram.json"

    private val gson = Gson()

    @Volatile
    var emojiKitchen: EmojiKitchen = emptyMap()
        private set

    @Volatile
    var emojiCatalog: List<EmojiMerge> = emptyList()
        private set

    /** Toàn bộ emoji trong danh mục (kể cả emoji không ghép được) — dùng cho mini-games. */
    @Volatile
    var allEmojis: List<EmojiTab> = emptyList()
        private set

    /** Danh sách từ cho Word Pictogram (bundle asset `word_pictogram.json`). */
    @Volatile
    var wordSpecs: List<WordSpec> = emptyList()
        private set

    private var savedStore: SavedEmojiStore? = null

    fun store(context: Context): SavedEmojiStore =
        savedStore ?: SavedEmojiStore(File(context.cacheDir, "EmojiDatabase.json"))
            .also { savedStore = it }

    fun isLoaded(): Boolean = emojiCatalog.isNotEmpty()

    /** Nạp tab + kitchen từ assets rồi dựng catalog (chỉ emoji có trong cả hai). */
    suspend fun load(context: Context) = withContext(Dispatchers.IO) {
        store(context)
        if (emojiCatalog.isNotEmpty()) return@withContext
        val tabs = loadTabs(context)
        val kitchen = loadKitchen(context)
        emojiKitchen = kitchen
        allEmojis = tabs
        emojiCatalog = tabs
            .filter { kitchen.containsKey(it.hex) }
            .map { EmojiMerge(emojiAHex = it.hex, emojiAChar = it.char, tabName = it.tabName) }
        wordSpecs = loadWordSpecs(context)
        Log.d(TAG, "Nạp xong: ${emojiCatalog.size} emoji, ${kitchen.size} khóa kitchen, ${wordSpecs.size} từ")
    }

    private fun loadTabs(context: Context): List<EmojiTab> {
        val json = context.assetText(ASSET_TABS) ?: return emptyList()
        return try {
            gson.fromJson<List<EmojiTab>>(json, object : TypeToken<List<EmojiTab>>() {}.type)
                ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Không parse được $ASSET_TABS", e)
            emptyList()
        }
    }

    private fun loadWordSpecs(context: Context): List<WordSpec> {
        val json = context.assetText(ASSET_WORDS) ?: return emptyList()
        return try {
            gson.fromJson<List<WordSpec>>(json, object : TypeToken<List<WordSpec>>() {}.type)
                ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Không parse được $ASSET_WORDS", e)
            emptyList()
        }
    }

    /** Giải nén asset zip vào cacheDir (chỉ lần đầu), rồi parse map ghép. */
    private fun loadKitchen(context: Context): EmojiKitchen {
        val cache = File(context.cacheDir, KITCHEN_CACHE)
        parseKitchen(cache.readTextOrNull())?.let { return it }
        cache.delete() // chưa có, rỗng, hoặc hỏng
        val json = try {
            context.assets.open(ASSET_KITCHEN_ZIP).use { readFirstJsonEntry(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Không đọc được asset $ASSET_KITCHEN_ZIP", e)
            null
        }
        val kitchen = parseKitchen(json) ?: return emptyMap()
        cache.writeTextSafely(json!!)
        return kitchen
    }

    /** Lấy entry `.json` đầu tiên trong zip (đọc thẳng trong memory, không lo zip-slip). */
    private fun readFirstJsonEntry(input: InputStream): String? =
        ZipInputStream(input).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                if (!entry.isDirectory && entry.name.endsWith(".json")) {
                    return zis.readBytes().toString(Charsets.UTF_8)
                }
                entry = zis.nextEntry
            }
            null
        }

    private fun parseKitchen(json: String?): EmojiKitchen? {
        if (json.isNullOrBlank()) return null
        return try {
            gson.fromJson<EmojiKitchen>(json, object : TypeToken<EmojiKitchen>() {}.type)
                ?.takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            Log.e(TAG, "Không parse được emoji kitchen", e)
            null
        }
    }

    /**
     * Tab + danh sách emoji lọc theo emoji đối tác đã chọn ở slot còn lại.
     * partnerHex rỗng => toàn bộ catalog. (Tương đương `emojiGetTabsFromEmoji`.)
     */
    fun tabsForPartner(partnerHex: String): Pair<Map<String, List<EmojiMerge>>, List<EmojiMerge>> {
        val list = if (partnerHex.isEmpty()) {
            emojiCatalog
        } else {
            val partners = emojiKitchen[partnerHex].orEmpty().keys
            emojiCatalog.filter { partners.contains(it.emojiAHex) }
        }
        val grouped = list.groupBy { it.tabName }
            .mapValues { it.value.distinctBy { e -> e.emojiAChar } }
        return grouped to list
    }

    /** Tra đường dẫn ảnh ghép (đối xứng, thử cả hai chiều). */
    fun resolveMerge(hex1: String, hex2: String): String? =
        emojiKitchen[hex1]?.get(hex2) ?: emojiKitchen[hex2]?.get(hex1)
}

private fun File.readTextOrNull(): String? =
    if (exists() && length() > 0) runCatching { readText() }.getOrNull() else null

private fun File.writeTextSafely(text: String) {
    try {
        parentFile?.mkdirs()
        writeText(text)
    } catch (e: Exception) {
        Log.e("EmojiRepository", "Không ghi được $this", e)
    }
}

private fun Context.assetText(name: String): String? = try {
    assets.open(name).bufferedReader().use { it.readText() }
} catch (e: Exception) {
    Log.e("EmojiRepository", "Không đọc được asset $name", e)
    null
}
