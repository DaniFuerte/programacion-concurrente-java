package data_source;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DataSourceManager3 {


		   //Atributos necesarios
			private boolean libre1 = false; // para data source 1
			private boolean libre2 = false; // para data source 2
			
			private ReentrantLock lock;
			private Condition d1; // condicion para data source 1
			private Condition d2; // condition para data source 2
			private Condition both; // condition para ambos
			
	
	
		   public DataSourceManager3(){ 
		      //Inicializaciones necesarias
			  this.libre1 = true;
			  this.libre2 = true;
			  this.lock = new ReentrantLock(true);
			  this.d1 = this.lock.newCondition();
			  this.d2 = this.lock.newCondition();
			  this.both = this.lock.newCondition();
		   } 
		   
		   
		   protected void accessDataSource1(){
			   
			   this.lock.lock();
			   try{
				   if(!libre1){
					   while(!libre1){
						   try {
							this.d1.await();
						   } catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						   }
					   }
				   }
				   
				   System.out.println("Usando Data Source 1");
				   libre1 = false;
				   
				   
			   }finally{
				   this.lock.unlock();
			   }
			   
		   }
		   
		   protected void accessDataSource2(){

			   this.lock.lock();
			   try{
				   if(!libre2){
					   while(!libre2){
						   try {
							this.d2.await();
						   } catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						   }
					   }
				   }
				   
				   System.out.println("Usando Data Source 1");
				   libre2 = false;
				   
				   
			   }finally{
				   this.lock.unlock();
			   }			   
			   
		   }
		   
		   
		   protected void freeDataSource1(){
			   this.lock.lock();
			   try{
				   System.out.println("Liberando Data Source 1");
				   libre1 = true;
				   if(lock.getWaitQueueLength(d1) > 0){
					   d1.signalAll();
				   }else{
					   both.signalAll();
				   }
			   }finally{
				   this.lock.unlock();
			   }
		   }
		   
		   protected void freeDataSource2(){
			   this.lock.lock();
			   try{
				   System.out.println("Liberando Data Source 2");
				   libre2 = true;
				   if(lock.getWaitQueueLength(d2) > 0){
					   d2.signalAll();
				   }else{
					   both.signalAll();
				   }
			   }finally{
				   this.lock.unlock();
			   }			   
		   }
		   
		   public void accessDataSource(int dataSource){ 
		      //Este método será invocado cuando se quiera usar un DataSource concreto. 
		      //Se bloqueará hasta que ese dataSource esté libre. Una vez desbloqueado,
		      //se podrá usar el dataSource indicado como parámetro. Cuando se termine de
		      //usar, se deberá invocar el método “freeDataSource”
			   
			   switch(dataSource){
			   		case 1: this.accessDataSource1();
			   				break;
			   		case 2: this.accessDataSource2();
			   				break;
			   		default: System.out.println("Invalid Data Source");
			   				break;
 			   }
			   
			   freeDataSource(dataSource);
			   
			   
		   } 
		   
		   public int accessAnyDataSource(){ 
		      //Este método será invocado cuando se quiera usar cualquier DataSource. 
		      //Se bloqueará hasta que cualquiera de los DataSource esté libre. El dataSource
		      //libre se tendrá que devolver para que sea usado una vez desbloqueado el método.
		      //Cuando se termine de usar, se deberá invocar el método “freeDataSource”
			   
			   int salida = 0;
			   
			   this.lock.lock();
			   try{
				   if(!libre1 && !libre2){
					   while(!libre1 && !libre2){
						   try {
							both.await();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					   }
				   }
				   
				   if(libre1){
					   System.out.println("Usando uno de los dos Data Source -> 1");
					   libre1 = false;
					   salida = 1;
				   }else if(libre2){
					   System.out.println("Usando uno de los dos Data Source -> 2");
					   libre2 = false;
					   salida = 2;
				   }
				   
			   }finally{
				   this.lock.unlock();
			   }
			   
			   if(salida > 0)
				   freeDataSource(salida);
			   
			   return salida;
		   } 
		   public void freeDataSource(int dataSource){ 
		      //Este método se utilizará para liberar el DataSource que se esté usando para
		      //que pueda ser usado por otros hilos.
			   
			   switch(dataSource){
			   		case 1: this.freeDataSource1();
			   				break;
			   		case 2: this.freeDataSource2();
			   				break;
			   		default: System.out.println("Invalid Data Source");
			   				break;   		
			   }
			   
		   } 
		
	
	
}
