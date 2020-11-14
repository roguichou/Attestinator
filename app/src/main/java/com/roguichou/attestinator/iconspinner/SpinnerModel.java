package com.roguichou.attestinator.iconspinner;

public class SpinnerModel {

    private String label;
    private  String image;
    private int type;

    /*********** Set Methods ******************/
    public SpinnerModel(String _label, String _image, int _type)
    {
        label = _label;
        image = _image;
        type = _type;
    }

    /*********** Get Methods ****************/

    public String getImage()
    {
        return this.image;
    }

    public String getLabel() {
        return label;
    }

    public int getType() {
        return type;
    }
}
