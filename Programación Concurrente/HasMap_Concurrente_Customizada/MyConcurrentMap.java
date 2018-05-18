package hoja3;

public interface MyConcurrentMap {

	public int insert(String key,String value);
	public String lookup(String key);
	public String waitreg(String key);
	
}
