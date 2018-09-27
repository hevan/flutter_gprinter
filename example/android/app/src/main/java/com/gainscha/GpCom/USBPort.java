package com.gainscha.GpCom;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class USBPort
        extends Port
{
  protected static final String TAG = "USBPort";
  UsbManager m_usbManager;
  HashMap<String, UsbDevice> m_usbDeviceList;
  UsbDevice m_USBDevice = null;
  UsbInterface m_USBInterface;
  UsbEndpoint m_sendEndpoint;
  UsbEndpoint m_receiveEndpoint;
  UsbDeviceConnection m_connection;
  Context m_context;
  Thread m_Thread;
  Exception m_Exception = null;
  boolean m_USBThreadRunning = false;
  Boolean m_CloseFlag = Boolean.valueOf(false);
  Boolean m_SendFlag = Boolean.valueOf(false);
  byte[] m_SendData;
  int m_bytesAvailable = 0;
  byte[] m_receiveData = new byte['?'];
  Vector<Byte> m_receiveBuffer = new Vector(4096, 1024);

  static UsbDevice getUsbDevice(UsbManager um)
  {
    HashMap<String, UsbDevice> lst = um.getDeviceList();

    Iterator<UsbDevice> deviceIterator = lst.values().iterator();
    while (deviceIterator.hasNext())
    {
      UsbDevice dev = (UsbDevice)deviceIterator.next();

      Log.d("USBPort", "usb device : " + String.format("%1$04X:%2$04X", new Object[] { Integer.valueOf(dev.getVendorId()), Integer.valueOf(dev.getProductId()) }));
      if ((dev.getVendorId() == 1137) || (dev.getVendorId() == 7358) || (dev.getVendorId() == 34918))
      {
        Log.d("USBPort", "******** found usb printer *********");
        return dev;
      }
    }
    return null;
  }

  USBPort(GpComDeviceParameters parameters)
  {
    super(parameters);

    this.m_context = this.m_deviceParameters.ApplicationContext;

    Log.d("USBPort", "Creating UsbManager...");
    this.m_usbManager =
            ((UsbManager)this.m_context.getSystemService("usb"));
    Log.d("USBPort", "Done creating UsbManager.");
  }

  GpCom.ERROR_CODE openPort()
  {
    Log.d("USBPort", "openPort()");

    GpCom.ERROR_CODE retval = GpCom.ERROR_CODE.SUCCESS;

    this.m_USBDevice = null;
    this.m_SendData = null;

    this.m_receiveBuffer.clear();
    this.m_SendFlag = Boolean.valueOf(false);
    this.m_bytesAvailable = 0;
    Log.d("USBPort", "Buffers cleared");

    Log.d("USBPort", "PortName='" + this.m_deviceParameters.PortName + "'");

    this.m_usbDeviceList = this.m_usbManager.getDeviceList();
    if (!this.m_deviceParameters.PortName.equals(""))
    {
      Log.d("USBPort", "PortName not empty. Trying to open it...");
      this.m_USBDevice =
              ((UsbDevice)this.m_usbDeviceList.get(this.m_deviceParameters.PortName));
    }
    else
    {
      Log.d("USBPort", "PortName is empty. Trying to find Gp device...");

      this.m_USBDevice = getUsbDevice(this.m_usbManager);
      if (this.m_USBDevice != null)
      {
        if (!getUsbPermission()) {
          return GpCom.ERROR_CODE.ERROR_OR_NO_ACCESS_PERMISSION;
        }
        retval = connectToDevice(this.m_USBDevice);
        String errorString = GpCom.getErrorText(retval);
        Log.d("USBPort", "connectToDevice returned " + errorString);
      }
      else
      {
        retval = GpCom.ERROR_CODE.NO_USB_DEVICE_FOUND;
        Log.d("USBPort", "No device selected or found");
      }
    }
    return retval;
  }

  class TransferLoop
          implements Runnable
  {
    TransferLoop() {}

    public void run()
    {
      Log.d("USBPort", "Thread started");
      Log.d("USBPort", "m_USBDevice==null? " + Boolean.toString(USBPort.this.m_USBDevice == null));
      try
      {
        USBPort.this.m_USBInterface = USBPort.this.m_USBDevice
                .getInterface(0);
        Log.d("USBPort",
                "m_USBInterface==null? " +

                        Boolean.toString(USBPort.this.m_USBInterface == null));
        int epCount = USBPort.this.m_USBInterface.getEndpointCount();
        Log.d("USBPort", "epCount=" + Integer.toString(epCount));

        String messageString = Integer.toString(epCount) +
                " endpoints: ";
        for (int i = 0; i < epCount; i++)
        {
          messageString = messageString + Integer.toString(i) + "-";
          if (USBPort.this.m_USBInterface.getEndpoint(i)
                  .getDirection() == 0)
          {
            messageString = messageString + "out";
            USBPort.this.m_sendEndpoint = USBPort.this.m_USBInterface
                    .getEndpoint(i);
            Log.d("USBPort",
                    "m_sendEndpoint==null? " +

                            Boolean.toString(USBPort.this.m_sendEndpoint == null));
          }
          else
          {
            messageString = messageString + "in";
            USBPort.this.m_receiveEndpoint = USBPort.this.m_USBInterface
                    .getEndpoint(i);
            Log.d("USBPort",
                    "m_receiveEndpoint==null? " +

                            Boolean.toString(USBPort.this.m_receiveEndpoint == null));
          }
          messageString = messageString + " ";
        }
        Log.d("USBPort", messageString);

        USBPort.this.m_connection = USBPort.this.m_usbManager
                .openDevice(USBPort.this.m_USBDevice);
        Log.d("USBPort",
                "m_connection==null? " +

                        Boolean.toString(USBPort.this.m_connection == null));
        if (USBPort.this.m_connection == null)
        {
          Log.d("USBPort",
                  "Error or no permission to access the port");
          USBPort.this.m_Error = GpCom.ERROR_CODE.ERROR_OR_NO_ACCESS_PERMISSION;
        }
        else
        {
          USBPort.this.m_connection.claimInterface(
                  USBPort.this.m_USBInterface, true);
        }
      }
      catch (SecurityException e)
      {
        USBPort.this.m_Exception = e;
        Log.d("USBPort",
                "Exception in connectToDevice: " + e.toString());
        USBPort.this.m_Error = GpCom.ERROR_CODE.NO_ACCESS_GRANTED_BY_USER;
      }
      catch (Exception e)
      {
        USBPort.this.m_Exception = e;
        Log.d("USBPort",
                "Exception in connectToDevice: " + e.toString());
        USBPort.this.m_Error = GpCom.ERROR_CODE.FAILED;
      }
      if ((USBPort.this.m_Exception == null) &&
              (USBPort.this.m_Error == GpCom.ERROR_CODE.SUCCESS))
      {
        Log.d("USBPort", "Starting communication loop");
        USBPort.this.m_USBThreadRunning = false;
        USBPort.this.m_CloseFlag = Boolean.valueOf(false);
        while (!USBPort.this.m_CloseFlag.booleanValue()) {
          try
          {
            if (USBPort.this.m_SendFlag.booleanValue()) {
              try
              {
                Log.d("USBPort",
                        "Sending data: " +

                                Integer.toString(USBPort.this.m_SendData.length));
                USBPort.this.m_connection.bulkTransfer(
                        USBPort.this.m_sendEndpoint,
                        USBPort.this.m_SendData,
                        USBPort.this.m_SendData.length, 100);

                USBPort.this.m_SendFlag =
                        Boolean.valueOf(false);
              }
              catch (Exception e)
              {
                Log.d("USBPort",
                        "Exception occured in send data part of run loop: " +
                                e.toString() + " - " +
                                e.getMessage());
              }
            }
            try
            {
              if (USBPort.this.m_receiveEndpoint != null)
              {
                USBPort.this.m_receiveData[0] = 0;
                int receiveCount = USBPort.this.m_connection
                        .bulkTransfer(
                                USBPort.this.m_receiveEndpoint,
                                USBPort.this.m_receiveData,
                                USBPort.this.m_receiveData.length,
                                200);
                if (receiveCount > 0)
                {
                  Log.d("USBPort",
                          "Receiving data: " +

                                  Integer.toString(receiveCount) +
                                  " bytes");
                  for (int i = 0; i < receiveCount; i++) {
                    USBPort.this.m_receiveBuffer.add(
                            Byte.valueOf(USBPort.this.m_receiveData[i]));
                  }
                  USBPort.this.saveData(USBPort.this.m_receiveBuffer);
                  if (USBPort.this.m_callbackInfo.ReceivedDataType != GpCom.DATA_TYPE.NOTHING)
                  {
                    if (USBPort.this.m_callback != null) {
                      USBPort.this.m_callback.CallbackMethod(USBPort.this.m_callbackInfo);
                    }
                    USBPort.this.afterCallbackAction(USBPort.this.m_callbackInfo.ReceivedDataType);
                  }
                }
              }
            }
            catch (Exception e)
            {
              Log.d("USBPort",
                      "Exception occured in receive data part of run loop: " +
                              e.toString() + " - " +
                              e.getMessage());
            }
            USBPort.this.m_USBThreadRunning = true;

            Thread.sleep(30L);
          }
          catch (Exception e)
          {
            Log.d("USBPort",
                    "Exception occured in run loop: " +
                            e.getMessage());

            USBPort.this.m_Exception = e;
            USBPort.this.m_CloseFlag = Boolean.valueOf(true);
            USBPort.this.m_Error = GpCom.ERROR_CODE.FAILED;
          }
        }
        Log.d("USBPort", "Closing USB port");
        try
        {
          USBPort.this.m_connection.releaseInterface(USBPort.this.m_USBInterface);
          USBPort.this.m_connection.close();
          USBPort.this.m_connection = null;
        }
        catch (Exception localException1) {}
        USBPort.this.m_USBThreadRunning = false;
      }
    }
  }

  static String ACTION_USB_PERMISSION = "com.gainscha.USBPort.USB_PERMISSION";

  public static GpCom.ERROR_CODE requestPermission(Context context)
  {
    UsbManager um = (UsbManager)context.getSystemService("usb");

    UsbDevice usbdev = getUsbDevice(um);
    if (usbdev != null)
    {
      if (!um.hasPermission(usbdev))
      {
        postRequestPermission(context, um, usbdev);

        return GpCom.ERROR_CODE.ERROR_OR_NO_ACCESS_PERMISSION;
      }
      return GpCom.ERROR_CODE.SUCCESS;
    }
    return GpCom.ERROR_CODE.NO_USB_DEVICE_FOUND;
  }

  private static void postRequestPermission(Context context, UsbManager um, UsbDevice ud)
  {
    BroadcastReceiver receiver = new BroadcastReceiver()
    {
      public void onReceive(Context context, Intent intent)
      {
        Log.d("USBPort", intent.getAction());
        context.unregisterReceiver(this);
      }
    };
    IntentFilter ifilter = new IntentFilter(ACTION_USB_PERMISSION);
    context.registerReceiver(receiver, ifilter);

    PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(
            ACTION_USB_PERMISSION), 0);
    um.requestPermission(ud, pi);
  }

  private boolean getUsbPermission()
  {
    if (this.m_usbManager.hasPermission(this.m_USBDevice)) {
      return true;
    }
    Log.d("USBPort", "permission denied for device " + this.m_USBDevice);
    postRequestPermission(this.m_context, this.m_usbManager, this.m_USBDevice);
    this.m_USBDevice = null;
    return false;
  }

  private GpCom.ERROR_CODE connectToDevice(UsbDevice device)
  {
    this.m_Exception = null;
    this.m_Error = GpCom.ERROR_CODE.SUCCESS;

    Log.d("USBPort", "connectToDevice()");

    this.m_Thread = new Thread(new TransferLoop());
    this.m_Thread.start();
    try
    {
      Thread.sleep(50L);
    }
    catch (Exception localException) {}
    while ((!this.m_USBThreadRunning) && (this.m_Exception == null) &&
            (this.m_Error == GpCom.ERROR_CODE.SUCCESS)) {
      try
      {
        Thread.sleep(50L);
      }
      catch (Exception localException1) {}
    }
    if (this.m_USBThreadRunning)
    {
      String command = String.format("GS a 0", new Object[0]);
      Vector<Byte> binaryData = GpTools.convertEscposToBinary(command);
      writeData(binaryData);
    }
    return this.m_Error;
  }

  GpCom.ERROR_CODE closePort()
  {
    GpCom.ERROR_CODE retval = GpCom.ERROR_CODE.SUCCESS;

    Log.d("USBPort", "closePort()");

    Date NowDate = new Date();
    Date TimeoutDate = new Date(NowDate.getTime() + 2000L);
    while (((this.m_SendFlag.booleanValue()) || (this.m_bytesAvailable > 0)) && (
            NowDate.before(TimeoutDate)))
    {
      try
      {
        Thread.sleep(50L);
      }
      catch (Exception localException1) {}
      NowDate = new Date();
    }
    if (NowDate.before(TimeoutDate)) {
      try
      {
        this.m_connection.releaseInterface(this.m_USBInterface);
        this.m_connection.close();
        this.m_connection = null;
        this.m_CloseFlag = Boolean.valueOf(true);
      }
      catch (Exception e)
      {
        retval = GpCom.ERROR_CODE.FAILED;
      }
    } else {
      retval = GpCom.ERROR_CODE.TIMEOUT;
    }
    return retval;
  }

  boolean isPortOpen()
  {
    boolean retval = true;
    if ((this.m_connection != null) && (this.m_sendEndpoint != null)) {
      retval = true;
    } else {
      retval = false;
    }
    return retval;
  }

  GpCom.ERROR_CODE writeData(Vector<Byte> data)
  {
    GpCom.ERROR_CODE retval = GpCom.ERROR_CODE.SUCCESS;
    if ((data != null) && (data.size() > 0))
    {
      parseOutgoingData(data);
      if ((this.m_connection != null) && (this.m_sendEndpoint != null))
      {
        Date NowDate = new Date();
        Date TimeoutDate = new Date(NowDate.getTime() + 3000L);
        while ((this.m_SendFlag.booleanValue()) && (
                NowDate.before(TimeoutDate)))
        {
          try
          {
            Thread.sleep(50L);
          }
          catch (InterruptedException localInterruptedException) {}
          NowDate = new Date();
        }
        if (NowDate.before(TimeoutDate))
        {
          this.m_SendData = new byte[data.size()];
          if (data.size() > 0)
          {
            for (int i = 0; i < data.size(); i++) {
              this.m_SendData[i] = ((Byte)data.get(i))
                      .byteValue();
            }
            this.m_SendFlag = Boolean.valueOf(true);
          }
        }
        else
        {
          retval = GpCom.ERROR_CODE.TIMEOUT;
        }
      }
      else
      {
        retval = GpCom.ERROR_CODE.FAILED;
      }
    }
    return retval;
  }

  protected GpCom.ERROR_CODE writeDataImmediately(Vector<Byte> data)
  {
    GpCom.ERROR_CODE retval = GpCom.ERROR_CODE.SUCCESS;
    if ((data != null) && (data.size() > 0))
    {
      byte[] sendData = new byte[data.size()];
      if (data.size() > 0)
      {
        for (int i = 0; i < data.size(); i++) {
          sendData[i] = ((Byte)data.get(i)).byteValue();
        }
        try
        {
          this.m_connection.bulkTransfer(this.m_sendEndpoint,
                  sendData, sendData.length, 100);
        }
        catch (Exception e)
        {
          Log.d("USBPort",
                  "Exception occured while sending data immediately: " +
                          e.getMessage());
          retval = GpCom.ERROR_CODE.FAILED;
        }
      }
    }
    return retval;
  }
}
