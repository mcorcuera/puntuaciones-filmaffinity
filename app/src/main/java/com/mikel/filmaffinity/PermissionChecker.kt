package com.mikel.filmaffinity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings


class PermissionChecker(private val mContext: Context) {
    val isRequiredPermissionGranted: Boolean
        get() = if (isMarshmallowOrHigher) {
            Settings.canDrawOverlays(mContext)
        } else true

    fun createRequiredPermissionIntent(): Intent? {
        return if (isMarshmallowOrHigher) {
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + mContext.packageName)
            )
        } else null
    }

    private val isMarshmallowOrHigher: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    companion object {
        const val REQUIRED_PERMISSION_REQUEST_CODE = 2121
    }

}