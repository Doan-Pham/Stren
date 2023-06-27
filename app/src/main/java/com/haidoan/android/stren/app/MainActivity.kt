package com.haidoan.android.stren.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.ui.StrenApp
import com.haidoan.android.stren.core.designsystem.theme.StrenTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


val LocalFacebookCallbackManager =
    staticCompositionLocalOf<CallbackManager> { error("No CallbackManager provided") }
val LocalActivity = staticCompositionLocalOf<ComponentActivity> {
    error("LocalActivity is not present")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var facebookCallbackManager = CallbackManager.Factory.create()

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private var isLoading = true
    private val workoutInProgressViewModel by viewModels<WorkoutInProgressViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { isLoading }
        createNotificationChannel()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                workoutInProgressViewModel.uiState.collect { uiState ->
                    if (uiState.isTraineeFinishResting) {
                        vibrateDevice()
                        workoutInProgressViewModel.finishRestTimer()
                    }
                }
            }
        }



        Firebase.firestore.firestoreSettings =
            FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.GOOGLE_WEB_CLIENT_ID))
                    .build()
            )
            .build()

        setContent {
            StrenTheme {
                CompositionLocalProvider(
                    LocalFacebookCallbackManager provides facebookCallbackManager,
                    LocalActivityResultRegistryOwner provides this,
                    LocalActivity provides this
                ) {
                    StrenApp(onAuthStateResolved = { isLoading = false })
                }
            }
        }
    }

    private fun createNotificationChannel() {

        val name = "notification_channel_stren"
        val descriptionText = "notification_channel_stren"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("CHANNEL_ID_STREN", name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

    }

    @Suppress("DEPRECATION")
    private fun vibrateDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                applicationContext.getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            val vibrator = applicationContext.getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}
