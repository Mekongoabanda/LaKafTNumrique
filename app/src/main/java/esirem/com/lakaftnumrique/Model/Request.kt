package esirem.com.lakaftnumrique.Model

class Request {

    var phone: String? = null
    var name: String? = null
    var address: String? = null
    var total: String? = null
    var status: String? = null
    var foods: List<Order>? = null //liste du panier.

    constructor() {}

    constructor(phone: String, name: String, address: String, total: String, foods: List<Order>) {
        this.phone = phone
        this.name = name
        this.address = address
        this.total = total
        this.foods = foods
        this.status = "0" // par défaut c'est 0 , 0: Placé (placed) , 1: expédition (shipping) , 2: expédié (shipped)
    }
}
