package cn.deepink.framework.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import java.lang.reflect.Field

/**
 * ViewModel构建工厂
 * @since 2020/3/1
 */
object ViewModelFactory {

    @Suppress("UNCHECKED_CAST")
    fun create(owner: ViewModelStoreOwner, field: Field): ViewModel {
        return ViewModelProvider(owner)[field.genericType as Class<ViewModel>]
    }

}