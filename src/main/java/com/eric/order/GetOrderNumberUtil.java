package com.eric.order;

/**
 * @author EricShen
 * @date 2020-05-14
 */
public class GetOrderNumberUtil {

    public static int number = 1;


    public String getNumber() {
        return "\t生成订单号" + (number++);
    }


}
