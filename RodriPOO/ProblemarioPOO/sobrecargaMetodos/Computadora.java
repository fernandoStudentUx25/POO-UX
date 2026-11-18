package sobrecargaMetodos;

public class Computadora {

    private String nombre;
    private String cpu;
    private String ram;
    private String tarjetaGrafica;

    public Computadora(){}

    public Computadora(String nombre, String cpu) {
        this.nombre = nombre;
        this.cpu = cpu;
    }

    public Computadora(String nombre, String cpu, String ram, String tarjetaGrafica) {
        this.nombre = nombre;
        this.cpu = cpu;
        this.ram = ram;
        this.tarjetaGrafica = tarjetaGrafica;
    }

    public void login(String password){
        if(password.equals("1234")){
            System.out.println("Bienvenido!");
        }else{
            System.out.println("No puedes entrar");
        }
    }
    public void login(String username,String password){
        if(password.equals("1234") & username.equals("admin")){
            System.out.println("Bienvenido!");
        }else{
            System.out.println("No puedes entrar");
        }
    }
}
