package com.motologr.data.logging.maint

import com.motologr.data.logging.Log
import com.motologr.data.objects.maint.Repair
import com.motologr.data.objects.maint.RepairEntity

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

    companion object {
        fun castRepairLoggableEntities(repairEntities : List<RepairEntity>?) : RepairLog {
            val repairLog = RepairLog()

            if (repairEntities == null)
                return repairLog

            for (repairEntity in repairEntities){
                repairLog.addRepairToRepairLog(repairEntity.convertToRepairObject())
            }

            return repairLog
        }
    }
}