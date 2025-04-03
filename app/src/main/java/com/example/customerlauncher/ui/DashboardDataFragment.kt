package com.example.customerlauncher.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.StatFs
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.customerlauncher.R
import java.io.File
import java.net.InetAddress
import java.net.NetworkInterface
import java.text.DecimalFormat

class DashboardDataFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dashboard_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val networkStatus = view.findViewById<TextView>(R.id.network_status)
        val deviceInfo = view.findViewById<TextView>(R.id.device_info)
        val storageInfo = view.findViewById<TextView>(R.id.storage_info)

        val appNetflix = view.findViewById<ImageView>(R.id.app_netflix)
        val appWave = view.findViewById<ImageView>(R.id.app_wave)
        val appYoutube = view.findViewById<ImageView>(R.id.app_youtube)
        setAppIcon(appNetflix, "com.netflix.ninja")
        setAppIcon(appWave, "kr.co.captv") // wavve 실제 패키지 확인 필요
        setAppIcon(appYoutube, "com.google.android.youtube.tv")
        // 네트워크 상태
        networkStatus.text = getNetworkStatus()

        // 디바이스 정보
        val ip = getLocalIpAddress()
        val model = Build.MODEL
        val version = Build.DISPLAY
        deviceInfo.text = "IP: $ip\nModel: $model\nBuild: $version"
        Log.d("Dash", "$ip $model $version")
        // 저장 공간
        storageInfo.text = getStorageInfo()

        // 앱 실행
        appNetflix.setOnClickListener { launchApp("com.netflix.ninja") }
        appWave.setOnClickListener { launchApp("kr.co.captv") }
        appYoutube.setOnClickListener { launchApp("com.google.android.youtube.tv") }
    }

    private fun getNetworkStatus(): String {
        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        Log.d("Dash", "${cm.activeNetwork}")
        val network = cm.activeNetwork ?: return "네트워크 없음"
        val capabilities = cm.getNetworkCapabilities(network) ?: return "네트워크 없음"
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi 연결됨"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "LAN 연결됨"
            else -> "네트워크 없음"
        }
    }

    private fun getLocalIpAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            for (intf in interfaces) {
                val addrs = intf.inetAddresses
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress && addr is InetAddress) {
                        val hostAddress = addr.hostAddress ?: continue
                        if (hostAddress.indexOf(':') < 0) return hostAddress
                    }
                }
            }
        } catch (ex: Exception) {}
        return "-"
    }

    private fun getStorageInfo(): String {
        val stat = StatFs(File(requireContext().filesDir.absolutePath).absolutePath)
        val total = stat.blockCountLong * stat.blockSizeLong
        val avail = stat.availableBlocksLong * stat.blockSizeLong
        val format = DecimalFormat("#.##")
        return "저장공간: ${format.format(avail / 1e9)}GB / ${format.format(total / 1e9)}GB"
    }

    private fun launchApp(packageName: String) {
        val launchIntent = requireContext().packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?.let { startActivity(it) }
    }

    private fun setAppIcon(imageView: ImageView, packageName: String) {
        try {
            val pm = requireContext().packageManager
            val icon = pm.getApplicationIcon(packageName)
            imageView.setImageDrawable(icon)

            imageView.setOnClickListener {
                val intent = pm.getLaunchIntentForPackage(packageName)
                intent?.let { startActivity(it) }
            }
        } catch (e: Exception) {
            imageView.visibility = View.GONE // 앱 설치 안됨
        }
    }
}