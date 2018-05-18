package data_source;

public class DemoDataSource {

	DataSourceManager3 dS = new DataSourceManager3();
	
	
	public void hilo(int pid) throws InterruptedException{
		//System.out.println("[Thread "+pid+"] Iniciando");
		while(true){
			int rand = (int)(Math.random()*3);
			dS.accessDataSource(rand);
			//System.out.print("[Thread "+pid+"]");
			Thread.sleep(3000);
			dS.accessAnyDataSource();
			
			//dS.freeDataSource(rand);
			Thread.sleep(5000);
		}
		
	}
	
	
	public void exec(){
		for(int i = 0; i < 5;i++){
			int pid = i;
			Thread thread = new Thread(()->{
				try {
					hilo(pid);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			thread.start();
		}
	}
	
	public static void main(String[]args){
		new DemoDataSource().exec();
	}
	
	
}
