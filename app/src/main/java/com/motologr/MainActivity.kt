package com.motologr

import ExpandableListAdapter
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ExpandableListView
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.motologr.databinding.ActivityMainBinding
import com.motologr.ui.data.DataManager
import com.motologr.ui.data.Repair
import com.motologr.ui.data.Service
import com.motologr.ui.data.Vehicle
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_vehicle_1,
                R.id.nav_vehicle_2,
                R.id.nav_vehicle_3,
                R.id.nav_vehicle_4,
                R.id.nav_vehicle_5,
                R.id.nav_plus
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val drawerToggle : ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.appBarMain.toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        ) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)

                fuckingGarbageFunction()
            }
        }

        drawerLayout.setDrawerListener(drawerToggle)
        drawerToggle.setToolbarNavigationClickListener {
            onBackPressed()
        }

        // REMOVE IN LIVE COPY
        sampleData()
        // REMOVE IN LIVE COPY

        fuckingGarbageFunction()


        navController.addOnDestinationChangedListener { controller: NavController?, destination: NavDestination, arguments: Bundle? ->

            // Hide/show top search bar
            if (destination.id == R.id.nav_vehicle_1 || destination.id == R.id.nav_plus) {
                drawerToggle.setDrawerIndicatorEnabled(true) // <<< Add this line of code to enable the burger icon
            }

            // Fragments that you want to show the back button
/*            if (destination.id == R.id.nav_add) {*/
            else {
                // Disable the functionality of opening the side drawer, when the burger icon is clicked
                drawerToggle.setDrawerIndicatorEnabled(false)
            }
        }

        if (DataManager.isVehicles())
            navController.navigate(R.id.nav_vehicle_1)
    }

    private fun sampleData() {
        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        val vehicle = Vehicle("Mazda", "323",
            1989,
            format.parse("10/09/2024"),
            format.parse("08/08/2024"),
            1243)

        DataManager.CreateNewVehicle(vehicle)

        var service: Service = Service(0, 123.0, format.parse("31/03/2023"), "Av", "")
        var service2: Service = Service(0, 123.0, format.parse("1/04/2023"), "Av", "")
        var repair: Repair = Repair(0, 123.0, format.parse("1/04/2023"), "Av", "")
        var repair2: Repair = Repair(0, 123.0, format.parse("1/04/2024"), "Av", "")

        DataManager.ReturnVehicle(0)?.logService(service)
        DataManager.ReturnVehicle(0)?.logService(service2)
        DataManager.ReturnVehicle(0)?.logRepair(repair)
        DataManager.ReturnVehicle(0)?.logRepair(repair2)

        DataManager.SetActiveVehicle(0)
    }

    internal object ExpandableListData {
        val data: HashMap<Int, List<String>>
            get() {
                val expandableListDetail =
                    HashMap<Int, List<String>>()

                for (i in 0 until DataManager.ReturnVehicleArrayLength()) {
                    var vehicle : Vehicle? = DataManager.ReturnVehicle(i)

                    val vehicleOptions: MutableList<String> =
                        ArrayList()
                    vehicleOptions.add("Overview")
                    vehicleOptions.add("Insurance")
                    vehicleOptions.add("Maintenance")
                    vehicleOptions.add("Fuel")
                    vehicleOptions.add("Expenses")

                    val vehicleId = vehicle?.getId()

                    expandableListDetail[vehicleId!!] = vehicleOptions
                }

                expandableListDetail[-1] = ArrayList()

                return expandableListDetail
            }
    }

    fun navigateToVehicleOption(optionName: String) {
        val navigationController = findNavController(R.id.nav_host_fragment_content_main)

        if (optionName == "Overview") {
            navigationController.navigate(R.id.nav_vehicle_1)
        }
        else if (optionName == "Insurance") {
            navigationController.navigate(R.id.nav_insurance_logging)
        }
        else if (optionName == "Maintenance") {
            navigationController.navigate(R.id.nav_maint_logging)
        }
        else if (optionName == "Fuel") {
            navigationController.navigate(R.id.nav_fuel_logging)
        }
        else if (optionName == "Expenses") {
            navigationController.navigate(R.id.nav_expenses)
        }
    }

    fun navigateToNewVehicle() {
        val navigationController = findNavController(R.id.nav_host_fragment_content_main)

        navigationController.navigate(R.id.nav_plus)
    }

    var expandableListView: ExpandableListView? = null
    private var adapter: ExpandableListAdapter? = null
    private var expandedGroups: ArrayList<Int> = ArrayList()

    fun fuckingGarbageFunction() {

        val expandableListView: ExpandableListView = findViewById(R.id.navigation_menu)

        val listData = ExpandableListData.data

        var titleList = ArrayList(listData.keys)

        titleList.removeIf { i -> i == -1 }
        titleList.add(-1)

        adapter = ExpandableListAdapter(this, titleList as ArrayList<Int>, listData)

        expandableListView.setAdapter(adapter)

        for (value in expandedGroups) {
            expandableListView.expandGroup(value)
        }

        expandableListView.setOnGroupExpandListener { groupPosition ->
            if (titleList[groupPosition] ==  -1) {
                navigateToNewVehicle()
                binding.drawerLayout.closeDrawer(Gravity.LEFT)
            } else if (!expandedGroups.contains(groupPosition)) {
                expandedGroups.add(groupPosition)
            }
        }

        expandableListView.setOnGroupCollapseListener { groupPosition ->
            if (expandedGroups.contains(groupPosition))
                expandedGroups.remove(groupPosition)
        }

        expandableListView.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            DataManager.SetActiveVehicle(groupPosition);
            navigateToVehicleOption(listData[(titleList as ArrayList<Int>)[groupPosition]]!!.get(childPosition))
            // Pass
            binding.drawerLayout.closeDrawer(Gravity.LEFT)
            false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}