package dev.binclub.javaception.profiler;

import dev.binclub.javaception.classfile.MethodInfo;

import java.util.HashMap;
import java.util.Map;

public class Profiler {
	public static Map<MethodInfo, ProfileData> dataMap = new HashMap<>();
	
	public static void start(MethodInfo method) {
		ProfileData pd = Profiler.dataMap.get(method);
		if (pd == null) {
			pd = new ProfileData();
			Profiler.dataMap.put(method, pd);
		}
		pd.start = System.nanoTime();
	}
	
	public static void finish(MethodInfo method) {
		ProfileData pd = Profiler.dataMap.get(method);
		long dif = System.nanoTime() - pd.start;
		pd.count++;
		pd.totalTime += dif;
	}
	
	public static class ProfileData {
		long totalTime;
		long count;
		long start;
		
		public long getAverage() {
			return this.totalTime / this.count;
		}
		
	}
	
	public static void printAllProfileData() {
		dataMap.forEach((method, pdata) -> {
			System.out.printf("%s took %d microseconds %n", method, pdata.getAverage() / 1000);
		});
	}
}
