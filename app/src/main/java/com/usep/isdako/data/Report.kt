package com.usep.isdako.data

data class Report(
    val userId: String,
    var lat: Double,
    var lng: Double,
    var time: String,
    var length: Float,
    var weight: Float,
    var species: String)