package sk.nixone;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionUtil {
	static public <T> List<T> cloneForIteration(List<T> original) {
		synchronized(original) {
			return new ArrayList<T>(original);
		}
	}
	
	static public <T> Set<T> cloneForIteration(Set<T> original) {
		synchronized(original) {
			return new HashSet<T>(original);
		}
	}
}
