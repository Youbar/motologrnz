package com.motologr

import ExpandableListAdapter
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.motologr.databinding.ActivityMainBinding
import com.motologr.ui.data.DataManager
import com.motologr.ui.data.Vehicle
import com.google.android.material.navigation.NavigationView
import com.motologr.ui.data.Repair
import com.motologr.ui.data.Service
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        val sharedPreferences = getPreferences(MODE_PRIVATE)
        val sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString("Olivia", "is cute");
        sharedPreferencesEditor.commit();
        val fabText = sharedPreferences.getString("Olivia", "No result")

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

        fuckingGarbageFunction()

        // REMOVE IN LIVE COPY
        sampleData()
        // REMOVE IN LIVE COPY

        navController.navigate(R.id.nav_vehicle_1)
    }

    private fun sampleData() {
        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        val vehicle = Vehicle("Mazda 323",
            1989,
            format.parse("16/10/2000"),
            format.parse("16/10/2000"),
            1243)

        DataManager.CreateNewVehicle(vehicle)

        var service: Service = Service(0, 123.0, format.parse("16/10/2001"), "Av", "")
        var repair: Repair = Repair(0, 123.0, format.parse("17/10/2002"), "Av", "")

        DataManager.ReturnVehicle(0)?.logService(service)
        DataManager.ReturnVehicle(0)?.logService(service)
        DataManager.ReturnVehicle(0)?.logRepair(repair)

        DataManager.SetActiveVehicle(0)
    }

    internal object ExpandableListData {
        val data: HashMap<String, List<String>>
            get() {
                val expandableListDetail =
                    HashMap<String, List<String>>()

                for (i in 0 until DataManager.ReturnVehicleArrayLength()) {
                    var vehicle : Vehicle? = DataManager.ReturnVehicle(i)

                    val vehicleOptions: MutableList<String> =
                        ArrayList()
                    vehicleOptions.add("Overview")
                    vehicleOptions.add("Insurance")
                    vehicleOptions.add("Maintenance")
                    vehicleOptions.add("Fuel")

                    val vehicleTitle = vehicle?.modelName as String
                    expandableListDetail[vehicleTitle] = vehicleOptions
                }

                expandableListDetail["Add New Vehicle"] = ArrayList()

                return expandableListDetail
            }
    }

    private var expandableListView: ExpandableListView? = null
    private var adapter: ExpandableListAdapter? = null
    private var titleList: List<String>? = null

    fun navigateToVehicleOption(optionName: String) {
        val navigationController = findNavController(R.id.nav_host_fragment_content_main)

        if (optionName == "Overview") {
            navigationController.navigate(R.id.nav_vehicle_1)
        }
        else if (optionName == "Insurance") {
            navigationController.navigate(R.id.nav_insurance)
        }
        else if (optionName == "Maintenance") {
            navigationController.navigate(R.id.nav_maint_logging)
        }
        else if (optionName == "Fuel") {
            navigationController.navigate(R.id.nav_fuel_logging)
        }
    }

    fun navigateToNewVehicle() {
        val navigationController = findNavController(R.id.nav_host_fragment_content_main)

        navigationController.navigate(R.id.nav_plus)
    }

    fun fuckingGarbageFunction() {

        val expandableListView: ExpandableListView = findViewById<ExpandableListView>(R.id.navigation_menu)

        if (expandableListView != null) {
            val listData = ExpandableListData.data
            titleList = ArrayList(listData.keys)
            adapter = ExpandableListAdapter(this, titleList as ArrayList<String>, listData)
            expandableListView!!.setAdapter(adapter)

            expandableListView!!.setOnGroupExpandListener { groupPosition ->
                Toast.makeText(
                    applicationContext,
                    (titleList as ArrayList<String>)[groupPosition] + " List Expanded.",
                    Toast.LENGTH_SHORT
                ).show()

                if (ArrayList(listData.keys)[groupPosition] ==  "Add New Vehicle") {
                    navigateToNewVehicle()
                }
            }
            expandableListView!!.setOnGroupCollapseListener { groupPosition ->
                Toast.makeText(
                    applicationContext,
                    (titleList as ArrayList<String>)[groupPosition] + " List Collapsed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            expandableListView!!.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
                navigateToVehicleOption(listData[(titleList as ArrayList<String>)[groupPosition]]!!.get(childPosition))
                Toast.makeText(
                    applicationContext,
                    "Clicked: " + (titleList as ArrayList<String>)[groupPosition] + " -> " + listData[(
                            titleList as
                                    ArrayList<String>
                            )
                            [groupPosition]]!!.get(
                        childPosition
                    ),
                    Toast.LENGTH_SHORT
                ).show()
                binding.drawerLayout.closeDrawer(Gravity.LEFT)
                false
            }
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