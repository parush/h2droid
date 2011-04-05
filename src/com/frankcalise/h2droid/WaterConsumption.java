package com.frankcalise.h2droid;

import java.util.ArrayList;
import java.util.Calendar;

public class WaterConsumption {
	private Calendar mCalendar;
	private ArrayList<Entry> mEntryList;
	private double mGoalAmount;
	private double mAmount;
	
	public WaterConsumption() {
		
	}
	
	public double getAmount() {
		return mAmount;
	}
	
	public double getGoalAmount() {
		return mGoalAmount;
	}
	
	public double getAmountToGoal() {
		return (mGoalAmount - mAmount);
	}
	
	public boolean isGoalMet() {
		if (mAmount >= mGoalAmount) {
			return true;
		}
		
		return false;
	}
	
	public void addAmount(double _amount) {
		
	}
	
	public boolean undoLastAmount() {
		return false;
	}
}
