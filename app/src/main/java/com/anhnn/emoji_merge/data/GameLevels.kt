package com.anhnn.emoji_merge.data

import kotlin.random.Random

/**
 * Sinh nội dung mini-game một cách **tất định theo số level** (cùng level luôn cho cùng đề),
 * tái hiện thuật toán của GuessEmojiActivity / WordPictogramActivity trong project tham khảo.
 */
object GameLevels {

    const val LEVEL_TIME_MS = 60_000L

    /** Guess Emoji: đề là ảnh ghép, đáp án là cặp emoji nguồn. */
    fun guessLevel(level: Int): GuessLevel? {
        val catalog = EmojiRepository.emojiCatalog
        val kitchen = EmojiRepository.emojiKitchen
        if (catalog.isEmpty()) return null

        val rng = Random(level * 1_000_003L + 17L)
        val charByHex = catalog.associateBy({ it.emojiAHex }, { it.emojiAChar })

        repeat(80) {
            val a = catalog[rng.nextInt(catalog.size)]
            val partners = kitchen[a.emojiAHex]?.keys
                ?.filter { charByHex.containsKey(it) && it != a.emojiAHex }
                .orEmpty()
            if (partners.isEmpty()) return@repeat

            val bHex = partners[rng.nextInt(partners.size)]
            val path = kitchen[a.emojiAHex]?.get(bHex) ?: return@repeat
            val aChar = a.emojiAChar
            val bChar = charByHex[bHex] ?: return@repeat
            if (aChar == bChar) return@repeat

            val answer = listOf(aChar, bChar)
            val pool = catalog.map { it.emojiAChar }
                .filter { it !in answer }
                .distinct()
                .toMutableList()
            if (pool.size < 4) return@repeat
            pool.shuffle(rng)
            val distractors = pool.take(4)

            val candidates = (answer + distractors).shuffled(rng)
            val target = EmojiMerge(mergeResult = path).imageUrl()
            return GuessLevel(target, answer.toSet(), candidates)
        }
        return null
    }

    /** Word Pictogram: hiện từ gợi ý, chọn đúng 2 emoji theo thứ tự. */
    fun wordLevel(level: Int): WordLevel? {
        val specs = EmojiRepository.wordSpecs
        val all = EmojiRepository.allEmojis
        if (specs.isEmpty() || all.isEmpty()) return null

        val spec = specs[(level - 1).mod(specs.size)]
        val rng = Random(level * 2_000_003L + 29L)

        val answerHexes = spec.hexes.map { normalizeHex(it) }
        val answerSet = answerHexes.toSet()
        val answerCandidates = answerHexes.map { WordCandidate(it, hexToEmoji(it)) }

        val pool = all.map { normalizeHex(it.hex) }
            .filter { it !in answerSet }
            .distinct()
            .toMutableList()
        pool.shuffle(rng)
        val distractors = pool.take(4).map { WordCandidate(it, hexToEmoji(it)) }

        val candidates = (answerCandidates + distractors).shuffled(rng)
        return WordLevel(spec.word, answerHexes, candidates)
    }
}
