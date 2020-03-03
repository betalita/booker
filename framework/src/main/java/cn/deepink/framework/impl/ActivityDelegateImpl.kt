package cn.deepink.framework.impl

import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.ViewModelStoreOwner
import cn.deepink.framework.annotation.BindLayout
import cn.deepink.framework.annotation.BindViewModel
import cn.deepink.framework.common.ViewModelFactory
import cn.deepink.framework.delegate.ActivityDelegate
import cn.deepink.framework.delegate.ViewDelegate

/**
 * Activity代理实现
 * @since   2020/3/1
 */
class ActivityDelegateImpl(private val activity: Activity) : ActivityDelegate {

    private val delegate = activity as ViewDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        onAnalyzeAnnotation()
        delegate.onViewCreated()
        delegate.onDataObserve()
    }

    override fun onStart() {}

    override fun onResume() {}

    override fun onPause() {}

    override fun onStop() {}

    override fun onSaveInstanceState(activity: Activity?, outState: Bundle?) {}

    override fun onDestroy() {}

    private fun onAnalyzeAnnotation() {
        activity.javaClass.getAnnotation(BindLayout::class.java)?.run { activity.setContentView(layoutResId) }
        activity.javaClass.fields.filter { it.isAnnotationPresent(BindViewModel::class.java) }
            .forEach { field ->
                field.isAccessible = true
                field.set(activity, ViewModelFactory.create(activity as ViewModelStoreOwner, field))
            }
    }

}