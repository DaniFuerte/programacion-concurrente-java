package hoja3;

import java.util.HashMap;
import java.util.Map;
//import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyConcurrentHashMap implements MyConcurrentMap {

	private Map<String,String> myHashMap = new HashMap<>();
	private Lock lock = new ReentrantLock();
	private Condition flag = lock.newCondition();
	
	
	@Override
	public int insert(String key, String value) {
		// TODO Auto-generated method stub
		this.lock.lock();
		try{
			String valor = myHashMap.get(key);
			if(valor != null)
				return 1;
			myHashMap.put(key, value);
			this.flag.signalAll();
		}finally{
			this.lock.unlock();
		}
		return 0;
	}

	@Override
	public String lookup(String key) {
		// TODO Auto-generated method stub
		String value;
		this.lock.lock();
		try{
			value = myHashMap.get(key);
		}finally{
			this.lock.unlock();
		}
		return value;
	}

	@Override
	public String waitreg(String key) {
		// TODO Auto-generated method stub
		String value;
		this.lock.lock();
		try{
			value = myHashMap.get(key);
			if(value == null){
				while(value == null){
					try {
						System.out.println("ESPERANDO "+key);
						this.flag.await();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					value = myHashMap.get(key);
				}
			}
			
		}finally{
			this.lock.unlock();
		}
		return value;
	}

}
