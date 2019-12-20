package esirem.com.lakaftnumrique.Model

//MenuViewHolder.class, Home.class, Category.class, menu_item.xml, home.xml, activity_home_drawer.xml, activity_home.xml
// app_bar_home.xml, content_home.xml, nav_header_home.xml

class Category {

    var name: String? = null
    var image: String? = null

    constructor() {

    }

    constructor(name: String, image: String) {
        this.name = name
        this.image = image
    }
}
