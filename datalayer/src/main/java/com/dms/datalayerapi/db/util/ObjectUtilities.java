package com.dms.datalayerapi.db.util;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author Raja
 */
public class ObjectUtilities {
    /**
     * Returns a clone of the specified object, if it can be cloned, otherwise
     * throws a CloneNotSupportedException.
     *
     * @param object the object to clone (<code>null</code> not permitted).
     * @return A clone of the specified object.
     * @throws CloneNotSupportedException if the object cannot be cloned.
     */
    public static Object clone(final Object object)
            throws CloneNotSupportedException {
        if (object == null) {
            throw new IllegalArgumentException("Null 'object' argument.");
        }
        if (object instanceof PublicCloneable) {
            final PublicCloneable pc = (PublicCloneable) object;
            return pc.clone();
        } else {
            try {
                final Method method = object.getClass().getMethod("clone",
                        (Class[]) null);
                if (Modifier.isPublic(method.getModifiers())) {
                    return method.invoke(object, (Object[]) null);
                }
            } catch (NoSuchMethodException e) {
                Log.w("ObjectUtilities",
                        "Object without clone() method is impossible.");
            } catch (IllegalAccessException e) {
                Log.w("ObjectUtilities",
                        "Object.clone(): unable to call method.");
            } catch (InvocationTargetException e) {
                Log.w("ObjectUtilities",
                        "Object without clone() method is impossible.");
            }
        }
        throw new CloneNotSupportedException("Failed to clone.");
    }
}
