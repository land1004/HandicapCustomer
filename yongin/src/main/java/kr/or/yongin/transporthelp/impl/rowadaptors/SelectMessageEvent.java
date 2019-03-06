package kr.or.yongin.transporthelp.impl.rowadaptors;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class SelectMessageEvent
{
    private final String THIS_TAG = "SelectMessageEvent";

    private int selectIndex = 0;

    public SelectMessageEvent()
    {
    }

    public SelectMessageEvent(int select)
    {
        this.selectIndex = select;
    }

    /**
     * 메시지 수신 상태
     * @return
     */
    public int getSelectIndex () {return this.selectIndex;}
}
