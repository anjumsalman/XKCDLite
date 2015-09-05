package com.stellerapps.xkcdlite;


public class XKCD {
    private int xkcdNumber;
    private String xkcdTitle;
    private String xkcdImgUrl;

    public XKCD(int xkcdNumber,String xkcdTitle,String xkcdImgUrl){
        this.xkcdNumber=xkcdNumber;
        this.xkcdTitle=xkcdTitle;
        this.xkcdImgUrl=xkcdImgUrl;
    }

    public XKCD(){

    }

    public int getXkcdNumber(){
        return xkcdNumber;
    }

    public void setXkcdNumber(int xkcdNumber){
        this.xkcdNumber=xkcdNumber;
    }

    public String getXkcdTitle(){
        return xkcdTitle;
    }

    public void setXkcdTitle(String xkcdTitle){
        this.xkcdTitle=xkcdTitle;
    }

    public String getXkcdImgUrl(){
        return xkcdImgUrl;
    }

    public void setXkcdImgUrl(String xkcdImgUrl){
        this.xkcdImgUrl=xkcdImgUrl;
    }
}
