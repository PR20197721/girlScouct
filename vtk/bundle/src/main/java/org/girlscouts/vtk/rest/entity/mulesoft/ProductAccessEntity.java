package org.girlscouts.vtk.rest.entity.mulesoft;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductAccessEntity {
    @SerializedName("VTKAdmin")
    private boolean vtkAdmin;
    @SerializedName("SU")
    private boolean su;
    @SerializedName("Products")
    private List<String> products;
    @SerializedName("DP")
    private boolean dp;

    public boolean isVtkAdmin() {
        return vtkAdmin;
    }

    public void setVtkAdmin(boolean vtkAdmin) {
        this.vtkAdmin = vtkAdmin;
    }

    public boolean isSu() {
        return su;
    }

    public void setSu(boolean su) {
        this.su = su;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }

    public boolean isDp() {
        return dp;
    }

    public void setDp(boolean dp) {
        this.dp = dp;
    }
}
