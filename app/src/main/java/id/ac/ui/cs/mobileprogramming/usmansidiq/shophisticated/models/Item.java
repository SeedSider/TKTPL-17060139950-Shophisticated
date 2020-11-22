package id.ac.ui.cs.mobileprogramming.usmansidiq.shophisticated.models;

public class Item {
    public String image, name, amount, price;

    public Item() {}

    public Item(String image, String name, String amount, String price) {
        this.image = image;
        this.name = name;
        this.amount = amount;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public String getAmount() {
        return amount;
    }
}
