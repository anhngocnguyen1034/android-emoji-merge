package com.anhnn.emoji_merge.ui.nav

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.anhnn.emoji_merge.data.EmojiMerge
import com.anhnn.emoji_merge.data.LocalMergeRepository
import com.anhnn.emoji_merge.data.LocalMergeResult
import com.anhnn.emoji_merge.ui.collection.CollectionScreen
import com.anhnn.emoji_merge.ui.collection.EmojiDetailScreen
import com.anhnn.emoji_merge.ui.games.GamesHubScreen
import com.anhnn.emoji_merge.ui.games.GuessEmojiScreen
import com.anhnn.emoji_merge.ui.games.WordPictogramScreen
import com.anhnn.emoji_merge.ui.home.HomeScreen
import com.anhnn.emoji_merge.ui.localmerge.LocalMergeResultScreen
import com.anhnn.emoji_merge.ui.localmerge.LocalMergeScreen
import com.anhnn.emoji_merge.ui.merge.MergeScreen
import com.anhnn.emoji_merge.ui.result.ResultScreen
import com.anhnn.emoji_merge.ui.splash.SplashScreen

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val MERGE = "merge"
    const val GAMES = "games"
    const val GUESS = "guess"
    const val WORD = "word"
    const val COLLECTION = "collection"

    // Tab ghép bộ ảnh riêng (Local Merge)
    const val LOCAL_MERGE = "local_merge"

    const val ARG_ID = "id"
    const val ARG_EMOJI_A = "a"
    const val ARG_EMOJI_B = "b"
    const val ARG_LOCAL_A_ID = "la"
    const val ARG_LOCAL_B_ID = "lb"
    const val ARG_LOCAL_DRAW = "ld"

    const val DETAIL = "detail?$ARG_ID={$ARG_ID}"
    const val RESULT = "result?$ARG_ID={$ARG_ID}&$ARG_EMOJI_A={$ARG_EMOJI_A}&$ARG_EMOJI_B={$ARG_EMOJI_B}"
    const val LOCAL_RESULT = "local_result?$ARG_LOCAL_A_ID={$ARG_LOCAL_A_ID}" +
        "&$ARG_LOCAL_B_ID={$ARG_LOCAL_B_ID}&$ARG_LOCAL_DRAW={$ARG_LOCAL_DRAW}"

    /**
     * Result bộ ảnh riêng: chỉ truyền id nguồn + tên drawable kết quả.
     * Tái tạo [LocalMergeResult] từ repository tại điểm đích.
     */
    fun localResult(r: LocalMergeResult) = "local_result" +
        "?$ARG_LOCAL_A_ID=${Uri.encode(r.sourceA.id)}" +
        "&$ARG_LOCAL_B_ID=${Uri.encode(r.sourceB.id)}" +
        "&$ARG_LOCAL_DRAW=${Uri.encode(r.resultDrawable)}"

    /**
     * Detail tra lại item từ kho đã lưu nên chỉ cần id.
     *
     * [mergeResult] là đường dẫn ảnh tương đối (có chứa "/") nên phải encode và đưa vào
     * query param — dùng path arg sẽ bị nav-compose tách đoạn sai.
     */
    fun detail(mergeResult: String) = "detail?$ARG_ID=${Uri.encode(mergeResult)}"

    /**
     * Result mang theo đủ dữ liệu nó cần hiển thị, để không phụ thuộc vào việc bản ghép
     * đã được lưu vào kho hay chưa.
     */
    fun result(item: EmojiMerge) = "result" +
        "?$ARG_ID=${Uri.encode(item.mergeResult)}" +
        "&$ARG_EMOJI_A=${Uri.encode(item.emojiAChar)}" +
        "&$ARG_EMOJI_B=${Uri.encode(item.emojiBChar)}"
}

