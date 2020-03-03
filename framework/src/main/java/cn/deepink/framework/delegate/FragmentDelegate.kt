package cn.deepink.framework.delegate

import android.content.Context
import android.os.Bundle
import android.view.View

/**
 * @since   2020/3/1
 */
interface FragmentDelegate  {

    fun onAttached(context: Context)

    fun onCreated(savedInstanceState: Bundle?)

    fun onViewCreated(v: View, savedInstanceState: Bundle?)

    fun onActivityCreate(savedInstanceState: Bundle?)

    fun onStarted()

    fun onResumed()

    fun onPaused()

    fun onStopped()

    fun onSaveInstanceState(outState: Bundle)

    fun onViewDestroyed()

    fun onDestroyed()

    fun onDetached()

}