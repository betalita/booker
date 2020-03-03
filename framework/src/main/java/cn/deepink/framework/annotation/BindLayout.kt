package cn.deepink.framework.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class BindLayout(val layoutResId: Int)