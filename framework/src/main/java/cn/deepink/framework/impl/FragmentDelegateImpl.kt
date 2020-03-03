package cn.deepink.framework.impl

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelStoreOwner
import cn.deepink.framework.annotation.BindLayout
import cn.deepink.framework.annotation.BindViewModel
import cn.deepink.framework.common.ViewModelFactory
import cn.deepink.framework.delegate.FragmentDelegate
import cn.deepink.framework.delegate.ViewDelegate

class FragmentDelegateImpl(private val fragment: Fragment) : FragmentDelegate {

    private val delegate = fragment as ViewDelegate

    override fun onAttached(context: Context) {}

    override fun onCreated(savedInstanceState: Bundle?) {
        onAnalyzeAnnotation()
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        delegate.onViewCreated()
        delegate.onDataObserve()
    }

    override fun onActivityCreate(savedInstanceState: Bundle?) {}

    override fun onStarted() {}

    override fun onResumed() {}

    override fun onPaused() {}

    override fun onStopped() {}

    override fun onSaveInstanceState(outState: Bundle) {}

    override fun onViewDestroyed() {}

    override fun onDestroyed() {}

    override fun onDetached() {}

    private fun onAnalyzeAnnotation() {
        try {
            fragment.javaClass.getAnnotation(BindLayout::class.java)?.run {
                val field = fragment.javaClass.superclass?.getDeclaredField("mContentLayoutId")
                field?.isAccessible = true
                field?.set(fragment, layoutResId)
            }
        }catch (e: Exception) {
        }
        fragment.javaClass.fields.filter { it.isAnnotationPresent(BindViewModel::class.java) }
            .forEach { field ->
                field.isAccessible = true
                field.set(fragment, ViewModelFactory.create(fragment.activity as ViewModelStoreOwner, field))
            }
    }
}