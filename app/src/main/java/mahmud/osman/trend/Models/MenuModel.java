package mahmud.osman.trend.Models;

import android.graphics.drawable.Drawable;

import androidx.fragment.app.Fragment;

public class MenuModel {

    public String menuName;
    public Fragment fragment;
    public Drawable drawable;
    public boolean hasChildren, isGroup;


    public MenuModel(String menuName, boolean isGroup, boolean hasChildren, Fragment fragment, Drawable drawable ) {

        this.menuName = menuName;
        this.fragment = fragment;
        this.isGroup = isGroup;
        this.hasChildren = hasChildren;
        this.drawable = drawable;
    }
}
