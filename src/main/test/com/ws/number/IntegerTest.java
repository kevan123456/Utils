package com.ws.number;

import junit.framework.TestCase;
import org.junit.Test;

public class IntegerTest extends TestCase{

    @Test
    public void testInt(){
        Integer i1 = 127;
        Integer i2 = 127;
        Integer i3 = 128;
        Integer i4 = 128;
        Integer i5 = Integer.valueOf(127);
        //Integer的IntegerCache默认缓存-128到127,i1和i2是从缓存中取的对象
        System.out.println(i1 == i2);
        System.out.println(i3 == i4);
        System.out.println(i1 == i5);
    }

    @Test
    public void testString(){
        String s = "123" ;
        System.out.println(Integer.valueOf(s));
    }

}
