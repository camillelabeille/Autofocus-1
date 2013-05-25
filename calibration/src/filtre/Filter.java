package filtre;

import java.util.*;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import common.TypeCalibration;

public class Filter {

	private ArrayList<DescriptiveStatistics> variables;
	private int windowSize;
	private SlidingWindow<VecteurFiltrable<Double>> window;
	private int noiseThreshold;

	public Filter(int windowSize,TypeCalibration t) {
		if (t.equals(TypeCalibration.ACCELEROMETER)){
			noiseThreshold = 3;
		}
		if(t.equals(TypeCalibration.MAGNETOMETER)){
			noiseThreshold = 3;
		}
		
		this.windowSize = windowSize;
		window = new SlidingWindow<VecteurFiltrable<Double>>(windowSize);
		variables = null;
	}

	public Filter() {
		this.windowSize = 40;
	}

	private void update(VecteurFiltrable<Double> v) {
		boolean valable = true;
		for (DescriptiveStatistics e : variables) {
			System.out.println("std : " + e.getStandardDeviation());
			valable = (e.getStandardDeviation() < noiseThreshold) && valable;
		}
		if (valable) v.setTrue(); else v.setFalse();   
	}

	public void add(VecteurFiltrable<Double> v) {
		Collection<Double> toAdd = v.setArray();
		Iterator<Double> a = toAdd.iterator();
		if (variables != null) {
			//System.out.println("variables non null");
			Iterator<DescriptiveStatistics> d = variables.iterator();
			while (a.hasNext() && d.hasNext()) {
				d.next().addValue(a.next());
				//System.out.println("ajoute les donnees");
			}
			window.add(v);
			update(v);
		} else {
			//System.out.println("cree variabless");
			this.variables = new ArrayList<DescriptiveStatistics>(toAdd.size());
			for (int i = 0; i < toAdd.size(); i++) {
				//System.out.println("cree variable");
				variables.add(new DescriptiveStatistics(windowSize));
			}
			this.add(v);
		}
	}

}
