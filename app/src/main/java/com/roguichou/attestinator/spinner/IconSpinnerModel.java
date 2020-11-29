package com.roguichou.attestinator.spinner;

public class IconSpinnerModel {

    private final String image;
    private final int type;

    /*********** Set Methods ******************/
    public IconSpinnerModel(String _image, int _type)
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
