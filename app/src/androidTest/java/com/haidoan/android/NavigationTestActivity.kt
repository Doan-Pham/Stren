package com.haidoan.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.facebook.CallbackManager
import com.haidoan.android.stren.app.LocalFacebookCallbackManager
import com.haidoan.android.stren.app.StrenAppViewModel
import com.haidoan.android.stren.app.ui.StrenApp
import com.haidoan.android.stren.core.datasource.remote.impl.UserFirestoreDataSource
import com.haidoan.android.stren.core.designsystem.theme.StrenTheme
import com.haidoan.android.stren.core.domain.HandleUserCreationUseCase
import com.haidoan.android.stren.core.repository.impl.UserRepositoryImpl
import com.haidoan.android.stren.core.service.FakeAuthenticationServiceImpl
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class NavigationTestActivity : ComponentActivity() {
    private var facebookCallbackManager = CallbackManager.Factory.create()
    var isUserSignedIn by mutableStateOf(false)
    private val fakeAuthenticationServiceImpl = FakeAuthenticationServiceImpl()
    private val handleUserCreationUseCase = HandleUserCreationUseCase(
        UserRepositoryImpl(UserFirestoreDataSource()),
        fakeAuthenticationServiceImpl
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        val viewModel: StrenAppViewModel by viewModels {
            StrenAppViewModel.Factory(
                handleUserCreationUseCase
            )
        }
        setContent {
            StrenTheme {
                CompositionLocalProvider(
                    LocalFacebookCallbackManager provides facebookCallbackManager,
                    LocalActivityResultRegistryOwner provides this
                ) {
                    Timber.d("isUserSignedIn: $isUserSignedIn")
                    fakeAuthenticationServiceImpl.isUserSignedIn = isUserSignedIn

                    StrenApp(viewModel = viewModel)
                }
            }
        }
    }
}