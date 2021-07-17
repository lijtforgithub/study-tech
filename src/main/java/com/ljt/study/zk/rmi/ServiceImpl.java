package com.ljt.study.zk.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

class ServiceImpl extends UnicastRemoteObject implements Service {

    private static final long serialVersionUID = 5787919934128900864L;

    protected ServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String helloWorld() throws RemoteException {
        return "Hello World";
    }

    @Override
    public String say(String name) throws RemoteException {
        return "Hello " + name;
    }

}

interface Service extends Remote {

    String helloWorld() throws RemoteException;

    String say(String name) throws RemoteException;

}