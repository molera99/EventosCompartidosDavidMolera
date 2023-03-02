package modelos

import java.io.Serializable

data class User(
    var id:String,
    var email:String,
    var password:String,
    var name:String,
    var age: Int,
    var rol:String,
    var activate:Boolean): Serializable {}
