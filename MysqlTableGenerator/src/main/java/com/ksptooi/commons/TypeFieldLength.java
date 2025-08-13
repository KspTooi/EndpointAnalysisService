package com.ksptooi.commons;


public class TypeFieldLength {

    public TypeFieldLength(int l,int d){
        this.length = l;
        this.decimals = d;
    }

    private int length;
    private int decimals;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }
}
