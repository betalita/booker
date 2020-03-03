package cn.deepink.framework.common

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import cn.deepink.framework.delegate.FragmentDelegate
import cn.deepink.framework.delegate.ViewDelegate
import cn.deepink.framework.impl.FragmentDelegateImpl

object FragmentLifecycle : FragmentManager.FragmentLifecycleCallbacks() {

    private val delegates by lazy { HashMap<String, FragmentDelegate>() }

    override fun onFragmentAttached(fm: FragmentManager, fragment: Fragment, context: Context) {
        findDelegateFromCache(fragment) { it.onAttached(context) }
    }

    override fun onFragmentCreated(fm: FragmentManager, fragment: Fragment, savedInstanceState: Bundle?) {
        findDelegateFromCache(fragment) { it.onCreated(savedInstanceState) }
    }

    override fun onFragmentViewCreated(fm: FragmentManager, fragment: Fragment, v: View, savedInstanceState: Bundle?) {
        findDelegateFromCache(fragment) { it.onViewCreated(v, savedInstanceState) }
    }

    override fun onFragmentActivityCreated(fm: FragmentManager, fragment: Fragment, savedInstanceState: Bundle?) {
        findDelegateFromCache(fragment) { it.onActivityCreate(savedInstanceState) }
    }

    override fun onFragmentStarted(fm: FragmentManager, fragment: Fragment) {
        findDelegateFromCache(fragment) { it.onStarted() }
    }

    override fun onFragmentResumed(fm: FragmentManager, fragment: Fragment) {
        findDelegateFromCache(fragment) { it.onResumed() }
    }

    override fun onFragmentPaused(fm: FragmentManager, fragment: Fragment) {
        findDelegateFromCache(fragment) { it.onPaused() }
    }

    override fun onFragmentStopped(fm: FragmentManager, fragment: Fragment) {
        findDelegateFromCache(fragment) { it.onStopped() }
    }

    override fun onFragmentSaveInstanceState(fm: FragmentManager, fragment: Fragment, outState: Bundle) {
        findDelegateFromCache(fragment) { it.onSaveInstanceState(outState) }
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, fragment: Fragment) {
        findDelegateFromCache(fragment) { it.onViewDestroyed() }
    }

    override fun onFragmentDestroyed(fm: FragmentManager, fragment: Fragment) {
        findDelegateFromCache(fragment) { it.onDestroyed() }
    }

    override fun onFragmentDetached(fm: FragmentManager, fragment: Fragment) {
        findDelegateFromCache(fragment) {
            it.onDetached()
            delegates.remove(fragment.javaClass.name)
        }
    }

    private fun findDelegateFromCache(fragment: Fragment, block: (delegate: FragmentDelegate) -> Unit) {
        if (fragment !is ViewDelegate) return
        val key = fragment.javaClass.name
        block.invoke(delegates[key] ?: FragmentDelegateImpl(fragment).also { delegates[key] = it })
    }
}