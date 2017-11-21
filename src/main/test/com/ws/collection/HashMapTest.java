package com.ws.collection;

import java.util.HashMap;
import java.util.Map;

public class HashMapTest {
    public static void main(String[] args) {
        Map<String,String> map = new HashMap();
        map.put(null, "1");
        map.put(null, null);
        map.put("3", null);
        map.put("3", "3");
        for(Map.Entry<String,String> entry:map.entrySet()){
            System.out.println(entry.getKey()+":\t"+entry.getValue());
        }

    }
}
