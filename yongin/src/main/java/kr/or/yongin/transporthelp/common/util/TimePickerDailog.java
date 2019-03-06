package kr.or.yongin.transporthelp.common.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;

import kr.or.yongin.transporthelp.common.util.wheel.ArrayWheelAdapter;
import kr.or.yongin.transporthelp.common.util.wheel.NumericWheelAdapter;
import kr.or.yongin.transporthelp.common.util.wheel.OnWheelChangedListener;
import kr.or.yongin.transporthelp.common.util.wheel.WheelView;

public class TimePickerDailog extends Dialog
{
	private final String THIS_TAG = "TimePickerDailog";

	private Context Mcontex;

	private WheelView mHourWheel, mMinuteWheel;
	private ArrayList<Integer> mHours, mMinutes;
	private int mHourIndex = 0, mMinuteIndex = 0;
	private int mStartMinute, mEndMinute;

	public TimePickerDailog(Context context, String startTime, String endTime, final TimePickerListner tpl)
	{
		super(context);
		Mcontex = context;
		LinearLayout lytmain = new LinearLayout(Mcontex);
		lytmain.setOrientation(LinearLayout.VERTICAL);
		LinearLayout lytdate = new LinearLayout(Mcontex);
		LinearLayout lytbutton = new LinearLayout(Mcontex);

		Button btnset = new Button(Mcontex);
		Button btncancel = new Button(Mcontex);

		btnset.setText("확인");
		btncancel.setText("취소");

		mHourWheel = new WheelView(Mcontex);
		mMinuteWheel = new WheelView(Mcontex);

		lytdate.addView(mHourWheel, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
		lytdate.addView(mMinuteWheel, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		lytbutton.addView(btnset, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

		lytbutton.addView(btncancel, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
		lytbutton.setPadding(5, 5, 5, 5);
		lytmain.addView(lytdate);
		lytmain.addView(lytbutton);

		setContentView(lytmain);

		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);

		String start[] = startTime.split(":");
		int startHour = Integer.parseInt(start[0]);
		mStartMinute = Integer.parseInt(start[1]);
		if (mStartMinute > 0)
			mStartMinute = (int)(mStartMinute/10);

		String end[] = endTime.split(":");
		int endHour = Integer.parseInt(end[0]);
		mEndMinute = Integer.parseInt(end[1]);
		if (mEndMinute > 0)
			mEndMinute = (int)(mEndMinute/10);

		String[] hourItems = new String[endHour - startHour+1];
		mHours = new ArrayList<Integer>();
		for (int i=startHour,j=0; i<=endHour; i++,j++) {
			hourItems[j] = String.format("%02d 시", i);
			mHours.add(i);
		}

		mHourWheel.setViewAdapter(new DateArrayAdapter(context, hourItems, 0));
		mHourWheel.setCurrentItem(0);
		mHourWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				//Logview.Logwrite(THIS_TAG, "Hour : oldValue : " + oldValue + " / newValue : " + newValue);
				mHourIndex = newValue;
				updateMinute(oldValue, newValue);
			}
		});

		// 시작 분
		mMinutes = new ArrayList<Integer>();

		int step = (6 - mStartMinute);
		final String[] minuteItems = new String[step];
		for (int i=mStartMinute, j=0; i<6; i++, j++)
		{
			minuteItems[j] = String.format("%02d 분", i*10);
			mMinutes.add(i*10);
		}

		mMinuteWheel.setViewAdapter(new DateArrayAdapter(context, minuteItems, 0));
		mMinuteWheel.setCurrentItem(0);
		mMinuteWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				//Logview.Logwrite(THIS_TAG, "Minute : oldValue : " + oldValue + " / newValue : " + newValue);
				mMinuteIndex = newValue;
			}
		});

		btnset.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tpl.OnDoneButton(TimePickerDailog.this, mHours.get(mHourIndex), mMinutes.get(mMinuteIndex));
			}
		});

		btncancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				tpl.OnCancelButton(TimePickerDailog.this);
			}
		});

	}

	private void updateMinute(int oldValue, int newValue)
	{
		if (oldValue > 1 && newValue < (mHours.size() - 2))
			return;
		//Logview.Logwrite(THIS_TAG, "updateMinute");

		// 시작 분
		if (mMinutes == null)
			mMinutes = new ArrayList<Integer>();
		mMinutes.clear();

		int step = 0, startValue=0;
		String[] minuteItems = null;
		if ((newValue + 1) == mHours.size()) {
			step = mEndMinute + 1;
			startValue = 0;
			minuteItems = new String[step];
		} else {
			if (newValue == 0) {
				step = 6;
				minuteItems = new String[(6 - mStartMinute)];
				startValue = mStartMinute;
			} else {
				step = 6;
				minuteItems = new String[step];
				startValue = 0;
			}
		}

		if (minuteItems != null) {
			for (int i = startValue, j = 0; i < step; i++, j++) {
				minuteItems[j] = String.format("%02d 분", i * 10);
				mMinutes.add(i * 10);
			}

			mMinuteWheel.setViewAdapter(new DateArrayAdapter(Mcontex, minuteItems, 0));
			mMinuteWheel.setCurrentItem(0);
		}
		mMinuteIndex = 0;
	}

	private class DateNumericAdapter extends NumericWheelAdapter {
		int currentItem;
		int currentValue;

		public DateNumericAdapter(Context context, int minValue, int maxValue,
				int current) {
			super(context, minValue, maxValue);
			this.currentValue = current;
			setTextSize(20);
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			if (currentItem == currentValue) {
				view.setTextColor(0xFF0000F0);
			}
			view.setTypeface(null, Typeface.BOLD);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			currentItem = index;
			return super.getItem(index, cachedView, parent);
		}
	}

	private class DateArrayAdapter extends ArrayWheelAdapter<String> {
		int currentItem;
		int currentValue;

		public DateArrayAdapter(Context context, String[] items, int current) {
			super(context, items);
			this.currentValue = current;
			setTextSize(20);
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			if (currentItem == currentValue) {
				view.setTextColor(0xFF0000F0);
			}
			view.setTypeface(null, Typeface.BOLD);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			currentItem = index;
			return super.getItem(index, cachedView, parent);
		}
	}

	public interface TimePickerListner {
		public void OnDoneButton(Dialog datedialog, int hour, int minute);

		public void OnCancelButton(Dialog datedialog);
	}
}