@Composable
fun AppNav(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onDone = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onOpenMerge = { navController.navigate(Routes.MERGE) },
                onOpenLocalMerge = { navController.navigate(Routes.LOCAL_MERGE) },
                onOpenGames = { navController.navigate(Routes.GAMES) },
                onOpenCollection = { navController.navigate(Routes.COLLECTION) },
            )
        }

        composable(Routes.COLLECTION) {
            CollectionScreen(
                onBack = { navController.popBackStack() },
                onCreateNew = {
                    navController.navigate(Routes.MERGE) { popUpTo(Routes.HOME) }
                },
                onOpenDetail = { saved -> navController.navigate(Routes.detail(saved.mergeResult)) },
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument(Routes.ARG_ID) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            ),
        ) { entry ->
            EmojiDetailScreen(
                mergeResult = entry.arguments?.getString(Routes.ARG_ID).orEmpty(),
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.GAMES) {
            GamesHubScreen(
                onBack = { navController.popBackStack() },
                onOpenGuess = { navController.navigate(Routes.GUESS) },
                onOpenWord = { navController.navigate(Routes.WORD) },
            )
        }

        composable(Routes.GUESS) {
            GuessEmojiScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.WORD) {
            WordPictogramScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.MERGE) {
            MergeScreen(
                onBack = { navController.popBackStack() },
                onMerged = { result -> navController.navigate(Routes.result(result)) }
            )
        }

        composable(Routes.LOCAL_MERGE) {
            LocalMergeScreen(
                onBack = { navController.popBackStack() },
                onMerged = { result -> navController.navigate(Routes.localResult(result)) }
            )
        }

        composable(
            route = Routes.LOCAL_RESULT,
            arguments = listOf(
                navArgument(Routes.ARG_LOCAL_A_ID) { type = NavType.StringType; defaultValue = "" },
                navArgument(Routes.ARG_LOCAL_B_ID) { type = NavType.StringType; defaultValue = "" },
                navArgument(Routes.ARG_LOCAL_DRAW) { type = NavType.StringType; defaultValue = "" },
            ),
        ) { entry ->
            val args = entry.arguments
            val aId = args?.getString(Routes.ARG_LOCAL_A_ID).orEmpty()
            val bId = args?.getString(Routes.ARG_LOCAL_B_ID).orEmpty()
            val draw = args?.getString(Routes.ARG_LOCAL_DRAW).orEmpty()
            val sourceA = LocalMergeRepository.sourceById(aId)
            val sourceB = LocalMergeRepository.sourceById(bId)
            val result = if (sourceA != null && sourceB != null) {
                LocalMergeResult(sourceA, sourceB, draw)
            } else {
                null
            }
            if (result == null) {
                navController.popBackStack()
                return@composable
            }
            LocalMergeResultScreen(
                result = result,
                onBack = { navController.popBackStack() },
                onHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onCreateNew = {
                    navController.navigate(Routes.LOCAL_MERGE) { popUpTo(Routes.HOME) }
                },
            )
        }

        composable(
            route = Routes.RESULT,
            arguments = listOf(
                navArgument(Routes.ARG_ID) { type = NavType.StringType; defaultValue = "" },
                navArgument(Routes.ARG_EMOJI_A) { type = NavType.StringType; defaultValue = "" },
                navArgument(Routes.ARG_EMOJI_B) { type = NavType.StringType; defaultValue = "" },
            ),
        ) { entry ->
            val args = entry.arguments
            ResultScreen(
                result = EmojiMerge(
                    mergeResult = args?.getString(Routes.ARG_ID).orEmpty(),
                    emojiAChar = args?.getString(Routes.ARG_EMOJI_A).orEmpty(),
                    emojiBChar = args?.getString(Routes.ARG_EMOJI_B).orEmpty(),
                ),
                onBack = { navController.popBackStack() },
                onHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onCreateNew = {
                    // Về Merge và reset lựa chọn (tương đương EXTRA_CLEAR_SELECTION).
                    navController.navigate(Routes.MERGE) {
                        popUpTo(Routes.HOME)
                    }
                },
                onOpenCollection = {
                    navController.navigate(Routes.COLLECTION) { popUpTo(Routes.HOME) }
                },
            )
        }
    }
}
