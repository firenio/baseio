/*
 * Copyright 2015 The FireNio Project
 * Copyright 2013 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.others.undertow;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.logging.Logger;
import org.xnio.ChannelListener;
import org.xnio.FutureResult;
import org.xnio.IoFuture;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Xnio;
import org.xnio.XnioWorker;
import org.xnio.channels.Channels;
import org.xnio.channels.ConnectedStreamChannel;

/**
 * Test for TCP connected stream channels.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 * @author <a href="mailto:frainone@redhat.com">Flavia Rainone</a>
 */
@SuppressWarnings("deprecation")
public class NioTcpChannelTestCase {

    protected static final Logger log = Logger.getLogger("TEST");

    protected static final int SERVER_PORT = 12345;
    private final List<Throwable> problems = new CopyOnWriteArrayList<>();
    private OptionMap clientOptionMap = OptionMap.EMPTY;
    private OptionMap serverOptionMap = OptionMap.create(Options.REUSE_ADDRESSES, Boolean.TRUE);                                                                   // any random map

    private int threads = 1;

    public static void main(String[] args) throws Exception {
        log.info("Test: acceptor");
        final CountDownLatch ioLatch           = new CountDownLatch(4);
        final CountDownLatch closeLatch        = new CountDownLatch(2);
        final AtomicBoolean  clientOpened      = new AtomicBoolean();
        final AtomicBoolean  clientReadOnceOK  = new AtomicBoolean();
        final AtomicBoolean  clientReadDoneOK  = new AtomicBoolean();
        final AtomicBoolean  clientReadTooMuch = new AtomicBoolean();
        final AtomicBoolean  clientWriteOK     = new AtomicBoolean();
        final AtomicBoolean  serverOpened      = new AtomicBoolean();
        final AtomicBoolean  serverReadOnceOK  = new AtomicBoolean();
        final AtomicBoolean  serverReadDoneOK  = new AtomicBoolean();
        final AtomicBoolean  serverReadTooMuch = new AtomicBoolean();
        final AtomicBoolean  serverWriteOK     = new AtomicBoolean();
        final byte[]         bytes             = "Ummagumma!".getBytes();
        final Xnio           xnio              = Xnio.getInstance("nio");
        final XnioWorker worker = xnio.createWorker(OptionMap.create(Options.WORKER_WRITE_THREADS, 2, Options.WORKER_READ_THREADS, 2));
        try {
            final FutureResult<InetSocketAddress> futureAddressResult = new FutureResult<>();
            final IoFuture<InetSocketAddress>     futureAddress       = futureAddressResult.getIoFuture();
            worker.acceptStream(new InetSocketAddress(InetAddress.getByAddress(new byte[]{127, 0, 0, 1}), 0), new ChannelListener<ConnectedStreamChannel>() {
                private final ByteBuffer inboundBuf = ByteBuffer.allocate(512);
                private final ByteBuffer outboundBuf = ByteBuffer.wrap(bytes);
                private int readCnt = 0;

                @Override
                public void handleEvent(final ConnectedStreamChannel ch) {
                    ch.getCloseSetter().set((ChannelListener<ConnectedStreamChannel>) ch16 -> closeLatch.countDown());
                    ch.getReadSetter().set((ChannelListener<ConnectedStreamChannel>) ch15 -> {
                        try {
                            final int res = ch15.read(inboundBuf);
                            if (res == -1) {
                                serverReadDoneOK.set(true);
                                ioLatch.countDown();
                                ch15.shutdownReads();
                            } else if (res > 0) {
                                final int ttl = readCnt += res;
                                if (ttl == bytes.length) {
                                    serverReadOnceOK.set(true);
                                } else if (ttl > bytes.length) {
                                    serverReadTooMuch.set(true);
                                    IoUtils.safeClose(ch15);
                                    return;
                                }
                            }
                        } catch (IOException e) {
                            log.errorf(e, "Server read failed");
                            IoUtils.safeClose(ch15);
                        }
                    });
                    ch.getWriteSetter().set((ChannelListener<ConnectedStreamChannel>) ch14 -> {
                        try {
                            ch14.write(outboundBuf);
                            if (!outboundBuf.hasRemaining()) {
                                serverWriteOK.set(true);
                                Channels.shutdownWritesBlocking(ch14);
                                ioLatch.countDown();
                            }
                        } catch (IOException e) {
                            log.errorf(e, "Server write failed");
                            IoUtils.safeClose(ch14);
                        }
                    });
                    ch.resumeReads();
                    ch.resumeWrites();
                    serverOpened.set(true);
                }
            }, ch -> futureAddressResult.setResult(ch.getLocalAddress(InetSocketAddress.class)), OptionMap.create(Options.REUSE_ADDRESSES, Boolean.TRUE));
            final InetSocketAddress localAddress = futureAddress.get();
            worker.connectStream(localAddress, new ChannelListener<ConnectedStreamChannel>() {
                private final ByteBuffer inboundBuf = ByteBuffer.allocate(512);
                private final ByteBuffer outboundBuf = ByteBuffer.wrap(bytes);
                private int readCnt = 0;

                @Override
                public void handleEvent(final ConnectedStreamChannel ch) {
                    ch.getCloseSetter().set((ChannelListener<ConnectedStreamChannel>) ch13 -> closeLatch.countDown());
                    ch.getReadSetter().set((ChannelListener<ConnectedStreamChannel>) ch12 -> {
                        try {
                            final int res = ch12.read(inboundBuf);
                            if (res == -1) {
                                ch12.shutdownReads();
                                clientReadDoneOK.set(true);
                                ioLatch.countDown();
                            } else if (res > 0) {
                                final int ttl = readCnt += res;
                                if (ttl == bytes.length) {
                                    clientReadOnceOK.set(true);
                                } else if (ttl > bytes.length) {
                                    clientReadTooMuch.set(true);
                                    IoUtils.safeClose(ch12);
                                    return;
                                }
                            }
                        } catch (IOException e) {
                            log.errorf(e, "Client read failed");
                            IoUtils.safeClose(ch12);
                        }
                    });
                    ch.getWriteSetter().set((ChannelListener<ConnectedStreamChannel>) ch1 -> {
                        try {
                            ch1.write(outboundBuf);
                            if (!outboundBuf.hasRemaining()) {
                                clientWriteOK.set(true);
                                Channels.shutdownWritesBlocking(ch1);
                                ioLatch.countDown();
                            }
                        } catch (IOException e) {
                            log.errorf(e, "Client write failed");
                            IoUtils.safeClose(ch1);
                        }
                    });
                    ch.resumeReads();
                    ch.resumeWrites();
                    clientOpened.set(true);
                }
            }, null, OptionMap.EMPTY);
            //            assertTrue("Read timed out", ioLatch.await(500L, TimeUnit.MILLISECONDS));
            //            assertTrue("Close timed out", closeLatch.await(500L, TimeUnit.MILLISECONDS));
            //            assertFalse("Client read too much", clientReadTooMuch.get());
            //            assertTrue("Client read OK", clientReadOnceOK.get());
            //            assertTrue("Client read done", clientReadDoneOK.get());
            //            assertTrue("Client write OK", clientWriteOK.get());
            //            assertFalse("Server read too much", serverReadTooMuch.get());
            //            assertTrue("Server read OK", serverReadOnceOK.get());
            //            assertTrue("Server read done", serverReadDoneOK.get());
            //            assertTrue("Server write OK", serverWriteOK.get());
        } finally {
            worker.shutdown();
        }
    }

}
