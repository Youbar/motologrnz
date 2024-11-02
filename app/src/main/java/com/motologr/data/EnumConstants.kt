package com.motologr.data

object EnumConstants {
    enum class LoggableType(val id: Int) {
        Repair(0),
        Service(1),
        WOF(2),
        Reg(3),
        Ruc(4),
        Fuel(100),
        Insurance(200),
        InsuranceBill(201)
    }

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

    enum class InsuranceCoverage {
        Third,
        ThirdPlus,
        Comprehensive
    }

    enum class InsuranceBillingCycle {
        Fortnightly,
        Monthly,
        Annually
    }
}