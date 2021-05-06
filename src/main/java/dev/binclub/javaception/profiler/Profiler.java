package profiler;

import dev.binclub.javaception.classfile.MethodInfo;

import java.util.HashMap;
import java.util.Map;

public class Profiler {
	public static Map<String, ProfileData> dataMap = new HashMap<>();
	
	public static void start(MethodInfo method) {
		String name = method.owner.name + ":" + method.name + ":" + method.signature;
		ProfileData pd = Profiler.dataMap.get(name);
		if (pd == null) {
			pd = new ProfileData();
			Profiler.dataMap.put(name, pd);
		}
		pd.start = System.nanoTime();
	}
	
	public static void finish(MethodInfo method) {
		String name = method.owner.name + ":" + method.name + ":" + method.signature;
		ProfileData pd = Profiler.dataMap.get(name);
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
}
