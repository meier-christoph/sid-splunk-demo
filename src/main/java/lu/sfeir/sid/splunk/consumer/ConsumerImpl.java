/*
 * Copyright (C) 2014 - Christoph Meier.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package lu.sfeir.sid.splunk.consumer;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lu.sfeir.sid.splunk.broker.BrokerAcknowledgmentEvent;
import lu.sfeir.sid.splunk.message.SwiftAcknowledgment;
import lu.sfeir.sid.splunk.message.SwiftMessage;
import lu.sfeir.sid.splunk.utils.Failures;
import lu.sfeir.sid.splunk.utils.Threads;
import lu.sfeir.sid.splunk.utils.UUIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsumerImpl implements Consumer
{

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerImpl.class);
    private final EventBus eventBus;
    private int nThreads;
    private ExecutorService srv;

    public ConsumerImpl(EventBus eventBus, final int nThreads)
    {
        this.eventBus = eventBus;
        this.nThreads = nThreads;
    }

    @Override
    public void start()
    {
        eventBus.register(this);
        srv = Executors.newFixedThreadPool(nThreads);
    }

    @Override
    public void stop()
    {
        Threads.shotdown(srv);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onEvent(final ConsumerMessageEvent event)
    {
        srv.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    MDC.put("UUID", UUIDs.newUUID());
                    LOG.info("event=consumer-message, state=start");
                    SwiftMessage swift = event.getPayload();
                    LOG.info("{}", swift);

                    Threads.sleep(100, 350);

                    Failures.fail(0.076, "[C076] Failed to process swift message [%s]", swift.getReference());

                    SwiftAcknowledgment ack = new SwiftAcknowledgment(swift);
                    LOG.info("{}", ack);

                    Failures.fail(0.027, "[C027] Failed to send acknowledgment [%s]", ack.getReference());

                    eventBus.post(new BrokerAcknowledgmentEvent(ack));
                    LOG.info("event=consumer-message, state=success");
                }
                catch (Exception ex)
                {
                    LOG.error("event=consumer-message, state=error", ex);
                }
            }
        });
    }
}
