/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InetAddressHandle implements Serializable, HessianHandle {
    private static final Logger log = Logger.getLogger(InetAddressHandle.class.getName());
    private String hostName;
    private byte[] address;

    public InetAddressHandle(String hostName, byte[] address) {
        this.hostName = hostName;
        this.address = address;
    }

    private Object readResolve() {
        try {
            return InetAddress.getByAddress(this.hostName, this.address);
        } catch (Exception var2) {
            log.log(Level.FINE, var2.toString(), var2);
            return null;
        }
    }
}
