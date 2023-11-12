package com.haidoan.android.stren.core.platform.android.permissions

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
fun handlePermission(
    permissionsState: MultiplePermissionsState,
    onPermissionGranted: () -> Unit,
    onPermissionNotGranted: () -> Unit,
    onNotAllPermissionGranted: () -> Unit,
    onShouldShowRationale: () -> Unit,
) {
    permissionsState.launchMultiplePermissionRequest()

    if (permissionsState.allPermissionsGranted) {
        onPermissionGranted()
    } else {
        val allPermissionsRevoked =
            permissionsState.permissions.size ==
                    permissionsState.revokedPermissions.size

        if (!allPermissionsRevoked) {
            onNotAllPermissionGranted()
        } else if (permissionsState.shouldShowRationale) {
            onShouldShowRationale()
        } else {
            onPermissionNotGranted()
        }
    }
}