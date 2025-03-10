package com.motologr

import ExpandableListAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ExpandableListView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.google.android.gms.ads.MobileAds
import com.google.android.material.navigation.NavigationView
import com.motologr.databinding.ActivityMainBinding
import com.motologr.data.AppDatabase
import com.motologr.data.billing.BillingClientHelper
import com.motologr.data.DataManager
import com.motologr.data.DatabaseMigration
import com.motologr.data.logging.fuel.FuelLog
import com.motologr.data.logging.insurance.InsuranceLog
import com.motologr.data.logging.maint.RepairLog
import com.motologr.data.logging.maint.ServiceLog
import com.motologr.data.logging.maint.WofLog
import com.motologr.data.logging.reg.RegLog
import com.motologr.data.objects.vehicle.Vehicle
import com.motologr.data.sampleData.SampleData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    companion object {
        var db: AppDatabase? = null
        fun getDatabase(): AppDatabase? {
            return db
        }
    }

    private fun initDb() : Thread {
        return Thread {
            db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "motologr"
            )
                .addMigrations(DatabaseMigration.MIGRATION_1_2)
                .addMigrations(DatabaseMigration.MIGRATION_2_3)
                .addMigrations(DatabaseMigration.MIGRATION_3_4)
                .addMigrations(DatabaseMigration.MIGRATION_4_5)
                .addMigrations(DatabaseMigration.MIGRATION_5_6)
                .addMigrations(DatabaseMigration.MIGRATION_6_7)
                .addMigrations(DatabaseMigration.MIGRATION_7_8)
                .addMigrations(DatabaseMigration.MIGRATION_8_9)
                .addMigrations(DatabaseMigration.MIGRATION_9_10)
                .addMigrations(DatabaseMigration.MIGRATION_10_11)
                .addMigrations(DatabaseMigration.MIGRATION_11_12)
                .addMigrations(DatabaseMigration.MIGRATION_12_13)
                .addMigrations(DatabaseMigration.MIGRATION_13_14)
                .addMigrations(DatabaseMigration.MIGRATION_14_15)
                .addMigrations(DatabaseMigration.MIGRATION_15_16)
                .addMigrations(DatabaseMigration.MIGRATION_16_17)
                .addMigrations(DatabaseMigration.MIGRATION_17_18)
                .addMigrations(DatabaseMigration.MIGRATION_18_19)
                .addMigrations(DatabaseMigration.MIGRATION_19_20)
                .addMigrations(DatabaseMigration.MIGRATION_20_21)
                .build()

            BillingClientHelper.initBillingHelper(this)
        }
    }

    private fun initAdMob() {
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) {

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initAdMob()

        var thread: Thread = initDb()
        thread.start()

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
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)

                setAppDrawerExpandableListView()
            }
        }

        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.setToolbarNavigationClickListener {
            onBackPressed()
        }

        thread.join()

        if (BuildConfig.DEBUG) {
            thread = Thread {
                if (DataManager.isInitialised())
                    return@Thread

                DataManager.initialiseDataManager()
                val vehicles : List<Vehicle> = Vehicle.castVehicleEntities(db?.vehicleDao()?.getAll())

                if (vehicles.isEmpty())
                    SampleData()
                else {
                    for(vehicle in vehicles) {
                        vehicle.repairLog = RepairLog.castRepairLoggableEntities(db?.repairDao()?.getAllByVehicleId(vehicle.id))
                        vehicle.serviceLog = ServiceLog.castServiceLoggableEntities(db?.serviceDao()?.getAllByVehicleId(vehicle.id))
                        vehicle.regLog = RegLog.castRegLoggableEntities(db?.regDao()?.getAllByVehicleId(vehicle.id))
                        vehicle.rucLog = Vehicle.castRucLoggableEntities(db?.rucDao()?.getAllByVehicleId(vehicle.id))
                        vehicle.wofLog = WofLog.castWofLoggableEntities(db?.wofDao()?.getAllByVehicleId(vehicle.id))
                        vehicle.fuelLog = FuelLog.castFuelLoggableEntities(db?.fuelDao()?.getAllByVehicleId(vehicle.id))
                        vehicle.insuranceLog = InsuranceLog.castInsuranceLoggableEntities(db?.insuranceDao()?.getAllByVehicleId(vehicle.id), db?.insuranceBillDao()?.getAll())

                        DataManager.pullVehicleFromDb(vehicle)

                    }
                }

                DataManager.setIdCounterLoggable()
                DataManager.setIdCounterVehicle()
                DataManager.setIdCounterInsurance()

                DataManager.setFirstVehicleActive()
            }
        } else {
            thread = Thread {
                if (DataManager.isInitialised())
                    return@Thread

                DataManager.initialiseDataManager()
                val vehicles : List<Vehicle> = Vehicle.castVehicleEntities(db?.vehicleDao()?.getAll())

                for(vehicle in vehicles) {
                    vehicle.repairLog = RepairLog.castRepairLoggableEntities(db?.repairDao()?.getAllByVehicleId(vehicle.id))
                    vehicle.serviceLog = ServiceLog.castServiceLoggableEntities(db?.serviceDao()?.getAllByVehicleId(vehicle.id))
                    vehicle.regLog = RegLog.castRegLoggableEntities(db?.regDao()?.getAllByVehicleId(vehicle.id))
                    vehicle.rucLog = Vehicle.castRucLoggableEntities(db?.rucDao()?.getAllByVehicleId(vehicle.id))
                    vehicle.wofLog = WofLog.castWofLoggableEntities(db?.wofDao()?.getAllByVehicleId(vehicle.id))
                    vehicle.fuelLog = FuelLog.castFuelLoggableEntities(db?.fuelDao()?.getAllByVehicleId(vehicle.id))
                    vehicle.insuranceLog = InsuranceLog.castInsuranceLoggableEntities(db?.insuranceDao()?.getAllByVehicleId(vehicle.id), db?.insuranceBillDao()?.getAll())

                    DataManager.pullVehicleFromDb(vehicle)
                }

                DataManager.setIdCounterLoggable()
                DataManager.setIdCounterVehicle()
                DataManager.setIdCounterInsurance()

                DataManager.setFirstVehicleActive()
            }
        }

        thread.start()

        navController.addOnDestinationChangedListener { controller: NavController?, destination: NavDestination, arguments: Bundle? ->

            // Hide/show top search bar
            if (destination.id == R.id.nav_vehicle_1 || destination.id == R.id.nav_plus) {
                drawerToggle.isDrawerIndicatorEnabled = true // <<< Add this line of code to enable the burger icon
            }

            // Fragments that you want to show the back button
/*            if (destination.id == R.id.nav_add) {*/
            else {
                // Disable the functionality of opening the side drawer, when the burger icon is clicked
                drawerToggle.isDrawerIndicatorEnabled = false
            }
        }

        thread.join()
        setAppDrawerExpandableListView()

        if (!DataManager.isVehicles()) {
            navController.navigate(R.id.nav_plus)
        } else {
            navController.navigate(R.id.nav_vehicle_1, null, NavOptions.Builder()
                .setPopUpTo(R.id.nav_vehicle_1, true).build())
        }
    }

    override fun onResume() {
        super.onResume()

        BillingClientHelper.validatePurchases()
    }

    internal object ExpandableListData {
        val data: HashMap<Int, List<String>>
            get() {
                val expandableListDetail =
                    HashMap<Int, List<String>>()

                for (i in 0 until DataManager.returnVehicleArrayLength()) {
                    val vehicle : Vehicle? = DataManager.returnVehicle(i)

                    val vehicleOptions: MutableList<String> =
                        ArrayList()
                    vehicleOptions.add("Overview")
                    vehicleOptions.add("Fuel")
                    vehicleOptions.add("Mechanical")
                    vehicleOptions.add("Insurance")
                    vehicleOptions.add("Settings")

                    val vehicleId = vehicle?.id!!

                    expandableListDetail[vehicleId] = vehicleOptions
                }

                expandableListDetail[-1] = ArrayList()

                return expandableListDetail
            }
    }

    fun navigateToVehicleOption(optionName: String) {
        val navigationController = findNavController(R.id.nav_host_fragment_content_main)

        if (optionName == "Overview") {
            navigationController.navigate(R.id.nav_vehicle_1, null, NavOptions.Builder()
                .setPopUpTo(R.id.nav_vehicle_1, true).build())
        }
        else if (optionName == "Insurance") {
            navigationController.navigate(R.id.nav_insurance_logging)
        }
        else if (optionName == "Mechanical") {
            navigationController.navigate(R.id.nav_maint_logging)
        }
        else if (optionName == "Fuel") {
            navigationController.navigate(R.id.nav_fuel_logging)
        }
        else if (optionName == "Settings") {
            navigationController.navigate(R.id.nav_vehicle_settings)
        }
    }

    fun navigateToNewVehicle() {
        val navigationController = findNavController(R.id.nav_host_fragment_content_main)

        navigationController.navigate(R.id.nav_plus)
    }

    private var adapter: ExpandableListAdapter? = null
    private var expandedGroups: ArrayList<Int> = ArrayList()

    fun setAppDrawerExpandableListView() {

        val expandableListView: ExpandableListView = findViewById(R.id.navigation_menu)

        val listData = ExpandableListData.data

        val titleList = ArrayList(listData.keys)

        titleList.removeIf { i -> i == -1 }

        // Cap garage at 3 vehicles
        if (DataManager.returnVehicleArrayLength() < 3) {
            titleList.add(-1)
        }

        adapter = ExpandableListAdapter(this, titleList as ArrayList<Int>, listData)

        // If rotation is enabled, stops drawer layout resetting
/*        if (expandableListView.expandableListAdapter != null)
            return*/

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
            DataManager.setActiveVehicle(groupPosition)
            navigateToVehicleOption(listData[(titleList)[groupPosition]]!!.get(childPosition))
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

    @SuppressLint("RestrictedApi")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navigationController = findNavController(R.id.nav_host_fragment_content_main)

        val settingsDestination = navigationController.findDestination(R.id.action_settings)
        val addonsDestination = navigationController.findDestination(R.id.nav_addons)

        // Navigation can be nested (e.g. Settings -> Purchases -> Settings)
        if (item.itemId == R.id.action_settings && navigationController.currentDestination != settingsDestination) {
            navigationController.navigate(R.id.nav_settings)
            return true
        }
        if (item.itemId == R.id.action_addons && navigationController.currentDestination != addonsDestination) {
            navigationController.navigate(R.id.nav_addons)
            return true
        }

        return super.onOptionsItemSelected(item)
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