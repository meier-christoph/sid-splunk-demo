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

import lu.sfeir.sid.splunk.utils.Randoms;
import lu.sfeir.sid.splunk.utils.UUIDs;

public class SwiftMessage
{

    private static final String[] BICS = {
            "AXABFRPPXXX",
            "FETALULLDIS",
            "BGLLLULLXXX",
            "FNETLULLXXX",
            "BSUILULLXXX",
            "CEDELULLCSK",
            "COBALULUXXX",
            "EUCOLULLXXX",
            "AGRILULAXXX",
            "CRESLULLDLX",
            "DEUTLULBXXX",
            "DXIALULLXXX",
            "TUBDLULLXXX",
            "CELLLULLXXX",
            "CELLLULLCTD",
            "CELLLULLGSD",
            "CELLLULLTRD",
            "RFLCLULXXXX",
            "NOLALULLXXX"
    };
    private static final String[] TYPES = {
            "101",
            "103",
            "101",
            "103",
            "101",
            "103",
            "101",
            "103",
            "101",
            "103",
            "202",
            "101",
            "103",
            "202",
            "101",
            "103",
            "202",
            "535",
            "536",
            "541",
            "543",
            "541",
            "543",
            "541",
            "543",
            "541",
            "543",
            "999"
    };
    private final String reference;
    private final String sender;
    private final String receiver;
    private final String nature;
    private final String type;

    public SwiftMessage(final String reference,
                        final String sender,
                        final String receiver,
                        final String nature,
                        final String type)
    {
        this.reference = reference;
        this.sender = sender;
        this.receiver = receiver;
        this.nature = nature;
        this.type = type;
    }

    public SwiftMessage()
    {
        this(UUIDs.newUUID(), Randoms.anyOf(BICS), Randoms.anyOf(BICS), "MT", Randoms.anyOf(TYPES));
    }

    public String getReference()
    {
        return reference;
    }

    public String getSender()
    {
        return sender;
    }

    public String getReceiver()
    {
        return receiver;
    }

    public String getNature()
    {
        return nature;
    }

    public String getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return String.format("Reference=%s, Sender=\"%s\", Receiver=\"%s\", Nature=%s, Type=%s",
                             getReference(),
                             getSender(),
                             getReceiver(),
                             getNature(),
                             getType());
    }
}
