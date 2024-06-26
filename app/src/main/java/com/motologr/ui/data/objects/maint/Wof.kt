package com.motologr.ui.data.objects.maint

import com.motologr.ui.data.logging.Loggable
import java.util.Date

class Wof(var wofDate: Date, var wofCompletedDate: Date, var price: Double) : Loggable(wofCompletedDate, 2, price)