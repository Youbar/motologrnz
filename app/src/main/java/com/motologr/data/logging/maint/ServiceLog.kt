package com.motologr.data.logging.maint

import com.motologr.data.logging.Log
import com.motologr.data.objects.maint.Service
import com.motologr.data.objects.maint.ServiceEntity

class ServiceLog : Log() {
    private var serviceLog = ArrayList<Service>()

    fun addServiceToServiceLog(service: Service) {
        serviceLog.add(service)
    }

    fun returnServiceLog(): ArrayList<Service> {
        return serviceLog
    }

    fun returnService(index: Int) : Service {
        return serviceLog[index]
    }

    companion object {
        fun castServiceLoggableEntities(repairEntities : List<ServiceEntity>?) : ServiceLog {
            val serviceLog = ServiceLog()

            if (repairEntities == null)
                return serviceLog

            for (repairEntity in repairEntities){
                serviceLog.addServiceToServiceLog(repairEntity.convertToServiceObject())
            }

            return serviceLog
        }
    }
}