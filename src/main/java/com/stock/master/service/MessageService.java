package com.stock.master.service;

public interface MessageService {

    void send(String body);

    void sendMd(String title, String body);

}
