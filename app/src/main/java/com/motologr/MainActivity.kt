package com.motologr

import ExpandableListAdapter
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
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.material3.Text
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
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.QueryPurchasesParams
import com.google.android.material.navigation.NavigationView
import com.motologr.databinding.ActivityMainBinding
import com.motologr.data.AppDatabase
import com.motologr.data.BillingHelper
import com.motologr.data.DataManager
import com.motologr.data.MIGRATION_1_2
import com.motologr.data.MIGRATION_2_3
import com.motologr.data.logging.fuel.FuelLog
import com.motologr.data.logging.insurance.InsuranceLog
import com.motologr.data.logging.maint.RepairLog
import com.motologr.data.logging.maint.ServiceLog
import com.motologr.data.logging.maint.WofLog
import com.motologr.data.logging.reg.RegLog
import com.motologr.data.objects.vehicle.Vehicle
import com.motologr.data.sampleData.SampleData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .build()

            DataManager.setIdCounterLoggable()
            DataManager.setIdCounterVehicle()
            DataManager.setIdCounterInsurance()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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
                        vehicle.wofLog = WofLog.castWofLoggableEntities(db?.wofDao()?.getAllByVehicleId(vehicle.id))
                        vehicle.fuelLog = FuelLog.castFuelLoggableEntities(db?.fuelDao()?.getAllByVehicleId(vehicle.id))
                        vehicle.insuranceLog = InsuranceLog.castInsuranceLoggableEntities(db?.insuranceDao()?.getAllByVehicleId(vehicle.id), db?.insuranceBillDao()?.getAll())

                        DataManager.pullVehicleFromDb(vehicle)
                    }
                }

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
                    vehicle.wofLog = WofLog.castWofLoggableEntities(db?.wofDao()?.getAllByVehicleId(vehicle.id))
                    vehicle.fuelLog = FuelLog.castFuelLoggableEntities(db?.fuelDao()?.getAllByVehicleId(vehicle.id))
                    vehicle.insuranceLog = InsuranceLog.castInsuranceLoggableEntities(db?.insuranceDao()?.getAllByVehicleId(vehicle.id), db?.insuranceBillDao()?.getAll())

                    DataManager.pullVehicleFromDb(vehicle)
                }

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

        BillingHelper.initBillingHelper(this)

        if (!DataManager.isVehicles()) {
            navController.navigate(R.id.nav_plus)
        } else {
            navController.navigate(R.id.nav_vehicle_1, null, NavOptions.Builder()
                .setPopUpTo(R.id.nav_vehicle_1, true).build())
        }
    }

    private val purchasesResponseListener =
        PurchasesResponseListener { billingResult, purchases ->
            // To be implemented in a later section.
        }

    override fun onResume() {
        super.onResume()

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)

        // uses queryPurchasesAsync Kotlin extension function
        val purchasesResult = BillingHelper.billingClient.queryPurchasesAsync(params.build(), purchasesResponseListener)
    }

    internal object ExpandableListData {
        val data: HashMap<Int, List<String>>
            get() {
                val expandableListDetail =
                    HashMap<Int, List<String>>()

                for (i in 0 until DataManager.ReturnVehicleArrayLength()) {
                    val vehicle : Vehicle? = DataManager.ReturnVehicle(i)

                    val vehicleOptions: MutableList<String> =
                        ArrayList()
                    vehicleOptions.add("Overview")
                    vehicleOptions.add("Fuel")
                    vehicleOptions.add("Maintenance")
                    vehicleOptions.add("Insurance")
                    vehicleOptions.add("Expenses")

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

    private var adapter: ExpandableListAdapter? = null
    private var expandedGroups: ArrayList<Int> = ArrayList()

    fun setAppDrawerExpandableListView() {

        val expandableListView: ExpandableListView = findViewById(R.id.navigation_menu)

        val listData = ExpandableListData.data

        val titleList = ArrayList(listData.keys)

        titleList.removeIf { i -> i == -1 }

        // Cap garage at 3 vehicles
        if (DataManager.ReturnVehicleArrayLength() < 3) {
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
            DataManager.SetActiveVehicle(groupPosition)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navigationController = findNavController(R.id.nav_host_fragment_content_main)

        if (item.itemId == R.id.action_settings) {
            navigationController.navigate(R.id.nav_settings)
            return true
        }
        if (item.itemId == R.id.action_addons) {
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

/*    fun checkPermission() : Boolean {
        // checking of permissions.
        val permission1 = ContextCompat.checkSelfPermission (applicationContext, WRITE_EXTERNAL_STORAGE);
        val permission2 = ContextCompat.checkSelfPermission (applicationContext, READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    fun requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this,
            arrayOf (WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
            200 // PERMISSION_REQUEST_CODE
        );
    }*/

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }


        if (requestCode == 200) { // PERMISSION_REQUEST_CODE)
            requestPermissionLauncher.launch("WRITE_EXTERNAL_STORAGE")
        }
    }
}