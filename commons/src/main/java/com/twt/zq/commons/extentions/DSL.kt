package com.twt.zq.commons.extentions

/**
 * Created by SGXM on 2018/7/12.
 */
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
import com.twt.zq.commons.R
import com.twt.zq.commons.common.CommonContext
import com.twt.zq.commons.util.DensityUtil.dp2px
import kotlinx.android.synthetic.main.item_double_text.view.*
import kotlinx.android.synthetic.main.item_double_text_expand.view.*
import kotlinx.android.synthetic.main.item_empty.view.*
import kotlinx.android.synthetic.main.item_expand.view.*
import kotlinx.android.synthetic.main.item_expand.view.layout_content
import kotlinx.android.synthetic.main.item_expand.view.rec_content
import kotlinx.android.synthetic.main.item_img_text.view.*
import kotlinx.android.synthetic.main.item_single_edit.view.*
import kotlinx.android.synthetic.main.item_single_text.view.*
import org.jetbrains.anko.*

interface Item {
    val controller: ItemController

    fun areItemsTheSame(newItem: Item): Boolean = false

    fun areContentsTheSame(newItem: Item): Boolean = false
}

class EmptyItem(val img: Drawable = CommonContext.application.getDrawable(R.drawable.item_empty_data)) : Item {
    companion object : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_empty, parent, false)
            return ViewHolder(view, view.pic)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as EmptyItem
            holder.pic.image = item.img
        }
    }

    class ViewHolder(val view: View, val pic: ImageView) :
        RecyclerView.ViewHolder(view)

    override val controller = Companion
}

fun LoadMoreItem() = SingTextItem("上拉显示更多", false) {
    backgroundColor = Color.TRANSPARENT
}

fun NoMoreItem() = SingTextItem("~亲，数据都加载完啦！~", false) {
    backgroundColor = Color.TRANSPARENT
}

fun NoDataItem() = SingTextItem("没有数据", false) {
    backgroundColor = Color.TRANSPARENT
}

fun MutableList<Item>.addNoMore() {
    if (this.isNotEmpty() && this.last() !is EmptyItem && this.last() is SingTextItem) {
        this.removeAt(this.size - 1)
        add(NoMoreItem())
    } else {
        add(NoMoreItem())
    }
}

fun MutableList<Item>.addLoadMore(): MutableList<Item> {
    if (this.isNotEmpty() && this.last() !is EmptyItem && this.last() is SingTextItem) {
        this.removeAt(this.size - 1)
        add(LoadMoreItem())
    } else {
        add(LoadMoreItem())
    }
    return this
}

interface canLoadMore {
    public var hasMore: MutableLiveData<Boolean>
    public var page: Int
}

fun MutableList<Item>.dealGet(obj: canLoadMore): MutableList<Item> {
    if (isEmpty()) {
        add(EmptyItem(CommonContext.application.getDrawable(R.drawable.item_empty_data)))
        obj.hasMore.value = false
    } else {
        obj.hasMore.value = true
        addLoadMore()
    }
    return this
}

fun MutableList<Item>.dealFetch(obj: canLoadMore, list: List<Item>): MutableList<Item> {
    if (list.isEmpty()) {
        obj.hasMore.value = false
        remove(last())
        addNoMore()
    } else {
        remove(last())
        addAll(list)
        addLoadMore()
    }
    return this
}

