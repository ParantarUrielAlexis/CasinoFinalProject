package com.example.casinoroyale;

import java.awt.*;

public class ScreenHelper {

    public static double getDPIScale() {
        double dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        double defaultDPI = 96.0; // Default DPI for most systems

        if (dpi == 120) {
            defaultDPI = 150;
        }
        System.out.println(dpi);
        return dpi / defaultDPI;
    }

}
