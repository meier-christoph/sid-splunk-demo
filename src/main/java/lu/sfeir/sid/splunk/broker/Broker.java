/*
 * Copyright (C) 2014 - Christoph Meier.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package lu.sfeir.sid.splunk.broker;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lu.sfeir.sid.splunk.consumer.ConsumerMessageEvent;
import lu.sfeir.sid.splunk.message.SwiftAcknowledgment;
import lu.sfeir.sid.splunk.message.SwiftMessage;
import lu.sfeir.sid.splunk.producer.ProducerAcknowledgmentEvent;
import lu.sfeir.sid.splunk.utils.Failures;
import lu.sfeir.sid.splunk.utils.Threads;
import lu.sfeir.sid.splunk.utils.UUIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.concurrent.ExecutorService;

public class Broker
{

    private static final Logger LOG = LoggerFactory.getLogger(Broker.class);
    private final EventBus eventBus;
    private ExecutorService pool;

    public Broker(final EventBus eventBus, final ExecutorService pool)
    {
        this.eventBus = eventBus;
        this.pool = pool;
        eventBus.register(this);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onEvent(final BrokerMessageEvent event)
    {
        pool.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    MDC.put("UUID", UUIDs.newUUID());
                    LOG.info("event=broker-message, state=start");
                    final SwiftMessage swift = event.getPayload();
                    LOG.info("{}", swift);

                    Threads.sleep(100, 200);

                    Failures.fail(0.007, "[B007] Failed to route swift message [%s]", swift.getReference());

                    eventBus.post(new ConsumerMessageEvent(swift));

                    LOG.info("event=broker-message, state=success");
                }
                catch (Exception ex)
                {
                    LOG.error("event=broker-message, state=error", ex);
                }
            }
        });
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onEvent(final BrokerAcknowledgmentEvent event)
    {
        pool.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    MDC.put("UUID", UUIDs.newUUID());
                    LOG.info("event=broker-ack, state=start");
                    final SwiftAcknowledgment ack = event.getPayload();
                    LOG.info("{}", ack);

                    Threads.sleep(100, 200);

                    Failures.fail(0.021, "[B021] Failed to route swift acknowledgment [%s]", ack.getReference());

                    eventBus.post(new ProducerAcknowledgmentEvent(ack));

                    LOG.info("event=broker-ack, state=success");
                }
                catch (Exception ex)
                {
                    LOG.error("event=broker-ack, state=error", ex);
                }
            }
        });
    }
}
