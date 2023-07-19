package com.babyblue;

import com.babyblue.api.IHelloService;

/**
 * Hello world!
 *
 */
public class App1
{
    public static void main( String[] args ) {
        RpcProxyClient client = new RpcProxyClient("localhost", 8888);
        IHelloService service = client.clientProxy(IHelloService.class);
        System.out.println(service.sayHello("文愉涵"));
    }
}
