package com.motologr.ui.data.logging.reg

import com.motologr.ui.data.logging.Log
import com.motologr.ui.data.objects.reg.Reg

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
}