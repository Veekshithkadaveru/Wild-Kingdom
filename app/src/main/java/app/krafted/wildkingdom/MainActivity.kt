package app.krafted.wildkingdom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.krafted.wildkingdom.ui.BookmarksScreen
import app.krafted.wildkingdom.ui.ChapterScreen
import app.krafted.wildkingdom.ui.HomeScreen
import app.krafted.wildkingdom.ui.SearchScreen
import app.krafted.wildkingdom.ui.SplashScreen
import app.krafted.wildkingdom.ui.QuizScreen
import app.krafted.wildkingdom.ui.theme.WildKingdomTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WildKingdomTheme {
                WildKingdomApp()
            }
        }
    }
}

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val CHAPTER = "chapter/{chapterId}?tipId={tipId}"
    const val BOOKMARKS = "bookmarks"
    const val SEARCH = "search"
    const val QUIZ = "quiz"

    fun chapter(chapterId: String, tipId: Int? = null): String {
        return if (tipId != null) "chapter/$chapterId?tipId=$tipId"
        else "chapter/$chapterId"
    }
}

@Composable
fun WildKingdomApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(280)
            ) + fadeIn(animationSpec = tween(280))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it / 3 },
                animationSpec = tween(280)
            ) + fadeOut(animationSpec = tween(280))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec = tween(280)
            ) + fadeIn(animationSpec = tween(280))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(280)
            ) + fadeOut(animationSpec = tween(280))
        }
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onChapterClick = { chapterId ->
                    navController.navigate(Routes.chapter(chapterId))
                },
                onSearchClick = {
                    navController.navigate(Routes.SEARCH)
                },
                onBookmarksClick = {
                    navController.navigate(Routes.BOOKMARKS)
                },
                onQuizClick = {
                    navController.navigate(Routes.QUIZ)
                }
            )
        }

        composable(Routes.QUIZ) {
            QuizScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.CHAPTER,
            arguments = listOf(
                navArgument("chapterId") { type = NavType.StringType },
                navArgument("tipId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
            val tipId = backStackEntry.arguments?.getInt("tipId", -1) ?: -1
            ChapterScreen(
                chapterId = chapterId,
                highlightTipId = if (tipId >= 0) tipId else null,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.BOOKMARKS) {
            BookmarksScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.SEARCH) {
            SearchScreen(
                onBackClick = { navController.popBackStack() },
                onResultClick = { chapterId, tipId ->
                    navController.navigate(Routes.chapter(chapterId, tipId))
                }
            )
        }
    }
}
