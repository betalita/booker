package cn.deepink.booker.app

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import cn.deepink.booker.R
import cn.deepink.booker.common.*
import cn.deepink.booker.controller.HomeViewModel
import cn.deepink.booker.model.Book
import cn.deepink.framework.annotation.BindLayout
import cn.deepink.framework.annotation.BindViewModel
import cn.deepink.framework.delegate.ViewDelegate
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.item_book.view.*

@BindLayout(R.layout.activity_home)
class HomeActivity : AppCompatActivity(), ViewDelegate, View.OnClickListener {

    @BindViewModel
    lateinit var controller: HomeViewModel
    private val adapter by lazy { HomeAdapter() }

    override fun onViewCreated() {
        mHomeRecycler.adapter = adapter
        mHomeRefreshLayout.setOnRefreshListener { checkUpdate() }
    }

    override fun onDataObserve() {
        controller.bookLiveData.observe(this, Observer {
            mHomeTips.isVisible = it.isEmpty()
            mHomeRefreshLayout.isEnabled = it.isNotEmpty()
            adapter.submitList(controller.addBookGroup(it.toMutableList()))
        })
        checkUpdate()
    }

    /**
     * 检查更新
     */
    private fun checkUpdate() {
        controller.checkUpdate().observe(this, Observer { mHomeRefreshLayout.isRefreshing = it })
    }

    /**
     * 显示或隐藏搜索布局
     */
    private fun showOrHideSearchLayout(visible: Int, fragment: Fragment) {
        if (visible == View.VISIBLE) {
            window.navigationBarColor = getColor(R.color.colorForeground)
            supportFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).add(R.id.container, fragment, TAG_SEARCH).commitAllowingStateLoss()
        } else {
            window.navigationBarColor = getColor(R.color.colorBackground)
            supportFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).remove(fragment).commitAllowingStateLoss()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            mHomeAddButton -> showOrHideSearchLayout(View.VISIBLE, SearchFragment())
        }
    }

    override fun onBackPressed() {
        supportFragmentManager.findFragmentByTag(TAG_SEARCH)?.run { return showOrHideSearchLayout(View.INVISIBLE, this) }
        super.onBackPressed()
    }

    /**
     * 查看详情
     */
    private fun openBook(view: View, book: Book) {
        val intent = Intent(this, BookActivity::class.java)
        intent.putExtra(TAG_HOME, book)
        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, getString(R.string.transition)).toBundle())
    }

    inner class HomeAdapter : ListAdapter<Book>(R.layout.item_book, { old, new -> old.areContentTheSame(new) }, { old, new -> old.link == new.link }, { item, book ->
        if (book.source == SOURCE_NONE) {
            (item.itemView as TextView).text = book.name
        } else {
            item.itemView.mBookName.setDrawableStart(book.sourceResId)
            item.itemView.mBookName.text = book.name
            item.itemView.mBookAuthor.text = book.author
            item.itemView.mBookUpdateChapter.text = book.lastChapterName
            item.itemView.mBookUpdateTime.text = book.lastUpdateTime.format()
            item.itemView.mBookStatistics.text = getString(book.statisticsResId, book.chapterTotal, book.monthlyTicket, book.recommendedTicket)
            item.itemView.setOnClickListener { openBook(item.itemView, book) }
        }
    }) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(LayoutInflater.from(this@HomeActivity).inflate(viewType, parent, false))
        }

        override fun getItemViewType(position: Int) = if (getItem(position).source == SOURCE_NONE) R.layout.item_book_group else R.layout.item_book
    }

}