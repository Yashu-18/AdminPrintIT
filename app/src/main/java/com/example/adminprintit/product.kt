package com.example.adminprintit

class product {
    var name:String=""
    var price:Double=-1.0
    var desc:String=""
    var imageurl:String=""
    var category:String=""
    var discount:Int=-1

    constructor(name: String, price: Double, desc: String, imageurl: String,category: String,discount:Int) {
        this.name = name
        this.price = price
        this.desc = desc
        this.imageurl = imageurl
        this.category = category
        this.discount = discount
    }
    constructor()
}