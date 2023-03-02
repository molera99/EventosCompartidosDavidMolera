package modelos

data class User(
    var email:String,
    var password:String,
    var name:String,
    var age: Int,
    var rol:String,
    var activate:Boolean)
