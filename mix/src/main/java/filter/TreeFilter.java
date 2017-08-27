package filter;

public class TreeFilter implements Filter {
    @Override
    public void doFilter(Request request, FilterChain chain) {
        System.out.println("Before TreeFilter");
        request.setMessage(request.getMessage() + " tree");
        chain.doFilter(request);
        System.out.println("After TreeFilter");
    }
}
