/*
 * Copyright (C) 2014 - Christoph Meier.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package lu.sfeir.sid.splunk.producer;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lu.sfeir.sid.splunk.broker.BrokerMessageEvent;
import lu.sfeir.sid.splunk.message.SwiftAcknowledgment;
import lu.sfeir.sid.splunk.message.SwiftMessage;
import lu.sfeir.sid.splunk.utils.Failures;
import lu.sfeir.sid.splunk.utils.Threads;
import lu.sfeir.sid.splunk.utils.UUIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.concurrent.ExecutorService;

public class Producer
{

    private static final Logger LOG = LoggerFactory.getLogger(Producer.class);
    private final EventBus eventBus;
    private ExecutorService pool;
    private ExecutorService loader;

    public Producer(final EventBus eventBus, ExecutorService pool, ExecutorService loader)
    {
        this.eventBus = eventBus;
        this.pool = pool;
        this.loader = loader;
        eventBus.register(this);
    }

    public void fireMessage()
    {
        loader.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    MDC.put("UUID", UUIDs.newUUID());
                    LOG.info("event=producer-send, state=start");

                    Threads.sleep(100, 250);

                    Failures.fail(0.012, "[P012] Failed to create swift message");

                    final SwiftMessage swift = new SwiftMessage();
                    LOG.info("{}", swift);

                    Failures.fail(0.034, "[P034] Failed to send swift message [%s]", swift.getReference());

                    eventBus.post(new BrokerMessageEvent(swift));
                    LOG.info("event=producer-send, state=success");
                }
                catch (Exception ex)
                {
                    LOG.error("event=producer-send, state=error", ex);
                }
            }

        });
    }

    public void fireMessages(final int n)
    {
        for (int i = 0; i < n; i++)
        {
            fireMessage();
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onEvent(final ProducerAcknowledgmentEvent event)
    {
        pool.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    MDC.put("UUID", UUIDs.newUUID());
                    LOG.info("event=producer-ack, state=start");
                    final SwiftAcknowledgment ack = event.getPayload();
                    LOG.info("{}", ack);

                    Threads.sleep(200, 350);

                    Failures.fail(0.092, "[P092] Failed to process swift acknowledgment [%s]", ack.getReference());

                    LOG.info("event=producer-ack, state=success");
                }
                catch (Exception ex)
                {
                    LOG.error("event=producer-ack, state=error", ex);
                }
            }
        });
    }
}
