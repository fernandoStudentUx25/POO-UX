package encapsulamientoUML;

public class Usuario {

    private String nombre;
    private String correo;

    public Usuario(){}

    public Usuario(String nombre, String correo){
        this.nombre = nombre;
        this.correo = correo;
    }

    public String getNombre(){
        return nombre;
    }
    public String getCorreo(){
        return correo;
    }

    public void setNombre(String nombre){
        if(nombre.equals("") | nombre.equals(null)){
            System.out.println("parámetro inválido");
            return;
        }
        this.nombre = nombre;
    }

    public void setCorreo(String correo){
        if(correo.equals("") | correo.equals(null)){
            System.out.println("parámetro inválido");
            return;
        }
        this.correo = correo;
    }
}
