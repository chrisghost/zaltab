package com.zenexity.zaltab;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class AboutDialog extends DialogPreference {
    public AboutDialog(Context oContext, AttributeSet attrs)
    {
        super(oContext,attrs);
        this.setDialogMessage("© Chrisghost SA - Since 1664\n" +
                              "Developers :       \n" +
                              "  Adrien Maillol    \n" +
                              "  Maxime Calmels    ");
    }
}
