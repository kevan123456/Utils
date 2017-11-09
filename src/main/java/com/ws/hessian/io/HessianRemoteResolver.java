/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;

import java.io.IOException;

public interface HessianRemoteResolver {
    Object lookup(String var1, String var2) throws IOException;
}
