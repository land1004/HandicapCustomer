package kr.or.yongin.transporthelp.common.util;

import com.ionemax.iomlibrarys.log.Logview;

import java.util.ArrayList;

import kr.or.yongin.transporthelp.common.db.DBControlManager;
import kr.or.yongin.transporthelp.common.db.DBSchema;
import kr.or.yongin.transporthelp.common.db.SelectHelper;

/**
 * Created by IONEMAX on 2017-06-30.
 */

public class AvailableDestination
{
    private final String THIS_TAG = "AvailableDestination";

    private ArrayList<DestinationItem> mDestinationList;

    public AvailableDestination ()
    {
        mDestinationList = new ArrayList<DestinationItem> ();

        initData();
    }

    public void terminate ()
    {
        if (mDestinationList != null)
            mDestinationList.clear();
        mDestinationList = null;
    }

    public boolean isAvailableDestination(String jibunFull)
    {
        boolean bret =false;

        if (mDestinationList != null)
        {
            //Logview.Logwrite(THIS_TAG, "isAvailableDestination : " + jibunFull);
            String find = "";
            for (DestinationItem item : mDestinationList)
            {
                find = item.si + " " + item.gu + " " + item.dong;
                //Logview.Logwrite(THIS_TAG, "isAvailableDestination : " + find);
                if (jibunFull.indexOf("용인시") > 0)
                {
                    Logview.Logwrite(THIS_TAG, "isAvailableDestination find : 용인시");
                    bret = true;
                    break;
                }
                else if (jibunFull.indexOf(find) > -1)
                {
                    bret = true;
                    break;
                }
            }
        }

        return bret;
    }

    private void initData ()
    {
        String sql = "SELECT * FROM " + DBSchema.TABLE_NAME_AVAILABLE_DESTINATION+ ";";
        SelectHelper select = DBControlManager.dbSelect(sql);

        DestinationItem item;
        if (select != null && select.getCount() > 0)
        {
            select.moveFirst();

            do {
                item = new DestinationItem();
                item.si = select.getValue("si");
                item.gu = select.getValue("gu");
                item.dong = select.getValue("dong");

                mDestinationList.add(item);
            } while (select.moveNext());
        }
    }

    class DestinationItem
    {
        public String si = "";
        public String gu = "";
        public String dong = "";
    }
}
