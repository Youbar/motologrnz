package com.motologr.ui.data.logging.reg

import com.motologr.ui.data.logging.Log
import com.motologr.ui.data.logging.maint.WofLog
import com.motologr.ui.data.objects.reg.Reg
import com.motologr.ui.data.objects.reg.RegEntity

class RegLog : Log() {
    private var regLog = ArrayList<Reg>()

    fun addRegToRegLog(reg: Reg) {
        regLog.add(reg)
    }

    fun returnRegLog(): ArrayList<Reg> {
        return regLog
    }

    fun returnReg(index: Int) : Reg {
        return regLog[index]
    }

    companion object {
        fun castRegLoggableEntities(regEntities : List<RegEntity>?) : RegLog {
            val regLog = RegLog()

            if (regEntities == null)
                return regLog

            for (regEntity in regEntities){
                regLog.addRegToRegLog(regEntity.convertToWofObject())
            }

            return regLog
        }
    }
}