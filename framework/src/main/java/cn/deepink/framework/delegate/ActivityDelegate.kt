package cn.deepink.framework.delegate

import android.app.Activity
import android.os.Bundle

/**
 * Activity代理接口
 * @since  2020/3/1
 */
interface ActivityDelegate {

    fun onCreate(savedInstanceState: Bundle?)

    fun onStart()

    fun onResume()

    fun onPause()

    fun onStop()

    fun onSaveInstanceState(activity: Activity?, outState: Bundle?)

    fun onDestroy()

}