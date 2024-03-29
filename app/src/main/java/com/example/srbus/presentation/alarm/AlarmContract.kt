package com.example.srbus.presentation.alarm

import com.example.srbus.data.remote.arrBus.ArrBusItem

interface AlarmContract {

    interface View {
        fun updateArrBusInfo(
            firstBusRemainingTime: String, firstBusRemainingStation: String?, firstBusFullOrNot: String, firstBusArrivalTime: String, firstBusCurrentStation: String, firstBusInfo:String,
            secondBusRemainingTime: String, secondBusRemainingStation: String?, secondBusFullOrNot: String, secondBusArrivalTime: String, secondBusCurrentStation: String, secondBusInfo:String
        )

        fun updateViewAlarmButton(ordinal: Int, alarmOn: Boolean)
        fun showAlertDialog(ordinal: Int, alarmState: AlarmPresenter.RideAlarm)
    }

    interface Presenter {
        var arrBusItem: ArrBusItem?

        fun getArrBus(arsId: String?)

        fun startRideAlarmService(ordinal: Int): Boolean
        fun stopRideAlarmService()

//        fun getArrBusFirstRemainingTimeMin(): String?
//        fun getArrBusFirstRemainingStation(): String?
//
//        fun getArrBusSecondRemainingTimeMin(): String?
//        fun getArrBusSecondRemainingStation(): String?

        fun startAutoRequestTimer(arsId: String?)
        fun finishAutoRequestTimer()
    }
}