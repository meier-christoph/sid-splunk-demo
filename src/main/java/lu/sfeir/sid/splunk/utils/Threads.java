/*
 * Copyright (C) 2014 - Christoph Meier.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package lu.sfeir.sid.splunk.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ExecutorService;

public final class Threads
{

    private static final Logger LOG = LoggerFactory.getLogger(Threads.class);

    private Threads()
    {
    }

    public static void shutdown(final ExecutorService srv)
    {
        srv.shutdown();
        waitForFinish(srv);
    }

    public static void waitForFinish(final ExecutorService srv)
    {
        while (!srv.isTerminated())
        {
        }
    }

    public static void sleep(final long time)
    {
        try
        {
            Thread.sleep(time);
        }
        catch (InterruptedException e)
        {
            LOG.warn("Failed to sleep current thread !", e);
        }
    }

    public static void sleep(final long min, final int delta)
    {
        sleep(new Random().nextInt(delta) + min);
    }
}
