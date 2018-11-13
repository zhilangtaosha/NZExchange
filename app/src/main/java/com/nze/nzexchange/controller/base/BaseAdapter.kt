package com.nze.nzexchange.controller.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

abstract class BaseAda<T>(var mContext: Context) : BaseAdapter() {
     var group: MutableList<T> = mutableListOf<T>()
        set(value) {
            if (field.size > 0)
                field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }
    protected var mInflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun getCount(): Int {
        return group.size
    }

    override fun getItem(position: Int): T? {
        /**防止外部调用时越界 */
        return if (position < 0 || position > group.size - 1) null else group[position]
    }

    fun removeItem(position: Int) {
        if (position < 0 || position > group.size)
            return
        group.removeAt(position)
        notifyDataSetChanged()
        return
    }

    //	protected void displayImg(String url,ImageView imageView){
    //		ImageLoader.getInstance().displayImage(url, imageView,options);
    //	}

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun isEmpty(): Boolean {
        return group.isEmpty()
    }

//    fun setGroup(g: MutableList<T>) {
//        if (group != null)
//            group!!.clear()
//        group!!.addAll(g)
//        notifyDataSetChanged()
//    }

    fun addItem(item: T) {
        group.add(item)
        notifyDataSetChanged()
    }

    /**
     * 添加指定位置的item
     */
    fun addItem(position: Int, item: T) {
        group.add(position, item)
        notifyDataSetChanged()
    }

    fun addItemNoNotify(item: T) {
        group.add(item)
    }

    fun addItems(items: List<T>) {
        group.addAll(items)
        notifyDataSetChanged()
    }

    fun clearGroup(notify: Boolean) {
        group.clear()
        if (notify) {
            notifyDataSetChanged()
        }
    }

}