class ImgTxtItem(
    val pic: String,
    val name: String = "",
    val text2: String = "",
    val uid: String = "",
    val obj: Any = "",
    val tag: String = "",
    var callBack: (Any) -> Unit = {}

) :
    Item {
    companion object : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_img_text, parent, false)
            return ViewHolder(view, view.img_avatar, view.tv_name, view.tv_uid, view.tv_tag)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as ImgTxtItem
            holder.apply {
                if (item.tag.isNotEmpty()) {
                    tag.visibility = View.VISIBLE
                    tag.text = item.tag

                } else {
                    tag.visibility = View.GONE
                }
                avatar.isOval = item.tag != "群"
                Glide.with(avatar).load(item.pic).into(avatar)
                name.text = item.name
                uid.text = item.text2
                view.setOnClickListener {
                    item.callBack(item.obj)
                }
            }
        }
    }

    class ViewHolder(
        val view: View,
        val avatar: RoundedImageView,
        val name: TextView,
        val uid: TextView,
        val tag: TextView
    ) :
        RecyclerView.ViewHolder(view) {
        fun update(pic: String) {
            Log.e("woggle", pic)
            Glide.with(avatar).load(pic).into(avatar)
        }
    }

    override val controller = Companion
}

class CheckedImgTxtItem(
    val pic: String,
    val name: String = "",
    val text2: String = "",
    val uid: String = "",
    val obj: Any = "",
    val tag: String = "",
    var isChecked: Boolean = false,
    var callBack: (Any) -> Unit = {}

) :
    Item {
    override fun areContentsTheSame(newItem: Item): Boolean = false

    companion object : ItemController {
        val checkedList = HashMap<String, Boolean>()

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_img_text, parent, false)
            return ViewHolder(view, view.img_avatar, view.tv_name, view.tv_uid, view.tv_tag, view.ck_select)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as CheckedImgTxtItem
            holder.apply {
                if (item.tag.isNotEmpty()) {
                    tag.visibility = View.VISIBLE
                    tag.text = item.tag

                } else {
                    tag.visibility = View.GONE
                }
                avatar.isOval = item.tag != "群"
                Glide.with(avatar).load(item.pic).into(avatar)
                name.text = item.name
                uid.text = item.text2
                check.visibility = View.VISIBLE
                Log.e("wogglec", item.uid + " " + (checkedList.get(item.uid) ?: false).toString())
                check.isChecked = checkedList.get(item.uid) ?: false
                check.isClickable = false
                view.setOnClickListener {
                    item.callBack(item.obj)
                    checkedList[item.uid] = !(checkedList.get(item.uid) ?: false)
                    check.isChecked = (checkedList.get(item.uid) ?: false)
                    Log.e("woggle", checkedList.toString())
                }

            }
        }
    }

    class ViewHolder(
        val view: View,
        val avatar: RoundedImageView,
        val name: TextView,
        val uid: TextView,
        val tag: TextView,
        val check: CheckBox
    ) :
        RecyclerView.ViewHolder(view) {
        fun update(pic: String) {
            Log.e("woggle", pic)
            Glide.with(avatar).load(pic).into(avatar)
        }
    }

    override val controller = Companion
}

class SingleEditItem(
    val title: String,
    val hint: String = "",
    val init: View.() -> Unit = {}
) : Item {
    override fun areItemsTheSame(newItem: Item): Boolean = false

    override fun areContentsTheSame(newItem: Item): Boolean = false

    companion object : ItemController {


        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_single_edit, parent, false)
            return ViewHolder(view, view.tv_single_edit, view.edit_single_edit)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as SingleEditItem
            holder.apply {
                title.text = item.title
                edt.hint = item.hint
                view.apply(item.init)
                edt.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        item.value = if (s.toString().isNotEmpty()) s.toString().toFloat() else 0f
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })

            }
        }
    }

    var value = 0f

    class ViewHolder(

        val view: View,
        val title: TextView,
        val edt: EditText
    ) : RecyclerView.ViewHolder(view)

    override val controller = Companion
}

