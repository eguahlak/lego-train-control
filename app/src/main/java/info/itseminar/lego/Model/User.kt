package info.itseminar.lego.Model

class User{

    var id: Int = 0
    var userName: String = ""
    var password: String = ""

    constructor(userName:String, password: String){
        this.userName = userName
        this.password = password
    }
    constructor()
}
