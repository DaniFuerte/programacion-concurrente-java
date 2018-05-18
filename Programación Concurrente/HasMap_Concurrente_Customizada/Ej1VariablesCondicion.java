package hoja3;

public class Ej1VariablesCondicion {

	static final String [] claves = {"MAD","BCN","LDN","NY","PRS","MNT"};
	static final String [] valores = {"Madrid","Barcelona","Londres","Nueva York","París","Montreux"};
	
	static MyConcurrentMap map = new MyConcurrentHashMap();
	
	
	
	public static void escribe(String key, String value){
		if(map.insert(key, value) == 0)
			System.out.println("[THREAD_Insertar] Inserté Valor -> "+value+"\n"
				+ "Con Clave -> "+key);
		else{
			System.out.println("[THREAD_Insertar] Error Clave -> "+key+" Ya existe ");
		}
	}
	
	public static void lee(String key){
		String value = map.waitreg(key);
		if(value != null){
			System.out.println("[THREAD_Recoge] Clave -> "+key+"\n"
					+ "Valor -> "+value);
		}else{
			System.out.println("[THREAD_Recoge] Error Clave "+key+" No existe, o ha habido un error");
		}
	}
	
	
	public static void exec(){
		
		
		for(int i = 0; i < 6;i++){
			int index = i;
			new Thread(()->{
				lee(claves[index]);
			}).start();
			new Thread(()->{
				escribe(claves[index],valores[index]);
			}).start();
		}
		
	}
	
	public static void main(String[]args){
		new Ej1VariablesCondicion();
		Ej1VariablesCondicion.exec();
	}
	
}
