package com.motologr.data

object EnumConstants {
    enum class FuelType {
        Unleaded91,
        Unleaded95,
        Unleaded98,
        Diesel
    }

    enum class RepairType {
        Minor,
        Major,
        Critical
    }

    enum class ServiceType {
        OilChange,
        General,
        Full
    }
}