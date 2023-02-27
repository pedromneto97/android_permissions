package br.eng.pedro_mendes.permissions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.eng.pedro_mendes.permissions.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setListeners()
    }

    private fun setListeners() {
        binding.buttonCall.setOnClickListener {
            call()
        }
    }

    private fun call() {
        if (
            checkSelfPermission(
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                createMoreInfoDialog()
            } else {
                requestCallPermission()
            }

        } else {
            makeCall(PHONE_NUMBER)
        }
    }

    private fun makeCall(phoneNumber: String) {
        val callIntent = Intent().apply {
            action = Intent.ACTION_CALL
            data = Uri.parse("tel:$phoneNumber")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        startActivity(callIntent)
    }

    private fun createMoreInfoDialog() {
        AlertDialog.Builder(this).apply {
            setMessage(getString(R.string.request_permission_message))
            setTitle(getString(R.string.permission))
            setPositiveButton(R.string.yes) { _, _ ->
                requestCallPermission()
            }
            setNegativeButton(getString(R.string.no)) { d, _ -> d.dismiss() }
        }.show()
    }

    private fun requestCallPermission() = requestPermissions(
        arrayOf(Manifest.permission.CALL_PHONE),
        CALL_PHONE_RESULT_CODE
    )

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CALL_PHONE_RESULT_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    createNeverAskAgainDialog()
                } else {
                    Toast.makeText(
                        this, getString(R.string.permission_denied), Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun createNeverAskAgainDialog() {
        AlertDialog.Builder(this).apply {
            setMessage(getString(R.string.never_ask_again_message))
            setTitle(getString(R.string.permission))
            setPositiveButton(R.string.go_to_settings) { _, _ -> goToAppDetailsSettings() }
            setNegativeButton(getString(R.string.no)) { d, _ -> d.dismiss() }
        }.show()
    }

    private fun goToAppDetailsSettings() {
        val appSettings = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", this@MainActivity.packageName, null)
        }

        startActivity(appSettings)
    }

    companion object {
        private const val CALL_PHONE_RESULT_CODE = 111
        private const val PHONE_NUMBER = "+5500000000000"
    }
}