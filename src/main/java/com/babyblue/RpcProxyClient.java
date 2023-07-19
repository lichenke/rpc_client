package com.babyblue;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Proxy;
import java.net.Socket;

public class RpcProxyClient {

    final String host;

    final int port;

    public RpcProxyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @SuppressWarnings("unchecked")
    public <T> T clientProxy(final Class<T> interfaceClz) {
        return (T) Proxy.newProxyInstance(interfaceClz.getClassLoader(), new Class<?>[]{interfaceClz}, (proxy, method, args) -> {
            RpcRequest req = new RpcRequest();
            req.setClassName(method.getDeclaringClass().getName());
            req.setMethodName(method.getName());
            req.setTypes(method.getParameterTypes());
            req.setParams(args);
            return send(req, host, port);
        });
    }

    public Object send(RpcRequest req, String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            try {
                oos.writeObject(req);
                oos.flush();
                return ois.readObject();
            } catch (Throwable t) {
                try {
                    ois.close();
                    oos.close();
                } catch (Throwable t_) {
                    t.addSuppressed(t_);
                }
                throw t;
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
