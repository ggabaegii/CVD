package com.example.cvd;

public class PhotoBook {
    private String photo_name; //이름
    private byte[] photo_image; //사진
    //private byte[] photo_origin; //원본사진

    public PhotoBook(String photo_name, byte[] photo_image) {
        this.photo_name = photo_name;
        this.photo_image = photo_image;
        //this.photo_origin = photo_origin;
    }


    public String getPhoto_name() {
        return photo_name;
    }

    public void setPhoto_name(String photo_name) {
        this.photo_name = photo_name;
    }

    public byte[] getPhoto_image() {
        return photo_image;
    }

    public void setPhoto_image(byte[] photo_image) {
        this.photo_image = photo_image;
    }

    /*public byte[] getPhoto_origin() {
        return photo_origin;
    }

    public void setPhoto_origin(byte[] photo_origin) {
        this.photo_origin = photo_origin;
    }
    )
     */
}
