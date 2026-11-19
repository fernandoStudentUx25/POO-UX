package sobrecargaMetodos;

public class Main{
    public static void main(String[] args) {
        Computadora laptop = new Computadora("apple", "ryzen 7000");
        
        System.out.println(laptop.getNombre());
    }
}