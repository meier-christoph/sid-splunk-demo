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
import lu.sfeir.sid.splunk.broker.Broker;
import lu.sfeir.sid.splunk.broker.BrokerImpl;
import lu.sfeir.sid.splunk.consumer.Consumer;
import lu.sfeir.sid.splunk.consumer.ConsumerImpl;
import lu.sfeir.sid.splunk.producer.Producer;
import lu.sfeir.sid.splunk.producer.ProducerImpl;
import lu.sfeir.sid.splunk.utils.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Launcher
{

    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

    public static void main(final String... args) throws Exception
    {
        final ExecutorService srv = Executors.newFixedThreadPool(3);
        final EventBus eventBus = new AsyncEventBus(srv);

        final Producer producer = new ProducerImpl(eventBus, 3);
        producer.start();

        final Broker broker = new BrokerImpl(eventBus, 3);
        broker.start();

        final Consumer consumer = new ConsumerImpl(eventBus, 3);
        consumer.start();


        producer.fireMessage();
        producer.fireMessages(500);

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                LOG.warn("shutdown initiated ...");
                Threads.shutdown(srv);
                producer.stop();
                broker.stop();
                consumer.stop();
            }
        });
    }
}