class SingEditItemEx(
    val title: String,
    val hint: String = "",
    val init: View.() -> Unit = {},
    val obj: Any = {}
) : Item {
    override fun areItemsTheSame(newItem: Item): Boolean = false
    override fun areContentsTheSame(newItem: Item): Boolean = false
//        (newItem as? SingEditItemEx)?.text == this.text && (newItem as? SingEditItemEx)?.title == this.title

    var set = false

    companion object : ItemController {
        val resMap = HashMap<String, String>()

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_single_edit_ex, parent, false)
            return ViewHolder(view, view.tv_single_edit, view.edit_single_edit)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as SingEditItemEx
            holder.apply {
                title.text = item.title
                edt.hint = item.hint
//                Log.e("woggle","load ${item.title}  ${item.text}")
                item.set = false
                edt.setText(resMap[item.title] ?: "")
                item.set = true
                view.apply(item.init)
                edt.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
//                        item.value = if (s.toString().isNotEmpty()) s.toString().toInt() else 0
//                        item.text = s.toString()
                        if (item.set) {
                            resMap[item.title] = s.toString()
                            Log.e("woggle", "store ${item.title}  ${s.toString()}")
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })

            }
        }
    }

    var value = 0

    class ViewHolder(

        val view: View,
        val title: TextView,
        val edt: EditText
    ) : RecyclerView.ViewHolder(view)

    override val controller = Companion
}


class SingTextItem(
    val title: String,
    val show: Boolean = true,
    val gravity: Int = Gravity.CENTER,
    val init: View.() -> Unit = {}
) : Item, Comparable<Item> {
    override fun compareTo(other: Item): Int {
        return -1
    }

    constructor(
        title: String,
        show: Boolean = true,
        end: String,
        gravity: Int = Gravity.CENTER,
        init: View.() -> Unit = {}
    ) : this(title, show, gravity, init) {
        endtext = end
    }

    var endtext = "v"
    override fun areItemsTheSame(newItem: Item): Boolean {
        return (newItem as? SingTextItem)?.title == this.title
    }

    override fun areContentsTheSame(newItem: Item): Boolean {
        return (newItem as? SingTextItem)?.title == this.title
    }

    companion object : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_single_text, parent, false)
            return ViewHolder(view, view.tv_single_title, view.tv_single_end)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as SingTextItem
            holder.apply {
                title.text = item.title
                title.gravity = item.gravity
                end.text = item.endtext
                end.visibility = if (item.show) View.VISIBLE else View.GONE
                view.apply(item.init)
            }
        }
    }

    class ViewHolder(

        val view: View,
        val title: TextView,
        val end: TextView
    ) : RecyclerView.ViewHolder(view)

    override val controller = Companion
}

class SingTextItemWrap(
    val title: String,
    val show: Boolean = true,
    val gravity: Int = Gravity.CENTER,
    val init: TextView.() -> Unit = {}
) : Item {

    constructor(
        title: String,
        show: Boolean = true,
        end: String,
        gravity: Int = Gravity.CENTER,
        init: View.() -> Unit = {}
    ) : this(title, show, gravity, init) {
        endtext = end
    }

    var endtext = "v"
    override fun areItemsTheSame(newItem: Item): Boolean {
        return (newItem as? SingTextItemWrap)?.title == this.title
    }

    override fun areContentsTheSame(newItem: Item): Boolean {
        return (newItem as? SingTextItemWrap)?.title == this.title
    }

    companion object : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_single_text_wrap, parent, false)
            return ViewHolder(view, view.tv_single_title, view.tv_single_end)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as SingTextItemWrap
            holder.apply {
                title.apply(item.init)
                title.text = item.title
                title.gravity = item.gravity
                end.text = item.endtext
                end.visibility = if (item.show) View.VISIBLE else View.GONE
                //  view.apply(item.init)
            }
        }
    }

    class ViewHolder(

        val view: View,
        val title: TextView,
        val end: TextView
    ) : RecyclerView.ViewHolder(view)

    override val controller = Companion
}

