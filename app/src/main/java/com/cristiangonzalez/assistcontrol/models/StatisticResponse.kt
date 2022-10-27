package com.cristiangonzalez.assistcontrol.models

data class StatisticResponse (
    var result: String,
    var message: String,
    var records: ArrayList<Statistic>
)