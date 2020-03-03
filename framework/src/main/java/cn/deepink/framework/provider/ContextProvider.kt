package cn.deepink.framework.provider

import android.app.Application
import android.content.Context
import cn.deepink.framework.BuildConfig
import cn.deepink.framework.common.ActivityLifecycle
import com.blankj.ALog

/**
 * @since   2020/3/1
 */
object ContextProvider {

    lateinit var context: Context

    fun attach(context: Context) {
        ALog.init(context.applicationContext).setLogSwitch(BuildConfig.DEBUG).setGlobalTag("Book")
        (context as? Application)?.registerActivityLifecycleCallbacks(ActivityLifecycle)
        this.context = context.applicationContext
    }

}