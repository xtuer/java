public class Test {
    public static void main(String[] args) {
        User user = new User(1, "Alice", "alice@gmail.com");
        System.out.println(user.getUsername());
        System.out.println(user.getEmail());
    }
}
