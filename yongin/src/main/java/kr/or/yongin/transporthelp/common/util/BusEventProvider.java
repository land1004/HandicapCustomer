package kr.or.yongin.transporthelp.common.util;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public final class BusEventProvider
{
    private static final Bus mBus = new Bus(ThreadEnforcer.ANY);

    private BusEventProvider()
    {

    }

    public static Bus getInstance()
    {
        return mBus;
    }
}
