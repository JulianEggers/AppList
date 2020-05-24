package systems.maju.myapplication

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.app_item.view.*
import kotlinx.android.synthetic.main.row.view.*
import kotlinx.android.synthetic.main.row.view.iconView
import kotlinx.android.synthetic.main.row.view.nameView


class RecyclerViewAdapter(val context: Context, val type: Type) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class Type {
        LIST,
        GRID
    }

    private var data: MutableList<App> = mutableListOf()

    fun addItems(list: MutableList<App>) {
        data = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            0 -> {
                val row =
                    LayoutInflater.from(context).inflate(R.layout.row, parent, false) as ViewGroup
                return RowHolder(row)
            }
            else -> {
                val item =
                    LayoutInflater.from(context).inflate(R.layout.app_item, parent, false) as ViewGroup
                return ItemHolder(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (type) {
            Type.LIST -> 0
            Type.GRID -> 1
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]

        when (holder.itemViewType) {
            0 -> {
                holder as RowHolder
                holder.nameView.text = item.appName
                holder.packageView.text = item.packageName
                holder.id.text = item.versionCode.toString()
                holder.iconView.setImageDrawable(context.packageManager.getApplicationIcon(item.packageName))
                holder.itemView.setOnClickListener {
                    val launchIntent: Intent? =
                        context.getPackageManager().getLaunchIntentForPackage(item.packageName)
                    context.startActivity(launchIntent)
                }
            }
            1 -> {
                holder as ItemHolder
                holder.nameView.text = item.appName
                holder.iconView.setImageDrawable(context.packageManager.getApplicationIcon(item.packageName))

                holder.itemView.setOnClickListener {
                    val launchIntent: Intent? =
                        context.getPackageManager().getLaunchIntentForPackage(item.packageName)
                    context.startActivity(launchIntent)
                }

                if (item.website == null) {
                    holder.website_button.visibility = View.GONE
                } else {
                    holder.website_button.visibility = View.VISIBLE
                    holder.website_button.setOnClickListener {
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(item.website))
                        context.startActivity(browserIntent)
                    }
                }
            }
        }

    }

}

open class AppHolder(itemView: ViewGroup) : RecyclerView.ViewHolder(itemView)

class RowHolder(itemView: ViewGroup) : AppHolder(itemView) {
    var iconView = itemView.iconView
    var nameView = itemView.nameView
    var packageView = itemView.packageView
    var id = itemView.idView
}

class ItemHolder(itemView: ViewGroup) : AppHolder(itemView) {
    var iconView = itemView.iconView
    var nameView = itemView.nameView
    var website_button = itemView.website
}