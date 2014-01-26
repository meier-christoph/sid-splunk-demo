/*
 * Copyright (C) 2014 - Christoph Meier.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package lu.sfeir.sid.splunk.message;

import lu.sfeir.sid.splunk.utils.Dates;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SwiftAcknowledgment extends SwiftMessage
{

    private final boolean success;
    private final String acknowledger;
    private final Date sentAt;

    public SwiftAcknowledgment(final String reference,
                               final String sender,
                               final String receiver,
                               final String nature,
                               final String type,
                               final boolean success,
                               final String acknowledger,
                               final Date sentAt)
    {
        super(reference, sender, receiver, nature, type);
        this.success = success;
        this.acknowledger = acknowledger;
        this.sentAt = Dates.clone(sentAt);
    }

    public SwiftAcknowledgment(final SwiftMessage swift)
    {
        this(swift.getReference(),
             swift.getSender(),
             swift.getReceiver(),
             swift.getNature(),
             swift.getType(),
             true,
             "SWIFT",
             Dates.now());
    }

    public boolean isSuccess()
    {
        return success;
    }

    public String getAcknowledger()
    {
        return acknowledger;
    }

    public Date getSentAt()
    {
        return Dates.clone(sentAt);
    }

    @Override
    public String toString()
    {
        return String.format("%s, Acknowledger=%s, Success=%s, SentAt=%s",
                             super.toString(),
                             getAcknowledger(),
                             isSuccess(),
                             new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(getSentAt()));
    }
}
