package esirem.com.lakaftnumrique.Model

//Class Order/commande

class Order {

    var productId: String? = null
    var productName: String? = null
    var quantity: String? = null
    var price: String? = null
    var discount: String? = null

    constructor() {}

    constructor(productId: String, productName: String, quantity: String, price: String, discount: String) {
        this.productId = productId
        this.productName = productName
        this.quantity = quantity
        this.price = price
        this.discount = discount
    }
}


