package filter;

public class AnimalFilter implements Filter {
    @Override
    public void doFilter(Request request, FilterChain chain) {
        System.out.println("Before AnimalFilter");
        request.setMessage(request.getMessage() + " animal");
        chain.doFilter(request);
        System.out.println("After AnimalFilter");
    }
}