class SingTextItemGreyEx(
    val title: String,
    val show: Boolean = true,
    val gravity: Int = Gravity.CENTER,
    val init: TextView.() -> Unit = {}
) : Item {

    constructor(
        title: String,
        show: Boolean = true,
        end: String,
        gravity: Int = Gravity.CENTER,
        init: View.() -> Unit = {}
    ) : this(title, show, gravity, init) {
        endtext = end
    }

    var endtext = "v"
    override fun areItemsTheSame(newItem: Item): Boolean {
        return (newItem as? SingTextItemGreyEx)?.title == this.title
    }

    override fun areContentsTheSame(newItem: Item): Boolean {
        return (newItem as? SingTextItemGreyEx)?.title == this.title
    }

    companion object : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_single_text, parent, false)
            return ViewHolder(view, view.tv_single_title, view.tv_single_end, view.img_del)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as SingTextItemGreyEx
            holder.apply {
                title.apply(item.init)
                title.text = item.title
                title.gravity = item.gravity
                end.text = item.endtext
                end.visibility = if (item.show) View.VISIBLE else View.GONE
                view.backgroundColorResource = R.color.homeBG
                //  view.apply(item.init)
            }
        }
    }

    class ViewHolder(

        val view: View,
        val title: TextView,
        val end: TextView,
        val del: View
    ) : RecyclerView.ViewHolder(view)

    override val controller = Companion
}

class SingTextItemEx(
    val title: String,
    val show: Boolean = true,
    val gravity: Int = Gravity.CENTER,
    val init: TextView.() -> Unit = {}
) : Item {

    constructor(
        title: String,
        show: Boolean = true,
        end: String,
        gravity: Int = Gravity.CENTER,
        init: View.() -> Unit = {}
    ) : this(title, show, gravity, init) {
        endtext = end
    }

    var endtext = "v"
    override fun areItemsTheSame(newItem: Item): Boolean {
        return (newItem as? SingTextItemEx)?.title == this.title
    }

    override fun areContentsTheSame(newItem: Item): Boolean {
        return (newItem as? SingTextItemEx)?.title == this.title
    }

    companion object : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_single_text, parent, false)
            return ViewHolder(view, view.tv_single_title, view.tv_single_end, view.img_del)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as SingTextItemEx
            holder.apply {
                title.apply(item.init)
                title.text = item.title
                title.gravity = item.gravity
                end.text = item.endtext
                end.visibility = if (item.show) View.VISIBLE else View.GONE
                //  view.apply(item.init)
            }
        }
    }

    class ViewHolder(

        val view: View,
        val title: TextView,
        val end: TextView,
        val del: View
    ) : RecyclerView.ViewHolder(view)

    override val controller = Companion
}

class DoubleTextItem(
    val start: String = "",
    val end: String = "",
    val sinit: TextView.() -> Unit = {},
    val viewinit: View.() -> Unit = {},
    val einit: TextView.() -> Unit = {}
) : Item {
    override fun areItemsTheSame(newItem: Item): Boolean = false

    override fun areContentsTheSame(newItem: Item): Boolean = false

    companion object : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_double_text, parent, false)
            return ViewHolder(view.tv_start, view.tv_end, view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as DoubleTextItem
            holder.apply {
                start.apply(item.sinit)
                end.apply(item.einit)
                view.apply(item.viewinit)
                start.text = item.start
                end.text = item.end
            }
        }
    }

    class ViewHolder(
        val start: TextView,
        val end: TextView,
        val view: View
    ) : RecyclerView.ViewHolder(view)

    override val controller = Companion
}

class DoubleTextItemEx(
    val start: String = "",
    val end: String = "",
    val sinit: TextView.() -> Unit = {},
    val viewinit: View.() -> Unit = {},
    val einit: TextView.() -> Unit = {}
) : Item {
    override fun areItemsTheSame(newItem: Item): Boolean = false

    override fun areContentsTheSame(newItem: Item): Boolean = false

    companion object : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_double_textex, parent, false)
            return ViewHolder(view.tv_start, view.tv_end, view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as DoubleTextItemEx
            holder.apply {
                start.apply(item.sinit)
                end.apply(item.einit)
                view.apply(item.viewinit)
                start.text = item.start
                end.text = item.end
            }
        }
    }

    class ViewHolder(
        val start: TextView,
        val end: TextView,
        val view: View
    ) : RecyclerView.ViewHolder(view)

    override val controller = Companion
}

