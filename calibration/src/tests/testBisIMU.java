package tests;

import imu.IMU;

import java.util.List;

import common.TypeCalibration;

import data.Data;
import ellipsoide.Sphere;
import filtre.FilterSphere;
import fr.dgac.ivy.IvyException;

public class testBisIMU {
	public static void main(String[] args){
	TypeCalibration t = TypeCalibration.ACCELEROMETER;
	System.out.println("type");
	Sphere sp = new Sphere(20,10);
	FilterSphere filtre = new FilterSphere(sp,40,t);
	System.out.println("filtre");
	Data data = new Data(t,filtre);
	System.out.println("data");
	
	System.out.println("data");
	try {
		IMU imu =new IMU();
		imu.IvyIdListener();
		List <Integer> listeId=imu.getList();
	} catch (IvyException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	}
}
