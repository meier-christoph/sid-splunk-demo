/*
 * Copyright (C) 2014 - Christoph Meier.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package lu.sfeir.sid.splunk;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.RateLimiter;
import lu.sfeir.sid.splunk.broker.Broker;
import lu.sfeir.sid.splunk.consumer.Consumer;
import lu.sfeir.sid.splunk.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;

public final class Launcher
{

    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

    public static void main(final String... args) throws Exception
    {
        final EventBus eventBus = new AsyncEventBus(Executors.newFixedThreadPool(3));

        final Producer producer = new Producer(eventBus,
                                               Executors.newFixedThreadPool(3),
                                               Executors.newFixedThreadPool(3));
        new Broker(eventBus, Executors.newFixedThreadPool(3));
        new Consumer(eventBus, Executors.newFixedThreadPool(3));

        RateLimiter limiter = RateLimiter.create(20);
        for (int i = 0; i < 500; )
        {
            if (limiter.tryAcquire())
            {
                producer.fireMessage();
                i++;
            }
        }
    }
}