class ExpandItem(
    val title: String,
    val data: List<Item>,
    val minHeight: Int = 300,
    val einit: TextView.() -> Unit = {}
) : Item {
    override fun areItemsTheSame(newItem: Item): Boolean {
        return (newItem as? ExpandItem)?.title == this.title
    }

    override fun areContentsTheSame(newItem: Item): Boolean {
        return false
//        return (newItem as? ExpandItem)?.title == this.title
    }

    companion object : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_expand, parent, false)
            return ViewHolder(view, view.tv_title, view.layout_content, view.rec_content)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as ExpandItem
            holder.apply {
                title.text = item.title
                title.paint.isFakeBoldText = true
                title.apply(item.einit)
                rec.minimumHeight = item.minHeight
                view.setOnClickListener {
                    content.visibility = if (content.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                    val rec = (view.parent) as RecyclerView
                    //Log.e("woggle", rec.getChildLayoutPosition(view).toString() + " " + rec.childCount)
                    if (content.visibility == View.VISIBLE && content.height > CommonContext.application.windowManager.defaultDisplay.height) {
                        (rec.layoutManager as LinearLayoutManager).stackFromEnd = true
                        rec.scrollToPosition(rec.getChildLayoutPosition(view))
                    } else {
                        (rec.layoutManager as LinearLayoutManager).stackFromEnd = false
                        rec.scrollToPosition(rec.getChildLayoutPosition(view))
                    }

                    // } else rec.scrollToPosition(rec.getChildLayoutPosition(view))

                }
                if (item.data.isNotEmpty()) {
                    rec.apply {
                        layoutManager = LinearLayoutManager(CommonContext.application)
                        withItems {
                            clear()
                            addAll(item.data)
                        }
                    }
                }
            }
        }
    }

    class ViewHolder(
        val view: View,
        val title: TextView,
        val content: androidx.constraintlayout.widget.ConstraintLayout,
        val rec: RecyclerView
    ) : RecyclerView.ViewHolder(view)

    override val controller = Companion
}

class PureExpandItem(
    val title: String,
    val data: MutableList<Item>,
    val show: Boolean = false,
    val minHeight: Int = 300,
    val itm: ItemManager = ItemManager(),
    val adt: ItemAdapter = ItemAdapter(itm)
) : Item {
    override fun areItemsTheSame(newItem: Item): Boolean {
        return (newItem as? PureExpandItem)?.title == this.title
    }

    override fun areContentsTheSame(newItem: Item): Boolean {
        return (newItem as? PureExpandItem)?.title == this.title
    }

    companion object : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_pure_expand, parent, false)
            return ViewHolder(view, view.tv_title, view.layout_content, view.rec_content)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as PureExpandItem
            holder.apply {
                if (item.show) {
                    content.visibility = View.VISIBLE
                } else {
                    content.visibility = View.GONE
                }
                title.text = item.title
                title.paint.isFakeBoldText = true
                rec.minimumHeight = item.minHeight
                view.setOnClickListener {
                    content.visibility = if (content.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                    val rec = (view.parent) as RecyclerView
                    // rec.requestLayout()
                    //Log.e("woggle", rec.getChildLayoutPosition(view).toString() + " " + rec.childCount)
                    if (content.visibility == View.VISIBLE && dp2px(
                            CommonContext.application,
                            ((rec.childCount + item.data.size) * 100f)
                        ) > CommonContext.application.windowManager.defaultDisplay.height
                    ) {
                        (rec.layoutManager as LinearLayoutManager).stackFromEnd = true
                        rec.scrollToPosition(rec.getChildLayoutPosition(view))
                    } else {
                        (rec.layoutManager as LinearLayoutManager).stackFromEnd = false
                        rec.scrollToPosition(rec.getChildLayoutPosition(view))
                    }
                }
                if (item.data.isNotEmpty()) {
                    rec.apply {
                        layoutManager = LinearLayoutManager(CommonContext.application)
                        adapter = item.adt
                        item.itm.clear()
                        item.itm.refreshAll(item.data)
//                        withItems {
//                            clear()
//                            addAll(item.data)
//                        }
                    }
                }
            }
        }
    }

    class ViewHolder(
        val view: View,
        val title: TextView,
        val content: androidx.constraintlayout.widget.ConstraintLayout,
        val rec: RecyclerView
    ) : RecyclerView.ViewHolder(view)

    override val controller = Companion
}

