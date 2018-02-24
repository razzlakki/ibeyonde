package com.dms.datalayerapi.network.exception;

/**
 * Created by Raja.p on 20-05-2016.
 */
public class NetworkManagerException extends Exception {
    public enum Type {
        CONNECTION_TIMEOUT_EXCEPTION, UNKNOWN_HOST_EXCEPTION, IO_EXCEPTION, UNKNOWN_EXCEPTION, UNKNOWN_RESPONSE_EXCEPTION, UNKNOWN_HEADERS_EXCEPTION
    }
}
