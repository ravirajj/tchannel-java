/*
 * Copyright (c) 2015 Uber Technologies, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.uber.tchannel.ping;

import com.uber.tchannel.api.Req;
import com.uber.tchannel.api.Res;
import com.uber.tchannel.api.TChannel;
import com.uber.tchannel.headers.ArgScheme;
import io.netty.util.concurrent.Promise;

import java.util.HashMap;

public class PingClient {

    private String host;
    private int port;

    public PingClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 8888;

        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        } else if (args.length == 2) {
            host = String.valueOf(args[0]);
            port = Integer.parseInt(args[1]);
        }

        System.out.println(String.format("Connecting from client to server on port: %d", port));
        new PingClient(host, port).run();
        System.out.println("Stopping Client...");

    }

    public void run() throws Exception {
        TChannel tchannel = new TChannel.Builder("ping-client").build();

        Req<Ping> req = new Req<Ping>(
                "ping",
                new HashMap<String, String>() {
                    {
                        put("some", "header");
                    }
                },
                new Ping("{'key': 'ping?'}")
        );

        Promise<Res<Pong>> f = tchannel.makeRequest(
                "localhost",
                8888,
                "service",
                ArgScheme.JSON.getScheme(),
                Pong.class,
                req
        );
        Res<Pong> res = f.get();
    }

}
