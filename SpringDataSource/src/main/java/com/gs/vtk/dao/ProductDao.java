package com.gs.vtk.dao;

import java.util.List;

import com.gs.vtk.Product;


public interface ProductDao {

    public List<Product> getProductList();

    public void saveProduct(Product prod);

}
