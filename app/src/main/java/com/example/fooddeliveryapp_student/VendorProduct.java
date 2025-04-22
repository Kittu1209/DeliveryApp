package com.example.fooddeliveryapp_student;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

@IgnoreExtraProperties
public class VendorProduct implements Serializable, Parcelable {
    @Exclude
    private String id;
    private String name;
    private double price;
    private String description;
    private String category;
    private String imageUrl;
    private String shopId;
    private boolean available;
    private int stockQuantity;

    @ServerTimestamp
    private Timestamp createdAt;

    @ServerTimestamp
    private Timestamp updatedAt;

    public VendorProduct() {
        // Required empty constructor for Firestore
    }

    public VendorProduct(String name, double price, String description, String category,
                         String imageUrl, String shopId, boolean available, int stockQuantity) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.imageUrl = imageUrl;
        this.shopId = shopId;
        this.available = available;
        this.stockQuantity = stockQuantity;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @PropertyName("imageUrl")
    public String getImageUrl() {
        return imageUrl;
    }

    @PropertyName("imageUrl")
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @PropertyName("shopId")
    public String getShopId() {
        return shopId;
    }

    @PropertyName("shopId")
    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    @PropertyName("available")
    public boolean isAvailable() {
        return available;
    }

    @PropertyName("available")
    public void setAvailable(boolean available) {
        this.available = available;
    }

    @PropertyName("stockQuantity")
    public int getStockQuantity() {
        return stockQuantity;
    }

    @PropertyName("stockQuantity")
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    @PropertyName("createdAt")
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    @PropertyName("createdAt")
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @PropertyName("updatedAt")
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    @PropertyName("updatedAt")
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Parcelable Implementation

    protected VendorProduct(Parcel in) {
        id = in.readString();
        name = in.readString();
        price = in.readDouble();
        description = in.readString();
        category = in.readString();
        imageUrl = in.readString();
        shopId = in.readString();
        available = in.readByte() != 0;
        stockQuantity = in.readInt();

        long createdAtMillis = in.readLong();
        if (createdAtMillis != -1) {
            createdAt = new Timestamp(new Date(createdAtMillis));
        }

        long updatedAtMillis = in.readLong();
        if (updatedAtMillis != -1) {
            updatedAt = new Timestamp(new Date(updatedAtMillis));
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeString(description);
        dest.writeString(category);
        dest.writeString(imageUrl);
        dest.writeString(shopId);
        dest.writeByte((byte) (available ? 1 : 0));
        dest.writeInt(stockQuantity);
        dest.writeLong(createdAt != null ? createdAt.toDate().getTime() : -1);
        dest.writeLong(updatedAt != null ? updatedAt.toDate().getTime() : -1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VendorProduct> CREATOR = new Creator<VendorProduct>() {
        @Override
        public VendorProduct createFromParcel(Parcel in) {
            return new VendorProduct(in);
        }

        @Override
        public VendorProduct[] newArray(int size) {
            return new VendorProduct[size];
        }
    };
}
