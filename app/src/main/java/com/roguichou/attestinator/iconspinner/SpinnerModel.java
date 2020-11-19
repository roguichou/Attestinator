package com.roguichou.attestinator.iconspinner;

public class SpinnerModel {

    private final String image;
    private final int type;

    /*********** Set Methods ******************/
    public SpinnerModel(String _image, int _type)
    {
        image = _image;
        type = _type;
    }

    /*********** Get Methods ****************/

    public String getImage()
    {
        return this.image;
    }

    public int getType() {
        return type;
    }
}
