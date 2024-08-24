import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.motologr.R
import com.motologr.data.DataManager


class ExpandableListAdapter internal constructor(
    private val context: Context,
    private val submenuList: List<Int>,
    private val menuList: HashMap<Int, List<String>>
) : BaseExpandableListAdapter() {

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        return this.menuList[this.submenuList[listPosition]]!![expandedListPosition]
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    override fun getChildView(
        listPosition: Int,
        expandedListPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        val expandedListText = getChild(listPosition, expandedListPosition) as String
        if (convertView == null) {
            val layoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.list_item, null)
        }
        val expandedListTextView = convertView!!.findViewById<TextView>(R.id.listView)
        expandedListTextView.text = expandedListText

        val bannedStrings: MutableList<String> =
            ArrayList()
        bannedStrings.add("Overview")
        bannedStrings.add("Insurance")
        bannedStrings.add("Maintenance")
        bannedStrings.add("Fuel")
        bannedStrings.add("Expenses")

        if (bannedStrings.contains(expandedListTextView.text)) {
            val listTitleImageView = convertView!!.findViewById<ImageView>(R.id.imageView2)
            listTitleImageView.visibility = View.INVISIBLE

            val listTitleImageView2 = convertView!!.findViewById<ImageView>(R.id.vehicleIcon)

            if (expandedListTextView.text == "Add New Vehicle") {
                listTitleImageView2.setImageResource(R.drawable.ic_menu_plus)
            } else {
                listTitleImageView2.visibility = View.INVISIBLE
            }
        }
        else {
            val listTitleImageView = convertView!!.findViewById<ImageView>(R.id.imageView2)
            listTitleImageView.rotation = 90.toFloat()
        }

        return convertView
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return this.menuList[this.submenuList[listPosition]]!!.size
    }

    override fun getGroup(listPosition: Int): Any {
        return this.submenuList[listPosition]
    }

    override fun getGroupCount(): Int {
        return this.submenuList.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun getGroupView(
        listPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        if (convertView == null) {
            val layoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.list_item, null)
        }
        val listTitleTextView = convertView!!.findViewById<TextView>(R.id.listView)
        listTitleTextView.setTypeface(null, Typeface.BOLD)

        val listId = getGroup(listPosition) as Int
        val vehicleBrand: String? = DataManager.ReturnVehicleById(listId)?.brandName;
        val vehicleName : String? = DataManager.ReturnVehicleById(listId)?.modelName

        if (vehicleBrand != null && vehicleName != null)
            listTitleTextView.text = vehicleBrand + " " + vehicleName
        else
            listTitleTextView.text = "Add New Vehicle"

        val bannedStrings: MutableList<String> =
            ArrayList()
        bannedStrings.add("Add New Vehicle")

        val parentView = convertView.findViewById<ImageView>(R.id.imageView2)
        parentView.rotation = 90.toFloat() // 90 = pointing left

        if (isExpanded)
            parentView.rotation = 0.toFloat() // 0 = pointing down

        val listTitleImageView2 = convertView.findViewById<ImageView>(R.id.vehicleIcon)
        listTitleImageView2.setImageResource(R.drawable.ic_menu_car)

        if (bannedStrings.contains(listTitleTextView.text)) {
            val listTitleImageView = convertView!!.findViewById<ImageView>(R.id.imageView2)
            listTitleImageView.visibility = View.INVISIBLE

            if (listTitleTextView.text == "Add New Vehicle") {
                listTitleImageView2.setImageResource(R.drawable.ic_menu_plus)
            } else {
                listTitleImageView2.visibility = View.INVISIBLE
            }
        } else {
            val listTitleImageView = convertView!!.findViewById<ImageView>(R.id.imageView2)
            listTitleImageView.visibility = View.VISIBLE

            listTitleImageView2.visibility = View.VISIBLE
        }

        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}