package com.gs.vtk;

import java.io.Serializable;

public class Product implements Serializable {

    private int id;
    private String description;
    private Double price;
    private String productId, numberAvailable;
    private int numberOrdered;
    
    public void setId(int i) {
        id = i;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Description: " + description + ";");
        buffer.append("Price: " + price);
        return buffer.toString();
    }

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getNumberAvailable() {
		return numberAvailable;
	}

	public void setNumberAvailable(String numberAvailable) {
		this.numberAvailable = numberAvailable;
	}

	public int getNumberOrdered() {
		return numberOrdered;
	}

	public void setNumberOrdered(int numberOrdered) {
		this.numberOrdered = numberOrdered;
	}
    
    
    
}
