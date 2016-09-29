package agroconnectapp.agroconnect.in.agroconnect.networkclient.network;


import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.TResponse;

public interface TRequestDelegate {
  void run(TResponse response);
}
