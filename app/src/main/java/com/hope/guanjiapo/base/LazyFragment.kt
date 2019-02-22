package com.hope.guanjiapo.base


/**
 *
 * @author kazeik.chen, QQ:77132995, email:kazeik@163.com
 * 类说明:
 */
abstract class LazyFragment : BaseFragment() {
    private var isVisible: Boolean ?= false

    /**
     * 在这里实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser
     */
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (userVisibleHint) {
            isVisible = true
            onVisible()
        } else {
            isVisible = false
            onInvisible()
        }
    }

    private fun onVisible() {
        lazyLoad()
    }

    protected abstract fun lazyLoad()

    protected fun onInvisible() {}
}
