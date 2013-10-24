package ru.mdmn.telsscore.core;

import java.text.DecimalFormat;

public class MyLongitude {

	
	
	public enum Direction{
		EAST("E"),
		WEST("W");
		
		private String view;
		
		private Direction(String view) {

			this.view=view;
		}
		public String getVal(){
			return view;
		}
		public String toString(){
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
	public String getStrValue() {
		return strValue;
	}
	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}
	public Direction getDir() {
		return dir;
	}
	public void setDir(Direction dir) {
		this.dir = dir;
	}
	
	public static MyLongitude  getInstance(double lon){
		MyLongitude result=new MyLongitude();
		
		try {
			
			if(lon<0){
				result.setDir(Direction.WEST);
			}else
				result.setDir(Direction.EAST);
			
			lon=Math.abs(lon);
			result.setDegree((int)lon);
			double min=(lon-result.getDegree())*60;
			result.setMin((int)min);
			float sec=(float) ((min-result.getMin())*60);
			result.setSec(sec);
			DecimalFormat format= (DecimalFormat) DecimalFormat.getNumberInstance();

			result.setStrValue(""+result.getDegree()+" "+result.getMin()+"'"+format.format(result.getSec())+"''"+result.getDir());
			
		} catch (Exception e) {
		}
		
		return result;
		
	}
	
}
