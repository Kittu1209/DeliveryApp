package com.example.fooddeliveryapp_student;


    import java.util.Date;

    public class CartItem {
        private String productId;
        private String name;
        private double price;
        private int quantity;
        private String imageUrl;
        private String shopId;
        private double total;
        private Date updatedAt;

        public CartItem() {} // Empty constructor for Firestore

        public CartItem(String productId, String name, double price, int quantity, String imageUrl, String shopId) {
            this.productId = productId;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.imageUrl = imageUrl;
            this.shopId = shopId;
            this.total = price * quantity;
            this.updatedAt = new Date();
        }

        // Getters and Setters
        public String getProductId() { return productId; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public String getImageUrl() { return imageUrl; }
        public String getShopId() { return shopId; }
        public double getTotal() { return total; }
        public Date getUpdatedAt() { return updatedAt; }
    }


