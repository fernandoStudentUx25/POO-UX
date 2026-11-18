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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getTarjetaGrafica() {
        return tarjetaGrafica;
    }

    public void setTarjetaGrafica(String tarjetaGrafica) {
        this.tarjetaGrafica = tarjetaGrafica;
    }

    
}
