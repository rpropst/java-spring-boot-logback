package io.sentry.demos.example;

public class Item {
    private String id;
    private String name;
    private int price;
    private String img;

    public Item() {}

    public Item(String id, String name, int price, String img) {
        this.name = name;
        this.id = id;
        this.price = price;
        this.img = img;
    }

    public String getName() {
        return name;
    }
    
    public String getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public String getImg() {
        return img;
    }
}
