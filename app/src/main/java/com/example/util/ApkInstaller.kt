package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ApkInstaller {

    fun canInstallApk(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    fun openInstallPermissionSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val intent = Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:${context.packageName}")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_SECURITY_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        }
    }

    fun installApkFromUri(context: Context, sourceUri: Uri): Boolean {
        return try {
            val updateDir = File(context.cacheDir, "updates").apply { mkdirs() }
            val updateFile = File(updateDir, "update.apk")
            if (updateFile.exists()) {
                updateFile.delete()
            }

            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(updateFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return false

            val apkUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                updateFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(installIntent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to launch installer: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            false
        }
    }
}
