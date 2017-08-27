package filter;

public interface Filter {
    void doFilter(Request request, FilterChain chain);
}
