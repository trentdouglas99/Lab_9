package com.csci448.trentdouglas.lab_9.data.jsonStuff

data class ApiData(val coord: coord,
                   val weather: weather,
                   val base:String,
                   val main: main,
                   val visibility:Int,
                   val wind: wind,
                   val clouds: clouds,
                   val dt: Int,
                   val sys: sys,
                   val timezone:Int,
                   val id: Int,
                   val name:String,
                   val cod:Int
                   )