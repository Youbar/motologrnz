package com.motologr.ui.data.logging.maint

import com.motologr.ui.data.logging.Log
import com.motologr.ui.data.objects.maint.Repair

class RepairLog : Log() {
    private var repairLog = ArrayList<Repair>()

    fun addRepairToRepairLog(repair: Repair) {
        repairLog.add(repair)
    }

    fun returnRepairLog(): ArrayList<Repair> {
        return repairLog
    }

    fun returnRepair(index: Int) : Repair {
        return repairLog[index]
    }
}