class DoubleTextExpandItem(val titleL: String, val titleR: String, val data: List<Item>) : Item {

    companion object : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_double_text_expand, parent, false)
            return ViewHolder(view, view.tv_titleL, view.tv_titleR, view.layout_content, view.rec_content)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as DoubleTextExpandItem
            holder.apply {
                titleL.text = item.titleL
                titleR.text = item.titleR
                view.setOnClickListener {
                    content.visibility = if (content.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                }
                if (item.data.isNotEmpty()) {
                    rec.apply {
                        layoutManager = LinearLayoutManager(CommonContext.application)
                        withItems {
                            clear()
                            addAll(item.data)
                        }
                    }
                }
            }
        }
    }

    class ViewHolder(
        val view: View,
        val titleL: TextView,
        val titleR: TextView,
        val content: androidx.constraintlayout.widget.ConstraintLayout,
        val rec: RecyclerView
    ) : RecyclerView.ViewHolder(view)

    override val controller = Companion
}

interface ItemController {
    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item)
}

open class ItemAdapter(val itemManager: ItemManagerAbstract) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    MutableList<Item> by itemManager {

    init {
        itemManager.observer = this
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemManager.getController(viewType).onCreateViewHolder(parent)

    override fun getItemCount() = itemManager.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        itemManager[position].controller.onBindViewHolder(holder, itemManager[position])

    override fun getItemViewType(position: Int) = ItemManager.getViewType(itemManager[position].controller)
}

fun RecyclerView.withItems(items: List<Item>) {
    adapter = ItemAdapter(ItemManager(items.toMutableList()))
}

fun RecyclerView.withItems(init: MutableList<Item>.() -> Unit) =
    withItems(mutableListOf<Item>().apply(init))

interface ItemManagerAbstract : MutableList<Item> {
    var observer: RecyclerView.Adapter<RecyclerView.ViewHolder>?
}

open class ItemManager(private val delegated: MutableList<Item> = mutableListOf()) : ItemManagerAbstract {
    override var observer: RecyclerView.Adapter<RecyclerView.ViewHolder>? =
        null
    val itemListSnapshot: List<Item> get() = delegated

    init {
        ensureControllers(delegated)
    }

    companion object ItemControllerManager {
        private var viewType = 0 // companion object��֤�˵��� ���ViewType�϶��Ǵ�0��ʼ

        // controller to view type
        private val c2vt = mutableMapOf<ItemController, Int>()

        // view type to controller
        private val vt2c = mutableMapOf<Int, ItemController>()

        /**
         * ���Item(��Ӧ��controller)�Ƿ��Ѿ���ע�ᣬ���û�У��Ǿ�ע��һ��ViewType
         */
        private fun ensureController(item: Item) {
            val controller = item.controller
            if (!c2vt.contains(controller)) {
                c2vt[controller] = viewType
                vt2c[viewType] = controller
                viewType++
            }
        }

        /**
         * ����һ��Collection��ViewTypeע�ᣬ�Ƚ���һ��ȥ��
         */
        private fun ensureControllers(items: Collection<Item>): Unit =
            items.distinctBy(Item::controller).forEach(ItemControllerManager::ensureController)

        /**
         * ����ItemController��ȡ��Ӧ��Item -> ����Adapter.getItemViewType
         */
        fun getViewType(controller: ItemController): Int = c2vt[controller]
            ?: throw IllegalStateException("ItemController $controller is not ensured")

        /**
         * ����ViewType��ȡItemController -> ����OnCreateViewHolder����߼�
         */
        fun getController(viewType: Int): ItemController = vt2c[viewType]
            ?: throw IllegalStateException("ItemController $viewType is unused")
    }


