package simulacro3;

public class DemoExecutor {

	public static int cuadrado(int numero){
		return numero*numero;
	}
	
	public static void main(String[]args){
		MyExecutor ejecutor = new MyExecutor(10,30);
		for(int i = 1; i <= 30; i++){
			int numero = i;
			Runnable tarea = ()->{
				System.out.println("Cuadrado de "+numero+" -> "+cuadrado(numero));
			};
			ejecutor.execute(tarea);
		}
	}
	
	
}
