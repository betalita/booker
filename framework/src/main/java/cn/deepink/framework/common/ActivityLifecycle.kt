package cn.deepink.framework.common

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import cn.deepink.framework.delegate.ActivityDelegate
import cn.deepink.framework.delegate.ViewDelegate
import cn.deepink.framework.impl.ActivityDelegateImpl

object ActivityLifecycle : Application.ActivityLifecycleCallbacks {

    private val delegates by lazy { HashMap<String, ActivityDelegate>() }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        findDelegateFromCache(activity) { it.onCreate(savedInstanceState) }
        if (activity is FragmentActivity) activity.supportFragmentManager.registerFragmentLifecycleCallbacks(FragmentLifecycle, true)
    }

    override fun onActivityResumed(activity: Activity) {
        findDelegateFromCache(activity) { it.onResume() }
    }

    override fun onActivityStarted(activity: Activity) {
        findDelegateFromCache(activity) { it.onStart() }
    }

    override fun onActivityPaused(activity: Activity) {
        findDelegateFromCache(activity) { it.onPause() }
    }

    override fun onActivityStopped(activity: Activity) {
        findDelegateFromCache(activity) { it.onStop() }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        findDelegateFromCache(activity) { it.onSaveInstanceState(activity, outState) }
    }

    override fun onActivityDestroyed(activity: Activity) {
        findDelegateFromCache(activity) {
            it.onDestroy()
            delegates.remove(activity.javaClass.name)
        }
    }

    private fun findDelegateFromCache(activity: Activity, block: (delegate: ActivityDelegate) -> Unit) {
        if (activity !is ViewDelegate) return
        val key = activity.javaClass.name
        block.invoke(delegates[key] ?: ActivityDelegateImpl(activity).also { delegates[key] = it })
    }

}