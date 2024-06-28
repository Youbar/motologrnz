package com.motologr.ui.data.logging.maint

import com.motologr.ui.data.logging.Log
import com.motologr.ui.data.objects.maint.Wof
import com.motologr.ui.data.objects.maint.WofEntity

class WofLog : Log() {
    private var wofLog = ArrayList<Wof>()

    fun addWofToWofLog(wof: Wof) {
        wofLog.add(wof)
    }

    fun returnWofLog(): ArrayList<Wof> {
        return wofLog
    }

    fun returnWof(index: Int) : Wof {
        return wofLog[index]
    }

    companion object {
        fun castWofLoggableEntities(wofEntities : List<WofEntity>?) : WofLog {
            val wofLog = WofLog()

            if (wofEntities == null)
                return wofLog

            for (wofEntity in wofEntities){
                wofLog.addWofToWofLog(wofEntity.convertToWofObject())
            }

            return wofLog
        }
    }
}