package cn.deepink.framework.delegate

/**
 * 视图（Activity/Fragment）代理接口
 * @since   2020/3/1
 */
interface ViewDelegate {

    fun onViewCreated()

    fun onDataObserve()

}