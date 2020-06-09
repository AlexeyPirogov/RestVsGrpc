package com.benchmark.service;

import com.benchmark.domain.Trade;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CommonTradeService {

    private List<Trade> trades;

    public void initTrades(int n) {
        trades = new ArrayList<>(n);
        PodamFactory factory = new PodamFactoryImpl();

        for (int i = 0; i < n; i++) {
            Trade trade = factory.manufacturePojoWithFullData(Trade.class);
            trade.setId(String.valueOf(i));
            trades.add(trade);
        }
    }

    public void initTradesFromFile(int n) {
        try {
            FileInputStream fis = new FileInputStream(n + "_trades.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);

            trades = (ArrayList<Trade>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
        }
    }

    public void serializeTrade() {
        try {
            FileOutputStream fos = new FileOutputStream(trades.size() + "_trades.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(trades);
            oos.close();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    public List<Trade> getTrades(int n) {
        if (n != trades.size())
            throw new IllegalArgumentException("Request size=" + n + ", doesn't match number of generated trades=" + trades.size());
        return trades;
    }
}