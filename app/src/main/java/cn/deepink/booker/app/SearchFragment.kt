package cn.deepink.booker.app

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import cn.deepink.booker.R
import cn.deepink.booker.common.ListAdapter
import cn.deepink.booker.common.TAG_SEARCH
import cn.deepink.booker.common.setDrawableStart
import cn.deepink.booker.controller.SearchViewModel
import cn.deepink.booker.model.Book
import cn.deepink.framework.annotation.BindLayout
import cn.deepink.framework.annotation.BindViewModel
import cn.deepink.framework.delegate.ViewDelegate
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.item_book_search.view.*

@BindLayout(R.layout.fragment_search)
class SearchFragment : Fragment(), ViewDelegate {

    //软键盘管理者
    private val inputMethodManager by lazy { activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager }
    //数据适配器
    private val bookSearchAdapter by lazy { buildBookInfoAdapter() }
    private val quickSearchAdapter by lazy { buildQuickSearchAdapter() }

    //控制器
    @BindViewModel
    lateinit var controller: SearchViewModel

    override fun onViewCreated() {
        mSearchResultRecycler.adapter = bookSearchAdapter
        mSearchQuickRecycler.adapter = quickSearchAdapter
        mSearchBoxInput.post { inputMethodManager?.showSoftInput(mSearchBoxInput, InputMethodManager.SHOW_IMPLICIT) }
        mSearchBoxInput.doAfterTextChanged { text -> quickSearch(text.toString()) }
        mSearchBoxInput.setOnEditorActionListener { _, _, _ -> search(controller.searchKey).let { true } }
        mSearchBoxClear.setOnClickListener { mSearchBoxInput.text?.clear() }
    }

    override fun onDataObserve() {
        controller.searchStateLiveData.observe(viewLifecycleOwner, Observer { view?.run { onSearchStateChanged(it) } })
    }

    /**
     * 图书搜索
     */
    private fun search(bookName: String) {
        controller.search(bookName).observe(viewLifecycleOwner, Observer { bookSearchAdapter.submitList(it) })
    }

    /**
     * 实时搜索
     */
    private fun quickSearch(bookName: String) {
        mSearchBoxClear.isVisible = bookName.isNotBlank()
        mSearchBoxInput.compoundDrawableTintList = ColorStateList.valueOf(requireActivity().getColor(if (bookName.isBlank()) R.color.colorSecondary else R.color.colorContent))
        controller.quickSearch(bookName).observe(viewLifecycleOwner, Observer { quickSearchAdapter.submitList(it) })
    }

    /**
     * 搜索状态变化
     */
    private fun onSearchStateChanged(isQuickSearch: Boolean) {
        if (!isQuickSearch) {
            inputMethodManager?.hideSoftInputFromWindow(mSearchBoxInput.windowToken, 0)
            mSearchBoxInput.clearFocus()
            quickSearchAdapter.submitList(emptyList())
        } else {
            bookSearchAdapter.submitList(emptyList())
        }
        mSearchQuickRecycler.isVisible = isQuickSearch
        mSearchResultTitle.isVisible = !isQuickSearch
        mSearchResultRecycler.isVisible = !isQuickSearch
    }

    private fun buildQuickSearchAdapter() = ListAdapter<Book>(R.layout.item_quick_search, sameContent = { _, _ -> false }) { item, book ->
        val indexOf = book.name.indexOf(controller.searchKey)
        val bookName = SpannableStringBuilder(book.name)
        if (indexOf > -1) {
            bookName.setSpan(ForegroundColorSpan((item.itemView as TextView).highlightColor), indexOf, indexOf + controller.searchKey.length, SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        (item.itemView as TextView).text = bookName
        item.itemView.setOnClickListener { search(book.name) }
    }

    private fun buildBookInfoAdapter() = ListAdapter<Book>(R.layout.item_book_search) { item, book ->
        item.itemView.mBookName.setDrawableStart(book.sourceType.icon)
        item.itemView.mBookName.text = book.name
        item.itemView.mBookAuthor.text = book.author
        item.itemView.mBookSummary.text = book.summary.trim()
        item.itemView.setOnClickListener {
            val intent = Intent(requireContext(), BookActivity::class.java)
            intent.putExtra(TAG_SEARCH, book)
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), it, getString(R.string.transition)).toBundle())
        }
    }

}