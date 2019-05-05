package com.usep.isdako

import java.io.Serializable

data class TunaModel(val imageResId: Int,
                    val name: String,
                    val description: String,
                    val size: String,
                    var text: String = "") : Serializable
