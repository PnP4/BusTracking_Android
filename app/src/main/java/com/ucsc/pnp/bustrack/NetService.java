package com.ucsc.pnp.bustrack;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * Created by nrv on 9/18/16.
 */
public class NetService extends Service {
Context servicecontext;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        servicecontext=getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);



        Runnable mainThreadrunnable=new Runnable() {
            @Override
            public void run() {

                Runnable runnable=new Runnable() {
                    @Override
                    public void run() {
                        Socket socket = null;
                        System.out.println("log 1");
                        final String QUEUE_NAME ="map";
                        System.out.println("log 2");

                        try {
                            //socket = new Socket("192.34.63.88", 8072);





                            Log.e("PPPP","step1");
                            ConnectionFactory factory = new ConnectionFactory();
                            Log.e("PPPP","step2");
                            try {
                                factory.setHost("10.0.2.2");
                                //factory.setPort(15672);
                            } catch(Exception e1){
                                e1.printStackTrace();
                            }
                            Log.e("PPPP","step3");
                            Connection connection = factory.newConnection();
                            Log.e("PPPP","step4");
                            Channel channel = connection.createChannel();
                            Log.e("PPPP","step5");
                            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                            //String message = "Hello World!";
                            //channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());

                            Consumer consumer = new DefaultConsumer(channel) {
                                @Override
                                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                                        throws IOException {
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024 * 1024);
                                    byte[] buffer = new byte[1024 * 1024];
                                    int bytesRead;
                                    //String message = new String(body, "UTF-8");
                                    Log.e("Service", "Data In");
                                    byteArrayOutputStream.write(body, 0, body.length);
                                    String data = byteArrayOutputStream.toString("UTF-8");
                                    //System.out.println(" [x] Received '" + message + "'");
                                    Intent intent = new Intent();
                                    intent.putExtra("data", data);
                                    intent.setAction("com.ucsc.pnp.bustrack.CUSTOM");
                                    sendBroadcast(intent);
                                    Log.e("Service", data);
                                }
                            };
                            channel.basicConsume(QUEUE_NAME, true, consumer);
                            //System.out.println(" [x] Sent '" + message + "'");
                               // InputStream inputStream = socket.getInputStream();

         /*
          * notice: inputStream.read() will block if no data return
          */
                               // bytesRead = inputStream.read(buffer);
                               // Log.e("Service", "Data In");
                               // byteArrayOutputStream.write(buffer, 0, bytesRead);
                                //String data = byteArrayOutputStream.toString("UTF-8");




                        } catch (UnknownHostException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                        } catch (TimeoutException e){

                            e.printStackTrace();

                        } finally {
                            if (socket != null) {
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                };


                for (int k=0;true;k++) {

                    try {
                        Thread.sleep(10000,0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Thread thread = new Thread(runnable);
                    thread.start();

                }

            }
        };
        Thread mainthread=new Thread(mainThreadrunnable);
        mainthread.start();

        return START_STICKY;
    }
}
