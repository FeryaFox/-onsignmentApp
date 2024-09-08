package ru.feryafox.consignmentapp.ProductRepository;

import jakarta.persistence.*;

import java.util.Date;


@Entity
@Table(name = "product")
public class Product implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "delivery_date")
    private Date deliveryDate;

    @Column(name = "delivery_price")
    private Double deliveryPrice;

    @Column(name = "name")
    private String name;

    @Column(name = "display_price")
    private Double displayPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability")
    private Availability availability;

    @Override
    public String toString() {
        return  (name != null ? name : "") +
                (deliveryDate != null ? deliveryDate.toString() : "") +
                (deliveryPrice != null ? deliveryPrice.toString() : "") +
                (displayPrice != null ? displayPrice.toString() : "") +
                (availability != null ? availability.toString() : "");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Double getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(Double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getDisplayPrice() {
        return displayPrice;
    }

    public void setDisplayPrice(Double displayPrice) {
        this.displayPrice = displayPrice;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    public enum Availability {
        IN_STOCK, LOW_STOCK, OUT_OF_STOCK
    }

    public boolean isNull() {
        return deliveryPrice == null && name == null && displayPrice == null && availability == null && deliveryDate == null;
    }

    public Product clone() {
        try {
            Product clonedProduct = (Product) super.clone();
            clonedProduct.deliveryDate = (Date) deliveryDate.clone();
            return clonedProduct;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}