    override val size: Int get() = delegated.size

    override fun contains(element: Item) = delegated.contains(element)

    override fun containsAll(elements: Collection<Item>) = delegated.containsAll(elements)

    override fun get(index: Int): Item = delegated[index]

    override fun indexOf(element: Item) = delegated.indexOf(element)

    override fun isEmpty() = delegated.isEmpty()

    override fun iterator() = delegated.iterator()

    override fun lastIndexOf(element: Item) = delegated.lastIndexOf(element)

    override fun listIterator() = delegated.listIterator()

    override fun listIterator(index: Int) = delegated.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int) = delegated.subList(fromIndex, toIndex)

    override fun add(element: Item) =
        delegated.add(element).also {
            ensureController(element)
            if (it) observer?.notifyItemInserted(size)
        }

    override fun add(index: Int, element: Item) =
        delegated.add(index, element).also {
            ensureController(element)
            observer?.notifyItemInserted(index)
        }

    override fun addAll(index: Int, elements: Collection<Item>) =
        delegated.addAll(elements).also {
            ensureControllers(elements)
            if (it) observer?.notifyItemRangeInserted(index, elements.size)
        }

    override fun addAll(elements: Collection<Item>) =
        delegated.addAll(elements).also {
            ensureControllers(elements)
            if (it) observer?.notifyItemRangeInserted(size, elements.size)
        }

    override fun clear() =
        delegated.clear().also {
            observer?.notifyItemRangeRemoved(0, size)
        }

    override fun remove(element: Item): Boolean =
        delegated.remove(element).also {
            if (it) observer?.notifyDataSetChanged()
        }

    override fun removeAll(elements: Collection<Item>): Boolean =
        delegated.removeAll(elements).also {
            if (it) observer?.notifyDataSetChanged()
        }

    override fun removeAt(index: Int) =
        delegated.removeAt(index).also {
            observer?.notifyItemRemoved(index)
            if (index != itemListSnapshot.size)
                observer?.notifyItemRangeChanged(index, itemListSnapshot.size - index)
        }

    override fun retainAll(elements: Collection<Item>) =
        delegated.retainAll(elements).also {
            if (it) observer?.notifyDataSetChanged()
        }

    override fun set(index: Int, element: Item) =
        delegated.set(index, element).also {
            ensureController(element)
            observer?.notifyItemChanged(index)
        }

    fun refreshAll(elements: List<Item>) {
        val tag = "refresh diff"
        val diffCallback = object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = delegated[oldItemPosition]
                val newItem = elements[newItemPosition]
                // log(tag, "items the same: old-> $oldItemPosition new-> $newItemPosition diff: ${oldItem.areItemsTheSame(newItem)}")

                return oldItem.areItemsTheSame(newItem)
                //return false
            }

            override fun getOldListSize(): Int = delegated.size
            override fun getNewListSize(): Int = elements.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = delegated[oldItemPosition]
                val newItem = elements[newItemPosition]
                // Log.e(tag, "content the same: old-> $oldItemPosition new-> $newItemPosition diff: ${oldItem.areContentsTheSame(newItem)}")

                return oldItem.areContentsTheSame(newItem)
                //return false

            }
        }
        val result = DiffUtil.calculateDiff(diffCallback, true)
        delegated.clear()
        delegated.addAll(elements)
        ensureControllers(elements)
        result.dispatchUpdatesTo(observer!!)
    }


    fun refreshAll(init: MutableList<Item>.() -> Unit) = refreshAll(mutableListOf<Item>().apply(init))

    fun autoRefresh(init: MutableList<Item>.() -> Unit) {
        val snapshot = this.itemListSnapshot.toMutableList()
        snapshot.apply(init)
        refreshAll(snapshot)
    }


}

internal fun log(tag: String, message: String) {
    Log.e(tag, message)
}

