package com.gainscha.GpCom;

import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Port
{
  protected GpComDeviceParameters m_deviceParameters;
  private GpCom.DATA_TYPE m_expectedReceiveType;
  protected GpComCallbackInfo m_callbackInfo = new GpComCallbackInfo();
  protected CallbackInterface m_callback = null;
  private Vector<Byte> m_receiveBuffer = new Vector(1024, 1024);
  private Vector<Byte> m_ASB = new Vector(10);
  private byte m_RealtimeStatus;
  private Boolean m_RealtimeStatusReady;
  private Boolean m_ImageDataReady;
  private Vector<Byte> m_fileInfoBlock = new Vector(1024, 1024);
  private Vector<Byte> m_sizeInfoBlock = new Vector(1024, 1024);
  private GpCom.RECEIVESTATE m_receiveState;
  private GpCom.RECEIVESUBSTATE m_receiveSubState;
  private long m_receiveCounter;
  private int m_dataBlockSize;
  private byte m_headerByte;
  private byte m_identifier;
  private byte m_identificationStatus;
  private ReentrantLock m_receiveBufferLock = new ReentrantLock();
  private ReentrantLock m_ASBLock = new ReentrantLock();
  protected GpCom.ERROR_CODE m_Error = GpCom.ERROR_CODE.SUCCESS;

  abstract GpCom.ERROR_CODE openPort();

  abstract GpCom.ERROR_CODE closePort();

  abstract boolean isPortOpen();

  abstract GpCom.ERROR_CODE writeData(Vector<Byte> paramVector);

  protected abstract GpCom.ERROR_CODE writeDataImmediately(Vector<Byte> paramVector);

  Port(GpComDeviceParameters parameters)
  {
    this.m_deviceParameters = parameters;
    this.m_expectedReceiveType = GpCom.DATA_TYPE.GENERAL;

    this.m_RealtimeStatus = 0;
    this.m_RealtimeStatusReady = Boolean.valueOf(false);

    this.m_ImageDataReady = Boolean.valueOf(false);

    this.m_receiveState = GpCom.RECEIVESTATE.RSTATE_SINGLE;
    this.m_receiveSubState = GpCom.RECEIVESUBSTATE.RSUBSTATE_INITSTATE;
    this.m_receiveCounter = 0L;
    this.m_dataBlockSize = 0;
    this.m_identifier = 0;
    this.m_identificationStatus = 0;
  }

  public GpCom.ERROR_CODE registerCallback(CallbackInterface callback)
  {
    GpCom.ERROR_CODE retval = GpCom.ERROR_CODE.SUCCESS;
    if (callback != null) {
      this.m_callback = callback;
    } else {
      retval = GpCom.ERROR_CODE.INVALID_CALLBACK_OBJECT;
    }
    return retval;
  }

  /* Error */
  private Vector<Byte> getData(Vector<Byte> source, ReentrantLock sourceLock)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aload_1
    //   3: invokevirtual 156	java/util/Vector:size	()I
    //   6: ifle +71 -> 77
    //   9: aload_2
    //   10: invokevirtual 160	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   13: new 67	java/util/Vector
    //   16: dup
    //   17: aload_1
    //   18: invokespecial 163	java/util/Vector:<init>	(Ljava/util/Collection;)V
    //   21: astore_3
    //   22: aload_1
    //   23: invokevirtual 166	java/util/Vector:clear	()V
    //   26: goto +47 -> 73
    //   29: astore 4
    //   31: ldc -87
    //   33: new 171	java/lang/StringBuilder
    //   36: dup
    //   37: ldc -83
    //   39: invokespecial 175	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   42: aload 4
    //   44: invokevirtual 178	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   47: invokevirtual 184	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   50: invokevirtual 188	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   53: invokestatic 191	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   56: pop
    //   57: aload_2
    //   58: invokevirtual 197	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   61: goto +16 -> 77
    //   64: astore 5
    //   66: aload_2
    //   67: invokevirtual 197	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   70: aload 5
    //   72: athrow
    //   73: aload_2
    //   74: invokevirtual 197	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   77: aload_3
    //   78: areturn
    // Line number table:
    //   Java source line #86	-> byte code offset #0
    //   Java source line #88	-> byte code offset #2
    //   Java source line #90	-> byte code offset #9
    //   Java source line #93	-> byte code offset #13
    //   Java source line #95	-> byte code offset #22
    //   Java source line #97	-> byte code offset #29
    //   Java source line #99	-> byte code offset #31
    //   Java source line #103	-> byte code offset #57
    //   Java source line #102	-> byte code offset #64
    //   Java source line #103	-> byte code offset #66
    //   Java source line #104	-> byte code offset #70
    //   Java source line #103	-> byte code offset #73
    //   Java source line #107	-> byte code offset #77
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	79	0	this	Port
    //   0	79	1	source	Vector<Byte>
    //   0	79	2	sourceLock	ReentrantLock
    //   1	77	3	data	Vector
    //   29	14	4	e	Exception
    //   64	7	5	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   13	26	29	java/lang/Exception
    //   13	57	64	finally
  }

  /* Error */
  private byte[] getDataBytes(Vector<Byte> source, ReentrantLock sourceLock)
  {
    // Byte code:
    //   0: aconst_null
    //   1: checkcast 211	[B
    //   4: astore_3
    //   5: aload_1
    //   6: invokevirtual 156	java/util/Vector:size	()I
    //   9: ifle +103 -> 112
    //   12: aload_1
    //   13: invokevirtual 156	java/util/Vector:size	()I
    //   16: newarray <illegal type>
    //   18: astore_3
    //   19: aload_2
    //   20: invokevirtual 160	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   23: iconst_0
    //   24: istore 4
    //   26: goto +22 -> 48
    //   29: aload_3
    //   30: iload 4
    //   32: aload_1
    //   33: iload 4
    //   35: invokevirtual 213	java/util/Vector:get	(I)Ljava/lang/Object;
    //   38: checkcast 217	java/lang/Byte
    //   41: invokevirtual 219	java/lang/Byte:byteValue	()B
    //   44: bastore
    //   45: iinc 4 1
    //   48: iload 4
    //   50: aload_1
    //   51: invokevirtual 156	java/util/Vector:size	()I
    //   54: if_icmplt -25 -> 29
    //   57: aload_1
    //   58: invokevirtual 166	java/util/Vector:clear	()V
    //   61: goto +47 -> 108
    //   64: astore 4
    //   66: ldc -33
    //   68: new 171	java/lang/StringBuilder
    //   71: dup
    //   72: ldc -83
    //   74: invokespecial 175	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   77: aload 4
    //   79: invokevirtual 178	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   82: invokevirtual 184	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   85: invokevirtual 188	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   88: invokestatic 191	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   91: pop
    //   92: aload_2
    //   93: invokevirtual 197	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   96: goto +16 -> 112
    //   99: astore 5
    //   101: aload_2
    //   102: invokevirtual 197	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   105: aload 5
    //   107: athrow
    //   108: aload_2
    //   109: invokevirtual 197	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   112: aload_3
    //   113: areturn
    // Line number table:
    //   Java source line #112	-> byte code offset #0
    //   Java source line #114	-> byte code offset #5
    //   Java source line #116	-> byte code offset #12
    //   Java source line #118	-> byte code offset #19
    //   Java source line #121	-> byte code offset #23
    //   Java source line #123	-> byte code offset #29
    //   Java source line #121	-> byte code offset #45
    //   Java source line #126	-> byte code offset #57
    //   Java source line #128	-> byte code offset #64
    //   Java source line #130	-> byte code offset #66
    //   Java source line #134	-> byte code offset #92
    //   Java source line #133	-> byte code offset #99
    //   Java source line #134	-> byte code offset #101
    //   Java source line #135	-> byte code offset #105
    //   Java source line #134	-> byte code offset #108
    //   Java source line #138	-> byte code offset #112
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	114	0	this	Port
    //   0	114	1	source	Vector<Byte>
    //   0	114	2	sourceLock	ReentrantLock
    //   4	109	3	data	byte[]
    //   24	25	4	i	int
    //   64	14	4	e	Exception
    //   99	7	5	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   23	61	64	java/lang/Exception
    //   23	92	99	finally
  }

  public Vector<Byte> readData()
  {
    return getData(this.m_receiveBuffer, this.m_receiveBufferLock);
  }

  public Vector<Byte> getASB()
  {
    return getData(this.m_ASB, this.m_ASBLock);
  }

  public Boolean isRealtimeStatusAvailable()
  {
    return this.m_RealtimeStatusReady;
  }

  public GpCom.ERROR_CODE getError()
  {
    return this.m_Error;
  }

  public Boolean isImageDataAvailable()
  {
    return this.m_ImageDataReady;
  }

  public byte getRealtimeStatus()
  {
    byte retval = -1;
    if (this.m_RealtimeStatusReady.booleanValue())
    {
      retval = this.m_RealtimeStatus;
      this.m_RealtimeStatusReady = Boolean.valueOf(false);
    }
    return retval;
  }

  protected void parseOutgoingData(Vector<Byte> data)
  {
    this.m_RealtimeStatusReady = Boolean.valueOf(false);
    if ((data.size() > 2) && (((Byte)data.get(0)).byteValue() == GpCom.ASCII_CONTROL_CODE.DLE.getASCIIValue()) && (((Byte)data.get(1)).byteValue() == GpCom.ASCII_CONTROL_CODE.EOT.getASCIIValue())) {
      this.m_expectedReceiveType = GpCom.DATA_TYPE.DEVICESTATUS;
    }
  }

  protected void afterCallbackAction(GpCom.DATA_TYPE dataType) {}

  /* Error */
  protected GpCom.ERROR_CODE saveData(Vector<Byte> receivedData)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: iconst_0
    //   3: istore_3
    //   4: iconst_0
    //   5: istore 4
    //   7: iconst_0
    //   8: invokestatic 108	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   11: astore 5
    //   13: iconst_0
    //   14: istore 6
    //   16: bipush 121
    //   18: istore 7
    //   20: bipush 80
    //   22: istore 8
    //   24: getstatic 262	com/gainscha/GpCom/GpCom$ASCII_CONTROL_CODE:ACK	Lcom/gainscha/GpCom/GpCom$ASCII_CONTROL_CODE;
    //   27: invokevirtual 249	com/gainscha/GpCom/GpCom$ASCII_CONTROL_CODE:getASCIIValue	()B
    //   30: istore 9
    //   32: new 67	java/util/Vector
    //   35: dup
    //   36: iconst_1
    //   37: invokespecial 74	java/util/Vector:<init>	(I)V
    //   40: astore 10
    //   42: aload 10
    //   44: new 217	java/lang/Byte
    //   47: dup
    //   48: iload 9
    //   50: invokespecial 265	java/lang/Byte:<init>	(B)V
    //   53: invokevirtual 268	java/util/Vector:add	(Ljava/lang/Object;)Z
    //   56: pop
    //   57: aload_1
    //   58: invokevirtual 156	java/util/Vector:size	()I
    //   61: istore 6
    //   63: iload 6
    //   65: ifne +1672 -> 1737
    //   68: getstatic 272	com/gainscha/GpCom/GpCom$ERROR_CODE:FAILED	Lcom/gainscha/GpCom/GpCom$ERROR_CODE;
    //   71: areturn
    //   72: invokestatic 275	com/gainscha/GpCom/Port:$SWITCH_TABLE$com$gainscha$GpCom$GpCom$RECEIVESTATE	()[I
    //   75: aload_0
    //   76: getfield 123	com/gainscha/GpCom/Port:m_receiveState	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   79: invokevirtual 278	com/gainscha/GpCom/GpCom$RECEIVESTATE:ordinal	()I
    //   82: iaload
    //   83: tableswitch	default:+1654->1737, 1:+49->132, 2:+270->353, 3:+273->356, 4:+276->359, 5:+378->461, 6:+1176->1259, 7:+1654->1737, 8:+1654->1737, 9:+1654->1737
    //   132: aload_1
    //   133: iconst_0
    //   134: invokevirtual 213	java/util/Vector:get	(I)Ljava/lang/Object;
    //   137: checkcast 217	java/lang/Byte
    //   140: invokevirtual 219	java/lang/Byte:byteValue	()B
    //   143: istore_2
    //   144: aload_0
    //   145: iload_2
    //   146: putfield 281	com/gainscha/GpCom/Port:m_headerByte	B
    //   149: iload_2
    //   150: sipush 147
    //   153: iand
    //   154: bipush 18
    //   156: if_icmpne +28 -> 184
    //   159: aload_0
    //   160: iload_2
    //   161: putfield 106	com/gainscha/GpCom/Port:m_RealtimeStatus	B
    //   164: aload_0
    //   165: getstatic 99	com/gainscha/GpCom/GpCom$DATA_TYPE:GENERAL	Lcom/gainscha/GpCom/GpCom$DATA_TYPE;
    //   168: putfield 104	com/gainscha/GpCom/Port:m_expectedReceiveType	Lcom/gainscha/GpCom/GpCom$DATA_TYPE;
    //   171: aload_0
    //   172: getfield 63	com/gainscha/GpCom/Port:m_callbackInfo	Lcom/gainscha/GpCom/GpComCallbackInfo;
    //   175: getstatic 255	com/gainscha/GpCom/GpCom$DATA_TYPE:DEVICESTATUS	Lcom/gainscha/GpCom/GpCom$DATA_TYPE;
    //   178: putfield 283	com/gainscha/GpCom/GpComCallbackInfo:ReceivedDataType	Lcom/gainscha/GpCom/GpCom$DATA_TYPE;
    //   181: goto +158 -> 339
    //   184: iload_2
    //   185: sipush 147
    //   188: iand
    //   189: bipush 16
    //   191: if_icmpne +65 -> 256
    //   194: aload_0
    //   195: getfield 88	com/gainscha/GpCom/Port:m_ASBLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   198: invokevirtual 160	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   201: aload_0
    //   202: getfield 77	com/gainscha/GpCom/Port:m_ASB	Ljava/util/Vector;
    //   205: invokevirtual 166	java/util/Vector:clear	()V
    //   208: aload_0
    //   209: getfield 77	com/gainscha/GpCom/Port:m_ASB	Ljava/util/Vector;
    //   212: aload_1
    //   213: iconst_0
    //   214: invokevirtual 213	java/util/Vector:get	(I)Ljava/lang/Object;
    //   217: checkcast 217	java/lang/Byte
    //   220: invokevirtual 268	java/util/Vector:add	(Ljava/lang/Object;)Z
    //   223: pop
    //   224: goto +15 -> 239
    //   227: astore 11
    //   229: aload_0
    //   230: getfield 88	com/gainscha/GpCom/Port:m_ASBLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   233: invokevirtual 197	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   236: aload 11
    //   238: athrow
    //   239: aload_0
    //   240: getfield 88	com/gainscha/GpCom/Port:m_ASBLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   243: invokevirtual 197	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   246: aload_0
    //   247: getstatic 286	com/gainscha/GpCom/GpCom$RECEIVESTATE:RSTATE_ASB	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   250: putfield 123	com/gainscha/GpCom/Port:m_receiveState	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   253: goto +86 -> 339
    //   256: iload_2
    //   257: bipush 83
    //   259: if_icmpne +18 -> 277
    //   262: aload_0
    //   263: iconst_0
    //   264: putfield 134	com/gainscha/GpCom/Port:m_dataBlockSize	I
    //   267: aload_0
    //   268: getstatic 289	com/gainscha/GpCom/GpCom$RECEIVESTATE:RSTATE_BLOCK_BINARY	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   271: putfield 123	com/gainscha/GpCom/Port:m_receiveState	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   274: goto +65 -> 339
    //   277: aload_0
    //   278: getfield 86	com/gainscha/GpCom/Port:m_receiveBufferLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   281: invokevirtual 160	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   284: aload_0
    //   285: getfield 72	com/gainscha/GpCom/Port:m_receiveBuffer	Ljava/util/Vector;
    //   288: aload_1
    //   289: iconst_0
    //   290: invokevirtual 213	java/util/Vector:get	(I)Ljava/lang/Object;
    //   293: checkcast 217	java/lang/Byte
    //   296: invokevirtual 268	java/util/Vector:add	(Ljava/lang/Object;)Z
    //   299: pop
    //   300: goto +15 -> 315
    //   303: astore 11
    //   305: aload_0
    //   306: getfield 86	com/gainscha/GpCom/Port:m_receiveBufferLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   309: invokevirtual 197	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   312: aload 11
    //   314: athrow
    //   315: aload_0
    //   316: getfield 86	com/gainscha/GpCom/Port:m_receiveBufferLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   319: invokevirtual 197	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   322: aload_0
    //   323: getstatic 118	com/gainscha/GpCom/GpCom$RECEIVESTATE:RSTATE_SINGLE	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   326: putfield 123	com/gainscha/GpCom/Port:m_receiveState	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   329: aload_0
    //   330: getfield 63	com/gainscha/GpCom/Port:m_callbackInfo	Lcom/gainscha/GpCom/GpComCallbackInfo;
    //   333: getstatic 99	com/gainscha/GpCom/GpCom$DATA_TYPE:GENERAL	Lcom/gainscha/GpCom/GpCom$DATA_TYPE;
    //   336: putfield 283	com/gainscha/GpCom/GpComCallbackInfo:ReceivedDataType	Lcom/gainscha/GpCom/GpCom$DATA_TYPE;
    //   339: aload_1
    //   340: iconst_0
    //   341: invokevirtual 292	java/util/Vector:remove	(I)Ljava/lang/Object;
    //   344: pop
    //   345: aload_0
    //   346: lconst_1
    //   347: putfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   350: goto +1387 -> 1737
    //   353: goto +1384 -> 1737
    //   356: goto +1381 -> 1737
    //   359: iconst_4
    //   360: aload_0
    //   361: getfield 77	com/gainscha/GpCom/Port:m_ASB	Ljava/util/Vector;
    //   364: invokevirtual 156	java/util/Vector:size	()I
    //   367: isub
    //   368: aload_1
    //   369: invokevirtual 156	java/util/Vector:size	()I
    //   372: invokestatic 295	java/lang/Math:min	(II)I
    //   375: istore_3
    //   376: aload_0
    //   377: getfield 88	com/gainscha/GpCom/Port:m_ASBLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   380: invokevirtual 160	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   383: aload_0
    //   384: getfield 77	com/gainscha/GpCom/Port:m_ASB	Ljava/util/Vector;
    //   387: aload_1
    //   388: iconst_0
    //   389: iload_3
    //   390: invokevirtual 301	java/util/Vector:subList	(II)Ljava/util/List;
    //   393: invokevirtual 305	java/util/Vector:addAll	(Ljava/util/Collection;)Z
    //   396: pop
    //   397: goto +15 -> 412
    //   400: astore 11
    //   402: aload_0
    //   403: getfield 88	com/gainscha/GpCom/Port:m_ASBLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   406: invokevirtual 197	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   409: aload 11
    //   411: athrow
    //   412: aload_0
    //   413: getfield 88	com/gainscha/GpCom/Port:m_ASBLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   416: invokevirtual 197	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   419: aload_1
    //   420: iconst_0
    //   421: iload_3
    //   422: invokevirtual 301	java/util/Vector:subList	(II)Ljava/util/List;
    //   425: invokeinterface 309 1 0
    //   430: aload_0
    //   431: getfield 77	com/gainscha/GpCom/Port:m_ASB	Ljava/util/Vector;
    //   434: invokevirtual 156	java/util/Vector:size	()I
    //   437: iconst_4
    //   438: if_icmpne +1299 -> 1737
    //   441: aload_0
    //   442: getstatic 118	com/gainscha/GpCom/GpCom$RECEIVESTATE:RSTATE_SINGLE	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   445: putfield 123	com/gainscha/GpCom/Port:m_receiveState	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   448: aload_0
    //   449: getfield 63	com/gainscha/GpCom/Port:m_callbackInfo	Lcom/gainscha/GpCom/GpComCallbackInfo;
    //   452: getstatic 312	com/gainscha/GpCom/GpCom$DATA_TYPE:ASB	Lcom/gainscha/GpCom/GpCom$DATA_TYPE;
    //   455: putfield 283	com/gainscha/GpCom/GpComCallbackInfo:ReceivedDataType	Lcom/gainscha/GpCom/GpCom$DATA_TYPE;
    //   458: goto +1279 -> 1737
    //   461: aload_0
    //   462: getfield 281	com/gainscha/GpCom/Port:m_headerByte	B
    //   465: lookupswitch	default:+659->1124, 49:+659->1124, 55:+43->508, 57:+659->1124, 91:+659->1124
    //   508: aload_0
    //   509: getfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   512: lconst_1
    //   513: lcmp
    //   514: ifne +244 -> 758
    //   517: aload_0
    //   518: aload_1
    //   519: iconst_0
    //   520: invokevirtual 213	java/util/Vector:get	(I)Ljava/lang/Object;
    //   523: checkcast 217	java/lang/Byte
    //   526: invokevirtual 219	java/lang/Byte:byteValue	()B
    //   529: putfield 136	com/gainscha/GpCom/Port:m_identifier	B
    //   532: aload_1
    //   533: iconst_0
    //   534: invokevirtual 292	java/util/Vector:remove	(I)Ljava/lang/Object;
    //   537: pop
    //   538: aload_0
    //   539: dup
    //   540: getfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   543: lconst_1
    //   544: ladd
    //   545: putfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   548: aload_0
    //   549: getfield 136	com/gainscha/GpCom/Port:m_identifier	B
    //   552: bipush 116
    //   554: if_icmpeq +570 -> 1124
    //   557: aload_0
    //   558: getfield 136	com/gainscha/GpCom/Port:m_identifier	B
    //   561: bipush 42
    //   563: if_icmpeq +561 -> 1124
    //   566: aload_0
    //   567: getfield 86	com/gainscha/GpCom/Port:m_receiveBufferLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   570: invokevirtual 160	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   573: aload_0
    //   574: getfield 72	com/gainscha/GpCom/Port:m_receiveBuffer	Ljava/util/Vector;
    //   577: new 217	java/lang/Byte
    //   580: dup
    //   581: aload_0
    //   582: getfield 281	com/gainscha/GpCom/Port:m_headerByte	B
    //   585: invokespecial 265	java/lang/Byte:<init>	(B)V
    //   588: invokevirtual 268	java/util/Vector:add	(Ljava/lang/Object;)Z
    //   591: pop
    //   592: aload_0
    //   593: getfield 72	com/gainscha/GpCom/Port:m_receiveBuffer	Ljava/util/Vector;
    //   596: new 217	java/lang/Byte
    //   599: dup
    //   600: aload_0
    //   601: getfield 136	com/gainscha/GpCom/Port:m_identifier	B
    //   604: invokespecial 265	java/lang/Byte:<init>	(B)V
    //   607: invokevirtual 268	java/util/Vector:add	(Ljava/lang/Object;)Z
    //   610: pop
    //   611: goto +15 -> 626
    //   614: astore 11
    //   616: aload_0
    //   617: getfield 86	com/gainscha/GpCom/Port:m_receiveBufferLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   620: invokevirtual 197	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   623: aload 11
    //   625: athrow
    //   626: aload_0
    //   627: getfield 86	com/gainscha/GpCom/Port:m_receiveBufferLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   630: invokevirtual 197	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   633: iconst_0
    //   634: istore_3
    //   635: aload_1
    //   636: invokevirtual 156	java/util/Vector:size	()I
    //   639: istore 4
    //   641: goto +9 -> 650
    //   644: iinc 3 1
    //   647: iinc 4 -1
    //   650: iload 4
    //   652: ifle +17 -> 669
    //   655: aload_1
    //   656: iload_3
    //   657: invokevirtual 213	java/util/Vector:get	(I)Ljava/lang/Object;
    //   660: checkcast 217	java/lang/Byte
    //   663: invokevirtual 219	java/lang/Byte:byteValue	()B
    //   666: ifne -22 -> 644
    //   669: aload_0
    //   670: getfield 86	com/gainscha/GpCom/Port:m_receiveBufferLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   673: invokevirtual 160	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   676: aload_0
    //   677: getfield 72	com/gainscha/GpCom/Port:m_receiveBuffer	Ljava/util/Vector;
    //   680: aload_1
    //   681: iconst_0
    //   682: iload_3
    //   683: invokevirtual 301	java/util/Vector:subList	(II)Ljava/util/List;
    //   686: invokevirtual 305	java/util/Vector:addAll	(Ljava/util/Collection;)Z
    //   689: pop
    //   690: goto +15 -> 705
    //   693: astore 11
    //   695: aload_0
    //   696: getfield 86	com/gainscha/GpCom/Port:m_receiveBufferLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   699: invokevirtual 197	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   702: aload 11
    //   704: athrow
    //   705: aload_0
    //   706: getfield 86	com/gainscha/GpCom/Port:m_receiveBufferLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   709: invokevirtual 197	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   712: aload_1
    //   713: iload_3
    //   714: invokevirtual 213	java/util/Vector:get	(I)Ljava/lang/Object;
    //   717: checkcast 217	java/lang/Byte
    //   720: invokevirtual 219	java/lang/Byte:byteValue	()B
    //   723: ifne +10 -> 733
    //   726: aload_0
    //   727: getstatic 118	com/gainscha/GpCom/GpCom$RECEIVESTATE:RSTATE_SINGLE	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   730: putfield 123	com/gainscha/GpCom/Port:m_receiveState	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   733: aload_1
    //   734: iconst_0
    //   735: iload_3
    //   736: invokevirtual 301	java/util/Vector:subList	(II)Ljava/util/List;
    //   739: invokeinterface 309 1 0
    //   744: aload_0
    //   745: dup
    //   746: getfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   749: iload_3
    //   750: i2l
    //   751: ladd
    //   752: putfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   755: goto +369 -> 1124
    //   758: aload_0
    //   759: getfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   762: ldc2_w 315
    //   765: lcmp
    //   766: ifle +358 -> 1124
    //   769: invokestatic 317	com/gainscha/GpCom/Port:$SWITCH_TABLE$com$gainscha$GpCom$GpCom$RECEIVESUBSTATE	()[I
    //   772: aload_0
    //   773: getfield 130	com/gainscha/GpCom/Port:m_receiveSubState	Lcom/gainscha/GpCom/GpCom$RECEIVESUBSTATE;
    //   776: invokevirtual 319	com/gainscha/GpCom/GpCom$RECEIVESUBSTATE:ordinal	()I
    //   779: iaload
    //   780: tableswitch	default:+344->1124, 1:+344->1124, 2:+338->1118, 3:+40->820, 4:+189->969, 5:+344->1124, 6:+341->1121
    //   820: aload_0
    //   821: getfield 63	com/gainscha/GpCom/Port:m_callbackInfo	Lcom/gainscha/GpCom/GpComCallbackInfo;
    //   824: getstatic 320	com/gainscha/GpCom/GpCom$DATA_TYPE:NOTHING	Lcom/gainscha/GpCom/GpCom$DATA_TYPE;
    //   827: putfield 283	com/gainscha/GpCom/GpComCallbackInfo:ReceivedDataType	Lcom/gainscha/GpCom/GpCom$DATA_TYPE;
    //   830: iconst_0
    //   831: invokestatic 108	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   834: astore 5
    //   836: iconst_0
    //   837: istore_3
    //   838: aload_1
    //   839: invokevirtual 156	java/util/Vector:size	()I
    //   842: istore 4
    //   844: goto +9 -> 853
    //   847: iinc 3 1
    //   850: iinc 4 -1
    //   853: iload 4
    //   855: ifle +17 -> 872
    //   858: aload_1
    //   859: iload_3
    //   860: invokevirtual 213	java/util/Vector:get	(I)Ljava/lang/Object;
    //   863: checkcast 217	java/lang/Byte
    //   866: invokevirtual 219	java/lang/Byte:byteValue	()B
    //   869: ifne -22 -> 847
    //   872: aload_1
    //   873: iload_3
    //   874: invokevirtual 213	java/util/Vector:get	(I)Ljava/lang/Object;
    //   877: checkcast 217	java/lang/Byte
    //   880: invokevirtual 219	java/lang/Byte:byteValue	()B
    //   883: ifne +15 -> 898
    //   886: iinc 3 1
    //   889: iinc 4 -1
    //   892: iconst_1
    //   893: invokestatic 108	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   896: astore 5
    //   898: aload_0
    //   899: getfield 79	com/gainscha/GpCom/Port:m_fileInfoBlock	Ljava/util/Vector;
    //   902: aload_1
    //   903: iconst_0
    //   904: iload_3
    //   905: invokevirtual 301	java/util/Vector:subList	(II)Ljava/util/List;
    //   908: invokevirtual 305	java/util/Vector:addAll	(Ljava/util/Collection;)Z
    //   911: pop
    //   912: aload_1
    //   913: iconst_0
    //   914: iload_3
    //   915: invokevirtual 301	java/util/Vector:subList	(II)Ljava/util/List;
    //   918: invokeinterface 309 1 0
    //   923: aload_0
    //   924: dup
    //   925: getfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   928: iload_3
    //   929: i2l
    //   930: ladd
    //   931: putfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   934: aload 5
    //   936: invokevirtual 237	java/lang/Boolean:booleanValue	()Z
    //   939: ifne +6 -> 945
    //   942: goto +795 -> 1737
    //   945: aload_0
    //   946: getstatic 323	com/gainscha/GpCom/GpCom$RECEIVESUBSTATE:RSUBSTATE_SIZEINFO	Lcom/gainscha/GpCom/GpCom$RECEIVESUBSTATE;
    //   949: putfield 130	com/gainscha/GpCom/Port:m_receiveSubState	Lcom/gainscha/GpCom/GpCom$RECEIVESUBSTATE;
    //   952: aload_0
    //   953: getstatic 118	com/gainscha/GpCom/GpCom$RECEIVESTATE:RSTATE_SINGLE	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   956: putfield 123	com/gainscha/GpCom/Port:m_receiveState	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   959: aload_0
    //   960: aload 10
    //   962: invokevirtual 326	com/gainscha/GpCom/Port:writeDataImmediately	(Ljava/util/Vector;)Lcom/gainscha/GpCom/GpCom$ERROR_CODE;
    //   965: pop
    //   966: goto +158 -> 1124
    //   969: aload_0
    //   970: getfield 63	com/gainscha/GpCom/Port:m_callbackInfo	Lcom/gainscha/GpCom/GpComCallbackInfo;
    //   973: getstatic 320	com/gainscha/GpCom/GpCom$DATA_TYPE:NOTHING	Lcom/gainscha/GpCom/GpCom$DATA_TYPE;
    //   976: putfield 283	com/gainscha/GpCom/GpComCallbackInfo:ReceivedDataType	Lcom/gainscha/GpCom/GpCom$DATA_TYPE;
    //   979: iconst_0
    //   980: invokestatic 108	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   983: astore 5
    //   985: iconst_0
    //   986: istore_3
    //   987: aload_1
    //   988: invokevirtual 156	java/util/Vector:size	()I
    //   991: istore 4
    //   993: goto +9 -> 1002
    //   996: iinc 3 1
    //   999: iinc 4 -1
    //   1002: iload 4
    //   1004: ifle +17 -> 1021
    //   1007: aload_1
    //   1008: iload_3
    //   1009: invokevirtual 213	java/util/Vector:get	(I)Ljava/lang/Object;
    //   1012: checkcast 217	java/lang/Byte
    //   1015: invokevirtual 219	java/lang/Byte:byteValue	()B
    //   1018: ifne -22 -> 996
    //   1021: aload_1
    //   1022: iload_3
    //   1023: invokevirtual 213	java/util/Vector:get	(I)Ljava/lang/Object;
    //   1026: checkcast 217	java/lang/Byte
    //   1029: invokevirtual 219	java/lang/Byte:byteValue	()B
    //   1032: ifne +15 -> 1047
    //   1035: iinc 3 1
    //   1038: iinc 4 -1
    //   1041: iconst_1
    //   1042: invokestatic 108	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   1045: astore 5
    //   1047: aload_0
    //   1048: getfield 81	com/gainscha/GpCom/Port:m_sizeInfoBlock	Ljava/util/Vector;
    //   1051: aload_1
    //   1052: iconst_0
    //   1053: iload_3
    //   1054: invokevirtual 301	java/util/Vector:subList	(II)Ljava/util/List;
    //   1057: invokevirtual 305	java/util/Vector:addAll	(Ljava/util/Collection;)Z
    //   1060: pop
    //   1061: aload_1
    //   1062: iconst_0
    //   1063: iload_3
    //   1064: invokevirtual 301	java/util/Vector:subList	(II)Ljava/util/List;
    //   1067: invokeinterface 309 1 0
    //   1072: aload_0
    //   1073: dup
    //   1074: getfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   1077: iload_3
    //   1078: i2l
    //   1079: ladd
    //   1080: putfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   1083: aload 5
    //   1085: invokevirtual 237	java/lang/Boolean:booleanValue	()Z
    //   1088: ifne +6 -> 1094
    //   1091: goto +646 -> 1737
    //   1094: aload_0
    //   1095: getstatic 328	com/gainscha/GpCom/GpCom$RECEIVESUBSTATE:RSUBSTATE_IMAGEDATA	Lcom/gainscha/GpCom/GpCom$RECEIVESUBSTATE;
    //   1098: putfield 130	com/gainscha/GpCom/Port:m_receiveSubState	Lcom/gainscha/GpCom/GpCom$RECEIVESUBSTATE;
    //   1101: aload_0
    //   1102: getstatic 118	com/gainscha/GpCom/GpCom$RECEIVESTATE:RSTATE_SINGLE	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   1105: putfield 123	com/gainscha/GpCom/Port:m_receiveState	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   1108: aload_0
    //   1109: aload 10
    //   1111: invokevirtual 326	com/gainscha/GpCom/Port:writeDataImmediately	(Ljava/util/Vector;)Lcom/gainscha/GpCom/GpCom$ERROR_CODE;
    //   1114: pop
    //   1115: goto +9 -> 1124
    //   1118: goto +6 -> 1124
    //   1121: goto +616 -> 1737
    //   1124: aload_0
    //   1125: getfield 63	com/gainscha/GpCom/Port:m_callbackInfo	Lcom/gainscha/GpCom/GpComCallbackInfo;
    //   1128: getstatic 99	com/gainscha/GpCom/GpCom$DATA_TYPE:GENERAL	Lcom/gainscha/GpCom/GpCom$DATA_TYPE;
    //   1131: putfield 283	com/gainscha/GpCom/GpComCallbackInfo:ReceivedDataType	Lcom/gainscha/GpCom/GpCom$DATA_TYPE;
    //   1134: iconst_0
    //   1135: istore_3
    //   1136: aload_1
    //   1137: invokevirtual 156	java/util/Vector:size	()I
    //   1140: istore 4
    //   1142: goto +9 -> 1151
    //   1145: iinc 3 1
    //   1148: iinc 4 -1
    //   1151: iload 4
    //   1153: ifle +17 -> 1170
    //   1156: aload_1
    //   1157: iload_3
    //   1158: invokevirtual 213	java/util/Vector:get	(I)Ljava/lang/Object;
    //   1161: checkcast 217	java/lang/Byte
    //   1164: invokevirtual 219	java/lang/Byte:byteValue	()B
    //   1167: ifne -22 -> 1145
    //   1170: aload_1
    //   1171: iload_3
    //   1172: invokevirtual 213	java/util/Vector:get	(I)Ljava/lang/Object;
    //   1175: checkcast 217	java/lang/Byte
    //   1178: invokevirtual 219	java/lang/Byte:byteValue	()B
    //   1181: ifne +10 -> 1191
    //   1184: aload_0
    //   1185: getstatic 118	com/gainscha/GpCom/GpCom$RECEIVESTATE:RSTATE_SINGLE	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   1188: putfield 123	com/gainscha/GpCom/Port:m_receiveState	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   1191: aload_0
    //   1192: getfield 86	com/gainscha/GpCom/Port:m_receiveBufferLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   1195: invokevirtual 160	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   1198: aload_0
    //   1199: getfield 72	com/gainscha/GpCom/Port:m_receiveBuffer	Ljava/util/Vector;
    //   1202: aload_1
    //   1203: iconst_0
    //   1204: iload_3
    //   1205: invokevirtual 301	java/util/Vector:subList	(II)Ljava/util/List;
    //   1208: invokevirtual 305	java/util/Vector:addAll	(Ljava/util/Collection;)Z
    //   1211: pop
    //   1212: goto +15 -> 1227
    //   1215: astore 11
    //   1217: aload_0
    //   1218: getfield 86	com/gainscha/GpCom/Port:m_receiveBufferLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   1221: invokevirtual 197	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   1224: aload 11
    //   1226: athrow
    //   1227: aload_0
    //   1228: getfield 86	com/gainscha/GpCom/Port:m_receiveBufferLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   1231: invokevirtual 197	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   1234: aload_1
    //   1235: iconst_0
    //   1236: iload_3
    //   1237: invokevirtual 301	java/util/Vector:subList	(II)Ljava/util/List;
    //   1240: invokeinterface 309 1 0
    //   1245: aload_0
    //   1246: dup
    //   1247: getfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   1250: iload_3
    //   1251: i2l
    //   1252: ladd
    //   1253: putfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   1256: goto +481 -> 1737
    //   1259: aload_0
    //   1260: getfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   1263: lconst_1
    //   1264: lcmp
    //   1265: ifne +67 -> 1332
    //   1268: aload_0
    //   1269: aload_1
    //   1270: iconst_0
    //   1271: invokevirtual 331	java/util/Vector:elementAt	(I)Ljava/lang/Object;
    //   1274: checkcast 217	java/lang/Byte
    //   1277: invokevirtual 219	java/lang/Byte:byteValue	()B
    //   1280: putfield 136	com/gainscha/GpCom/Port:m_identifier	B
    //   1283: aload_1
    //   1284: iconst_0
    //   1285: invokevirtual 292	java/util/Vector:remove	(I)Ljava/lang/Object;
    //   1288: pop
    //   1289: aload_0
    //   1290: dup
    //   1291: getfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   1294: lconst_1
    //   1295: ladd
    //   1296: putfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   1299: aload_0
    //   1300: getfield 136	com/gainscha/GpCom/Port:m_identifier	B
    //   1303: bipush 32
    //   1305: if_icmpne +432 -> 1737
    //   1308: aload_0
    //   1309: getfield 79	com/gainscha/GpCom/Port:m_fileInfoBlock	Ljava/util/Vector;
    //   1312: invokevirtual 166	java/util/Vector:clear	()V
    //   1315: aload_0
    //   1316: getfield 81	com/gainscha/GpCom/Port:m_sizeInfoBlock	Ljava/util/Vector;
    //   1319: invokevirtual 166	java/util/Vector:clear	()V
    //   1322: aload_0
    //   1323: getstatic 334	com/gainscha/GpCom/GpCom$RECEIVESUBSTATE:RSUBSTATE_FILEINFO	Lcom/gainscha/GpCom/GpCom$RECEIVESUBSTATE;
    //   1326: putfield 130	com/gainscha/GpCom/Port:m_receiveSubState	Lcom/gainscha/GpCom/GpCom$RECEIVESUBSTATE;
    //   1329: goto +408 -> 1737
    //   1332: aload_0
    //   1333: getfield 136	com/gainscha/GpCom/Port:m_identifier	B
    //   1336: bipush 32
    //   1338: if_icmpne +147 -> 1485
    //   1341: aload_0
    //   1342: getfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   1345: ldc2_w 315
    //   1348: lcmp
    //   1349: ifne +37 -> 1386
    //   1352: aload_0
    //   1353: aload_1
    //   1354: iconst_0
    //   1355: invokevirtual 331	java/util/Vector:elementAt	(I)Ljava/lang/Object;
    //   1358: checkcast 217	java/lang/Byte
    //   1361: invokevirtual 219	java/lang/Byte:byteValue	()B
    //   1364: putfield 138	com/gainscha/GpCom/Port:m_identificationStatus	B
    //   1367: aload_1
    //   1368: iconst_0
    //   1369: invokevirtual 292	java/util/Vector:remove	(I)Ljava/lang/Object;
    //   1372: pop
    //   1373: aload_0
    //   1374: dup
    //   1375: getfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   1378: lconst_1
    //   1379: ladd
    //   1380: putfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   1383: goto +354 -> 1737
    //   1386: aload_0
    //   1387: getfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   1390: ldc2_w 337
    //   1393: lcmp
    //   1394: ifne +37 -> 1431
    //   1397: aload_0
    //   1398: aload_1
    //   1399: iconst_0
    //   1400: invokevirtual 331	java/util/Vector:elementAt	(I)Ljava/lang/Object;
    //   1403: checkcast 217	java/lang/Byte
    //   1406: invokevirtual 219	java/lang/Byte:byteValue	()B
    //   1409: putfield 134	com/gainscha/GpCom/Port:m_dataBlockSize	I
    //   1412: aload_1
    //   1413: iconst_0
    //   1414: invokevirtual 292	java/util/Vector:remove	(I)Ljava/lang/Object;
    //   1417: pop
    //   1418: aload_0
    //   1419: dup
    //   1420: getfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   1423: lconst_1
    //   1424: ladd
    //   1425: putfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   1428: goto +309 -> 1737
    //   1431: aload_0
    //   1432: getfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   1435: ldc2_w 339
    //   1438: lcmp
    //   1439: ifne +46 -> 1485
    //   1442: aload_0
    //   1443: dup
    //   1444: getfield 134	com/gainscha/GpCom/Port:m_dataBlockSize	I
    //   1447: aload_1
    //   1448: iconst_0
    //   1449: invokevirtual 331	java/util/Vector:elementAt	(I)Ljava/lang/Object;
    //   1452: checkcast 217	java/lang/Byte
    //   1455: invokevirtual 219	java/lang/Byte:byteValue	()B
    //   1458: sipush 256
    //   1461: imul
    //   1462: iadd
    //   1463: putfield 134	com/gainscha/GpCom/Port:m_dataBlockSize	I
    //   1466: aload_1
    //   1467: iconst_0
    //   1468: invokevirtual 292	java/util/Vector:remove	(I)Ljava/lang/Object;
    //   1471: pop
    //   1472: aload_0
    //   1473: dup
    //   1474: getfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   1477: lconst_1
    //   1478: ladd
    //   1479: putfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   1482: goto +255 -> 1737
    //   1485: invokestatic 317	com/gainscha/GpCom/Port:$SWITCH_TABLE$com$gainscha$GpCom$GpCom$RECEIVESUBSTATE	()[I
    //   1488: aload_0
    //   1489: getfield 130	com/gainscha/GpCom/Port:m_receiveSubState	Lcom/gainscha/GpCom/GpCom$RECEIVESUBSTATE;
    //   1492: invokevirtual 319	com/gainscha/GpCom/GpCom$RECEIVESUBSTATE:ordinal	()I
    //   1495: iaload
    //   1496: tableswitch	default:+238->1734, 1:+238->1734, 2:+238->1734, 3:+40->1536, 4:+139->1635, 5:+238->1734, 6:+238->1734
    //   1536: aload_0
    //   1537: getfield 63	com/gainscha/GpCom/Port:m_callbackInfo	Lcom/gainscha/GpCom/GpComCallbackInfo;
    //   1540: getstatic 320	com/gainscha/GpCom/GpCom$DATA_TYPE:NOTHING	Lcom/gainscha/GpCom/GpCom$DATA_TYPE;
    //   1543: putfield 283	com/gainscha/GpCom/GpComCallbackInfo:ReceivedDataType	Lcom/gainscha/GpCom/GpCom$DATA_TYPE;
    //   1546: aload_1
    //   1547: invokevirtual 156	java/util/Vector:size	()I
    //   1550: aload_0
    //   1551: getfield 134	com/gainscha/GpCom/Port:m_dataBlockSize	I
    //   1554: invokestatic 295	java/lang/Math:min	(II)I
    //   1557: istore_3
    //   1558: aload_0
    //   1559: getfield 79	com/gainscha/GpCom/Port:m_fileInfoBlock	Ljava/util/Vector;
    //   1562: aload_1
    //   1563: iconst_0
    //   1564: iload_3
    //   1565: invokevirtual 301	java/util/Vector:subList	(II)Ljava/util/List;
    //   1568: invokevirtual 305	java/util/Vector:addAll	(Ljava/util/Collection;)Z
    //   1571: pop
    //   1572: aload_1
    //   1573: iconst_0
    //   1574: iload_3
    //   1575: invokevirtual 301	java/util/Vector:subList	(II)Ljava/util/List;
    //   1578: invokeinterface 309 1 0
    //   1583: aload_0
    //   1584: dup
    //   1585: getfield 134	com/gainscha/GpCom/Port:m_dataBlockSize	I
    //   1588: iload_3
    //   1589: isub
    //   1590: putfield 134	com/gainscha/GpCom/Port:m_dataBlockSize	I
    //   1593: aload_0
    //   1594: dup
    //   1595: getfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   1598: iload_3
    //   1599: i2l
    //   1600: ladd
    //   1601: putfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   1604: aload_0
    //   1605: getfield 134	com/gainscha/GpCom/Port:m_dataBlockSize	I
    //   1608: ifne +129 -> 1737
    //   1611: aload_0
    //   1612: getstatic 323	com/gainscha/GpCom/GpCom$RECEIVESUBSTATE:RSUBSTATE_SIZEINFO	Lcom/gainscha/GpCom/GpCom$RECEIVESUBSTATE;
    //   1615: putfield 130	com/gainscha/GpCom/Port:m_receiveSubState	Lcom/gainscha/GpCom/GpCom$RECEIVESUBSTATE;
    //   1618: aload_0
    //   1619: getstatic 118	com/gainscha/GpCom/GpCom$RECEIVESTATE:RSTATE_SINGLE	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   1622: putfield 123	com/gainscha/GpCom/Port:m_receiveState	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   1625: aload_0
    //   1626: aload 10
    //   1628: invokevirtual 326	com/gainscha/GpCom/Port:writeDataImmediately	(Ljava/util/Vector;)Lcom/gainscha/GpCom/GpCom$ERROR_CODE;
    //   1631: pop
    //   1632: goto +105 -> 1737
    //   1635: aload_0
    //   1636: getfield 63	com/gainscha/GpCom/Port:m_callbackInfo	Lcom/gainscha/GpCom/GpComCallbackInfo;
    //   1639: getstatic 320	com/gainscha/GpCom/GpCom$DATA_TYPE:NOTHING	Lcom/gainscha/GpCom/GpCom$DATA_TYPE;
    //   1642: putfield 283	com/gainscha/GpCom/GpComCallbackInfo:ReceivedDataType	Lcom/gainscha/GpCom/GpCom$DATA_TYPE;
    //   1645: aload_1
    //   1646: invokevirtual 156	java/util/Vector:size	()I
    //   1649: aload_0
    //   1650: getfield 134	com/gainscha/GpCom/Port:m_dataBlockSize	I
    //   1653: invokestatic 295	java/lang/Math:min	(II)I
    //   1656: istore_3
    //   1657: aload_0
    //   1658: getfield 81	com/gainscha/GpCom/Port:m_sizeInfoBlock	Ljava/util/Vector;
    //   1661: aload_1
    //   1662: iconst_0
    //   1663: iload_3
    //   1664: invokevirtual 301	java/util/Vector:subList	(II)Ljava/util/List;
    //   1667: invokevirtual 305	java/util/Vector:addAll	(Ljava/util/Collection;)Z
    //   1670: pop
    //   1671: aload_1
    //   1672: iconst_0
    //   1673: iload_3
    //   1674: invokevirtual 301	java/util/Vector:subList	(II)Ljava/util/List;
    //   1677: invokeinterface 309 1 0
    //   1682: aload_0
    //   1683: dup
    //   1684: getfield 134	com/gainscha/GpCom/Port:m_dataBlockSize	I
    //   1687: iload_3
    //   1688: isub
    //   1689: putfield 134	com/gainscha/GpCom/Port:m_dataBlockSize	I
    //   1692: aload_0
    //   1693: dup
    //   1694: getfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   1697: iload_3
    //   1698: i2l
    //   1699: ladd
    //   1700: putfield 132	com/gainscha/GpCom/Port:m_receiveCounter	J
    //   1703: aload_0
    //   1704: getfield 134	com/gainscha/GpCom/Port:m_dataBlockSize	I
    //   1707: ifne +30 -> 1737
    //   1710: aload_0
    //   1711: getstatic 323	com/gainscha/GpCom/GpCom$RECEIVESUBSTATE:RSUBSTATE_SIZEINFO	Lcom/gainscha/GpCom/GpCom$RECEIVESUBSTATE;
    //   1714: putfield 130	com/gainscha/GpCom/Port:m_receiveSubState	Lcom/gainscha/GpCom/GpCom$RECEIVESUBSTATE;
    //   1717: aload_0
    //   1718: getstatic 118	com/gainscha/GpCom/GpCom$RECEIVESTATE:RSTATE_SINGLE	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   1721: putfield 123	com/gainscha/GpCom/Port:m_receiveState	Lcom/gainscha/GpCom/GpCom$RECEIVESTATE;
    //   1724: aload_0
    //   1725: aload 10
    //   1727: invokevirtual 326	com/gainscha/GpCom/Port:writeDataImmediately	(Ljava/util/Vector;)Lcom/gainscha/GpCom/GpCom$ERROR_CODE;
    //   1730: pop
    //   1731: goto +6 -> 1737
    //   1734: goto +3 -> 1737
    //   1737: aload_1
    //   1738: invokevirtual 156	java/util/Vector:size	()I
    //   1741: ifgt -1669 -> 72
    //   1744: invokestatic 341	com/gainscha/GpCom/Port:$SWITCH_TABLE$com$gainscha$GpCom$GpCom$DATA_TYPE	()[I
    //   1747: aload_0
    //   1748: getfield 63	com/gainscha/GpCom/Port:m_callbackInfo	Lcom/gainscha/GpCom/GpComCallbackInfo;
    //   1751: getfield 283	com/gainscha/GpCom/GpComCallbackInfo:ReceivedDataType	Lcom/gainscha/GpCom/GpCom$DATA_TYPE;
    //   1754: invokevirtual 343	com/gainscha/GpCom/GpCom$DATA_TYPE:ordinal	()I
    //   1757: iaload
    //   1758: tableswitch	default:+78->1836, 1:+68->1826, 2:+78->1836, 3:+78->1836, 4:+34->1792, 5:+55->1813
    //   1792: ldc_w 344
    //   1795: ldc_w 346
    //   1798: invokestatic 191	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   1801: pop
    //   1802: aload_0
    //   1803: iconst_1
    //   1804: invokestatic 108	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   1807: putfield 114	com/gainscha/GpCom/Port:m_RealtimeStatusReady	Ljava/lang/Boolean;
    //   1810: goto +26 -> 1836
    //   1813: ldc_w 344
    //   1816: ldc_w 348
    //   1819: invokestatic 191	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   1822: pop
    //   1823: goto +13 -> 1836
    //   1826: ldc_w 344
    //   1829: ldc_w 350
    //   1832: invokestatic 191	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   1835: pop
    //   1836: getstatic 90	com/gainscha/GpCom/GpCom$ERROR_CODE:SUCCESS	Lcom/gainscha/GpCom/GpCom$ERROR_CODE;
    //   1839: areturn
    // Line number table:
    //   Java source line #255	-> byte code offset #0
    //   Java source line #256	-> byte code offset #2
    //   Java source line #257	-> byte code offset #4
    //   Java source line #258	-> byte code offset #7
    //   Java source line #259	-> byte code offset #13
    //   Java source line #261	-> byte code offset #16
    //   Java source line #262	-> byte code offset #20
    //   Java source line #264	-> byte code offset #24
    //   Java source line #265	-> byte code offset #32
    //   Java source line #266	-> byte code offset #42
    //   Java source line #268	-> byte code offset #57
    //   Java source line #269	-> byte code offset #63
    //   Java source line #271	-> byte code offset #68
    //   Java source line #276	-> byte code offset #72
    //   Java source line #276	-> byte code offset #75
    //   Java source line #279	-> byte code offset #132
    //   Java source line #280	-> byte code offset #144
    //   Java source line #282	-> byte code offset #149
    //   Java source line #284	-> byte code offset #159
    //   Java source line #286	-> byte code offset #164
    //   Java source line #287	-> byte code offset #171
    //   Java source line #289	-> byte code offset #184
    //   Java source line #291	-> byte code offset #194
    //   Java source line #294	-> byte code offset #201
    //   Java source line #295	-> byte code offset #208
    //   Java source line #298	-> byte code offset #227
    //   Java source line #299	-> byte code offset #229
    //   Java source line #300	-> byte code offset #236
    //   Java source line #299	-> byte code offset #239
    //   Java source line #301	-> byte code offset #246
    //   Java source line #303	-> byte code offset #256
    //   Java source line #305	-> byte code offset #262
    //   Java source line #306	-> byte code offset #267
    //   Java source line #323	-> byte code offset #277
    //   Java source line #326	-> byte code offset #284
    //   Java source line #329	-> byte code offset #303
    //   Java source line #330	-> byte code offset #305
    //   Java source line #331	-> byte code offset #312
    //   Java source line #330	-> byte code offset #315
    //   Java source line #332	-> byte code offset #322
    //   Java source line #333	-> byte code offset #329
    //   Java source line #336	-> byte code offset #339
    //   Java source line #337	-> byte code offset #345
    //   Java source line #338	-> byte code offset #350
    //   Java source line #340	-> byte code offset #353
    //   Java source line #342	-> byte code offset #356
    //   Java source line #344	-> byte code offset #359
    //   Java source line #345	-> byte code offset #376
    //   Java source line #348	-> byte code offset #383
    //   Java source line #351	-> byte code offset #400
    //   Java source line #352	-> byte code offset #402
    //   Java source line #353	-> byte code offset #409
    //   Java source line #352	-> byte code offset #412
    //   Java source line #354	-> byte code offset #419
    //   Java source line #355	-> byte code offset #430
    //   Java source line #357	-> byte code offset #441
    //   Java source line #358	-> byte code offset #448
    //   Java source line #360	-> byte code offset #458
    //   Java source line #362	-> byte code offset #461
    //   Java source line #365	-> byte code offset #508
    //   Java source line #367	-> byte code offset #517
    //   Java source line #368	-> byte code offset #532
    //   Java source line #369	-> byte code offset #538
    //   Java source line #371	-> byte code offset #548
    //   Java source line #373	-> byte code offset #566
    //   Java source line #376	-> byte code offset #573
    //   Java source line #377	-> byte code offset #592
    //   Java source line #380	-> byte code offset #614
    //   Java source line #381	-> byte code offset #616
    //   Java source line #382	-> byte code offset #623
    //   Java source line #381	-> byte code offset #626
    //   Java source line #384	-> byte code offset #633
    //   Java source line #385	-> byte code offset #635
    //   Java source line #386	-> byte code offset #641
    //   Java source line #388	-> byte code offset #644
    //   Java source line #389	-> byte code offset #647
    //   Java source line #386	-> byte code offset #650
    //   Java source line #392	-> byte code offset #669
    //   Java source line #395	-> byte code offset #676
    //   Java source line #398	-> byte code offset #693
    //   Java source line #399	-> byte code offset #695
    //   Java source line #400	-> byte code offset #702
    //   Java source line #399	-> byte code offset #705
    //   Java source line #402	-> byte code offset #712
    //   Java source line #404	-> byte code offset #726
    //   Java source line #407	-> byte code offset #733
    //   Java source line #408	-> byte code offset #744
    //   Java source line #433	-> byte code offset #758
    //   Java source line #435	-> byte code offset #769
    //   Java source line #435	-> byte code offset #772
    //   Java source line #438	-> byte code offset #820
    //   Java source line #439	-> byte code offset #830
    //   Java source line #440	-> byte code offset #836
    //   Java source line #441	-> byte code offset #838
    //   Java source line #443	-> byte code offset #844
    //   Java source line #445	-> byte code offset #847
    //   Java source line #446	-> byte code offset #850
    //   Java source line #443	-> byte code offset #853
    //   Java source line #449	-> byte code offset #872
    //   Java source line #451	-> byte code offset #886
    //   Java source line #452	-> byte code offset #889
    //   Java source line #453	-> byte code offset #892
    //   Java source line #456	-> byte code offset #898
    //   Java source line #457	-> byte code offset #912
    //   Java source line #459	-> byte code offset #923
    //   Java source line #461	-> byte code offset #934
    //   Java source line #462	-> byte code offset #942
    //   Java source line #463	-> byte code offset #945
    //   Java source line #464	-> byte code offset #952
    //   Java source line #465	-> byte code offset #959
    //   Java source line #467	-> byte code offset #966
    //   Java source line #469	-> byte code offset #969
    //   Java source line #470	-> byte code offset #979
    //   Java source line #471	-> byte code offset #985
    //   Java source line #472	-> byte code offset #987
    //   Java source line #474	-> byte code offset #993
    //   Java source line #476	-> byte code offset #996
    //   Java source line #477	-> byte code offset #999
    //   Java source line #474	-> byte code offset #1002
    //   Java source line #480	-> byte code offset #1021
    //   Java source line #482	-> byte code offset #1035
    //   Java source line #483	-> byte code offset #1038
    //   Java source line #484	-> byte code offset #1041
    //   Java source line #487	-> byte code offset #1047
    //   Java source line #488	-> byte code offset #1061
    //   Java source line #490	-> byte code offset #1072
    //   Java source line #492	-> byte code offset #1083
    //   Java source line #493	-> byte code offset #1091
    //   Java source line #494	-> byte code offset #1094
    //   Java source line #495	-> byte code offset #1101
    //   Java source line #496	-> byte code offset #1108
    //   Java source line #498	-> byte code offset #1115
    //   Java source line #547	-> byte code offset #1118
    //   Java source line #549	-> byte code offset #1121
    //   Java source line #583	-> byte code offset #1124
    //   Java source line #584	-> byte code offset #1134
    //   Java source line #585	-> byte code offset #1136
    //   Java source line #587	-> byte code offset #1142
    //   Java source line #589	-> byte code offset #1145
    //   Java source line #590	-> byte code offset #1148
    //   Java source line #587	-> byte code offset #1151
    //   Java source line #593	-> byte code offset #1170
    //   Java source line #595	-> byte code offset #1184
    //   Java source line #598	-> byte code offset #1191
    //   Java source line #601	-> byte code offset #1198
    //   Java source line #604	-> byte code offset #1215
    //   Java source line #605	-> byte code offset #1217
    //   Java source line #606	-> byte code offset #1224
    //   Java source line #605	-> byte code offset #1227
    //   Java source line #608	-> byte code offset #1234
    //   Java source line #609	-> byte code offset #1245
    //   Java source line #611	-> byte code offset #1256
    //   Java source line #613	-> byte code offset #1259
    //   Java source line #615	-> byte code offset #1268
    //   Java source line #616	-> byte code offset #1283
    //   Java source line #617	-> byte code offset #1289
    //   Java source line #619	-> byte code offset #1299
    //   Java source line #621	-> byte code offset #1308
    //   Java source line #622	-> byte code offset #1315
    //   Java source line #624	-> byte code offset #1322
    //   Java source line #630	-> byte code offset #1332
    //   Java source line #632	-> byte code offset #1341
    //   Java source line #634	-> byte code offset #1352
    //   Java source line #635	-> byte code offset #1367
    //   Java source line #636	-> byte code offset #1373
    //   Java source line #637	-> byte code offset #1383
    //   Java source line #639	-> byte code offset #1386
    //   Java source line #641	-> byte code offset #1397
    //   Java source line #642	-> byte code offset #1412
    //   Java source line #643	-> byte code offset #1418
    //   Java source line #644	-> byte code offset #1428
    //   Java source line #646	-> byte code offset #1431
    //   Java source line #648	-> byte code offset #1442
    //   Java source line #649	-> byte code offset #1466
    //   Java source line #650	-> byte code offset #1472
    //   Java source line #651	-> byte code offset #1482
    //   Java source line #656	-> byte code offset #1485
    //   Java source line #656	-> byte code offset #1488
    //   Java source line #659	-> byte code offset #1536
    //   Java source line #660	-> byte code offset #1546
    //   Java source line #661	-> byte code offset #1558
    //   Java source line #662	-> byte code offset #1572
    //   Java source line #664	-> byte code offset #1583
    //   Java source line #665	-> byte code offset #1593
    //   Java source line #667	-> byte code offset #1604
    //   Java source line #669	-> byte code offset #1611
    //   Java source line #670	-> byte code offset #1618
    //   Java source line #671	-> byte code offset #1625
    //   Java source line #673	-> byte code offset #1632
    //   Java source line #675	-> byte code offset #1635
    //   Java source line #676	-> byte code offset #1645
    //   Java source line #677	-> byte code offset #1657
    //   Java source line #678	-> byte code offset #1671
    //   Java source line #680	-> byte code offset #1682
    //   Java source line #681	-> byte code offset #1692
    //   Java source line #683	-> byte code offset #1703
    //   Java source line #685	-> byte code offset #1710
    //   Java source line #686	-> byte code offset #1717
    //   Java source line #687	-> byte code offset #1724
    //   Java source line #689	-> byte code offset #1731
    //   Java source line #728	-> byte code offset #1734
    //   Java source line #274	-> byte code offset #1737
    //   Java source line #765	-> byte code offset #1744
    //   Java source line #765	-> byte code offset #1747
    //   Java source line #768	-> byte code offset #1792
    //   Java source line #769	-> byte code offset #1802
    //   Java source line #770	-> byte code offset #1810
    //   Java source line #780	-> byte code offset #1813
    //   Java source line #781	-> byte code offset #1823
    //   Java source line #783	-> byte code offset #1826
    //   Java source line #787	-> byte code offset #1836
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1840	0	this	Port
    //   0	1840	1	receivedData	Vector<Byte>
    //   1	256	2	oneByte	byte
    //   3	1695	3	dataCounter	int
    //   5	1147	4	dataSize	int
    //   11	1073	5	blockEndFlag	Boolean
    //   14	50	6	receivedDataLength	int
    //   18	3	7	MICR_ORMask	byte
    //   22	3	8	MICR_ANDMask	byte
    //   30	19	9	bACK	byte
    //   40	1686	10	v_ACK	Vector
    //   227	10	11	localObject1	Object
    //   303	10	11	localObject2	Object
    //   400	10	11	localObject3	Object
    //   614	10	11	localObject4	Object
    //   693	10	11	localObject5	Object
    //   1215	10	11	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   201	227	227	finally
    //   284	303	303	finally
    //   383	400	400	finally
    //   573	614	614	finally
    //   676	693	693	finally
    //   1198	1215	1215	finally
  }
}
