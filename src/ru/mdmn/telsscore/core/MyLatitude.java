package ru.mdmn.telsscore.core;

import java.text.DecimalFormat;



public class MyLatitude {
	
	public enum Direction{
		NORTH("N"),
		SOUTH("S");
		
		private String view;
		
		private Direction(String view) {

			this.view=view;
		}
		public String getVal(){
			return view;
		}
	}
	
	private int degree;
	private int min;
	private float sec;
	private Direction dir; 
	private String strValue;
	
	public int getDegree() {
		return degree;
	}
	public void setDegree(int degree) {
		this.degree = degree;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public float getSec() {
		return sec;
	}
	public void setSec(float sec) {
		this.sec = sec;
	}
	public Direction getDir() {
		return dir;
	}
	public void setDir(Direction dir) {
		this.dir = dir;
	}
	public String getStrValue() {
		return strValue;
	}
	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}
	
	
	public static MyLatitude  getInstance(double lat){
		MyLatitude result=new MyLatitude();
		
		try {
			if(lat<0){
				result.setDir(Direction.SOUTH);
			}else
				result.setDir(Direction.NORTH);
			
			lat=Math.abs(lat);
			result.setDegree((int)lat);
			double min=(lat-result.getDegree())*60;
			result.setMin((int)min);
			float sec=(float) ((min-result.getMin())*60);
			result.setSec(sec);
			DecimalFormat format= (DecimalFormat) DecimalFormat.getNumberInstance();

			result.setStrValue(""+result.getDegree()+" "+result.getMin()+"'"+format.format(result.getSec())+"''"+result.getDir());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
		
	}
}
