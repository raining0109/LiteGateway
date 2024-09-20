package me.raining.gateway.common.utils;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @author raining
 * @version 1.0.0
 * @description 属性转换工具类
 * 将配置 properties 中的（key，value）解析出来放入object
 */
public class PropertiesUtils {
    public static void properties2Object(final Properties p, final Object object, String prefix) {
        Method[] methods = object.getClass().getMethods();
        //遍历所有的method，找到set方法对应的成员变量
        for (Method method : methods) {
            String mn = method.getName();
            if (mn.startsWith("set")) {
                try {
                    // set
                    String tmp = mn.substring(4);
                    //
                    String first = mn.substring(3, 4);

                    String key = prefix + first.toLowerCase() + tmp;
                    String property = p.getProperty(key);
                    //不为null，说明存在该属性
                    if (property != null) {
                        Class<?>[] pt = method.getParameterTypes();
                        if (pt != null && pt.length > 0) {
                            String cn = pt[0].getSimpleName();
                            Object arg = null;
                            if (cn.equals("int") || cn.equals("Integer")) {
                                arg = Integer.parseInt(property);
                            } else if (cn.equals("long") || cn.equals("Long")) {
                                arg = Long.parseLong(property);
                            } else if (cn.equals("double") || cn.equals("Double")) {
                                arg = Double.parseDouble(property);
                            } else if (cn.equals("boolean") || cn.equals("Boolean")) {
                                arg = Boolean.parseBoolean(property);
                            } else if (cn.equals("float") || cn.equals("Float")) {
                                arg = Float.parseFloat(property);
                            } else if (cn.equals("String")) {
                                arg = property;
                            } else {
                                continue;
                            }
                            method.invoke(object, arg);
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
        }
    }

    public static void properties2Object(final Properties p, final Object object) {
        properties2Object(p, object, "");
    }
}
