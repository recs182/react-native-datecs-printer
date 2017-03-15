package com.renancsoares.datecsprinter.network;

import java.net.Socket;

public interface PrinterServerListener {
    public void onConnect(Socket socket);
}
