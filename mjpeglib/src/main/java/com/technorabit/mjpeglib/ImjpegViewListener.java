/**
 *
 */
package com.technorabit.mjpeglib;

import android.graphics.Bitmap;

/**
 * @author Raja
 */
public interface ImjpegViewListener {

    void sucess();

    void error();

    void hasBitmap(Bitmap bm);
}
