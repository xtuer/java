package filter;

public class Client {
    public static void main(String[] args) {
        FilterChain chain = new FilterChain();
        chain.addFilter(new AnimalFilter());
        chain.addFilter(new TreeFilter());

        Request request = new Request("Hello");
        chain.doFilter(request);
        System.out.println(request.getMessage());
    }
}
