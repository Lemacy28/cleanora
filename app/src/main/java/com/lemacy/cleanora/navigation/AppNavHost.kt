package com.lemacy.cleanora.navigation


import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lemacy.cleanora.data.AuthViewModel
import com.lemacy.cleanora.data.CleanerSearchViewModel
import com.lemacy.cleanora.navigation.NavRoutes.CLEANER_JOB_SEARCH
import com.lemacy.cleanora.navigation.NavRoutes.CLEANER_PROFILE
import com.lemacy.cleanora.navigation.NavRoutes.CLIENT_PROFILE
import com.lemacy.cleanora.navigation.NavRoutes.CLIENT_SEARCH
import com.lemacy.cleanora.navigation.NavRoutes.MPESA_PAYMENT
import com.lemacy.cleanora.ui.theme.clientprofile.ClientProfileScreen
import com.lemacy.cleanora.ui.theme.clientsearch.ClientCleanerSearchScreen
import com.lemacy.cleanora.ui.theme.home.CleanerHomeScreen
import com.lemacy.cleanora.ui.theme.home.ClientHomeScreen
import com.lemacy.cleanora.ui.theme.home.GeneralHomeScreen
import com.lemacy.cleanora.ui.theme.jobpostdialog.JobPostScreen
import com.lemacy.cleanora.ui.theme.jobsearch.CleanerJobSearchScreen
import com.lemacy.cleanora.ui.theme.login.LoginScreen
import com.lemacy.cleanora.ui.theme.mpesa.MpesaPaymentScreen
import com.lemacy.cleanora.ui.theme.profile.ProfileScreen
import com.lemacy.cleanora.ui.theme.register.RegisterScreen
import com.lemacy.cleanora.ui.theme.splash.SplashScreen

@Composable
fun AppNavHost(
    navController: NavHostController= rememberNavController()

) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController)
        }
        composable(NavRoutes.GENERAL_HOME) { GeneralHomeScreen(navController) }
        composable(NavRoutes.LOGIN) { LoginScreen(navController) }
        composable(NavRoutes.REGISTER) { RegisterScreen(navController) }
        composable(NavRoutes.CLEANER_HOME) { CleanerHomeScreen(navController) }
        composable(NavRoutes.CLIENT_HOME) { ClientHomeScreen(navController) }
        composable(NavRoutes.JOB_POST) {
            JobPostScreen(navController) // Pass jobViewModel here
        }
        composable(CLEANER_JOB_SEARCH) {
            CleanerJobSearchScreen(navController)
        }
        composable(CLIENT_SEARCH) {
            val cleanerSearchViewModel: CleanerSearchViewModel = viewModel()
            val authViewModel: AuthViewModel = viewModel()
            ClientCleanerSearchScreen(
                cleanerSearchViewModel = cleanerSearchViewModel,
                authViewModel = authViewModel,
                navController = navController
            )}
        composable (CLEANER_PROFILE) {
                ProfileScreen(navController)
            }
        composable(CLIENT_PROFILE) { ClientProfileScreen(navController) }
        composable(MPESA_PAYMENT) {
            MpesaPaymentScreen(navController)


        }

    }

}
