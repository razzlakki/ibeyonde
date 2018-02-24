package com.dms.datalayerapi.db.util;

/**
 * @author Raja.p
 */
public interface PublicCloneable extends Cloneable {

    /**
     * Returns a clone of the object.
     *
     * @return A clone.
     * @throws CloneNotSupportedException if cloning is not supported for some reason.
     */
    public Object clone() throws CloneNotSupportedException;

}