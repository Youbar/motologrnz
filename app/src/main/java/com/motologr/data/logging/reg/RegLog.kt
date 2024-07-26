package com.motologr.data.logging.reg

import com.motologr.data.logging.Log
import com.motologr.data.logging.maint.WofLog
import com.motologr.data.objects.reg.Reg
import com.motologr.data.objects.reg.RegEntity

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