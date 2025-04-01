package com.example.customerlauncher.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.customerlauncher.R
import com.example.customerlauncher.custom.CustomTitleView
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*


class MainFragment : BrowseSupportFragment() {

    private val mHandler = Handler(Looper.myLooper()!!)
    private val viewModel: HomeViewModel by inject()
    private lateinit var mBackgroundManager: BackgroundManager
    private var mDefaultBackground: Drawable? = null
    private lateinit var mMetrics: DisplayMetrics
    private var mBackgroundTimer: Timer? = null
    private var mBackgroundUri: String? = null
    private lateinit var clockView: TextView
    private lateinit var handler: Handler


    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        clockView = requireActivity().findViewById<TextView>(R.id.clock)
        handler = Handler(Looper.getMainLooper())
        prepareBackgroundManager()
        setupUIElements()
        loadLocationInfo()
        setFocusClickListenr()
    }

    fun View.applyFocusAnimation() {
        this.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).start()
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start()
            }
        }
    }


    private fun setFocusClickListenr() {
        val titleView = requireActivity().findViewById<CustomTitleView>(R.id.title_view)
        val content = titleView.findViewById<TextView>(R.id.content_tv)
        val setting = titleView.findViewById<TextView>(R.id.setting_tv)
        val ott = titleView.findViewById<TextView>(R.id.ott_tv)
        val weatherCardView = requireActivity().findViewById<View>(R.id.weather_card_group)

        content.setOnClickListener {
            weatherCardView.visibility= View.VISIBLE
        }

        setting.setOnClickListener {
            weatherCardView.visibility= View.GONE
        }
        content.applyFocusAnimation()
        setting.applyFocusAnimation()
        ott.applyFocusAnimation()
    }


    override fun onDestroy() {
        super.onDestroy()
        mBackgroundTimer?.cancel()
    }

    private fun loadLocationInfo() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loadLocationInformation()
                    showWeatherInformation()
                }
            }
        }
    }

    private suspend fun showWeatherInformation() {
        viewModel.weatherInformationStateFlow.collect {
            it?.let {

                Log.d("ICON", "${it.weatherIcon}")
//                Glide.with(this)
//                    .load("http://openweathermap.org/img/wn/${it.weatherIcon}.png")
//                    .into(weatherCardView.findViewById(R.id.weatherIcon))
                val weatherCardView = requireActivity().findViewById<View>(R.id.weather_card)
                weatherCardView.findViewById<TextView>(R.id.temperatureText).text = "${it.temp}°"
                weatherCardView.findViewById<TextView>(R.id.cityText).text = it.city
                weatherCardView.findViewById<TextView>(R.id.humidityText).text =
                    "습도 ${it.humidity}%"

                val cardContent = weatherCardView.findViewById<View>(R.id.cardContent)
                val blurredImageView =
                    weatherCardView.findViewById<ImageView>(R.id.blurredImageView)
                val bitmap = cardContent.drawToBitmap()
                val lottieView = weatherCardView.findViewById<LottieAnimationView>(R.id.weatherIcon)
                lottieView.setAnimation(R.raw.sunny)
                lottieView.playAnimation()
                Blurry.with(context)
                    .radius(10)
                    .sampling(2)
                    .from(bitmap)
                    .into(blurredImageView)

            }

        }
    }


    private fun prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(activity)
        mBackgroundManager.attach(activity!!.window)
        mDefaultBackground = ContextCompat.getDrawable(activity!!, R.drawable.background_gradient)
        mMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(mMetrics)
    }

    private fun setupUIElements() {
        // over title
        headersState = BrowseSupportFragment.HEADERS_DISABLED
        isHeadersTransitionOnBackEnabled = false
        // set fastLane (or headers) background color
        brandColor = ContextCompat.getColor(activity!!, android.R.color.transparent)
        startClockUpdate()


    }

    private fun startClockUpdate() {
        val clockTextView = requireActivity().findViewById<TextView>(R.id.clock)
        val handler = Handler(Looper.getMainLooper())

        val updateTimeRunnable = object : Runnable {
            override fun run() {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                timeFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
                val currentTime = timeFormat.format(Date())
                clockTextView.text = currentTime
                handler.postDelayed(this, 1000) // 1초마다 갱신
            }
        }

        handler.post(updateTimeRunnable)
    }


    private fun updateBackground(uri: String?) {
        val width = mMetrics.widthPixels
        val height = mMetrics.heightPixels
        Glide.with(activity!!)
            .load(uri)
            .centerCrop()
            .error(mDefaultBackground)
            .into<SimpleTarget<Drawable>>(
                object : SimpleTarget<Drawable>(width, height) {
                    override fun onResourceReady(
                        drawable: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        mBackgroundManager.drawable = drawable
                    }
                })
        mBackgroundTimer?.cancel()
    }


    private inner class UpdateBackgroundTask : TimerTask() {

        override fun run() {
            mHandler.post { updateBackground(mBackgroundUri) }
        }
    }


}