/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;

import java.io.IOException;

public class RemoteSerializer extends AbstractSerializer {
    public RemoteSerializer() {
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        HessianRemoteObject remoteObject = (HessianRemoteObject)obj;
        out.writeObject(new HessianRemote(remoteObject.getHessianType(), remoteObject.getHessianURL()));
    }
}
